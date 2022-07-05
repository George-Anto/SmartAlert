package com.example.smartalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminMenuActivity extends AppCompatActivity {
    private String uid;
    private String role;

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
                    DangerousSituationsGroup aForestFireGroup = child.getValue(DangerousSituationsGroup.class);
                    dangerousSituationGroups.add(aForestFireGroup);
                });
                System.out.println("--------------------------------");
                System.out.println(dangerousSituationGroups);
                System.out.println("--------------------------------");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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