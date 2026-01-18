package com.example.medisync;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

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
    public Holder onCreateViewHolder(@NonNull ViewGroup p, int v) {
        return new Holder(LayoutInflater.from(p.getContext()).inflate(R.layout.item_alarm, p, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int p) {
        AlarmModel m = list.get(p);
        h.desc.setText(m.description);
        h.time.setText(String.format("%02d:%02d", m.hour, m.minute));
        h.days.setText(formatDays(m.days));
        h.toggle.setChecked(m.enabled);

        h.toggle.setOnCheckedChangeListener((b, c) -> {
            m.enabled = c;
            listener.onToggle(m);
        });

        h.itemView.setOnClickListener(v -> showDayPicker(h, m));
    }

    private void showDayPicker(Holder h, AlarmModel m) {
        boolean[] checked = new boolean[7];
        for (int d : m.days) checked[d - 1] = true;

        new AlertDialog.Builder(h.itemView.getContext())
                .setTitle("Select Days")
                .setMultiChoiceItems(dayNames, checked, (d, i, c) -> checked[i] = c)
                .setPositiveButton("Save", (d, i) -> {
                    m.days.clear();
                    for (int x = 0; x < 7; x++) if (checked[x]) m.days.add(x + 1);
                    h.days.setText(formatDays(m.days));
                    listener.onDaysChanged(m);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private String formatDays(List<Integer> days) {
        if (days.isEmpty()) return "No days";
        StringBuilder s = new StringBuilder();
        for (int d : days) s.append(dayNames[d - 1]).append(" ");
        return s.toString().trim();
    }

    @Override
    public int getItemCount() { return list.size(); }

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
