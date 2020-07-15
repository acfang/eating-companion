package com.example.eatingcompanion.models;

import com.google.gson.annotations.SerializedName;

public class Location {
    @SerializedName("address1")
    private String address;

    public String getAddress() {
        return address;
    }
}
