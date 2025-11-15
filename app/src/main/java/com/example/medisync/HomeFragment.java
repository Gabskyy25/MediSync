package com.example.medisync;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView alarmBtn = view.findViewById(R.id.alarm);
        ImageView contactBtn = view.findViewById(R.id.contact);
        ImageView scheduleBtn = view.findViewById(R.id.schedule);

        alarmBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), alarm.class);
            startActivity(intent);
        });

        contactBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), contactinfo.class);
            startActivity(intent);
        });

        scheduleBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), schedule.class);
            startActivity(intent);
        });
    }
}
