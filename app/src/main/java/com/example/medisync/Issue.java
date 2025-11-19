package com.example.medisync;

public class Issue {
    private long id;
    private String issue;
    private String resolution;
    private long savedAt;

    public Issue() {}

    public Issue(String issue, String resolution, long savedAt) {
        this.issue = issue;
        this.resolution = resolution;
        this.savedAt = savedAt;
    }

    public Issue(long id, String issue, String resolution, long savedAt) {
        this.id = id;
        this.issue = issue;
        this.resolution = resolution;
        this.savedAt = savedAt;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getIssue() { return issue; }
    public void setIssue(String issue) { this.issue = issue; }

    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }

    public long getSavedAt() { return savedAt; }
    public void setSavedAt(long savedAt) { this.savedAt = savedAt; }
}
