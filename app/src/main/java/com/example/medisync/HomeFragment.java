package com.example.medisync;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView slider;
    private Handler handler = new Handler();
    private int position = 0;

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

        alarmBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(), alarm.class)));
        contactBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(), contactinfo.class)));
        scheduleBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(), schedule.class)));

        slider = view.findViewById(R.id.imageSlider);

        List<Integer> images = Arrays.asList(
                R.drawable.image1,
                R.drawable.image2,
                R.drawable.image3,
                R.drawable.image4,
                R.drawable.image5,
                R.drawable.image6
        );

        ImageSliderAdapter adapter = new ImageSliderAdapter(getContext(), images);
        slider.setAdapter(adapter);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        slider.setLayoutManager(layoutManager);

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(slider);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (position == images.size()) {
                    position = 0;
                }
                slider.smoothScrollToPosition(position);
                position++;
                handler.postDelayed(this, 3000);
            }
        }, 3000);
    }
}
