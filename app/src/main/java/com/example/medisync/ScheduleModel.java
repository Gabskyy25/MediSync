package com.example.medisync;

public class ScheduleModel {
    public String title, time, fullData;
    public int id;

    public ScheduleModel(String title, String time, int id, String fullData) {
        this.title = title;
        this.time = time;
        this.id = id;
        this.fullData = fullData;
    }
}