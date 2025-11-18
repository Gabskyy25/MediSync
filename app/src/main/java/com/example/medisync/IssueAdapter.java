package com.example.medisync;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class IssueAdapter extends RecyclerView.Adapter<IssueAdapter.VH> {

    private List<Issue> issues;

    public IssueAdapter(List<Issue> issues) {
        this.issues = issues;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_issue, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Issue issue = issues.get(position);
        holder.tvIssue.setText(issue.getIssue());
        holder.tvResolve.setText(issue.getResolution());
        holder.tvLevel.setText("");
        holder.tvSavedOn.setText("Saved on: " + formatTimestamp(issue.getSavedAt()));
    }

    @Override
    public int getItemCount() {
        return issues == null ? 0 : issues.size();
    }

    public void setData(List<Issue> data) {
        this.issues = data;
        notifyDataSetChanged();
    }

    private String formatTimestamp(long millis) {
        Date d = new Date(millis);
        return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(d);
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvIssue, tvResolve, tvLevel, tvSavedOn;
        VH(@NonNull View v) {
            super(v);
            tvIssue = v.findViewById(R.id.tvIssue);
            tvResolve = v.findViewById(R.id.tvResolve);
            tvLevel = v.findViewById(R.id.tvLevel);
            tvSavedOn = v.findViewById(R.id.tvSavedOn);
        }
    }
}
