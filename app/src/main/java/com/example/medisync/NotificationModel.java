package com.example.medisync;

public class NotificationModel {

    // Firestore document ID
    private String id;

    private String title;
    private String message;
    private long timestamp;
    private String entityType;
    private String entityId;

    // REQUIRED empty constructor for Firestore
    public NotificationModel() {}

    public NotificationModel(
            String title,
            String message,
            long timestamp,
            String entityType,
            String entityId
    ) {
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.entityType = entityType;
        this.entityId = entityId;
    }

    // ===== GETTERS =====
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
    public String getEntityType() { return entityType; }
    public String getEntityId() { return entityId; }

    // ===== SETTERS =====
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setMessage(String message) { this.message = message; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
}
