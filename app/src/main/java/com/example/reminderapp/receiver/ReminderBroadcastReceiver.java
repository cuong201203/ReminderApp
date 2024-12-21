package com.example.reminderapp.receiver;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.example.reminderapp.NotificationHelper;
import com.example.reminderapp.dao.NotificationDAO;
import com.example.reminderapp.dao.ReminderDAO;  // Import ReminderDAO
import com.example.reminderapp.entity.Notification;

import java.util.Calendar;

public class ReminderBroadcastReceiver extends BroadcastReceiver {

    private static final String ACTION_SNOOZE = "com.example.reminderapp.ACTION_SNOOZE";
    private static final String ACTION_CONFIRM = "com.example.reminderapp.ACTION_CONFIRM";

    @Override
    public void onReceive(Context context, Intent intent) {
        int reminderId = intent.getIntExtra("reminderId", -1);
        String title = intent.getStringExtra("reminderTitle");
        String description = intent.getStringExtra("reminderDescription");
        boolean isSnooze = intent.getBooleanExtra("isSnooze", false);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Log.d("ReminderBroadcastReceiver", "Received reminder with ID: " + reminderId);
        Log.d("ReminderBroadcastReceiver", "Title: " + title);
        Log.d("ReminderBroadcastReceiver", "Description: " + description);

        if (ACTION_SNOOZE.equals(intent.getAction())) {
            Log.d("ReminderBroadcastReceiver", "Snooze action received for reminder ID: " + reminderId);
            snoozeNotification(context, reminderId, title, description);
            notificationManager.cancel(reminderId);
        } else if (ACTION_CONFIRM.equals(intent.getAction())) {
            Log.d("ReminderBroadcastReceiver", "Confirm action received for reminder ID: " + reminderId);
            notificationManager.cancel(reminderId);
            deleteReminder(context, reminderId);
            Toast.makeText(context, "Thông báo đã được xác nhận và nhắc nhở đã bị xóa", Toast.LENGTH_SHORT).show();
        } else {
            NotificationHelper notificationHelper = new NotificationHelper(context);
            notificationHelper.sendNotification(title, description, reminderId);
            int status = isSnooze ? 1 : 0;
            addNotificationToDatabase(context, reminderId, title, description, status);
        }
    }

    private void snoozeNotification(Context context, int reminderId, String title, String description) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.e("ReminderBroadcastReceiver", "Cannot schedule exact alarms. Requesting permission.");
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                context.startActivity(intent);
                return;
            }
        }

        Intent intent = new Intent(context, ReminderBroadcastReceiver.class);
        intent.putExtra("reminderTitle", title);
        intent.putExtra("reminderDescription", description);
        intent.putExtra("reminderId", reminderId);
        intent.putExtra("isSnooze", true);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, reminderId, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 1); // Thêm thời gian nhắc lại là 1 phút

        try {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Log.d("ReminderBroadcastReceiver", "Notification snoozed for 1 minute");
            Toast.makeText(context, "Nhắc nhở sẽ được nhắc lại sau 1 phút", Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            Log.e("ReminderBroadcastReceiver", "Failed to schedule exact alarm. Handling SecurityException.", e);
        }
    }

    private void addNotificationToDatabase(Context context, int reminderId, String title, String description, int status) {
        String date = getCurrentDate();
        String time = getCurrentTime();

        NotificationDAO notificationDAO = new NotificationDAO(context);
        Notification notification = new Notification(reminderId, title, description, date, time, status, reminderId);
        notificationDAO.addNotification(notification);
        Log.d("ReminderBroadcastReceiver", "Notification added to database: " + title + " with status: " + status);
    }

    private void deleteReminder(Context context, int reminderId) {
        ReminderDAO reminderDAO = new ReminderDAO(context);
        reminderDAO.deleteReminder(reminderId);
        Log.d("ReminderBroadcastReceiver", "Reminder deleted from database with ID: " + reminderId);
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        return String.format("%02d/%02d/%04d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
    }

    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        return String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
    }
}
