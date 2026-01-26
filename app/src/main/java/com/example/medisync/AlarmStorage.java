package com.example.medisync;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlarmStorage {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static String getUid() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return null;
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    /* ================= SAVE SINGLE ALARM (FIX) ================= */

    public static void saveAlarm(@NonNull AlarmModel alarm) {
        String uid = getUid();
        if (uid == null || alarm.getId() == null) return;

        Map<String, Object> data = new HashMap<>();
        data.put("description", alarm.getDescription());
        data.put("time", alarm.getTime());
        data.put("enabled", alarm.isEnabled());
        data.put("days", alarm.getDays());

        db.collection("users")
                .document(uid)
                .collection("alarms")
                .document(alarm.getId())
                .set(data);
    }

    /* ================= LOAD ALARMS ================= */

    public static void loadAlarms(@NonNull AlarmLoadCallback callback) {
        String uid = getUid();
        if (uid == null) {
            callback.onLoaded(new ArrayList<>());
            return;
        }

        db.collection("users")
                .document(uid)
                .collection("alarms")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<AlarmModel> list = new ArrayList<>();

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {

                        List<Long> daysLong = (List<Long>) doc.get("days");
                        List<Integer> days = new ArrayList<>();

                        if (daysLong != null) {
                            for (Long d : daysLong) {
                                days.add(d.intValue());
                            }
                        }

                        AlarmModel model = new AlarmModel(
                                doc.getString("description"),
                                doc.getString("time"),
                                Boolean.TRUE.equals(doc.getBoolean("enabled")),
                                days
                        );

                        model.setId(doc.getId());
                        list.add(model);
                    }

                    callback.onLoaded(list);
                });
    }


    /* ================= DELETE ALARM ================= */

    public static void deleteAlarm(String alarmId) {
        String uid = getUid();
        if (uid == null) return;

        db.collection("users")
                .document(uid)
                .collection("alarms")
                .document(alarmId)
                .delete();
    }

    /* ================= CALLBACK ================= */

    public interface AlarmLoadCallback {
        void onLoaded(List<AlarmModel> alarms);
    }
}
