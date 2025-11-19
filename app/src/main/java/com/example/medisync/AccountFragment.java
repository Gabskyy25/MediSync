package com.example.medisync;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AccountFragment extends Fragment {

    private EditText patientName, patientAge, patientBirthdate, patientDisease;
    private Button editBtn, saveBtn, deleteBtn, logOutBtn, addPatientBtn;
    private RecyclerView patientRecycler;
    private PatientsAdapter adapter;
    private DatabaseHelper dbHelper;
    private List<Patient> patients;

    private TextView nameText, emailText, phoneText;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            return view;
        }

        String uid = currentUser.getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("profile");

        nameText = view.findViewById(R.id.Name);
        emailText = view.findViewById(R.id.gmail);
        phoneText = view.findViewById(R.id.PhoneNo);

        loadUserInfo();

        patientName = view.findViewById(R.id.patientName);
        patientAge = view.findViewById(R.id.patientAge);
        patientBirthdate = view.findViewById(R.id.patientBirthdate);
        patientDisease = view.findViewById(R.id.patientDisease);

        editBtn = view.findViewById(R.id.editBtn);
        saveBtn = view.findViewById(R.id.saveBtn);
        deleteBtn = view.findViewById(R.id.deleteBtn);
        logOutBtn = view.findViewById(R.id.button);
        addPatientBtn = view.findViewById(R.id.btnAddPatient);
        patientRecycler = view.findViewById(R.id.patientRecycler);

        dbHelper = new DatabaseHelper(getContext());
        patients = dbHelper.getAllPatients();
        adapter = new PatientsAdapter(patients);
        patientRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        patientRecycler.setAdapter(adapter);

        setEditable(false);

        editBtn.setOnClickListener(v -> {
            setEditable(true);
            saveBtn.setVisibility(View.VISIBLE);
            editBtn.setVisibility(View.GONE);
        });

        saveBtn.setOnClickListener(v -> {
            savePatientData();
            setEditable(false);
            saveBtn.setVisibility(View.GONE);
            editBtn.setVisibility(View.VISIBLE);
        });

        addPatientBtn.setOnClickListener(v -> {
            savePatientData();
            patientName.setText("");
            patientAge.setText("");
            patientBirthdate.setText("");
            patientDisease.setText("");
        });

        deleteBtn.setOnClickListener(v -> {
            dbHelper.deleteAllPatients();
            patients.clear();
            adapter.updatePatients(patients);
        });

        logOutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            if (getActivity() != null) getActivity().finish();
        });

        return view;
    }

    private void loadUserInfo() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    String email = snapshot.child("email").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);

                    emailText.setText(email != null ? email : "N/A");
                    phoneText.setText(phone != null ? phone : "N/A");

                    if (email != null && email.contains("@")) {
                        String prefix = email.substring(0, email.indexOf("@"));
                        nameText.setText(capitalize(prefix));
                    } else {
                        nameText.setText("Caregiver");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) { }
        });
    }

    private String capitalize(String s) {
        if (s.length() == 0) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private void setEditable(boolean editable) {
        patientName.setEnabled(editable);
        patientAge.setEnabled(editable);
        patientBirthdate.setEnabled(editable);
        patientDisease.setEnabled(editable);
    }

    private void savePatientData() {
        String name = patientName.getText().toString().trim();
        String ageStr = patientAge.getText().toString().trim();
        String birthdate = patientBirthdate.getText().toString().trim();
        String disease = patientDisease.getText().toString().trim();

        if (!name.isEmpty() && !ageStr.isEmpty()) {
            try {
                int age = Integer.parseInt(ageStr);
                Patient patient = new Patient(name, age, birthdate, disease);
                dbHelper.insertPatient(patient);
                patients = dbHelper.getAllPatients();
                adapter.updatePatients(patients);
            } catch (NumberFormatException ignored) {}
        }
    }
}
