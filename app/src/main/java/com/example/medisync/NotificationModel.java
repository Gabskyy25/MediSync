package com.example.medisync;

public class NotificationModel {

    private int id;
    private String title;
    private String message;
    private String timestamp;
    private String entityType;
    private int entityId;

    public NotificationModel(int id, String title, String message,
                             String timestamp, String entityType, int entityId) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getTimestamp() { return timestamp; }
    public String getEntityType() { return entityType; }
    public int getEntityId() { return entityId; }
}
