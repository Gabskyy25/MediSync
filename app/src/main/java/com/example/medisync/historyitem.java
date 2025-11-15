package com.example.medisync;

public class historyitem {
    private String issue;
    private String resolve;
    private String level;

    public historyitem(String issue, String resolve, String level) {
        this.issue = issue;
        this.resolve = resolve;
        this.level = level;
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
}
