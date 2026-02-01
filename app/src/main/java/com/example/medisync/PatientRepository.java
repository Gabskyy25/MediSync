package com.example.medisync;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class PatientRepository {

    private final FirebaseFirestore db;
    private final String uid;

    public PatientRepository() {
        db = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    // ADD PATIENT
    public void addPatient(Patient patient, OnPatientAdded callback) {
        db.collection("users")
                .document(uid)
                .collection("patients")
                .add(patient)
                .addOnSuccessListener(doc -> callback.onAdded(doc.getId()));
    }

    public interface OnPatientAdded {
        void onAdded(String docId);
    }

    // GET ALL PATIENTS
    public Query getPatients() {
        return db.collection("users")
                .document(uid)
                .collection("patients")
                .orderBy("name", Query.Direction.ASCENDING);
    }

    // DELETE PATIENT
    public void deletePatient(String patientId) {
        db.collection("users")
                .document(uid)
                .collection("patients")
                .document(patientId)
                .delete();
    }


}
