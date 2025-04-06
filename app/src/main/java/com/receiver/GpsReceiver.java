package com.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.strings.ActionCollections;

public class GpsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().matches("android.location.PROVIDERS_CHANGED")){
            Intent intent1 = new Intent();
            intent1.setAction(ActionCollections.GPS_STATUS);
            context.sendBroadcast(intent1);
        }
    }
}
