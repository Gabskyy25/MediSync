package com.example.medisync;

import java.util.List;

public class AlarmModel {

    private int hour;
    private int minute;

    private String id;
    private String description;
    private String time;
    private boolean enabled;
    private List<Integer> days;

    // REQUIRED empty constructor for Firebase
    public AlarmModel() {}

    public AlarmModel(String description, String time, boolean enabled, List<Integer> days) {
        this.id = String.valueOf(System.currentTimeMillis());
        this.description = description;
        this.time = time;
        this.enabled = enabled;
        this.days = days;
    }

    /* ===== GETTERS ===== */

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getTime() {
        return time;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public List<Integer> getDays() {
        return days;
    }

    /* ===== SETTERS ===== */

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setDays(List<Integer> days) {
        this.days = days;
    }
}
