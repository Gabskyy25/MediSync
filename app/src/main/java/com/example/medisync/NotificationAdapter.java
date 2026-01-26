package com.example.medisync;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.VH> {

    private List<NotificationModel> list;

    public NotificationAdapter(List<NotificationModel> list) {
        this.list = list;
    }

    /* ================= UPDATE DATA (FIRESTORE) ================= */

    public void updateList(List<NotificationModel> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    /* ================= VIEW HOLDER ================= */

    static class VH extends RecyclerView.ViewHolder {
        TextView title, msg, time;

        VH(View v) {
            super(v);
            title = v.findViewById(R.id.notifTitle);
            msg = v.findViewById(R.id.notifMessage);
            time = v.findViewById(R.id.notifTime);
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.notification_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        NotificationModel n = list.get(position);
        holder.title.setText(n.getTitle());
        holder.msg.setText(n.getMessage());
        holder.time.setText(n.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }
}
