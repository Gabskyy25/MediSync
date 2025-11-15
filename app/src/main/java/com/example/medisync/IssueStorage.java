package com.example.medisync;

import java.util.ArrayList;
import java.util.List;

public class IssueStorage {

    private static IssueStorage instance;
    private final List<Issue> issues;

    private IssueStorage() {
        issues = new ArrayList<>();
    }

    public static IssueStorage getInstance() {
        if (instance == null) {
            instance = new IssueStorage();
        }
        return instance;
    }


    public void addIssue(Issue issue) {
        issues.add(issue);
    }

    public List<Issue> getIssues() {
        return issues;
    }
}
