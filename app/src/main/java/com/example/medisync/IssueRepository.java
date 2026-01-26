package com.example.medisync;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class IssueRepository {

    private final FirebaseFirestore db;
    private final String uid;

    public IssueRepository() {
        db = FirebaseFirestore.getInstance();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            throw new IllegalStateException("User must be logged in");
        }

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    /* ================= ADD ISSUE ================= */

    public void addIssue(Issue issue) {
        db.collection("users")
                .document(uid)
                .collection("issues")
                .add(issue);
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

                        // 🔥 IMPORTANT: assign Firestore document ID
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
                );
    }

    /* ================= DELETE ISSUE ================= */

    public void deleteIssue(String issueId) {
        if (issueId == null) return;

        db.collection("users")
                .document(uid)
                .collection("issues")
                .document(issueId)
                .delete();
    }

    /* ================= CALLBACK ================= */

    public interface OnIssuesLoaded {
        void onLoaded(ArrayList<Issue> issues);
    }
}
