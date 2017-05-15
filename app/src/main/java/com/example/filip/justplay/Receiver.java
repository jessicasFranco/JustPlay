package com.example.filip.justplay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Receiver", "Service Stops");
        context.startService(new Intent(context, SensorService.class));
    }
}
