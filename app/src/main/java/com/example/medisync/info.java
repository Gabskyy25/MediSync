package com.example.medisync;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;

public class info extends Fragment {

    private EditText name, age, birthdate, disease;
    private Button saveBtn, deleteBtn;
    private RecyclerView recycler;
    private PatientsAdapter adapter;
    private DatabaseHelper db;
    private List<Patient> list;

    public info() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_info, container, false);

        name = v.findViewById(R.id.patientName);
        age = v.findViewById(R.id.patientAge);
        birthdate = v.findViewById(R.id.patientBirthdate);
        disease = v.findViewById(R.id.patientDisease);
        saveBtn = v.findViewById(R.id.saveBtn);
        deleteBtn = v.findViewById(R.id.deletePatientBtn);
        recycler = v.findViewById(R.id.patientRecycler);

        db = new DatabaseHelper(getContext());
        list = db.getAllPatients();
        adapter = new PatientsAdapter(list);

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);

        birthdate.setOnClickListener(vw -> showDatePicker());

        saveBtn.setOnClickListener(view -> {
            savePatient();
            clearFields();
            refreshList();
        });

        deleteBtn.setOnClickListener(view -> {
            db.deleteAllPatients();
            refreshList();
        });

        return v;
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH);
        int d = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dp = new DatePickerDialog(
                getContext(),
                (view, year, month, day) -> {
                    String date = (month + 1) + "/" + day + "/" + year;
                    birthdate.setText(date);
                },
                y, m, d
        );
        dp.show();
    }

    private void savePatient() {
        String n = name.getText().toString().trim();
        String a = age.getText().toString().trim();
        if (n.isEmpty() || a.isEmpty()) return;

        int ageInt = Integer.parseInt(a);
        String b = birthdate.getText().toString().trim();
        String d = disease.getText().toString().trim();

        db.insertPatient(new Patient(n, ageInt, b, d));
    }

    private void refreshList() {
        list = db.getAllPatients();
        adapter.updatePatients(list);
    }

    private void clearFields() {
        name.setText("");
        age.setText("");
        birthdate.setText("");
        disease.setText("");
    }
}
