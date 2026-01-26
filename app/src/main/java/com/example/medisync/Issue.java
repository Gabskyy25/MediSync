package com.example.medisync;

public class Issue {

    private String id;          // 🔥 Firestore ID
    private String issue;
    private String resolution;
    private long dateAdded;

    public Issue() {} // REQUIRED

    public Issue(String issue, String resolution, long dateAdded) {
        this.issue = issue;
        this.resolution = resolution;
        this.dateAdded = dateAdded;
    }

    public String getId() { return id; }
    public String getIssue() { return issue; }
    public String getResolution() { return resolution; }
    public long getDateAdded() { return dateAdded; }

    public void setId(String id) { this.id = id; }
    public void setIssue(String issue) { this.issue = issue; }
    public void setResolution(String resolution) { this.resolution = resolution; }
    public void setDateAdded(long dateAdded) { this.dateAdded = dateAdded; }
}
