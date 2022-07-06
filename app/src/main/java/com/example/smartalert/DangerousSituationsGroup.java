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
            return "There is a forest fire near yor location, you are advised to leave the forest.";
        else if (category.matches("City Fire"))
            return "There is a city fire near your location, evacuate the neighborhood to be safe.";
        else if (category.matches("Flood"))
            return "There is a flood near your location, you are advised to get to a high ground to be safe.";
        else if (category.matches("Earthquake"))
            return "There is an earthquake in progress, evacuate the buildings and go to an open area.";
        else if (category.matches("Tornado"))
            return "There is a tornado near you, get to a protected indoors location for your safety.";
        else if (category.matches("Other"))
            return "A dangerous situation is in progress near your location, be cautious.";
        else
            return "A dangerous situation is in progress near your location, be cautious.";
    }
}
