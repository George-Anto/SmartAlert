package com.example.smartalert;

import androidx.annotation.NonNull;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DangerousSituation {

    //The id of the user that send the request
    private String uid;
    private String date;
    private String time;
    private String timestamp;
    //The coordinates of the incident
    private Double latitude;
    private Double longitude;
    //The address of the incident (is calculated based on the coordinates)
    private String locationAddress;
    private String category;
    private String description;
    //The firebase storage Access Token for the specific file
    //(we can use it to download and/or view the file)
    private String imagePath;

    public DangerousSituation(String uid, Double latitude, Double longitude, String locationAddress,
                              String category, String description, String imagePath) {
        this.uid = uid;
        this.date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        this.time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        this.timestamp = String.valueOf(new Timestamp(System.currentTimeMillis()).getTime());
        this.latitude = latitude;
        this.longitude = longitude;
        setLocationAddress(locationAddress);
        this.category = category;
        setDescription(description);
        setImagePath(imagePath);
    }

    public  DangerousSituation() {}

    public String getUid() {
        return uid;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setLocationAddress(String locationAddress) {
        if (locationAddress == null) {
            this.locationAddress = "Unknown address";
            return;
        }
        this.locationAddress = locationAddress;
    }

    public void setDescription(String description) {
        if (description == null) {
            this.description = "No description provided";
            return;
        }
        this.description = description;
    }

    public void setImagePath(String imagePath) {
        if (imagePath == null) {
            this.imagePath = "No image attached";
            return;
        }
        this.imagePath = imagePath;
    }

    @NonNull
    @Override
    public String toString() {
        return "DangerousSituation{" +
                "uid='" + uid + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", locationAddress='" + locationAddress + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}
