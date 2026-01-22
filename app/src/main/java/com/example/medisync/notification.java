package com.example.medisync;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class notification extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        RecyclerView recycler = findViewById(R.id.notificationRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        NotificationDBHelper db = new NotificationDBHelper(this);
        List<NotificationModel> list = db.getAllNotifications();

        recycler.setAdapter(new NotificationAdapter(list));
    }


}
