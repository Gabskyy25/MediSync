package com.example.medisync;

public class Issue {
    private String issue;
    private String resolution;

    public Issue(String issue, String resolution) {
        this.issue = issue;
        this.resolution = resolution;
    }

    public String getIssue() {
        return issue;
    }

    public String getResolution() {
        return resolution;
    }
}
