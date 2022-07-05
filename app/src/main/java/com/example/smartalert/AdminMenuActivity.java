package com.example.smartalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
            showMessage("Alert", "Users Alerted!!!");
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
}