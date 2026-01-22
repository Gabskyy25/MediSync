package com.example.medisync;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.VH> {


    private final List<NotificationModel> list;

    public NotificationAdapter(List<NotificationModel> list) {
        this.list = list;
    }

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
    public VH onCreateViewHolder(ViewGroup p, int v) {
        return new VH(LayoutInflater.from(p.getContext())
                .inflate(R.layout.notification_item, p, false));
    }

    @Override
    public void onBindViewHolder(VH h, int i) {
        NotificationModel n = list.get(i);
        h.title.setText(n.getTitle());
        h.msg.setText(n.getMessage());
        h.time.setText(n.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
