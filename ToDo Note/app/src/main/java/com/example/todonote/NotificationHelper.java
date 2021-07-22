package com.example.todonote;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

public class NotificationHelper extends BroadcastReceiver  {

    private String CHANNEL_NAME = "todo_note";
    private String CHANNEL_ID = "com.example.notification";
    private String description = "Channel for Todo App";
    // private NotificationManager mManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 1001, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        String body = intent.getStringExtra("message");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_message)
                .setContentTitle("ToDo Note")
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent1)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(200, builder.build());
    }
}
