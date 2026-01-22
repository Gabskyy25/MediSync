package com.example.medisync;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class schedule extends AppCompatActivity {

    private Button inputButton;
    private RecyclerView recyclerSchedules;
    private ScheduleAdapter adapter;
    private List<ScheduleModel> scheduleList = new ArrayList<>();

    private SharedPreferences prefs;
    private static final String PREF_NAME = "SCHEDULES";
    private NotificationDBHelper notificationDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        inputButton = findViewById(R.id.inputButton);
        recyclerSchedules = findViewById(R.id.recyclerSchedules);
        ImageView backBtn = findViewById(R.id.backbtn);

        prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        notificationDB = new NotificationDBHelper(this);

        adapter = new ScheduleAdapter(scheduleList);
        recyclerSchedules.setLayoutManager(new LinearLayoutManager(this));
        recyclerSchedules.setAdapter(adapter);

        loadSchedules();
        enableSwipeToDelete();

        backBtn.setOnClickListener(v -> finish());
        inputButton.setOnClickListener(v -> showScheduleDialog());
    }

    private void showScheduleDialog() {
        EditText input = new EditText(this);
        input.setHint("Enter schedule details");

        new AlertDialog.Builder(this)
                .setTitle("ToDo:")
                .setView(input)
                .setPositiveButton("Next", (d, w) -> {
                    String title = input.getText().toString().trim();
                    if (!title.isEmpty()) pickDateTime(title);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void pickDateTime(String title) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (v, y, m, d) -> {
            calendar.set(y, m, d);
            new TimePickerDialog(this, (tv, h, min) -> {
                calendar.set(Calendar.HOUR_OF_DAY, h);
                calendar.set(Calendar.MINUTE, min);
                addSchedule(title, calendar.getTime());
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void addSchedule(String title, Date date) {
        int scheduleId = (int) System.currentTimeMillis();
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
        String formattedDate = df.format(date);
        String data = title + "|" + formattedDate + "|" + scheduleId;

        saveSchedule(data);
        scheduleList.add(new ScheduleModel(title, formattedDate, scheduleId, data));
        adapter.notifyItemInserted(scheduleList.size() - 1);

        notificationDB.addNotification("Schedule Added", title + " scheduled on " + formattedDate, "SCHEDULE", scheduleId);
    }

    private void enableSwipeToDelete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView r, @NonNull RecyclerView.ViewHolder v, @NonNull RecyclerView.ViewHolder t) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int d) {
                int pos = vh.getBindingAdapterPosition();
                ScheduleModel model = scheduleList.get(pos);

                deleteSchedule(model.fullData);
                notificationDB.addNotification("Schedule Deleted", model.title + " was removed", "SCHEDULE", model.id);

                scheduleList.remove(pos);
                adapter.notifyItemRemoved(pos);
                Toast.makeText(schedule.this, "Schedule Deleted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(Color.parseColor("#FF5252"))
                        .addSwipeLeftActionIcon(R.drawable.ic_delete)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerSchedules);
    }

    private void saveSchedule(String data) {
        Set<String> set = prefs.getStringSet("LIST", new HashSet<>());
        Set<String> copy = new HashSet<>(set);
        copy.add(data);
        prefs.edit().putStringSet("LIST", copy).apply();
    }

    private void deleteSchedule(String data) {
        Set<String> set = prefs.getStringSet("LIST", new HashSet<>());
        Set<String> copy = new HashSet<>(set);
        copy.remove(data);
        prefs.edit().putStringSet("LIST", copy).apply();
    }

    private void loadSchedules() {
        Set<String> set = prefs.getStringSet("LIST", new HashSet<>());
        scheduleList.clear();
        for (String s : set) {
            String[] parts = s.split("\\|");
            if (parts.length >= 3) {
                scheduleList.add(new ScheduleModel(parts[0], parts[1], Integer.parseInt(parts[2]), s));
            }
        }
        adapter.notifyDataSetChanged();
    }
}