package com.example.medisync;

import java.util.List;

public class AlarmModel {
    public String description;
    public String time;
    public boolean enabled;
    public List<Integer> days;

    public AlarmModel(String description, String time, boolean enabled, List<Integer> days) {
        this.description = description;
        this.time = time;
        this.enabled = enabled;
        this.days = days;
    }
}
