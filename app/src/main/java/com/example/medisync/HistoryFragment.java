package com.example.medisync;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private IssueAdapter adapter;
    private DBHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.recyclerHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = DBHelper.getInstance(requireContext());

        List<Issue> issues = dbHelper.getAllIssues();
        adapter = new IssueAdapter(issues);
        recyclerView.setAdapter(adapter);

        return view;
    }

    public void refreshList() {
        List<Issue> issues = dbHelper.getAllIssues();
        adapter.setData(issues);
    }
}
