package com.example.medisync;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private IssueAdapter adapter;
    private IssueRepository repository;

    private final ArrayList<Issue> issueList = new ArrayList<>();
    private ListenerRegistration issueListener; // 🔥 realtime listener

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.recyclerHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        repository = new IssueRepository();

        adapter = new IssueAdapter(
                requireContext(),
                issueList,
                repository,
                () -> {} // no manual reload needed anymore
        );

        recyclerView.setAdapter(adapter);

        return view;
    }

    /* ================= REALTIME LISTEN ================= */

    @Override
    public void onStart() {
        super.onStart();

        issueListener = repository.listenToIssues(list -> {
            issueList.clear();
            issueList.addAll(list);
            adapter.notifyDataSetChanged();
        });
    }

    /* ================= CLEANUP ================= */

    @Override
    public void onStop() {
        super.onStop();
        if (issueListener != null) {
            issueListener.remove();
        }
    }
}
