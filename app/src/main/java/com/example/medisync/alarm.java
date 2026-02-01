package com.example.medisync;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class alarm extends AppCompatActivity implements AlarmAdapter.Listener {

    private final List<AlarmModel> alarmList = new ArrayList<>();
    private AlarmAdapter adapter;

    private TimePicker timePicker;
    private EditText etAlarmDescription;

    // 🔔 Notification repo
    private NotificationRepository notificationRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        timePicker = findViewById(R.id.timePicker);
        etAlarmDescription = findViewById(R.id.etAlarmDescription);
        Button btnSetAlarm = findViewById(R.id.btnSetAlarm);
        ImageView backBtn = findViewById(R.id.backbtn);
        RecyclerView recyclerAlarms = findViewById(R.id.recyclerAlarms);

        adapter = new AlarmAdapter(alarmList, this);
        recyclerAlarms.setLayoutManager(new LinearLayoutManager(this));
        recyclerAlarms.setAdapter(adapter);

        enableSwipeToDelete(recyclerAlarms);

        notificationRepo = new NotificationRepository();

        backBtn.setOnClickListener(v -> finish());
        btnSetAlarm.setOnClickListener(v -> setAlarm());
    }

    /* ================= RELOAD ================= */

    @Override
    protected void onResume() {
        super.onResume();
        loadAlarmsFromFirestore();
    }

    /* ================= LOAD ================= */

    private void loadAlarmsFromFirestore() {
        AlarmStorage.loadAlarms(list -> {
            alarmList.clear();
            alarmList.addAll(list);
            adapter.notifyDataSetChanged();

            for (AlarmModel m : alarmList) {
                if (m.isEnabled()) {
                    scheduleAlarm(m);
                }
            }
        });
    }

    /* ================= ADD ================= */

    private void setAlarm() {

        String desc = etAlarmDescription.getText().toString().trim();
        if (desc.isEmpty()) desc = "Alarm";

        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        // 🔧 RAW time (used internally by alarm scheduling)
        String time24 = String.format(Locale.US, "%02d:%02d", hour, minute);

        // 🔧 FORMAT FOR NOTIFICATION (12-hour standard time)
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);

        String formattedTime =
                android.text.format.DateFormat
                        .format("hh:mm a", cal)
                        .toString();

        List<Integer> days = new ArrayList<>();
        days.add(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));

        AlarmModel model = new AlarmModel(desc, time24, true, days);

        // ✅ SAVE FIRST so ID exists
        AlarmStorage.saveAlarm(model);

        // 🔔 ADD NOTIFICATION (12-hour time)
        notificationRepo.addNotification(
                "Alarm Created",
                "Alarm \"" + desc + "\" set for " + formattedTime,
                "ALARM",
                model.getId()
        );

        alarmList.add(model);
        adapter.notifyItemInserted(alarmList.size() - 1);

        scheduleAlarm(model);

        Toast.makeText(this, "Alarm Added!", Toast.LENGTH_SHORT).show();
        etAlarmDescription.setText("");
    }


    /* ================= DELETE ================= */

    private void deleteAlarm(int position) {

        // Get alarm model
        AlarmModel model = alarmList.get(position);

        // Safety check
        if (model == null || model.getId() == null) {
            Toast.makeText(this, "Unable to delete alarm", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1️⃣ Cancel scheduled alarm
        cancelAlarm(model);

        // 2️⃣ ADD NOTIFICATION FIRST (ID still exists)
        notificationRepo.addNotification(
                "Alarm Deleted",
                "Alarm \"" + model.getDescription() + "\" was removed",
                "ALARM",
                model.getId()
        );

        // 3️⃣ Delete alarm from Firestore
        AlarmStorage.deleteAlarm(model.getId());

        // 4️⃣ Remove from UI
        alarmList.remove(position);
        adapter.notifyItemRemoved(position);

        Toast.makeText(this, "Alarm Deleted", Toast.LENGTH_SHORT).show();
    }


    /* ================= SCHEDULING ================= */

    private void scheduleAlarm(AlarmModel model) {

        cancelAlarm(model);

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (manager == null) return;

        String[] parts = model.getTime().split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        for (int day : model.getDays()) {

            Calendar c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_WEEK, day);
            c.set(Calendar.HOUR_OF_DAY, hour);
            c.set(Calendar.MINUTE, minute);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);

            if (c.before(Calendar.getInstance())) {
                c.add(Calendar.WEEK_OF_YEAR, 1);
            }

            Intent intent = new Intent(this, alarmreceiver.class);
            intent.putExtra("DESC", model.getDescription());

            PendingIntent pi = PendingIntent.getBroadcast(
                    this,
                    model.getId().hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    !manager.canScheduleExactAlarms()) {
                startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM));
                return;
            }

            manager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    c.getTimeInMillis(),
                    pi
            );
        }
    }

    private void cancelAlarm(AlarmModel model) {

        if (model.getId() == null) return;

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (manager == null) return;

        Intent intent = new Intent(this, alarmreceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(
                this,
                model.getId().hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        manager.cancel(pi);
    }

    /* ================= SWIPE ================= */

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
                deleteAlarm(vh.getBindingAdapterPosition());
            }

            @Override
            public void onChildDraw(@NonNull Canvas c,
                                    @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY,
                                    int actionState,
                                    boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(
                        c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(Color.parseColor("#FF5252"))
                        .addSwipeLeftActionIcon(R.drawable.ic_delete)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY,
                        actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recycler);
    }

    /* ================= ADAPTER CALLBACKS ================= */

    @Override
    public void onToggle(AlarmModel m) {
        if (m.isEnabled()) scheduleAlarm(m);
        else cancelAlarm(m);

        AlarmStorage.saveAlarm(m);
    }

    @Override
    public void onDaysChanged(AlarmModel m) {
        cancelAlarm(m);
        scheduleAlarm(m);
        AlarmStorage.saveAlarm(m);
    }
}
