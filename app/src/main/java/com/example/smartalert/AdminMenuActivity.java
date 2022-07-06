package com.example.smartalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminMenuActivity extends AppCompatActivity {
    //The layout that the cards with the data will be added
    private LinearLayout layout;
    //The view if there will be no data to display
    private View noDataView;
    private boolean firstCheck = true;
    //Lists that will hold the data, if any, of the different categories of dangerous situations groups
    private final ArrayList<DangerousSituationsGroup> forestFiresGroups = new ArrayList<>();
    private final ArrayList<DangerousSituationsGroup> cityFiresGroups = new ArrayList<>();
    private final ArrayList<DangerousSituationsGroup> floodsGroups = new ArrayList<>();
    private final ArrayList<DangerousSituationsGroup> earthquakesGroups = new ArrayList<>();
    private final ArrayList<DangerousSituationsGroup> tornadosGroups = new ArrayList<>();
    private final ArrayList<DangerousSituationsGroup> othersGroups = new ArrayList<>();

    //List that will hold all the users that exist in our database
    private final ArrayList<User> allUsers = new ArrayList<>();
    //List that will hold all the phone numbers of the users that will be alerted in each case
    ArrayList<String> usersToAlertPhoneNumbers = new ArrayList<>();
    //Users table reference
    private DatabaseReference usersTable;
    //Instance that will hold the current dangerous situation group in order to extract info to send the users
    private DangerousSituationsGroup dangerousSituationsGroupToAlertAbout;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        //Initialize the container layout
        layout = findViewById(R.id.admin_linearLayout_container);
        //Initialize the no data view
        noDataView = getLayoutInflater().inflate(R.layout.no_dangerous_situations_cards, null);

        //Initialize db and references for each table in our db
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference forestFiresGroupsTable = database.getReference("forest_fires_groups");
        DatabaseReference cityFiresGroupsTable = database.getReference("city_fires_groups");
        DatabaseReference floodsGroupsTable = database.getReference("floods_groups");
        DatabaseReference earthquakesGroupsTable = database.getReference("earthquakes_groups");
        DatabaseReference tornadosGroupsTable = database.getReference("tornados_groups");
        DatabaseReference othersGroupsTable = database.getReference("other_groups");

        //Initialize users table reference and call the method that retrives all the users
        usersTable = database.getReference("users");
        retrieveAllUsers();

        //Call the method that reads the data form the db for each table and corresponding ArrayList
        readDataFromDB(forestFiresGroupsTable, forestFiresGroups);
        readDataFromDB(cityFiresGroupsTable, cityFiresGroups);
        readDataFromDB(floodsGroupsTable, floodsGroups);
        readDataFromDB(earthquakesGroupsTable, earthquakesGroups);
        readDataFromDB(tornadosGroupsTable, tornadosGroups);
        readDataFromDB(othersGroupsTable, othersGroups);

        //Add the no data view to the layout
        //If some data exist, this view will be removed
        addOrRemoveNoDataView(true);
    }

    private void readDataFromDB(DatabaseReference dataBaseRef,
                                ArrayList<DangerousSituationsGroup> dangerousSituationGroups) {
        //Get the data from each table in the db using the listener
        dataBaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Get the data and store the in the ArrayList we created
                Iterable<DataSnapshot> children = snapshot.getChildren();
                children.forEach(child -> {
                    DangerousSituationsGroup aDangerousSituationGroup = child.getValue(DangerousSituationsGroup.class);
                    dangerousSituationGroups.add(aDangerousSituationGroup);
                    assert aDangerousSituationGroup != null;
                    //Call the method that writes the data retrieved to the admin's ui
                    //This method will be called for each DangerousSituationsGroup instance that
                    //will be retrieved from the database from each category
                    addDangerousSituationCard(aDangerousSituationGroup);
                    //For the first time only that some data are retrieved from the db, remove the no data view
                    if (firstCheck) {
                        addOrRemoveNoDataView(false);
                        firstCheck = false;
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void addDangerousSituationCard(DangerousSituationsGroup group) {
        //Create the view that holds each card with the groups data
        @SuppressLint("InflateParams")
        View view = getLayoutInflater().inflate(R.layout.dangerous_situation_card, null);
        //Initialize the textBox that will hold the data for each card
        TextView groupInfoView = view.findViewById(R.id.adminDangerousSituationGroupTextView);

        StringBuilder builder = new StringBuilder();
        //For each group, store its data to the StringBuilder we created
        builder.append(group.getCategory()).append("\n");
        builder.append("Alert: ").append(group.getAlertLevel()).append("\n");
        builder.append("Times Reported: ").append(group.getNumberOfTimesReported()).append("\n");
        builder.append("Date: ").append(group.getDate()).append("\n");
        builder.append("Time: ").append(group.getTime()).append("\n");
        //Call the method that sets the location of the group
        setLocation(group, builder);

        //Set the text to the dangerous situation group view to the contents of the StringBuilder
        groupInfoView.setText(builder.toString());
        //Initialize the button that sends the alert to the nearby users
        Button sendAlertButton = view.findViewById(R.id.adminSendAlertButton);
        //Create the on click listener
        sendAlertButton.setOnClickListener(v -> {
            //Call the method that alerts the users
            alertUsers(group);
//            layout.removeView(view);
        });
        //Insert the view to the layout container of the ui
        layout.addView(view);
    }

    //If an address location is present to a group, the address will be added to the ui
    //If not, the latitude and longitude will be added to the ui
    private void setLocation(DangerousSituationsGroup group, StringBuilder builder) {
        if (group.getLocationAddress().matches("Unknown address")) {
            builder.append("Location\n");
            builder.append("Latitude: ").append(group.getLatitude()).append("\n");
            builder.append("Longitude: ").append(group.getLongitude()).append("\n");
            return;
        }
        builder.append("Location Address: ").append(group.getLocationAddress()).append("\n");
    }

    //If the flag is true, the view is added to the ui, if not the view is removed
    private void addOrRemoveNoDataView(Boolean flag) {
        if (flag) {
            layout.addView(noDataView);
            return;
        }
        layout.removeView(noDataView);
    }

    //Show message helper method
    private void showMessage(String title, String message){
        new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle(title)
                .setMessage(message)
                .show();
    }

    //Method that retrieves all the users from our database
    private void retrieveAllUsers() {
        usersTable.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> children = snapshot.getChildren();
                children.forEach(child -> {
                    User aUser = child.getValue(User.class);
                    allUsers.add(aUser);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    //Method that calculates the distance between 2 coordinates in a sphere like earth
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;

        return (dist);
    }

    //This function converts decimal degrees to radians
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    //This function converts radians to decimal degrees
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    //Calculate which users will be alerted based on their location
    private void alertUsers(DangerousSituationsGroup group) {
        //Clear the list
        usersToAlertPhoneNumbers.clear();
        //Store the current dangerous situation group
        dangerousSituationsGroupToAlertAbout = group;
        //For each user in our database
        allUsers.forEach(user -> {
            //Calculate their distance from the current group
            double distance = distance(group.getLatitude(), group.getLongitude(),
                    user.getLatitude(), user.getLongitude());
            //If the distance is less that 50 kilometers and the have stored a phone number in our system
            //Add their number to the list
            if (distance <= 50 && user.getPhoneNumber() != 0) {
                usersToAlertPhoneNumbers.add(String.valueOf(user.getPhoneNumber()));
            }
        });
        //If no user found near that location, show a corresponding message
        if (usersToAlertPhoneNumbers.size() == 0) {
            showMessage("No Users to be Alerted",
                    "There are no known users near this Dangerous Situation.");
            return;
        }
        //If the user (admin) has given permission to our application to end SMSs
        //Call the corresponding method
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            sendSMS(usersToAlertPhoneNumbers);
        } else {
            //When permission is not granted, we will request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, 100);
        }
    }

    //When the user answers to the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Check condition
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //When permission is granted
            sendSMS(usersToAlertPhoneNumbers);
        } else {
            //When permission is denied
            showMessage("Permission Denied", "This application is not permitted to send SMSs.");
        }
    }

    //Method that sends the SMSs
    private void sendSMS(ArrayList<String> phoneNumbers) {
        StringBuilder builder = new StringBuilder();
        //For each SMS group, store its data to the StringBuilder we created
        builder.append(dangerousSituationsGroupToAlertAbout.getCategory()).append("\n");
        builder.append("Date: ").append(dangerousSituationsGroupToAlertAbout.getDate()).append("\n");
        builder.append("Time: ").append(dangerousSituationsGroupToAlertAbout.getTime()).append("\n");
        //Call the method that sets the location
        setLocation(dangerousSituationsGroupToAlertAbout, builder);
        builder.append("Advice from the civil protection: ")
                .append(dangerousSituationsGroupToAlertAbout.generalInfo()).append("\n");
        //For debugging purposes
        System.out.println("-------------------");
        System.out.println("Send SMS: " + builder);
        System.out.println("-------------------");
        //Create the manager that sends the SMSs
        SmsManager smsManager = SmsManager.getDefault();
        //For each phone number in the list, send a message for the dangerous situation group
        //The message body is depending on the current group specifics and its category
        phoneNumbers.forEach(phoneNumber -> smsManager.sendTextMessage(phoneNumber, null,
                builder.toString(), null, null));
        //Show a success message
        showMessage("Alert Sent", "An alert SMS has been sent to nearby users.");
    }
}