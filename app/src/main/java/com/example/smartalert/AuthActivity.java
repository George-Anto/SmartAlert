package com.example.smartalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AuthActivity extends AppCompatActivity implements LocationListener {
    EditText emailText, passwordText;
    FirebaseAuth auth;
    //We define here that all the users that can sign up to our app will have the user role
    //An admin user have already been created manually
    String userType = "user";
    private DatabaseReference usersTable;
    //The location manager to get user's location
    private LocationManager manager;

    private double latitude;
    private double longitude;
    private String locationAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        emailText = findViewById(R.id.authEmail);
        passwordText = findViewById(R.id.authPassword);
        auth = FirebaseAuth.getInstance();

        //Initialize the db and the reference
        //Db and reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersTable = database.getReference("users");

        //Initialize the location manager
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    public void onLogin(View view) {
        //Get the email and password the user provided
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        //if the email and password are not empty
        if (!(email.matches("") || password.matches(""))) {
            auth.signInWithEmailAndPassword(email, password).
                    addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            //Save the user's id and their role and pass them to the next activity
                            userAuthenticated(auth.getUid(), Objects.requireNonNull(Objects.requireNonNull(auth.getCurrentUser()).getDisplayName()));
                        } else {
                            try {
                                //Show the error message so the user can understand what went wrong
                                showMessage("Login Error", Objects.requireNonNull(task.getException()).getLocalizedMessage());
                            } catch (Exception e) {
                                showMessage("Error", "Unknown Error, please try again!");
                            }
                        }
                    });
            //If the email or password is empty, show corresponding message
        } else
            errorToast(email, password);
    }

    public void onSignup(View view) {
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        if (!(email.matches("") || password.matches(""))) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    //Save an additional info to the user that signed up successfully
                    //We also save the role of the user, we have defined that all the users that can
                    //enroll have the user role
                    UserProfileChangeRequest userRole = new UserProfileChangeRequest.Builder()
                            .setDisplayName(userType)
                            .build();
                    Objects.requireNonNull(task.getResult().getUser()).updateProfile(userRole);

                    //Get the new user's location and then create an entry for them in our database
                    //The method that creates that new entry is called inside the onLocationChanged() method of the listener
                    useGPS();

                    //Send the user's id and user type to the next activity
                    userAuthenticated(auth.getUid(), userType);
                } else {
                    try {
                        //Show the error message so the user can understand what went wrong
                        showMessage("Sign Up Error", Objects.requireNonNull(task.getException()).getLocalizedMessage());
                    } catch (Exception e) {
                        showMessage("Error", "Unknown Error, please try again!");
                    }
                }
            });
        }
    }

    //Show empty email or password field helper method
    private void errorToast(String email, String password) {
        if (email.matches("") && password.matches(""))
            Toast.makeText(this, "Please provide your Email and Password.", Toast.LENGTH_LONG).show();
        else if (email.matches(""))
            Toast.makeText(this, "Please provide your Email as well.", Toast.LENGTH_LONG).show();
        else if (password.matches(""))
            Toast.makeText(this, "Please provide your Password as well.", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Unknown Error\nPlease try again.", Toast.LENGTH_LONG).show();
    }

    //Show message helper method
    private void showMessage(String title, String message){
        new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle(title)
                .setMessage(message)
                .show();
    }

    //Successful authentication helper method
    //Redirect the user to the next activity depending on their role
    private void userAuthenticated(String uid, String role) {
        //If the user is a simple user, we redirect them to the UserMenuActivity and send there their id and role too
        if (role.equals("user"))
            startActivity(new Intent(this, UserMenuActivity.class)
                    .putExtra("Uid", uid)
                    .putExtra("Role", role));
        //If the user is an admin, we redirect them to the AdminMenuActivity and send there their id and role too
        else if (role.equals("admin"))
            startActivity(new Intent(this, AdminMenuActivity.class)
                    .putExtra("Uid", uid)
                    .putExtra("Role", role));
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
        //Write the new user to our database
        //If their role is user, we do not insert admins to the users table in the firebase
        if (userType.equals("user"))
            writeUserToDB(auth.getUid(), userType);
        //After we retrieve the user's location once, we stop getting any more location data from the user
        manager.removeUpdates(this);
    }

    public void useGPS() {
        //Use the manager to retrieve the user's location if permission is already granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
        } else {
            //When permission is not granted, we will request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        }
    }

    //When the user answers to the permission request
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Check condition
        if (requestCode == 123 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //When permission is granted
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
        } else {
            //When permission is denied
            showMessage("Location Permission Denied", "Grand permission to Sign Up.");
            useGPS();
        }
    }

    //Write a new user to our database
    public void writeUserToDB(String uid, String role) {
        User newUser = new User(uid, role, latitude, longitude, locationAddress);
        usersTable.push().setValue(newUser);
    }
}