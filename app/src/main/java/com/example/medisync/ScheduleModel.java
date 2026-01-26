package com.example.medisync;

public class ScheduleModel {

    private String id;
    private String title;
    private String time;

    public ScheduleModel() {
        // REQUIRED for Firestore
    }

    public ScheduleModel(String title, String time) {
        this.title = title;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }
}
