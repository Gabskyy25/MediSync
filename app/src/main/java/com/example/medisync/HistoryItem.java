package com.example.medisync;

public class HistoryItem {

    // Firestore document ID (optional but recommended)
    private String id;

    private String issue;
    private String resolve;
    private String level;

    // ✅ REQUIRED empty constructor for Firebase
    public HistoryItem() {
    }

    // Constructor for creating new history items
    public HistoryItem(String issue, String resolve, String level) {
        this.issue = issue;
        this.resolve = resolve;
        this.level = level;
    }

    /* ===== GETTERS ===== */

    public String getId() {
        return id;
    }

    public String getIssue() {
        return issue;
    }

    public String getResolve() {
        return resolve;
    }

    public String getLevel() {
        return level;
    }

    /* ===== SETTERS ===== */

    public void setId(String id) {
        this.id = id;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public void setResolve(String resolve) {
        this.resolve = resolve;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
