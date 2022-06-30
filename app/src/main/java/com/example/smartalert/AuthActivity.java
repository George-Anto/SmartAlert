package com.example.smartalert;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class AuthActivity extends AppCompatActivity {
    EditText emailText, passwordText;
    FirebaseAuth auth;
    //We define here that all the users that can sign up to our app will have the user role
    //An admin user have already been created manually
    String userType = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        emailText = findViewById(R.id.authEmail);
        passwordText = findViewById(R.id.authPassword);
        auth = FirebaseAuth.getInstance();
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
                    //Send the user's id and user type to the next activity
                    userAuthenticated(auth.getUid(), userType);
                } else {
                    try {
                        //Show the error message so the user can understand what went wrong
                        showMessage("Login Error", Objects.requireNonNull(task.getException()).getLocalizedMessage());
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
}