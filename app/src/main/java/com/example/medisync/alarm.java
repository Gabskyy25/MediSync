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

    private NotificationDBHelper notificationDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        notificationDB = new NotificationDBHelper(this);

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
        List<Integer> days = new ArrayList<>();
        days.add(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));

        String desc = etAlarmDescription.getText().toString().trim();
        if (desc.isEmpty()) desc = "Alarm";

        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        String time = String.format(Locale.US, "%02d:%02d", hour, minute);

        AlarmModel model = new AlarmModel(desc, time, true, days);
        alarmList.add(model);
        saveAlarms();
        adapter.notifyItemInserted(alarmList.size() - 1);

        scheduleAlarm(model);

        int alarmId = generateAlarmId(model);

        notificationDB.addNotification(
                "Alarm Added",
                desc + " at " + formatTime12(hour, minute),
                "alarm",
                alarmId
        );

        Toast.makeText(this, "Alarm Added!", Toast.LENGTH_SHORT).show();
    }

    private void scheduleAlarm(AlarmModel model) {
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (manager == null) return;

        String[] parts = model.time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        for (int day : model.days) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, hour);
            c.set(Calendar.MINUTE, minute);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);

            while (c.get(Calendar.DAY_OF_WEEK) != day || c.before(Calendar.getInstance())) {
                c.add(Calendar.DAY_OF_YEAR, 1);
            }

            Intent intent = new Intent(this, alarmreceiver.class);
            intent.putExtra("ALARM_DESCRIPTION", model.description);

            PendingIntent pi = PendingIntent.getBroadcast(
                    this,
                    generateAlarmId(model),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!manager.canScheduleExactAlarms()) {
                    startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM));
                    return;
                }
            }

            manager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    c.getTimeInMillis(),
                    pi
            );
        }
    }

    private void cancelAlarm(AlarmModel model) {
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (manager == null) return;

        Intent intent = new Intent(this, alarmreceiver.class);

        PendingIntent pi = PendingIntent.getBroadcast(
                this,
                generateAlarmId(model),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        manager.cancel(pi);
    }

    private void enableSwipeToDelete(RecyclerView recycler) {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView r,
                                  @NonNull RecyclerView.ViewHolder v,
                                  @NonNull RecyclerView.ViewHolder t) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int d) {
                int pos = vh.getBindingAdapterPosition();
                AlarmModel model = alarmList.get(pos);

                cancelAlarm(model);
                notificationDB.deleteByEntity("alarm", generateAlarmId(model));

                alarmList.remove(pos);
                adapter.notifyItemRemoved(pos);
                saveAlarms();

                Toast.makeText(
                        alarm.this,
                        "Alarm deleted",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }).attachToRecyclerView(recycler);
    }

    private int generateAlarmId(AlarmModel model) {
        return (model.description + model.time).hashCode();
    }

    private String formatTime12(int hour, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        return android.text.format.DateFormat.format("hh:mm a", c).toString();
    }

    private void saveAlarms() {
        Set<String> set = new HashSet<>();
        for (AlarmModel m : alarmList) {
            set.add(m.description + "|" + m.time + "|" + m.enabled + "|" + m.days.toString());
        }
        prefs.edit().putStringSet("ALARM_LIST", set).apply();
    }

    private void loadAlarms() {
        Set<String> set = prefs.getStringSet("ALARM_LIST", new HashSet<>());
        alarmList.clear();

        for (String s : set) {
            String[] p = s.split("\\|");
            List<Integer> days = new ArrayList<>();
            if (p.length >= 4) {
                String d = p[3].replace("[", "").replace("]", "");
                if (!d.isEmpty())
                    for (String x : d.split(",")) days.add(Integer.parseInt(x.trim()));
            }
            alarmList.add(new AlarmModel(p[0], p[1], Boolean.parseBoolean(p[2]), days));
        }
    }

    @Override
    public void onToggle(AlarmModel model) {
        if (model.enabled) scheduleAlarm(model);
        else cancelAlarm(model);
        saveAlarms();
    }

    @Override
    public void onDaysChanged(AlarmModel model) {
        cancelAlarm(model);
        scheduleAlarm(model);
        saveAlarms();
    }
}
