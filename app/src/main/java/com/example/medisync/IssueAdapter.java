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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class IssueAdapter extends RecyclerView.Adapter<IssueAdapter.IssueViewHolder> {

    private Context context;
    private ArrayList<Issue> issueList;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    public IssueAdapter(Context context, ArrayList<Issue> issueList) {
        this.context = context;
        this.issueList = issueList;
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
        holder.textDate.setText("Added on: " + issue.getDateAdded());

        holder.btnDelete.setOnClickListener(v -> {
            String uid = auth.getCurrentUser().getUid();

            db.collection("users")
                    .document(uid)
                    .collection("issues")
                    .document(issue.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        issueList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                    });
        });

        // ---------------- EDIT ----------------
        holder.btnEdit.setOnClickListener(v -> openEditDialog(issue, position));
    }

    private void openEditDialog(Issue issue, int position) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_issue, null);

        EditText editIssue = dialogView.findViewById(R.id.editIssue);
        EditText editResolution = dialogView.findViewById(R.id.editResolution);

        editIssue.setText(issue.getIssue());
        editResolution.setText(issue.getResolution());

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Edit Issue")
                .setView(dialogView)
                .setPositiveButton("Save", (dialogInterface, i) -> {

                    String newIssue = editIssue.getText().toString().trim();
                    String newResolution = editResolution.getText().toString().trim();

                    if (newIssue.isEmpty() || newResolution.isEmpty()) {
                        Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String uid = auth.getCurrentUser().getUid();

                    db.collection("users")
                            .document(uid)
                            .collection("issues")
                            .document(issue.getId())
                            .update(
                                    "issue", newIssue,
                                    "resolution", newResolution
                            )
                            .addOnSuccessListener(aVoid -> {
                                issue.setIssue(newIssue);
                                issue.setResolution(newResolution);
                                notifyItemChanged(position);
                                Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show();
                            });

                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();
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
