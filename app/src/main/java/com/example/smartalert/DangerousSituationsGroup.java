package com.example.smartalert;

import androidx.annotation.NonNull;

public class DangerousSituationsGroup {

    private String timestamp;
    private double latitude;
    private double longitude;
    private String category;
    private int numberOfTimesReported;
    private String alertLevel;

    public DangerousSituationsGroup() {}

    public String getTimestamp() {
        return timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getCategory() {
        return category;
    }

    public int getNumberOfTimesReported() {
        return numberOfTimesReported;
    }

    public String getAlertLevel() {
        return alertLevel;
    }

    @NonNull
    @Override
    public String toString() {
        return "DangerousSituationsGroup{" +
                "timestamp='" + timestamp + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", category='" + category + '\'' +
                ", numberOfTimesReported=" + numberOfTimesReported +
                ", alertLevel='" + alertLevel + '\'' +
                '}';
    }
}
