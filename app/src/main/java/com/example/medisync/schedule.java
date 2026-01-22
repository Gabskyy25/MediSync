package com.example.medisync;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class schedule extends AppCompatActivity {

    private Button inputButton;
    private LinearLayout scheduleContainer;

    private SharedPreferences prefs;
    private static final String PREF_NAME = "SCHEDULES";

    private NotificationDBHelper notificationDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        inputButton = findViewById(R.id.inputButton);
        scheduleContainer = findViewById(R.id.scheduleContainer);

        prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        notificationDB = new NotificationDBHelper(this);

        loadSchedules();

        inputButton.setOnClickListener(v -> showScheduleDialog());
    }

    /* ================= ADD SCHEDULE ================= */

    private void showScheduleDialog() {
        EditText input = new EditText(this);
        input.setHint("Enter schedule details");

        new AlertDialog.Builder(this)
                .setTitle("For:")
                .setView(input)
                .setPositiveButton("Next", (d, w) -> {
                    String title = input.getText().toString().trim();
                    if (!title.isEmpty()) {
                        pickDateTime(title);
                    }
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
            }, calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE), false).show();

        }, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void addSchedule(String title, Date date) {
        int scheduleId = (int) System.currentTimeMillis();

        SimpleDateFormat df =
                new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());

        String data = title + "|" + df.format(date) + "|" + scheduleId;

        saveSchedule(data);
        renderSchedule(data);

        // ✅ Schedule Added notification
        notificationDB.addNotification(
                "Schedule Added",
                title + " scheduled on " + df.format(date),
                "SCHEDULE",
                scheduleId
        );
    }

    /* ================= RENDER + SWIPE ================= */

    private void renderSchedule(String data) {
        String[] parts = data.split("\\|");

        String title = parts[0];
        String time = parts[1];
        int id = Integer.parseInt(parts[2]);

        View item = getLayoutInflater().inflate(R.layout.schedule_item, null);
        TextView txt = item.findViewById(R.id.scheduleText);

        txt.setText("For: " + title + "\nOn: " + time);

        final float[] startX = new float[1];

        item.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    startX[0] = event.getX();
                    return true;

                case MotionEvent.ACTION_MOVE:
                    float deltaX = event.getX() - startX[0];
                    v.setTranslationX(deltaX);
                    return true;

                case MotionEvent.ACTION_UP:
                    float diffX = event.getX() - startX[0];

                    if (Math.abs(diffX) > 200) {
                        swipeDeleteAnimation(v, data, title, id);
                    } else {
                        v.animate().translationX(0).setDuration(200).start();
                    }
                    return true;
            }
            return false;
        });

        scheduleContainer.addView(item);
    }

    private void swipeDeleteAnimation(View item, String data, String title, int id) {

        TranslateAnimation slideOut =
                new TranslateAnimation(0, item.getWidth(), 0, 0);
        slideOut.setDuration(250);
        slideOut.setFillAfter(true);

        AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);
        fadeOut.setDuration(250);
        fadeOut.setFillAfter(true);

        item.startAnimation(slideOut);
        item.startAnimation(fadeOut);

        item.postDelayed(() -> {
            scheduleContainer.removeView(item);
            deleteSchedule(data);

            // ✅ KEEP "Schedule Added"
            // ❌ DO NOT delete it anymore

            // ✅ ADD Schedule Deleted notification
            notificationDB.addNotification(
                    "Schedule Deleted",
                    title + " was removed",
                    "SCHEDULE",
                    id
            );

        }, 250);
    }

    /* ================= STORAGE ================= */

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
        for (String s : set) {
            renderSchedule(s);
        }
    }
}
