package com.example.medisync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class alarmreceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String description = intent.getStringExtra("ALARM_DESCRIPTION");
        if (description == null) {
            description = "Alarm";
        }


        Intent i = new Intent(context, alarmring.class);
        i.putExtra("ALARM_DESCRIPTION", description);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}