package com.example.medisync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class alarmreceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context c, Intent i) {
        String desc = i.getStringExtra("DESC");
        Intent r = new Intent(c, alarmring.class);
        r.putExtra("DESC", desc);
        r.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        c.startActivity(r);
    }
}