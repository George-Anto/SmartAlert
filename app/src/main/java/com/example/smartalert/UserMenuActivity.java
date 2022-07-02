package com.example.smartalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Locale;

public class UserMenuActivity extends AppCompatActivity implements LocationListener {
    String uid;
    //Those attributes will be used if the user decides to update their location status on our db
    private double latitude;
    private double longitude;
    private String locationAddress;

    //The location manager to get user's location
    private LocationManager manager;

    private EditText phoneNumberView;

    //The current user's info that are stored in our db
    private User currentUser;

    //Database Reference
    private DatabaseReference usersTable;
    private String userUpdateRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_menu);

        //Retrieve user's id and role
        uid = getIntent().getStringExtra("Uid");

        phoneNumberView = findViewById(R.id.usersPhoneNumberTextView);

        //Initialize db and reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersTable = database.getReference("users");

        //Get the current user from the database and store them in the currentUser instance
        Query query = usersTable.orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> children = snapshot.getChildren();
                children.forEach(child -> {
                    userUpdateRef = child.getKey();
                    currentUser = child.getValue(User.class);
                });
                //If the user has not sent their phone number yet, the variable will be 0
                //and if this is the case, no value will be presented to the user
                try {
                    if (currentUser.getPhoneNumber() != 0)
                        phoneNumberView.setText(String.valueOf(currentUser.getPhoneNumber()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Show message helper method
    private void showMessage(String title, String message) {
        new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle(title)
                .setMessage(message)
                .show();
    }

    public void onCreateNew(View view) {
        startActivity(new Intent(this, CreateDangerousSituationActivity.class)
                .putExtra("Uid", uid));
    }

    //Get the phone number input from the user and store it in their entry in our db
    public void onSendPhone(View view) {
        long newPhoneNumber;
        //If the user has entered letters in the input field as well
        //a toast will inform them so
        try {
            newPhoneNumber = Long.parseLong(phoneNumberView.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Not a Valid Number!", Toast.LENGTH_LONG).show();
            return;
        }
        //If the number that the user has entered does not meet our criteria, we inform them so
        if (newPhoneNumber < 6900000000L || newPhoneNumber >= 7000000000L) {
            Toast.makeText(this, "Not a Valid Phone Number!", Toast.LENGTH_LONG).show();
            return;
        }
        //If the input is valid, it will be sent to the db and a success message will be shown
        usersTable.child(userUpdateRef).child("phoneNumber").setValue(newPhoneNumber);
        showMessage("Phone Number Updated", String.valueOf(newPhoneNumber));
    }

    public void onSendCurrentLocation(View view) {
        //Ask for the user's permission to use their location if we do not currently have it or
        //Use the manager to retrieve the user's location
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
        //Save the updates to the database
        updateUsersLocationInfo();
        //After we retrieve the user's location once, we stop getting any more location data from the user
        manager.removeUpdates(this);
    }

    private void updateUsersLocationInfo() {
        //If the GPS has not retrieved coordinates from the device, alert the user to give the corresponding permissions
        if (latitude == 0 || longitude == 0) {
            Toast.makeText(this, "Please enable GPS to update your location.", Toast.LENGTH_LONG).show();
            return;
        }
        //Update the user's coordinates in our database
        usersTable.child(userUpdateRef).child("latitude").setValue(latitude);
        usersTable.child(userUpdateRef).child("longitude").setValue(longitude);
        if (locationAddress == null)
            locationAddress = "Unknown Address";
        //Update the address as well
        usersTable.child(userUpdateRef).child("locationAddress").setValue(locationAddress);
        //Show a success message
        showMessage("Location Updated", locationAddress);
    }
}