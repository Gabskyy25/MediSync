package com.example.medisync;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PatientsAdapter extends RecyclerView.Adapter<PatientsAdapter.PatientViewHolder> {
    private List<Patient> patients;

    public PatientsAdapter(List<Patient> patients) {
        this.patients = patients;
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_item, parent, false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        Patient patient = patients.get(position);
        holder.name.setText(patient.getName());
        holder.age.setText(String.valueOf(patient.getAge()));
        holder.birthdate.setText(patient.getBirthdate());
        holder.disease.setText(patient.getDisease());
    }

    @Override
    public int getItemCount() {
        return patients.size();
    }

    public void updatePatients(List<Patient> newPatients) {
        this.patients = newPatients;
        notifyDataSetChanged();
    }

    public static class PatientViewHolder extends RecyclerView.ViewHolder {
        TextView name, age, birthdate, disease;

        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.patientName);
            age = itemView.findViewById(R.id.patientAge);
            birthdate = itemView.findViewById(R.id.patientBirthdate);
            disease = itemView.findViewById(R.id.patientDisease);
        }
    }
}