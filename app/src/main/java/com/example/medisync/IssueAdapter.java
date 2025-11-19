package com.example.medisync;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IssueAdapter extends RecyclerView.Adapter<IssueAdapter.IssueViewHolder> {

    private Context context;
    private List<Issue> issueList;
    private DBHelper db;
    private Runnable onUpdated;

    public IssueAdapter(Context context, List<Issue> issueList, DBHelper db, Runnable onUpdated) {
        this.context = context;
        this.issueList = issueList;
        this.db = db;
        this.onUpdated = onUpdated;
    }

    @NonNull
    @Override
    public IssueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_issue, parent, false);
        return new IssueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IssueViewHolder holder, int position) {
        Issue issue = issueList.get(position);

        holder.textIssue.setText(issue.getIssue());
        holder.textResolution.setText(issue.getResolution());
        holder.textDate.setText("Saved at: " + issue.getDateAdded());

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete")
                    .setMessage("Delete this issue?")
                    .setPositiveButton("Yes", (d, w) -> {
                        db.deleteIssue(issue.getId());
                        issueList.remove(position);
                        notifyItemRemoved(position);
                        onUpdated.run();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        holder.btnEdit.setOnClickListener(v -> openEditDialog(issue, position));
    }

    private void openEditDialog(Issue issue, int position) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_issue, null);

        EditText editIssue = dialogView.findViewById(R.id.editIssue);
        EditText editResolution = dialogView.findViewById(R.id.editResolution);

        editIssue.setText(issue.getIssue());
        editResolution.setText(issue.getResolution());

        new AlertDialog.Builder(context)
                .setTitle("Edit Issue")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newIssue = editIssue.getText().toString().trim();
                    String newResolution = editResolution.getText().toString().trim();

                    if (newIssue.isEmpty() || newResolution.isEmpty()) {
                        Toast.makeText(context, "Empty fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    db.updateIssue(issue.getId(), newIssue, newResolution);
                    issue.setIssue(newIssue);
                    issue.setResolution(newResolution);
                    notifyItemChanged(position);
                    onUpdated.run();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return issueList.size();
    }

    public static class IssueViewHolder extends RecyclerView.ViewHolder {

        TextView textIssue, textResolution, textDate;
        ImageButton btnEdit, btnDelete;

        public IssueViewHolder(@NonNull View itemView) {
            super(itemView);

            textIssue = itemView.findViewById(R.id.textIssue);
            textResolution = itemView.findViewById(R.id.textResolution);
            textDate = itemView.findViewById(R.id.textDate);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
