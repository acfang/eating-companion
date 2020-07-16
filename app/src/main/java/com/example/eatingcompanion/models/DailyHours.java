package com.example.eatingcompanion.models;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

public class DailyHours {
    @SerializedName("start")
    private String start;
    @SerializedName("end")
    private String end;
    @SerializedName("day")
    private int day;

    private List days = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");

    public String getStart() {
        return start.substring(0,2) + ":" + start.substring(2);
    }

    public String getEnd() {
        return end.substring(0,2) + ":" + end.substring(2);
    }

    public String getDailyHours() {
        return days.get(day) + " " + getStart() + "-" + getEnd() + "\n";
    }
}
