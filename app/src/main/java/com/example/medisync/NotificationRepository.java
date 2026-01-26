package com.example.medisync;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NotificationRepository {

    private final FirebaseFirestore db;

    public NotificationRepository() {
        db = FirebaseFirestore.getInstance();
    }

    /* ================= ADD NOTIFICATION ================= */

    public void addNotification(String title,
                                String message,
                                String entityType,
                                String entityId) {

        String time = new SimpleDateFormat(
                "MMM dd, yyyy hh:mm a",
                Locale.getDefault()
        ).format(new Date());

        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("message", message);
        data.put("timestamp", time);
        data.put("entityType", entityType);
        data.put("entityId", entityId);

        db.collection("notifications").add(data);
    }

    /* ================= DELETE SINGLE ================= */

    public void deleteNotification(String notificationId) {
        db.collection("notifications")
                .document(notificationId)
                .delete();
    }

    /* ================= DELETE BY ENTITY ================= */

    public void deleteByEntity(String entityType, String entityId) {
        db.collection("notifications")
                .whereEqualTo("entityType", entityType)
                .whereEqualTo("entityId", entityId)
                .get()
                .addOnSuccessListener(query -> {
                    for (var doc : query.getDocuments()) {
                        doc.getReference().delete();
                    }
                });
    }

    /* ================= QUERY ALL ================= */

    public Query getAllNotificationsQuery() {
        return db.collection("notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING);
    }
}
