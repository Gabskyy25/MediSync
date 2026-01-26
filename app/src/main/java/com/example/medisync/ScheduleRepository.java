package com.example.medisync;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ScheduleRepository {

    private final FirebaseFirestore db;
    private final String uid;

    public ScheduleRepository() {
        db = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    // ADD SCHEDULE
    public void addSchedule(ScheduleModel model) {
        db.collection("users")
                .document(uid)
                .collection("schedules")
                .add(model);
    }

    // GET ALL SCHEDULES
    public Query getSchedules() {
        return db.collection("users")
                .document(uid)
                .collection("schedules")
                .orderBy("time", Query.Direction.ASCENDING);
    }

    // DELETE SCHEDULE
    public void deleteSchedule(String scheduleId) {
        db.collection("users")
                .document(uid)
                .collection("schedules")
                .document(scheduleId)
                .delete();
    }
}
