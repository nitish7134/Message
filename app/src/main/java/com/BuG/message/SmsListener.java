package com.BuG.message;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class SmsListener extends BroadcastReceiver {

    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    public static final String KEY_NOTIFICATION_GROUP = "GRP";
    int i = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("LISTENER", "MSGDETECTED");
        Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();
                    showNotification(context, senderNum, message);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showNotification(Context context, String phoneNum, String message) {

        Intent buttonIntent = new Intent(context, ButtonReceiver.class);
        buttonIntent.putExtra("notificationId", i);
        PendingIntent cancelP = PendingIntent.getBroadcast(context, 0, buttonIntent, 0);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("New SMS from " + phoneNum)
                .setContentText(message)
                .setGroup(KEY_NOTIFICATION_GROUP)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_message_black_24dp)
                .addAction(R.drawable.ic_thumb_up_black_24dp, "Thumbs Up", cancelP)
                .addAction(R.drawable.ic_thumb_down_black_24dp, "Thumbs Down", cancelP);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context);
            notification.setChannelId(NOTIFICATION_CHANNEL_ID);
        }
        notificationManager.notify(i++, notification.build());
    }

    private void createNotificationChannel(Context context) {
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NewMessage", importance);
        channel.setDescription("Intents When you Receive a new SMS");
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

    }
}
