package com.example.medisync;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class schedule extends AppCompatActivity {

    private RecyclerView recyclerSchedules;
    private ScheduleAdapter adapter;
    private final List<ScheduleModel> scheduleList = new ArrayList<>();

    private ScheduleRepository scheduleRepo;
    private NotificationRepository notificationRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        recyclerSchedules = findViewById(R.id.recyclerSchedules);
        ImageView backBtn = findViewById(R.id.backbtn);

        scheduleRepo = new ScheduleRepository();
        notificationRepo = new NotificationRepository();

        adapter = new ScheduleAdapter(scheduleList);
        recyclerSchedules.setLayoutManager(new LinearLayoutManager(this));
        recyclerSchedules.setAdapter(adapter);

        enableSwipeToDelete();
        loadSchedules();

        backBtn.setOnClickListener(v -> finish());

        findViewById(R.id.inputButton)
                .setOnClickListener(v -> showScheduleDialog());
    }

    /* ================= LOAD ================= */

    private void loadSchedules() {
        scheduleRepo.getSchedules()
                .addSnapshotListener((snap, e) -> {
                    if (snap == null) return;

                    scheduleList.clear();

                    for (DocumentSnapshot doc : snap) {
                        ScheduleModel model = doc.toObject(ScheduleModel.class);
                        if (model != null) {
                            model.setId(doc.getId());
                            scheduleList.add(model);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    /* ================= ADD ================= */

    private void showScheduleDialog() {
        EditText input = new EditText(this);
        input.setHint("Enter schedule details");

        new AlertDialog.Builder(this)
                .setTitle("Add Schedule")
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
                calendar.set(Calendar.SECOND, 0);

                addSchedule(title, calendar.getTime());

            }, calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false).show();

        }, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void addSchedule(String title, Date date) {

        // ✅ STANDARD (12-HOUR) TIME FORMAT
        String formattedDate = new SimpleDateFormat(
                "MMM dd, yyyy hh:mm a",
                Locale.getDefault()
        ).format(date);

        ScheduleModel model = new ScheduleModel(title, formattedDate);

        // ✅ SAVE SCHEDULE
        scheduleRepo.addSchedule(model);

        // 🔔 ADD NOTIFICATION (SAME PATTERN AS CONTACTS)
        notificationRepo.addNotification(
                "Schedule Added",
                "\"" + title + "\" scheduled for " + formattedDate,
                "SCHEDULE",
                title
        );

        Toast.makeText(this,
                "Schedule Added",
                Toast.LENGTH_SHORT).show();
    }

    /* ================= DELETE ================= */

    private void enableSwipeToDelete() {
        new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(
                        0,
                        ItemTouchHelper.LEFT
                ) {

                    @Override
                    public boolean onMove(
                            @NonNull RecyclerView r,
                            @NonNull RecyclerView.ViewHolder v,
                            @NonNull RecyclerView.ViewHolder t) {
                        return false;
                    }

                    @Override
                    public void onSwiped(
                            @NonNull RecyclerView.ViewHolder vh,
                            int d) {

                        int pos = vh.getBindingAdapterPosition();
                        ScheduleModel model = scheduleList.get(pos);

                        // 🗑 DELETE SCHEDULE
                        scheduleRepo.deleteSchedule(model.getId());

                        // 🔔 DELETE NOTIFICATION
                        notificationRepo.addNotification(
                                "Schedule Deleted",
                                "\"" + model.getTitle() + "\" was removed",
                                "SCHEDULE",
                                model.getId()
                        );

                        scheduleList.remove(pos);
                        adapter.notifyItemRemoved(pos);

                        Toast.makeText(
                                schedule.this,
                                "Schedule Deleted",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                    @Override
                    public void onChildDraw(
                            @NonNull Canvas c,
                            @NonNull RecyclerView rv,
                            @NonNull RecyclerView.ViewHolder vh,
                            float dX,
                            float dY,
                            int actionState,
                            boolean active) {

                        new RecyclerViewSwipeDecorator.Builder(
                                c, rv, vh, dX, dY, actionState, active)
                                .addSwipeLeftBackgroundColor(
                                        Color.parseColor("#FF5252"))
                                .addSwipeLeftActionIcon(R.drawable.ic_delete)
                                .create()
                                .decorate();

                        super.onChildDraw(
                                c, rv, vh, dX, dY, actionState, active);
                    }

                }).attachToRecyclerView(recyclerSchedules);
    }
}
