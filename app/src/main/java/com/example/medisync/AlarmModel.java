package com.example.medisync;

import java.util.List;

public class AlarmModel {

    public String description;
    public String time;
    public boolean enabled;
    public List<Integer> days;

    // 🔑 ID used to link alarm ↔ notification
    private int alarmId;


    public AlarmModel(String description, String time, boolean enabled,
                      List<Integer> days, int alarmId) {
        this.description = description;
        this.time = time;
        this.enabled = enabled;
        this.days = days;
        this.alarmId = alarmId;
    }


    public int getAlarmId() {
        return alarmId;
    }
}
