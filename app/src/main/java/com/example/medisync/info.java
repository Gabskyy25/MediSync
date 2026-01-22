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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

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
        recycler = v.findViewById(R.id.patientRecycler);

        db = new DatabaseHelper(getContext());
        list = db.getAllPatients();
        adapter = new PatientsAdapter(list);

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);

        enableSwipeToDelete();

        birthdate.setOnClickListener(vw -> showDatePicker());

        saveBtn.setOnClickListener(view -> {
            savePatient();
            clearFields();
            refreshList();
        });

        return v;
    }

    private void enableSwipeToDelete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                Patient patientToDelete = list.get(position);

                db.deletePatient(patientToDelete.getId());

                list.remove(position);
                adapter.notifyItemRemoved(position);

                Toast.makeText(getContext(), "Patient Deleted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(Color.parseColor("#FF5252"))
                        .addSwipeLeftActionIcon(R.drawable.ic_delete)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recycler);
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