package com.example.medisync;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class IssueRepository {

    private final FirebaseFirestore db;
    private final String uid;
    private final NotificationRepository notificationRepo;

    public IssueRepository() {
        db = FirebaseFirestore.getInstance();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            throw new IllegalStateException("User must be logged in");
        }

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        notificationRepo = new NotificationRepository();
    }

    /* ================= ADD ISSUE ================= */

    public void addIssue(Issue issue) {

        db.collection("users")
                .document(uid)
                .collection("issues")
                .add(issue)
                .addOnSuccessListener(doc -> {

                    String time = new SimpleDateFormat(
                            "MMM dd, yyyy hh:mm a",
                            Locale.getDefault()
                    ).format(new Date());

                    notificationRepo.addNotification(
                            "New Health Issue Added",
                            issue.getIssue() + " (saved at " + time + ")",
                            "ISSUE",
                            doc.getId()
                    );
                });
    }

    /* ================= REALTIME LISTENER ================= */

    public ListenerRegistration listenToIssues(OnIssuesLoaded listener) {
        return db.collection("users")
                .document(uid)
                .collection("issues")
                .orderBy("dateAdded", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null || snapshot == null) return;

                    ArrayList<Issue> list = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : snapshot) {
                        Issue issue = doc.toObject(Issue.class);
                        issue.setId(doc.getId());
                        list.add(issue);
                    }

                    listener.onLoaded(list);
                });
    }

    /* ================= UPDATE ISSUE ================= */

    public void updateIssue(String issueId, String issue, String resolution) {
        if (issueId == null) return;

        db.collection("users")
                .document(uid)
                .collection("issues")
                .document(issueId)
                .update(
                        "issue", issue,
                        "resolution", resolution
                )
                .addOnSuccessListener(v -> {

                    // ✅ STANDARD TIME
                    String time = new SimpleDateFormat(
                            "MMM dd, yyyy hh:mm a",
                            Locale.getDefault()
                    ).format(new Date());

                    // 🔔 UPDATE NOTIFICATION
                    notificationRepo.addNotification(
                            "Health Issue Updated",
                            "Updated issue:\n" +
                                    issue + "\nResolution:\n" +
                                    resolution + "\n\n(" + time + ")",
                            "ISSUE",
                            issueId
                    );
                });
    }

    /* ================= DELETE ISSUE ================= */

    public void deleteIssue(String issueId) {
        if (issueId == null) return;

        db.collection("users")
                .document(uid)
                .collection("issues")
                .document(issueId)
                .delete()
                .addOnSuccessListener(v -> {

                    String time = new SimpleDateFormat(
                            "MMM dd, yyyy hh:mm a",
                            Locale.getDefault()
                    ).format(new Date());

                    notificationRepo.addNotification(
                            "Health Issue Deleted",
                            "An issue was removed at " + time,
                            "ISSUE",
                            issueId
                    );
                });
    }

    /* ================= CALLBACK ================= */

    public interface OnIssuesLoaded {
        void onLoaded(ArrayList<Issue> issues);
    }
}
