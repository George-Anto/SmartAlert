package com.example.smartalert;

import androidx.annotation.NonNull;

public class User {

    private String uid;
    private String role;
    private double latitude;
    private double longitude;
    private String locationAddress;
    private long phoneNumber;

    public User(String uid, String role, double latitude, double longitude, String locationAddress) {
        this.uid = uid;
        this.role = role;
        this.latitude = latitude;
        this.longitude = longitude;
        setLocationAddress(locationAddress);
        phoneNumber = 0;
    }

    public User() {}

    public String getUid() {
        return uid;
    }

    public String getRole() {
        return role;
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

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setLocationAddress(String locationAddress) {
        if (locationAddress == null) {
            this.locationAddress = "Unknown Address";
            return;
        }
        this.locationAddress = locationAddress;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", role='" + role + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", locationAddress='" + locationAddress + '\'' +
                ", phoneNumber=" + phoneNumber +
                '}';
    }
}
