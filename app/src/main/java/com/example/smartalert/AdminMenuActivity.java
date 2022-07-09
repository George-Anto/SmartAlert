package com.example.smartalert;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

//This is a very simple activity
//Its a menu that redirects the admin to their available options
public class AdminMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);
    }

    //If the admin presses this button, it redirects them to view the current dangerous situations groups
    public void onGoToSendAlerts(View view) {
        startActivity(new Intent(this, AdminSendAlertsActivity.class));
    }

    //If the admin presses this button, they can view the same statistics a user can also view
    public void onGoToStatistics(View view) {
        startActivity(new Intent(this, StatisticsActivity.class));
    }
}