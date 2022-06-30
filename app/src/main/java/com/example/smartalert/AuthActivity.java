package com.example.smartalert;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class AuthActivity extends AppCompatActivity {
    EditText emailText, passwordText;
    FirebaseAuth auth;
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
                            //Save the user's id
                            userAuthenticated(auth.getUid(), auth.getCurrentUser().getDisplayName());
                           // showMessage("Current User", auth.getCurrentUser().getDisplayName());
                        } else {
                            try {
                                //Show the error message so the user can understand what went wrong
                                showMessage("Login Error", Objects.requireNonNull(task.getException()).getLocalizedMessage());
                            } catch (Exception e) {
                                showMessage("Error", "Unknown Error, please try again!");
                            }
                        }
                    });
        } else
            errorToast(email, password);
    }

    public void onSignup(View view) {
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        if (!(email.matches("") || password.matches(""))) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    //Save the user's id
                    //userAuthenticated(auth.getUid());
                    UserProfileChangeRequest userRole = new UserProfileChangeRequest.Builder()
                            .setDisplayName(userType)
                            .build();
                    task.getResult().getUser().updateProfile(userRole);
//                    showMessage("You did it", "Good job");
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
    //Redirect the user to the main menu activity
    private void userAuthenticated(String uid, String role) {
        if (role.equals("user"))
            //Send the user's id to the next activity as well
            startActivity(new Intent(this, UserMenuActivity.class).putExtra("Uid", uid).putExtra("Role", role));
        else if (role.equals("admin"))
            startActivity(new Intent(this, AdminMenuActivity.class).putExtra("Uid", uid).putExtra("Role", role));
    }
}