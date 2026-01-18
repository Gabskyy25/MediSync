package com.example.medisync;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.cardview.widget.CardView;


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
    private int position = 1000;
    private LinearLayout indicatorLayout;
    private int imageCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CardView alarmBtn = view.findViewById(R.id.alarm);
        CardView contactBtn = view.findViewById(R.id.contact);
        CardView scheduleBtn = view.findViewById(R.id.schedule);


        alarmBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(), alarm.class)));
        contactBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(), contactinfo.class)));
        scheduleBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(), schedule.class)));

        slider = view.findViewById(R.id.imageSlider);
        indicatorLayout = view.findViewById(R.id.indicatorLayout);

        List<Integer> images = Arrays.asList(
                R.drawable.image1,
                R.drawable.image2,
                R.drawable.image3,
                R.drawable.image4,
                R.drawable.image5,
                R.drawable.image6
        );

        imageCount = images.size();

        ImageSliderAdapter adapter = new ImageSliderAdapter(getContext(), images);
        slider.setAdapter(adapter);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        slider.setLayoutManager(layoutManager);

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(slider);

        setupIndicators(imageCount);
        selectIndicator(0);

        slider.scrollToPosition(position);

        slider.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int realPos = ((LinearLayoutManager) slider.getLayoutManager())
                            .findFirstVisibleItemPosition() % imageCount;
                    selectIndicator(realPos);
                }
            }
        });

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                position++;
                slider.smoothScrollToPosition(position);
                handler.postDelayed(this, 3000);
            }
        }, 3000);
    }

    private void setupIndicators(int count) {
        indicatorLayout.removeAllViews();
        for (int i = 0; i < count; i++) {
            View dot = new View(getContext());
            dot.setBackgroundResource(R.drawable.indicator_inactive);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
            params.setMargins(8, 0, 8, 0);
            indicatorLayout.addView(dot, params);
        }
    }

    private void selectIndicator(int index) {
        for (int i = 0; i < indicatorLayout.getChildCount(); i++) {
            View dot = indicatorLayout.getChildAt(i);
            if (i == index) dot.setBackgroundResource(R.drawable.indicator_active);
            else dot.setBackgroundResource(R.drawable.indicator_inactive);
        }
    }
}
