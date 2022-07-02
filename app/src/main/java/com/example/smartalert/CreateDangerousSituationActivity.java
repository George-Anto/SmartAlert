package com.example.smartalert;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CreateDangerousSituationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, LocationListener {
    //All the information that we will use to create the new request for the user
    private String uid;
    private String category;
    private double latitude;
    private double longitude;
    private String locationAddress;
    private String description;
    private String imagePath;

    //The location manager to get user's location
    private LocationManager manager;

    //Db and reference
    private FirebaseDatabase database;
    private DatabaseReference dangerousSituationsTable;

    //File storage for the images that will be uploaded and reference
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private ImageView imageToUploadView;
    public Uri imageUri;

    private EditText descriptionText;

    //Dropdown list with the category options
    private Spinner spinner;
    //The categories that will be used
    private static final String[] paths = {"Forest Fire", "City Fire", "Flood",
            "Earthquake", "Tornado", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_dangerous_situation);

        uid = getIntent().getStringExtra("Uid");

        //Initialize the view that the user will use to upload the image
        imageToUploadView = findViewById(R.id.newDangerousSituationImageView);
        //Action for when the user clicks to upload the image
        imageToUploadView.setOnClickListener(v -> { chooseImage(); });

        descriptionText = findViewById(R.id.newDangerousSituationDescriptionView);

        //Get the user's location using the GPS
        useGPS();

        //Create the dropdown list with it's options
        spinner = findViewById(R.id.newDangerousSituationSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateDangerousSituationActivity.this,
                android.R.layout.simple_spinner_item, paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        //Initialize the db and the reference
        database = FirebaseDatabase.getInstance();
        dangerousSituationsTable = database.getReference("dangerous_situations");

        //Initialize the file storage and the reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                category = paths[0];
                break;
            case 1:
                category = paths[1];
                break;
            case 2:
                category = paths[2];
                break;
            case 3:
                category = paths[3];
                break;
            case 4:
                category = paths[4];
                break;
            default:
                category = paths[5];
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        //Get the user's current coordinates
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        //Get the address of the user based on the coordinates we retrieved
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            //Get the first address of the produced list with possible addresses
            Address firstAddress = addresses.get(0);
            locationAddress = firstAddress.getAddressLine(0);
        } catch (Exception e) {
            Log.d("Error", e.getLocalizedMessage());
            e.printStackTrace();
        }
        //After we retrieve the user's location once, we stop getting any more location data from the user
        manager.removeUpdates(this);
    }

    private void useGPS() {
        //Ask for the user's permission to use their location if we do not currently have it or
        //Use the manger to retrieve the user's location
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},123);
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //We used some limits for the location updates, so that the updates will not arrive very fast
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
            }
        } else {
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
        }
    }

    //Helper method that opens the gallery of the phone and the user can then choose a photo to upload
    private void chooseImage() {
        startActivityForResult(new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT), 1);
    }

    //The method called when the user has chosen the image to upload
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //If the request code is returned from the gallery and if the data are correct, we upload the image
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //Preview of the image the user selected
            imageUri = data.getData();
            imageToUploadView.setImageURI(imageUri);
            //We call the actual method for the upload
            uploadImage();
        }
    }

    //The method that is responsible for the image upload
    private void uploadImage() {
        //Create a random String
        final String randomKey = UUID.randomUUID().toString();
        //Create the reference that points where in the firebase storage the images will be saved
        StorageReference imagesStorageRef = storageReference.child("images/" + randomKey);

        //Create  progress dialog so the user can see the progress
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Processing Image...");
        pd.show();

        //Upload the file (image) to our storage
        imagesStorageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            pd.dismiss();
            StorageReference currentImagePath = storageReference.child("images/" + randomKey);
            //Get the image token, we will then store in our db and link it with the current user
            //We can then use this token to download and show the current image in our app
            currentImagePath.getDownloadUrl().addOnSuccessListener(uri -> imagePath = uri.toString());
            //Show the completion message to the user
            Snackbar.make(findViewById(android.R.id.content), "Image Processed", Snackbar.LENGTH_LONG).show();
        }).addOnFailureListener(e -> {
            //If there is an error with the process, show corresponding message
            pd.dismiss();
            Toast.makeText(getApplicationContext(), "Process Failed", Toast.LENGTH_LONG).show();
        }).addOnProgressListener(snapshot -> {
            //Calculate and show the percentage progress to the user
            double progressPercent = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
            pd.setMessage((int) progressPercent + "%");
        });
    }

    public void onUploadDangerousSituation(View view) {
        //Get the description the user provided
        description = descriptionText.getText().toString();
        //If the user has not permitted the usage of the GPS to our app,
        //show them the corresponding message and ask for the permission again with the useGPS() method, then return
        if (latitude == 0 || longitude == 0) {
            Toast.makeText(this, "Please enable GPS to retrieve your location.", Toast.LENGTH_LONG).show();
            useGPS();
            return;
        }
        DangerousSituation currentDangerousSituation = new DangerousSituation(uid, latitude, longitude, locationAddress, category, description, imagePath);

        dangerousSituationsTable.push().setValue(currentDangerousSituation);

        showMessage("Success", "Your Request has been uploaded.");
    }

    //Show message helper method
    private void showMessage(String title, String message){
        new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle(title)
                .setMessage(message)
                .show();
    }
}