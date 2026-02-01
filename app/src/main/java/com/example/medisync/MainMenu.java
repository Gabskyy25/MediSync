package com.example.medisync;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.medisync.databinding.ActivityMainmenuBinding;

public class MainMenu extends AppCompatActivity {

    private ActivityMainmenuBinding binding;
    private IssueRepository issueRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainmenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        issueRepository = new IssueRepository();

        replaceFragment(new HomeFragment());

        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (id == R.id.history) {
                replaceFragment(new HistoryFragment());
            } else if (id == R.id.account) {
                replaceFragment(new AccountFragment());
            } else if (id == R.id.info) {
                replaceFragment(new info());
            }
            return true;
        });

        ImageView addButton = findViewById(R.id.addissuebtn);
        addButton.setOnClickListener(v -> showAddIssueDialog());


    }

    /* ================= ADD ISSUE ================= */

    private void showAddIssueDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_add_issue, null);
        builder.setView(dialogView);

        EditText editIssue = dialogView.findViewById(R.id.editIssue);
        EditText editResolution = dialogView.findViewById(R.id.editResolution);

        builder.setPositiveButton("Save", (dialog, which) -> {

            String issueText = editIssue.getText().toString().trim();
            String resolutionText = editResolution.getText().toString().trim();

            if (issueText.isEmpty() || resolutionText.isEmpty()) {
                Toast.makeText(this,
                        "Fill all fields",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Issue issue = new Issue(
                    issueText,
                    resolutionText,
                    System.currentTimeMillis()
            );

            issueRepository.addIssue(issue);

            Toast.makeText(this,
                    "Issue added",
                    Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel",
                (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    /* ================= NAV ================= */

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right,
                        android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right
                )
                .replace(R.id.frame_layout, fragment)
                .commit();
    }
}
