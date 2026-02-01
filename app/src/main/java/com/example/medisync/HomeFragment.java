package com.example.medisync;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    // ===== IMAGE SLIDER =====
    private RecyclerView slider;
    private Handler handler = new Handler();
    private int position = 1000;
    private LinearLayout indicatorLayout;
    private int imageCount;

    // ===== NOTIFICATIONS =====
    private RecyclerView notificationsRecycler;
    private NotificationAdapter notificationAdapter;
    private NotificationRepository notificationRepository;
    private ListenerRegistration notificationListener;

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        // ===== QUICK ACTIONS =====
        CardView alarmBtn = view.findViewById(R.id.alarm);
        CardView contactBtn = view.findViewById(R.id.contact);
        CardView scheduleBtn = view.findViewById(R.id.schedule);

        alarmBtn.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), alarm.class))
        );

        contactBtn.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), contactinfo.class))
        );

        scheduleBtn.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), schedule.class))
        );

        // ===== IMAGE SLIDER =====
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

        ImageSliderAdapter adapter =
                new ImageSliderAdapter(getContext(), images);

        slider.setAdapter(adapter);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(
                        getContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                );

        slider.setLayoutManager(layoutManager);

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(slider);

        setupIndicators(imageCount);
        selectIndicator(0);

        slider.scrollToPosition(position);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                position++;
                slider.smoothScrollToPosition(position);
                handler.postDelayed(this, 3000);
            }
        }, 3000);

        // ===== NOTIFICATIONS SECTION =====
        notificationsRecycler = view.findViewById(R.id.notificationsRecycler);
        notificationsRecycler.setLayoutManager(
                new LinearLayoutManager(getContext())
        );

        notificationRepository = new NotificationRepository();
        notificationAdapter = new NotificationAdapter(new ArrayList<>());
        notificationsRecycler.setAdapter(notificationAdapter);

        // 🔴 TEST WRITE — REMOVE AFTER CONFIRMING


        loadHomeNotifications();
    }

    // ===== LOAD NOTIFICATIONS =====
    private void loadHomeNotifications() {

        Query query = notificationRepository.getAllNotificationsQuery();
        if (query == null) return;

        notificationListener =
                query.limit(5)
                        .addSnapshotListener((QuerySnapshot snapshots,
                                              FirebaseFirestoreException e) -> {

                            if (e != null) {
                                e.printStackTrace();
                                return;
                            }

                            if (snapshots == null || snapshots.isEmpty()) {
                                notificationAdapter.updateList(new ArrayList<>());
                                return;
                            }

                            List<NotificationModel> list = new ArrayList<>();

                            for (DocumentSnapshot doc : snapshots.getDocuments()) {

                                NotificationModel model =
                                        doc.toObject(NotificationModel.class);

                                if (model != null) {
                                    model.setId(doc.getId());
                                    list.add(model);
                                }
                            }

                            notificationAdapter.updateList(list);
                        });
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (notificationListener != null) {
            notificationListener.remove();
        }
    }

    // ===== INDICATORS =====
    private void setupIndicators(int count) {
        indicatorLayout.removeAllViews();
        for (int i = 0; i < count; i++) {
            View dot = new View(getContext());
            dot.setBackgroundResource(R.drawable.indicator_inactive);
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(20, 20);
            params.setMargins(8, 0, 8, 0);
            indicatorLayout.addView(dot, params);
        }
    }

    private void selectIndicator(int index) {
        for (int i = 0; i < indicatorLayout.getChildCount(); i++) {
            View dot = indicatorLayout.getChildAt(i);
            dot.setBackgroundResource(
                    i == index
                            ? R.drawable.indicator_active
                            : R.drawable.indicator_inactive
            );
        }
    }
}
