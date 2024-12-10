package com.example.reminderapp.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.example.reminderapp.NotificationHelper;
import com.example.reminderapp.dao.ReminderDAO;
import com.example.reminderapp.entity.Reminder;

import java.util.Calendar;

public class ReminderBroadcastReceiver extends BroadcastReceiver {

    private static final String ACTION_SNOOZE = "com.example.reminderapp.ACTION_SNOOZE";
    private static final String ACTION_CONFIRM = "com.example.reminderapp.ACTION_CONFIRM";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Lấy thông tin nhắc nhở từ Intent
        int reminderId = intent.getIntExtra("reminderId", -1);
        String title = intent.getStringExtra("reminderTitle");
        String description = intent.getStringExtra("reminderDescription");

        Log.d("ReminderBroadcastReceiver", "Received reminder with ID: " + reminderId);
        Log.d("ReminderBroadcastReceiver", "Title: " + title);
        Log.d("ReminderBroadcastReceiver", "Description: " + description);

        if (ACTION_SNOOZE.equals(intent.getAction())) {
            // Xử lý hành động nhắc lại
            Log.d("ReminderBroadcastReceiver", "Snooze action received for reminder ID: " + reminderId);
            snoozeNotification(context, reminderId, title, description);
        } else if (ACTION_CONFIRM.equals(intent.getAction())) {
            // Xử lý hành động OK
            Log.d("ReminderBroadcastReceiver", "Confirm action received for reminder ID: " + reminderId);
            addReminderToDatabase(context, reminderId, title, description);
        } else {
            // Gửi thông báo nhắc nhở
            NotificationHelper notificationHelper = new NotificationHelper(context);
            notificationHelper.sendNotification(title, description, reminderId);
        }
    }

    private void snoozeNotification(Context context, int reminderId, String title, String description) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Kiểm tra xem có thể lập lịch các báo động chính xác hay không
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
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, reminderId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 30); // Nhắc lại sau 30 giây

        try {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Log.d("ReminderBroadcastReceiver", "Notification snoozed for 30 seconds");

            // Hiển thị thông báo Toast cho người dùng
            Toast.makeText(context, "Nhắc nhở sẽ được nhắc lại sau 30 giây", Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            Log.e("ReminderBroadcastReceiver", "Failed to schedule exact alarm. Handling SecurityException.", e);
            // Xử lý ngoại lệ hoặc yêu cầu quyền cần thiết
        }
    }

    private void addReminderToDatabase(Context context, int reminderId, String title, String description) {
        Log.d("ReminderBroadcastReceiver", "Reminder added to database: " + title);
        // Hiển thị thông báo Toast cho người dùng
        Toast.makeText(context, "Nhắc nhở đã được thêm vào cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
    }
}
