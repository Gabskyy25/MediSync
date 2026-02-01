package com.example.medisync;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class NotificationRepository {

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    public NotificationRepository() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    private String getUid() {
        return auth.getCurrentUser() != null
                ? auth.getCurrentUser().getUid()
                : null;
    }

    /* ================= ADD ================= */

    public void addNotification(
            String title,
            String message,
            String entityType,
            String entityId
    ) {
        String uid = getUid();
        if (uid == null) {
            Log.e("FIRESTORE", "User not authenticated");
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("message", message);
        data.put("timestamp", System.currentTimeMillis());
        data.put("entityType", entityType);
        data.put("entityId", entityId);

        db.collection("users")
                .document(uid)
                .collection("notifications")
                .add(data)
                .addOnSuccessListener(doc ->
                        Log.d("FIRESTORE", "Notification added: " + doc.getId())
                )
                .addOnFailureListener(e ->
                        Log.e("FIRESTORE", "Notification FAILED", e)
                );
    }

    /* ================= QUERY ================= */

    public Query getAllNotificationsQuery() {
        String uid = getUid();
        if (uid == null) return null;

        return db.collection("users")
                .document(uid)
                .collection("notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING);
    }

    /* ================= DELETE ================= */

    public void deleteNotification(String id) {
        String uid = getUid();
        if (uid == null) return;

        db.collection("users")
                .document(uid)
                .collection("notifications")
                .document(id)
                .delete();
    }

    public void deleteByEntity(String entityType, String entityId) {
        String uid = getUid();
        if (uid == null) return;

        db.collection("users")
                .document(uid)
                .collection("notifications")
                .whereEqualTo("entityType", entityType)
                .whereEqualTo("entityId", entityId)
                .get()
                .addOnSuccessListener(qs -> {
                    for (var doc : qs.getDocuments()) {
                        doc.getReference().delete();
                    }
                });
    }
}
