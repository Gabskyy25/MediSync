package com.example.medisync;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class IssueStorage {

    private static IssueStorage instance;
    private final List<Issue> issues;

    private Context context; // needed for notifications

    private IssueStorage(Context context) {
        this.context = context.getApplicationContext();
        issues = new ArrayList<>();
    }

    public static IssueStorage getInstance(Context context) {
        if (instance == null) {
            instance = new IssueStorage(context);
        }
        return instance;
    }

    // ✅ ISSUE ADDED + NOTIFICATION (ONLY ONCE)
    public void addIssue(Issue issue) {
        issues.add(issue);

        NotificationDBHelper notificationDB =
                new NotificationDBHelper(context);

        notificationDB.addNotification(
                "Issue Added",
                issue.getIssue(),
                "ISSUE",
                (int) issue.getId()
        );
    }

    public List<Issue> getIssues() {
        return issues;
    }
}
