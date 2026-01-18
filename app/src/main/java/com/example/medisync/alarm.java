package com.example.medisync;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class alarm extends AppCompatActivity implements AlarmAdapter.Listener {

    private List<AlarmModel> list;
    private AlarmAdapter adapter;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_alarm);

        prefs = getSharedPreferences("ALARMS", MODE_PRIVATE);

        TimePicker picker = findViewById(R.id.timePicker);
        EditText desc = findViewById(R.id.etAlarmDescription);
        Button add = findViewById(R.id.btnSetAlarm);
        RecyclerView rv = findViewById(R.id.recyclerAlarms);

        list = AlarmStorage.load(prefs);

        adapter = new AlarmAdapter(list, this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override public boolean onMove(RecyclerView r, RecyclerView.ViewHolder v, RecyclerView.ViewHolder t) { return false; }
            @Override public void onSwiped(RecyclerView.ViewHolder v, int d) {
                AlarmModel m = list.remove(v.getAdapterPosition());
                cancelAll(m);
                AlarmStorage.save(prefs, list);
                adapter.notifyDataSetChanged();
            }
        }).attachToRecyclerView(rv);

        add.setOnClickListener(v -> {
            AlarmModel m = new AlarmModel(
                    UUID.randomUUID().toString(),
                    desc.getText().toString().isEmpty() ? "Alarm" : desc.getText().toString(),
                    picker.getHour(),
                    picker.getMinute(),
                    true,
                    new ArrayList<>()
            );
            list.add(m);
            AlarmStorage.save(prefs, list);
            adapter.notifyDataSetChanged();
        });
    }

    private void scheduleDay(AlarmModel m, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, day);
        c.set(Calendar.HOUR_OF_DAY, m.hour);
        c.set(Calendar.MINUTE, m.minute);
        c.set(Calendar.SECOND, 0);
        if (c.before(Calendar.getInstance())) c.add(Calendar.WEEK_OF_YEAR, 1);

        Intent i = new Intent(this, alarmreceiver.class);
        i.putExtra("DESC", m.description);

        PendingIntent pi = PendingIntent.getBroadcast(
                this,
                (m.id + day).hashCode(),
                i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        ((AlarmManager) getSystemService(ALARM_SERVICE))
                .setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
    }

    private void cancelAll(AlarmModel m) {
        for (int d = 1; d <= 7; d++) {
            Intent i = new Intent(this, alarmreceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(
                    this,
                    (m.id + d).hashCode(),
                    i,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            ((AlarmManager) getSystemService(ALARM_SERVICE)).cancel(pi);
        }
    }

    @Override
    public void onToggle(AlarmModel m) {
        cancelAll(m);
        if (m.enabled) for (int d : m.days) scheduleDay(m, d);
        AlarmStorage.save(prefs, list);
    }

    @Override
    public void onDaysChanged(AlarmModel m) {
        cancelAll(m);
        if (m.enabled) for (int d : m.days) scheduleDay(m, d);
        AlarmStorage.save(prefs, list);
    }
}
