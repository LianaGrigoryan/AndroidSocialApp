package com.example.mypc.a8;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;


public class Notification extends FirebaseMessagingService {

    private NotificationCompat.Builder notification_builder;
    private String title, message, to;
    private Context context;
    private int notificationId=0;


    public Notification() {
    }

    public Notification(Context context) {
        this.context = context;
    }

        public void onMessageReceives(String name){

        sendNotification("NewsFeed", "You have new message from " + name);
    }
    private void sendNotification(String title, String message) {

        Intent open_activity_intent = new Intent(context, Choose_for_message.class);
        PendingIntent pending_intent = PendingIntent
                .getActivity(context, 0, open_activity_intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager notification_manager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String chanel_id = "3000";
            CharSequence name = "Channel Name";
            String description = "Chanel Description";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(chanel_id, name, importance);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.BLUE);
            notification_manager.createNotificationChannel(mChannel);
            notification_builder = new NotificationCompat.Builder(context, chanel_id);
        } else {
            notification_builder = new NotificationCompat.Builder(context);
        }
        notification_builder.setSmallIcon(R.drawable.message)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pending_intent);

        notification_manager.notify(notificationId++, notification_builder.build());
        if (notificationId == 50){
            notificationId = 0;
        }
    }
}
