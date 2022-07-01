package com.example.smartalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

public class UserMenuActivity extends AppCompatActivity {
    private String uid;
    private String role;

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
                    System.out.println(e);
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
    }

    public void onSendCurrentLocation(View view) {

    }
}