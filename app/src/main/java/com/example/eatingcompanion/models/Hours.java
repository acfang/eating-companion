package com.example.eatingcompanion.models;

import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.List;

public class Hours {

    @SerializedName("open")
    private List<DailyHours> openHours;

    public List<DailyHours> getOpenHours() {
        return openHours;
    }
}
