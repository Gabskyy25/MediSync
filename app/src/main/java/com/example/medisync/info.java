package com.example.medisync;

import android.app.DatePickerDialog;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class info extends Fragment {

    private EditText name, age, birthdate, disease;
    private Button saveBtn;
    private RecyclerView recycler;

    private PatientsAdapter adapter;
    private List<Patient> list;

    private PatientRepository patientRepo;
    private NotificationRepository notificationRepo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_info, container, false);

        name = v.findViewById(R.id.patientName);
        age = v.findViewById(R.id.patientAge);
        birthdate = v.findViewById(R.id.patientBirthdate);
        disease = v.findViewById(R.id.patientDisease);
        saveBtn = v.findViewById(R.id.saveBtn);
        recycler = v.findViewById(R.id.patientRecycler);

        patientRepo = new PatientRepository();
        notificationRepo = new NotificationRepository();

        list = new ArrayList<>();
        adapter = new PatientsAdapter(list);

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);

        enableSwipeToDelete();
        birthdate.setOnClickListener(vw -> showDatePicker());

        saveBtn.setOnClickListener(view -> {
            savePatient();
            clearFields();
        });

        listenForPatients();

        return v;
    }

    /* ================= LISTENER ================= */

    private void listenForPatients() {
        patientRepo.getPatients()
                .addSnapshotListener((snapshots, error) -> {

                    if (error != null || snapshots == null) return;

                    list.clear();

                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        Patient p = doc.toObject(Patient.class);
                        if (p != null) {
                            p.setId(doc.getId());
                            list.add(p);
                        }
                    }

                    adapter.notifyDataSetChanged();
                });
    }

    /* ================= SAVE ================= */

    private void savePatient() {

        String n = name.getText().toString().trim();
        String a = age.getText().toString().trim();

        if (n.isEmpty() || a.isEmpty()) {
            Toast.makeText(getContext(),
                    "Please fill required fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int ageInt = Integer.parseInt(a);
        String b = birthdate.getText().toString().trim();
        String d = disease.getText().toString().trim();

        Patient patient = new Patient(n, ageInt, b, d);

        patientRepo.addPatient(patient, docId -> {

            String time = new SimpleDateFormat(
                    "MMM dd, yyyy hh:mm a",
                    Locale.getDefault()
            ).format(new Date());

            notificationRepo.addNotification(
                    "Patient Added",
                    n + " was added (" + time + ")",
                    "PATIENT",
                    docId
            );

            Toast.makeText(getContext(),
                    "Patient Saved",
                    Toast.LENGTH_SHORT).show();
        });
    }

    /* ================= DELETE ================= */

    private void enableSwipeToDelete() {

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder,
                                 int direction) {

                int pos = viewHolder.getBindingAdapterPosition();
                Patient patient = list.get(pos);

                // ❌ delete patient only
                patientRepo.deletePatient(patient.getId());

                // ✅ KEEP "Patient Added" notification
                // ✅ ADD delete notification

                String time = new SimpleDateFormat(
                        "MMM dd, yyyy hh:mm a",
                        Locale.getDefault()
                ).format(new Date());

                notificationRepo.addNotification(
                        "Patient Removed",
                        patient.getName() + " was removed (" + time + ")",
                        "SYSTEM",
                        "PATIENT_REMOVED"
                );

                Toast.makeText(getContext(),
                        "Patient Deleted",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c,
                                    @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY,
                                    int actionState,
                                    boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(
                        c, recyclerView, viewHolder,
                        dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(Color.parseColor("#FF5252"))
                        .addSwipeLeftActionIcon(R.drawable.ic_delete)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder,
                        dX, dY, actionState, isCurrentlyActive);
            }

        }).attachToRecyclerView(recycler);
    }

    /* ================= DATE ================= */

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();

        new DatePickerDialog(
                getContext(),
                (view, year, month, day) ->
                        birthdate.setText((month + 1) + "/" + day + "/" + year),
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void clearFields() {
        name.setText("");
        age.setText("");
        birthdate.setText("");
        disease.setText("");
    }
}
