package com.example.medisync;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter
        extends RecyclerView.Adapter<NotificationAdapter.VH> {

    private final List<NotificationModel> list = new ArrayList<>();

    public NotificationAdapter(List<NotificationModel> initial) {
        if (initial != null) list.addAll(initial);
    }

    /* ================= UPDATE ================= */

    public void updateList(List<NotificationModel> newList) {
        list.clear();
        if (newList != null) list.addAll(newList);
        notifyDataSetChanged();
    }

    /* ================= VIEW HOLDER ================= */

    static class VH extends RecyclerView.ViewHolder {
        TextView title, message, time;

        VH(@NonNull View v) {
            super(v);
            title = v.findViewById(R.id.notifTitle);
            message = v.findViewById(R.id.notifMessage);
            time = v.findViewById(R.id.notifTime);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(
            @NonNull VH holder,
            int position
    ) {
        NotificationModel n = list.get(position);

        holder.title.setText(n.getTitle());
        holder.message.setText(n.getMessage());

        // ⏰ Standard 12-hour time (AM/PM)
        String formattedTime = DateFormat.format(
                "hh:mm a",   // ✅ 12-hour format
                n.getTimestamp()
        ).toString();

        holder.time.setText(formattedTime);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
