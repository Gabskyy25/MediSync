package com.example.medisync;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class alarm extends AppCompatActivity implements AlarmAdapter.Listener {

    private final List<AlarmModel> alarmList = new ArrayList<>();
    private AlarmAdapter adapter;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "ALARMS";
    private TimePicker timePicker;
    private EditText etAlarmDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        timePicker = findViewById(R.id.timePicker);
        Button btnSetAlarm = findViewById(R.id.btnSetAlarm);
        etAlarmDescription = findViewById(R.id.etAlarmDescription);
        RecyclerView recyclerAlarms = findViewById(R.id.recyclerAlarms);

        loadAlarms();

        adapter = new AlarmAdapter(alarmList, this);
        recyclerAlarms.setLayoutManager(new LinearLayoutManager(this));
        recyclerAlarms.setAdapter(adapter);

        enableSwipeToDelete(recyclerAlarms);

        btnSetAlarm.setOnClickListener(v -> setAlarm());
    }

    private void setAlarm() {
        List<Integer> selectedDays = new ArrayList<>();
        // Default to today
        selectedDays.add(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));

        String description = etAlarmDescription.getText().toString().trim();
        if (description.isEmpty()) {
            description = "Alarm";
        }

        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        String time = String.format(Locale.US, "%02d:%02d", hour, minute);

        AlarmModel newAlarm = new AlarmModel(description, time, true, selectedDays);
        alarmList.add(newAlarm);
        saveAlarms();
        adapter.notifyItemInserted(alarmList.size() - 1);

        scheduleAlarm(newAlarm);

        Toast.makeText(this, "Alarm Added!", Toast.LENGTH_SHORT).show();
    }

    private void scheduleAlarm(AlarmModel alarmModel) {
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (manager == null) return;

        Intent intent = new Intent(this, alarmreceiver.class);
        intent.putExtra("ALARM_DESCRIPTION", alarmModel.description);

      
        String[] timeParts = alarmModel.time.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        for (int day : alarmModel.days) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_WEEK, day);
            c.set(Calendar.HOUR_OF_DAY, hour);
            c.set(Calendar.MINUTE, minute);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);

            // If alarm time has already passed for today, schedule it for the same day next week
            if (c.before(Calendar.getInstance())) {
                c.add(Calendar.WEEK_OF_YEAR, 1);
            }

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this, (int) c.getTimeInMillis(), intent, // Unique request code
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (manager.canScheduleExactAlarms()) {
                    manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                } else {
                    // Optionally, direct user to settings
                    Intent settingsIntent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    startActivity(settingsIntent);
                    Toast.makeText(this, "Please grant permission to schedule exact alarms", Toast.LENGTH_LONG).show();
                }
            } else {
                manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
            }
        }
    }

    private void enableSwipeToDelete(RecyclerView recyclerAlarms) {
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getBindingAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            alarmList.remove(position);
                            adapter.notifyItemRemoved(position);
                            saveAlarms();
                        }
                    }
                });

        helper.attachToRecyclerView(recyclerAlarms);
    }

    private void saveAlarms() {
        Set<String> set = new HashSet<>();
        for (AlarmModel model : alarmList) {
            StringBuilder daysStr = new StringBuilder();
            for(int i=0; i < model.days.size(); i++) {
                daysStr.append(model.days.get(i));
                if(i < model.days.size() - 1) {
                    daysStr.append(",");
                }
            }
            set.add(model.description + "|" + model.time + "|" + model.enabled + "|" + daysStr.toString());
        }
        prefs.edit().putStringSet("ALARM_LIST", set).apply();
    }

    private void loadAlarms() {
        Set<String> set = prefs.getStringSet("ALARM_LIST", new HashSet<>());
        alarmList.clear();

        for (String s : set) {
            String[] p = s.split("\\|");

            if (p.length >= 3) {
                String desc = p[0];
                String time = p[1];
                boolean enabled = Boolean.parseBoolean(p[2]);

                List<Integer> days = new ArrayList<>();
                if (p.length >= 4 && !p[3].isEmpty()) {
                    String[] dayParts = p[3].split(",");
                    for (String d : dayParts) {
                        try {
                           days.add(Integer.parseInt(d.trim()));
                        } catch (NumberFormatException e) {
                            // ignore malformed day
                        }
                    }
                }
                alarmList.add(new AlarmModel(desc, time, enabled, days));
            }
        }
    }

    @Override
    public void onToggle(AlarmModel model) {
        // Find the model and update it, then save.
        for (int i = 0; i < alarmList.size(); i++) {
            if (alarmList.get(i).time.equals(model.time) && alarmList.get(i).description.equals(model.description)) {
                alarmList.set(i, model);
                saveAlarms();
                // Reschedule if needed
                if (model.enabled) {
                    scheduleAlarm(model);
                } else {
                    cancelAlarm(model);
                }
                break;
            }
        }
    }
    
    private void cancelAlarm(AlarmModel alarmModel) {
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (manager == null) return;

        Intent intent = new Intent(this, alarmreceiver.class);
        intent.putExtra("ALARM_DESCRIPTION", alarmModel.description);

        String[] timeParts = alarmModel.time.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        for (int day : alarmModel.days) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_WEEK, day);
            c.set(Calendar.HOUR_OF_DAY, hour);
            c.set(Calendar.MINUTE, minute);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this, (int) c.getTimeInMillis(), intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            manager.cancel(pendingIntent);
        }
    }

    @Override
    public void onDaysChanged(AlarmModel model) {
        // Similar to onToggle, find and update
        for (int i = 0; i < alarmList.size(); i++) {
            if (alarmList.get(i).time.equals(model.time) && alarmList.get(i).description.equals(model.description)) {
                alarmList.set(i, model);
                saveAlarms();
                if (model.enabled) {
                    // cancel existing alarms and reschedule with new days
                    cancelAlarm(alarmList.get(i));
                    scheduleAlarm(model);
                }
                break;
            }
        }
    }
}
