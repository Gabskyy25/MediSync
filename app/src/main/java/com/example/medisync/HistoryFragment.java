package com.example.medisync;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private IssueAdapter adapter;
    private DBHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.recyclerHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = DBHelper.getInstance(requireContext());

        Button btnClearAll = view.findViewById(R.id.btnClearAll);
        btnClearAll.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Clear All")
                    .setMessage("Are you sure you want to clear all history?")
                    .setPositiveButton("Yes", (d, w) -> {
                        dbHelper.clearAll();
                        refreshList();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        loadData();
        return view;
    }

    private void loadData() {
        List<Issue> issues = dbHelper.getAllIssues();
        adapter = new IssueAdapter(getContext(), issues, dbHelper, this::refreshList);
        recyclerView.setAdapter(adapter);
    }

    public void refreshList() {
        List<Issue> issues = dbHelper.getAllIssues();
        if (adapter == null) {
            adapter = new IssueAdapter(getContext(), issues, dbHelper, this::refreshList);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setData(issues);
        }
    }
}
