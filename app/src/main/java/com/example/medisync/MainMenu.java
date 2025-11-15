package com.example.medisync;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.medisync.databinding.ActivityMainmenuBinding;

public class MainMenu extends AppCompatActivity {

    private ActivityMainmenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainmenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


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
            }
            return true;
        });


        ImageView addButton = findViewById(R.id.addissuebtn);
        addButton.setOnClickListener(v -> showAddIssueDialog());
    }

    private void showAddIssueDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_issue, null);
        builder.setView(dialogView);

        EditText editIssue = dialogView.findViewById(R.id.editIssue);
        EditText editResolution = dialogView.findViewById(R.id.editResolution);

        builder.setPositiveButton("Save", (DialogInterface dialog, int which) -> {
            String issue = editIssue.getText().toString().trim();
            String resolution = editResolution.getText().toString().trim();

            if (!issue.isEmpty() && !resolution.isEmpty()) {

                IssueStorage.getInstance().addIssue(new Issue(issue, resolution));
                Toast.makeText(this, "Issue added", Toast.LENGTH_SHORT).show();


                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
                if (currentFragment instanceof HistoryFragment) {
                    ((HistoryFragment) currentFragment).refreshList();
                }
            } else {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();
    }
}
