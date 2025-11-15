package com.example.medisync;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IssueAdapter extends RecyclerView.Adapter<IssueAdapter.ViewHolder> {

    private List<Issue> issues;

    public IssueAdapter(List<Issue> issues) {
        this.issues = issues;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView issueText, resolutionText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            issueText = itemView.findViewById(R.id.textIssue);
            resolutionText = itemView.findViewById(R.id.textResolution);
        }
    }

    @NonNull
    @Override
    public IssueAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_issue, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull IssueAdapter.ViewHolder holder, int position) {
        Issue issue = issues.get(position);
        holder.issueText.setText("Issue: " + issue.getIssue());
        holder.resolutionText.setText("Resolution: " + issue.getResolution());
    }

    @Override
    public int getItemCount() {
        return issues.size();
    }
}
