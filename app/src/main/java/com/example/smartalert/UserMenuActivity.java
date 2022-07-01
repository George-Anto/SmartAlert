package com.example.smartalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
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

import java.io.Console;
import java.util.List;
import java.util.Locale;

public class UserMenuActivity extends AppCompatActivity implements LocationListener {
    private String uid;
    private String role;

    private double latitude;
    private double longitude;
    private String locationAddress;

    //The location manager to get user's location
    private LocationManager manager;

    private EditText phoneNumberView;

    private User currentUser;

    //Db and db reference
    private FirebaseDatabase database;
    private DatabaseReference usersTable;
    private String userUpdateRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_menu);

        //Retrieve user's id and role
        uid = getIntent().getStringExtra("Uid");
        role = getIntent().getStringExtra("Role");

        phoneNumberView = findViewById(R.id.usersPhoneNumberTextView);

        //Initialize db and reference
        database = FirebaseDatabase.getInstance();
        usersTable = database.getReference("users");

        Query query = usersTable.orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> children = snapshot.getChildren();
                children.forEach(child -> {
//                    System.out.println("-------------------");
//                    System.out.println(child.getKey());
//                    System.out.println("-------------------");
                    userUpdateRef = child.getKey();
                    currentUser = child.getValue(User.class);
                });
//                showMessage(uid, currentUser.toString());
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

    }

    public void onSendPhone(View view) {
        long newPhoneNumber = -1;
        try {
            newPhoneNumber = Long.parseLong(phoneNumberView.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Not a Valid Number!", Toast.LENGTH_LONG).show();
            return;
        }
        if (newPhoneNumber < 6900000000L || newPhoneNumber >= 7000000000L) {
            Toast.makeText(this, "Not a Valid Phone Number!", Toast.LENGTH_LONG).show();
            return;
        }
        usersTable.child(userUpdateRef).child("phoneNumber").setValue(newPhoneNumber);
        showMessage("Phone Number Updated", String.valueOf(newPhoneNumber));
    }

    public void onSendCurrentLocation(View view) {
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
        updateUsersLocationInfo();
        //After we retrieve the user's location once, we stop getting any more location data from the user
        manager.removeUpdates(this);
    }

    private void updateUsersLocationInfo() {
        if (latitude == 0 || longitude == 0) {
            Toast.makeText(this, "Please enable GPS to update your location.", Toast.LENGTH_LONG).show();
            return;
        }
        usersTable.child(userUpdateRef).child("latitude").setValue(latitude);
        usersTable.child(userUpdateRef).child("longitude").setValue(longitude);
        if (locationAddress == null)
            locationAddress = "Unknown Address";
        usersTable.child(userUpdateRef).child("locationAddress").setValue(locationAddress);
        showMessage("Location Updated", locationAddress);
    }
}