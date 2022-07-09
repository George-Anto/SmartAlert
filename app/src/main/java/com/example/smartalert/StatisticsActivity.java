package com.example.smartalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class StatisticsActivity extends AppCompatActivity {

    //Statistics table reference
    private DatabaseReference statisticsTable;
    //Statistics textView
    private TextView statisticsToShowTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        //Initialize database and reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        statisticsTable = database.getReference("statistics");

        statisticsToShowTextView = findViewById(R.id.statisticsTextview);
        //Call the method that creates and displays the statistics
        showStatistics();
    }

    private void showStatistics() {
        //For each category, we initialize their alert counters at 0
        AtomicInteger forestFires = new AtomicInteger();
        AtomicInteger cityFires = new AtomicInteger();
        AtomicInteger floods = new AtomicInteger();
        AtomicInteger earthquakes = new AtomicInteger();
        AtomicInteger tornadoes = new AtomicInteger();
        AtomicInteger others = new AtomicInteger();

        //For each entry on the statistics table
        statisticsTable.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> children = snapshot.getChildren();
                children.forEach(child -> {
                    //Get the entry value (its category)
                    String currentAlertCategory = child.getValue(String.class);
                    //If the category matches the corresponding category,
                    //increment its value by one
                    switch (Objects.requireNonNull(currentAlertCategory)) {
                        case "Forest Fire":
                            forestFires.getAndIncrement();
                            break;
                        case "City Fire":
                            cityFires.getAndIncrement();
                            break;
                        case "Flood":
                            floods.getAndIncrement();
                            break;
                        case "Earthquake":
                            earthquakes.getAndIncrement();
                            break;
                        case "Tornado":
                            tornadoes.getAndIncrement();
                            break;
                        default:
                            others.getAndIncrement();
                    }
                });
                //Create a String builder
                StringBuilder builder = new StringBuilder();
                //Fill the builder with the alerts that we retrieved
                builder.append(getString(R.string.forest_fires_alerts)).append(" ").append(forestFires).append("\n");
                builder.append(getString(R.string.city_fires_alerts)).append(" ").append(cityFires).append("\n");
                builder.append(getString(R.string.floods_alerts)).append(" ").append(floods).append("\n");
                builder.append(getString(R.string.earthquakes_alerts)).append(" ").append(earthquakes).append("\n");
                builder.append(getString(R.string.tornadoes_alerts)).append(" ").append(tornadoes).append("\n");
                builder.append(getString(R.string.others_alerts)).append(" ").append(others).append("\n");

                //Set the text of the textView to the contents of the builder
                statisticsToShowTextView.setText(builder.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}