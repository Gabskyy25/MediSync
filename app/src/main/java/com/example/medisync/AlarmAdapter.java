package com.example.medisync;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import android.widget.Switch;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {

    private final List<AlarmModel> alarmList;
    private final String[] dayNames = {"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};

    public AlarmAdapter(List<AlarmModel> alarmList) {
        this.alarmList = alarmList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alarm, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AlarmModel alarm = alarmList.get(position);

        holder.tvDescription.setText(alarm.description);
        holder.tvTime.setText(alarm.time);
        holder.switchEnable.setChecked(alarm.enabled);
        holder.tvDays.setText(formatDays(alarm.days));

        holder.itemView.setOnClickListener(v -> showDayPicker(holder, alarm));
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    private void showDayPicker(ViewHolder holder, AlarmModel alarm) {
        boolean[] checked = new boolean[7];
        for (int d : alarm.days) checked[d - 2] = true;

        new AlertDialog.Builder(holder.itemView.getContext())
                .setTitle("Select Days")
                .setMultiChoiceItems(dayNames, checked, (dialog, which, isChecked) -> checked[which] = isChecked)
                .setPositiveButton("Save", (dialog, which) -> {
                    alarm.days.clear();
                    for (int i = 0; i < 7; i++) if (checked[i]) alarm.days.add(i + 2);
                    holder.tvDays.setText(formatDays(alarm.days));
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private String formatDays(List<Integer> days) {
        if (days.isEmpty()) return "No days";
        StringBuilder sb = new StringBuilder();
        for (int d : days) sb.append(dayNames[d - 2]).append(" ");
        return sb.toString();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescription, tvTime, tvDays;
        Switch switchEnable;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDays = itemView.findViewById(R.id.tvDays);
            switchEnable = itemView.findViewById(R.id.switchEnable);
        }
    }
}
