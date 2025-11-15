package com.example.medisync;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class historyadapter extends RecyclerView.Adapter<historyadapter.HistoryViewHolder> {

    private List<historyitem> historyList;

    public historyadapter(List<historyitem> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.itemhistory, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        historyitem item = historyList.get(position);
        holder.tvIssue.setText("Issue: " + item.getIssue());
        holder.tvResolve.setText("Resolve: " + item.getResolve());
        holder.tvLevel.setText("Emergency: " + item.getLevel());
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvIssue, tvResolve, tvLevel;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIssue = itemView.findViewById(R.id.tvIssue);
            tvResolve = itemView.findViewById(R.id.tvResolve);
            tvLevel = itemView.findViewById(R.id.tvLevel);
        }
    }
}
