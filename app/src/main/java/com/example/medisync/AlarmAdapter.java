package com.example.medisync;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.Holder> {

    public interface Listener {
        void onToggle(AlarmModel model);
        void onDaysChanged(AlarmModel model);
    }

    private final List<AlarmModel> list;
    private final Listener listener;

    private final String[] dayNames = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};

    public AlarmAdapter(List<AlarmModel> list, Listener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alarm, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        AlarmModel m = list.get(position);

        h.desc.setText(m.description);
        h.time.setText(formatTo12Hour(m.time));
        h.days.setText(formatDays(m.days));
        h.toggle.setChecked(m.enabled);

        h.toggle.setOnCheckedChangeListener(null);
        h.toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            m.enabled = isChecked;
            listener.onToggle(m);
        });

        h.itemView.setOnClickListener(v -> showDayPicker(h, m));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void showDayPicker(Holder h, AlarmModel m) {
        boolean[] checked = new boolean[7];

        for (int d : m.days) {
            if (d >= 1 && d <= 7) {
                checked[d - 1] = true;
            }
        }

        new AlertDialog.Builder(h.itemView.getContext())
                .setTitle("Select Days")
                .setMultiChoiceItems(dayNames, checked,
                        (dialog, which, isChecked) -> checked[which] = isChecked)
                .setPositiveButton("Save", (dialog, which) -> {
                    m.days.clear();
                    for (int i = 0; i < 7; i++) {
                        if (checked[i]) {
                            m.days.add(i + 1);
                        }
                    }
                    h.days.setText(formatDays(m.days));
                    listener.onDaysChanged(m);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private String formatDays(List<Integer> days) {
        if (days.isEmpty()) return "No days";

        StringBuilder sb = new StringBuilder();
        for (int d : days) {
            if (d >= 1 && d <= 7) {
                sb.append(dayNames[d - 1]).append(" ");
            }
        }
        return sb.toString().trim();
    }

    private String formatTo12Hour(String time24) {
        try {
            SimpleDateFormat input =
                    new SimpleDateFormat("HH:mm", Locale.getDefault());
            SimpleDateFormat output =
                    new SimpleDateFormat("hh:mm a", Locale.getDefault());
            return output.format(input.parse(time24));
        } catch (ParseException e) {
            return time24;
        }
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView desc, time, days;
        Switch toggle;

        Holder(View v) {
            super(v);
            desc = v.findViewById(R.id.tvDescription);
            time = v.findViewById(R.id.tvTime);
            days = v.findViewById(R.id.tvDays);
            toggle = v.findViewById(R.id.switchEnable);
        }
    }
}
