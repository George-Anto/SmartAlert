package com.example.smartalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
    private String uid;
    private String role;

    private LinearLayout layout;

    private DatabaseReference forestFiresGroupsTable;
    private final ArrayList<DangerousSituationsGroup> forestFiresGroups = new ArrayList<>();
    private DatabaseReference cityFiresGroupsTable;
    private final ArrayList<DangerousSituationsGroup> cityFiresGroups = new ArrayList<>();
    private DatabaseReference floodsGroupsTable;
    private final ArrayList<DangerousSituationsGroup> floodsGroups = new ArrayList<>();
    private DatabaseReference earthquakesGroupsTable;
    private final ArrayList<DangerousSituationsGroup> earthquakesGroups = new ArrayList<>();
    private DatabaseReference tornadosGroupsTable;
    private final ArrayList<DangerousSituationsGroup> tornadosGroups = new ArrayList<>();
    private DatabaseReference othersGroupsTable;
    private final ArrayList<DangerousSituationsGroup> othersGroups = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        layout = findViewById(R.id.admin_linearLayout_container);

        //Retrieve user's id
        uid = getIntent().getStringExtra("Uid");
        role = getIntent().getStringExtra("Role");

//        showMessage(role + " Menu", "ID: " + uid);

        //Initialize db and reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        forestFiresGroupsTable = database.getReference("forest_fires_groups");
        cityFiresGroupsTable = database.getReference("city_fires_groups");
        floodsGroupsTable = database.getReference("floods_groups");
        earthquakesGroupsTable = database.getReference("earthquakes_groups");
        tornadosGroupsTable = database.getReference("tornados_groups");
        othersGroupsTable = database.getReference("other_groups");

        readDataFromDB(forestFiresGroupsTable, forestFiresGroups);
        readDataFromDB(cityFiresGroupsTable, cityFiresGroups);
        readDataFromDB(floodsGroupsTable, floodsGroups);
        readDataFromDB(earthquakesGroupsTable, earthquakesGroups);
        readDataFromDB(tornadosGroupsTable, tornadosGroups);
        readDataFromDB(othersGroupsTable, othersGroups);
    }

    private void readDataFromDB(DatabaseReference dataBaseRef, ArrayList dangerousSituationGroups) {
        dataBaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Get the data and store the in the ArrayList we created
                Iterable<DataSnapshot> children = snapshot.getChildren();
                children.forEach(child -> {
                    DangerousSituationsGroup aDangerousSituationGroup = child.getValue(DangerousSituationsGroup.class);
                    dangerousSituationGroups.add(aDangerousSituationGroup);
                    assert aDangerousSituationGroup != null;
                    addDangerousSituationCard(aDangerousSituationGroup);

                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addDangerousSituationCard(DangerousSituationsGroup group) {
        View view = getLayoutInflater().inflate(R.layout.dangerous_situation_card, null);

        TextView groupInfoView = view.findViewById(R.id.adminDangerousSituationGroupTextView);

        StringBuilder builder = new StringBuilder();
        //For each group, store its data to the StringBuilder we created
        builder.append(group.getCategory()).append("\n");
        builder.append("Alert: ").append(group.getAlertLevel()).append("\n");
        builder.append("Times Reported: ").append(group.getNumberOfTimesReported()).append("\n");
        builder.append("Latitude: ").append(group.getLatitude()).append("\n");
        builder.append("Longitude: ").append(group.getLongitude()).append("\n");

        //Set the text to the dangerous situation group view to the contents of the StringBuilder
        groupInfoView.setText(builder.toString());

        Button sendAlertButton = view.findViewById(R.id.adminSendAlertButton);
        sendAlertButton.setOnClickListener(v -> {
            showMessage("Alert", "Users Alerted!!!");
//            layout.removeView(view);
        });
        layout.addView(view);
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