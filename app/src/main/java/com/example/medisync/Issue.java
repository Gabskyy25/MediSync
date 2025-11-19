package com.example.medisync;

public class Issue {

    private long id;
    private String issue;
    private String resolution;
    private long dateAdded;

    public Issue() {
    }

    public Issue(long id, String issue, String resolution, long dateAdded) {
        this.id = id;
        this.issue = issue;
        this.resolution = resolution;
        this.dateAdded = dateAdded;
    }

    public Issue(String issue, String resolution, long dateAdded) {
        this.id = -1;
        this.issue = issue;
        this.resolution = resolution;
        this.dateAdded = dateAdded;
    }

    public long getId() {
        return id;
    }

    public String getIssue() {
        return issue;
    }

    public String getResolution() {
        return resolution;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }
}
