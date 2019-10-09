package com.BuG.message;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class BGService extends Service {

    private SmsListener messReceiver = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("SERVICE", "STARTED");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        messReceiver = new SmsListener();
        this.registerReceiver(messReceiver, intentFilter);
        Log.d("FA", "receiver registered");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (messReceiver != null) {
            Log.d("SERVICE", "STOPPED");
            unregisterReceiver(messReceiver);
        }
    }
}
