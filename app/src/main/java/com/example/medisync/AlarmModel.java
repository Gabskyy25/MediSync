package com.example.medisync;

import java.util.List;

public class AlarmModel {
    public String id;
    public String description;
    public int hour;
    public int minute;
    public boolean enabled;
    public List<Integer> days;

    public AlarmModel(String id, String description, int hour, int minute, boolean enabled, List<Integer> days) {
        this.id = id;
        this.description = description;
        this.hour = hour;
        this.minute = minute;
        this.enabled = enabled;
        this.days = days;
    }
}
