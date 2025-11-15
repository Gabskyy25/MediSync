package com.example.medisync;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class schedule extends AppCompatActivity {

    private Button inputButton;
    private LinearLayout scheduleContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        inputButton = findViewById(R.id.inputButton);
        scheduleContainer = findViewById(R.id.scheduleContainer);

        inputButton.setOnClickListener(v -> showScheduleDialog());
    }

    private void showScheduleDialog() {
        final EditText input = new EditText(this);
        input.setHint("Enter schedule details");

        new AlertDialog.Builder(this)
                .setTitle("For:")
                .setView(input)
                .setPositiveButton("Next", (dialog, which) -> {
                    String scheduleTitle = input.getText().toString().trim();
                    if (!scheduleTitle.isEmpty()) {
                        pickDateTime(scheduleTitle);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void pickDateTime(String scheduleTitle) {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog timePicker = new TimePickerDialog(this,
                            (timeView, hourOfDay, minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);

                                addScheduleToLayout(scheduleTitle, calendar.getTime());
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            false);
                    timePicker.show();

                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePicker.show();
    }

    private void addScheduleToLayout(String title, Date dateTime) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
        String scheduleDate = dateFormat.format(dateTime);


        SimpleDateFormat timeStampFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a", Locale.getDefault());
        String createdAt = timeStampFormat.format(new Date());


        View scheduleItem = getLayoutInflater().inflate(R.layout.schedule_item, null);


        TextView scheduleText = scheduleItem.findViewById(R.id.scheduleText);
        Button deleteBtn = scheduleItem.findViewById(R.id.deleteBtn);
        Button alarmBtn = scheduleItem.findViewById(R.id.alarmBtn);


        scheduleText.setText("For: " + title + "\nOn: " + scheduleDate + "\nAdded at: " + createdAt);


        deleteBtn.setOnClickListener(v -> scheduleContainer.removeView(scheduleItem));


        alarmBtn.setOnClickListener(v -> {

            Intent intent = new Intent(schedule.this, alarm.class);
            startActivity(intent);
        });


        scheduleContainer.addView(scheduleItem);
    }
}
