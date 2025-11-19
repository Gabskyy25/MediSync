package com.example.medisync;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IssueAdapter extends RecyclerView.Adapter<IssueAdapter.VH> {

    private List<Issue> issues;

    public IssueAdapter(List<Issue> issues) {
        this.issues = issues;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_issue, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Issue issue = issues.get(position);

        holder.textIssue.setText(issue.getIssue());
        holder.textResolution.setText(issue.getResolution());
    }

    @Override
    public int getItemCount() {
        return issues == null ? 0 : issues.size();
    }

    public void setData(List<Issue> data) {
        this.issues = data;
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView textIssue, textResolution;

        VH(@NonNull View v) {
            super(v);
            textIssue = v.findViewById(R.id.textIssue);
            textResolution = v.findViewById(R.id.textResolution);
        }
    }
}
