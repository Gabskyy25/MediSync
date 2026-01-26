package com.example.medisync;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NotificationStorage {

    private static final FirebaseFirestore db =
            FirebaseFirestore.getInstance();

    public static void addNotification(
            String title,
            String message,
            String entityType,
            String entityId
    ) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("message", message);
        data.put("entityType", entityType);
        data.put("entityId", entityId);
        data.put("timestamp", System.currentTimeMillis());

        db.collection("notifications").add(data);
    }

    public static void deleteByEntity(String entityType, String entityId) {
        db.collection("notifications")
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
