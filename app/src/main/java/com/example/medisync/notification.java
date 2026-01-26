package com.example.medisync;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class notification extends AppCompatActivity {

    private RecyclerView recycler;
    private NotificationAdapter adapter;
    private NotificationRepository repository;
    private ListenerRegistration listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        recycler = findViewById(R.id.notificationRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        repository = new NotificationRepository();
        adapter = new NotificationAdapter(new ArrayList<>());
        recycler.setAdapter(adapter);

        loadNotifications();
    }

    /* ================= LOAD NOTIFICATIONS ================= */

    private void loadNotifications() {
        listener = repository
                .getAllNotificationsQuery()
                .addSnapshotListener((snapshots, e) -> {

                    if (snapshots == null) return;

                    List<NotificationModel> list = new ArrayList<>();

                    for (var doc : snapshots.getDocuments()) {
                        NotificationModel model = doc.toObject(NotificationModel.class);
                        if (model != null) {
                            model.setId(doc.getId()); // IMPORTANT
                            list.add(model);
                        }
                    }

                    adapter.updateList(list);
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listener != null) listener.remove();
    }
}
