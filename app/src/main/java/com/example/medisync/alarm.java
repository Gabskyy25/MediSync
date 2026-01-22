package com.example.medisync;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class alarm extends AppCompatActivity {

    private TimePicker timePicker;
    private Button btnSetAlarm;
    private EditText etAlarmDescription;
    private RecyclerView recyclerAlarms;
    private List<AlarmModel> alarmList = new ArrayList<>();
    private AlarmAdapter adapter;
    private SharedPreferences prefs;
    private NotificationDBHelper notificationDBHelper;

    private static final String PREFS_NAME = "ALARMS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        notificationDBHelper = new NotificationDBHelper(this);

        timePicker = findViewById(R.id.timePicker);
        btnSetAlarm = findViewById(R.id.btnSetAlarm);
        etAlarmDescription = findViewById(R.id.etAlarmDescription);
        recyclerAlarms = findViewById(R.id.recyclerAlarms);

        loadAlarms();

        adapter = new AlarmAdapter(alarmList);
        recyclerAlarms.setLayoutManager(new LinearLayoutManager(this));
        recyclerAlarms.setAdapter(adapter);

        enableSwipeToDelete();

        btnSetAlarm.setOnClickListener(v -> setAlarm());
    }

    private void setAlarm() {
        List<Integer> selectedDays = new ArrayList<>();
        selectedDays.add(Calendar.MONDAY);

        String description = etAlarmDescription.getText().toString().trim();
        if (description.isEmpty()) description = "Alarm";

        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        String time = String.format("%02d:%02d", hour, minute);
        int alarmId = (int) (System.currentTimeMillis() / 1000);

        alarmList.add(new AlarmModel(description, time, true, selectedDays, alarmId));
        saveAlarms();
        adapter.notifyDataSetChanged();

        scheduleAlarm(description, hour, minute);

        notificationDBHelper.addNotification(
                "Alarm Added",
                description + " at " + time,
                "alarm",
                alarmId
        );

        Toast.makeText(this, "Alarm Added!", Toast.LENGTH_SHORT).show();
    }

    private void scheduleAlarm(String desc, int hour, int minute) {

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (manager == null) return;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (!manager.canScheduleExactAlarms()) {
                Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                Toast.makeText(this, "Please allow exact alarms", Toast.LENGTH_LONG).show();
                return;
            }
        }

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DAY_OF_YEAR, 1);
        }

        Intent intent = new Intent(this, alarmreceiver.class);
        intent.putExtra("ALARM_DESCRIPTION", desc);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        manager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                c.getTimeInMillis(),
                pendingIntent
        );
    }


    private void enableSwipeToDelete() {
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        int alarmId = alarmList.get(position).getAlarmId();

                        alarmList.remove(position);
                        adapter.notifyItemRemoved(position);
                        saveAlarms();

                        notificationDBHelper.deleteByEntity("alarm", alarmId);
                    }
                });

        helper.attachToRecyclerView(recyclerAlarms);
    }

    private void saveAlarms() {
        Set<String> set = new HashSet<>();
        for (AlarmModel model : alarmList) {
            set.add(
                    model.description + "|" +
                            model.time + "|" +
                            model.enabled + "|" +
                            model.days.toString() + "|" +
                            model.getAlarmId()
            );
        }
        prefs.edit().putStringSet("ALARM_LIST", set).apply();
    }

    private void loadAlarms() {
        Set<String> set = prefs.getStringSet("ALARM_LIST", new HashSet<>());
        alarmList.clear();

        for (String s : set) {
            String[] p = s.split("\\|");

            String desc = p[0];
            String time = p[1];
            boolean enabled = Boolean.parseBoolean(p[2]);

            List<Integer> days = new ArrayList<>();
            if (p.length >= 4) {
                String dayStr = p[3].replace("[", "").replace("]", "");
                if (!dayStr.isEmpty()) {
                    for (String d : dayStr.split(",")) {
                        days.add(Integer.parseInt(d.trim()));
                    }
                }
            }

            int alarmId = p.length >= 5
                    ? Integer.parseInt(p[4])
                    : (int) (System.currentTimeMillis() / 1000);

            alarmList.add(new AlarmModel(desc, time, enabled, days, alarmId));
        }
    }
}
