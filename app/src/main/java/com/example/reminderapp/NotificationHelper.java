package com.example.reminderapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.reminderapp.receiver.ReminderBroadcastReceiver;

public class NotificationHelper {
    private static final String CHANNEL_ID = "reminder_channel";
    private static final String ACTION_SNOOZE = "com.example.reminderapp.ACTION_SNOOZE";
    private static final String ACTION_CONFIRM = "com.example.reminderapp.ACTION_CONFIRM";
    private Context mContext;

    public NotificationHelper(Context context) {
        mContext = context;
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Reminder Channel";
            String description = "Channel for reminder notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Log.d("NotificationHelper", "Notification channel created");
        }
    }

    public void sendNotification(String title, String message, int reminderId) {
        // Intent cho hành động nhắc lại
        Intent snoozeIntent = new Intent(mContext, ReminderBroadcastReceiver.class);
        snoozeIntent.setAction(ACTION_SNOOZE);
        snoozeIntent.putExtra("reminderId", reminderId);
        snoozeIntent.putExtra("reminderTitle", title);
        snoozeIntent.putExtra("reminderDescription", message);
        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(mContext, reminderId, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Intent cho hành động xác nhận (OK)
        Intent confirmIntent = new Intent(mContext, ReminderBroadcastReceiver.class);
        confirmIntent.setAction(ACTION_CONFIRM);
        confirmIntent.putExtra("reminderId", reminderId);
        confirmIntent.putExtra("reminderTitle", title);
        confirmIntent.putExtra("reminderDescription", message);
        PendingIntent confirmPendingIntent = PendingIntent.getBroadcast(mContext, reminderId, confirmIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)  // Đảm bảo bạn có một hình ảnh biểu tượng hợp lệ
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_launcher_foreground, "OK", confirmPendingIntent)// Thêm nút "OK"
                .addAction(R.drawable.ic_launcher_foreground, "Nhắc lại", snoozePendingIntent); // Thêm nút "Nhắc lại"

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(reminderId, builder.build());
        Log.d("NotificationHelper", "Notification sent: " + title);
    }
}

