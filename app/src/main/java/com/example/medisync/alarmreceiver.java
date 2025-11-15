package com.example.medisync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class alarmreceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // When alarm triggers, open the ringing activity
        Intent i = new Intent(context, alarmring.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
