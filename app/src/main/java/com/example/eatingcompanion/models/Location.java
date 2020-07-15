package com.example.eatingcompanion.models;

import com.google.gson.annotations.SerializedName;

public class Location {
    @SerializedName("address1")
    private String address;
    @SerializedName("city")
    private String city;
    @SerializedName("zip_code")
    private String zipCode;
    @SerializedName("state")
    private String state;

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getState() {
        return state;
    }
}
