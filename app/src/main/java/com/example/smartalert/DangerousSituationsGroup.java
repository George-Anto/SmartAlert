package com.example.smartalert;

import androidx.annotation.NonNull;

public class DangerousSituationsGroup {

    private String timestamp;
    private String date;
    private String time;
    private double latitude;
    private double longitude;
    private String locationAddress;
    private String category;
    private int numberOfTimesReported;
    private String alertLevel;

    public DangerousSituationsGroup() {}

    public String getTimestamp() {
        return timestamp;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getLocationAddress() {
        return locationAddress;
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
                ", locationAddress='" + locationAddress + '\'' +
                '}';
    }

    public String generalInfo () {
        if (category.matches("Forest Fire"))
            return String.valueOf(R.string.forest_fire_alert);
        else if (category.matches("City Fire"))
            return String.valueOf(R.string.city_fire_alert);
        else if (category.matches("Flood"))
            return String.valueOf(R.string.flood_alert);
        else if (category.matches("Earthquake"))
            return String.valueOf(R.string.earthquake_alert);
        else if (category.matches("Tornado"))
            return String.valueOf(R.string.tornado_alert);
        else if (category.matches("Other"))
            return String.valueOf(R.string.other_alert);
        else
            return String.valueOf(R.string.other_alert);
    }
}
