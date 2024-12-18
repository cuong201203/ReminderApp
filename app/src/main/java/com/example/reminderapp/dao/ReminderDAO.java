package com.example.reminderapp.dao;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.example.reminderapp.DBUtils.DatabaseUtils;
import com.example.reminderapp.entity.Reminder;
import com.example.reminderapp.entity.Notification;
import com.example.reminderapp.receiver.ReminderBroadcastReceiver;

import java.util.ArrayList;
import java.util.Calendar;

public class ReminderDAO {
    private DatabaseUtils dbUtils;
    private Context context;

    public ReminderDAO(Context context) {
        dbUtils = new DatabaseUtils(context);
        this.context = context;
    }

    // Lấy toàn bộ danh sách nhắc nhở
    public ArrayList<Reminder> getAllReminders() {
        ArrayList<Reminder> reminders = new ArrayList<>();
        SQLiteDatabase db = dbUtils.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Reminder", null);

        if (cursor.moveToFirst()) {
            do {
                Reminder reminder = new Reminder(
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getInt(5)
                );
                reminder.setId(cursor.getInt(0));
                reminders.add(reminder);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return reminders;
    }

    // Tìm kiếm nhắc nhở theo title
    public ArrayList<Reminder> searchReminder(String infor) {
        ArrayList<Reminder> reminderList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM Reminder WHERE Title LIKE ?";
            db = dbUtils.getReadableDatabase();
            cursor = db.rawQuery(query, new String[]{"%" + infor + "%"});

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Tạo đối tượng Reminder từ dữ liệu trong cursor
                    Reminder reminder = new Reminder(
                            cursor.getString(1), // Title
                            cursor.getString(2), // Description
                            cursor.getString(3), // Time
                            cursor.getString(4), // Date
                            cursor.getInt(5)     // categoryId
                    );
                    reminder.setId(cursor.getInt(0)); // ID
                    reminderList.add(reminder);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace(); // Ghi log lỗi
        } finally {
            // Đảm bảo đóng cursor và database để tránh rò rỉ tài nguyên
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return reminderList;
    }

    public void addReminder(Reminder reminder) {
        Log.d("ReminderDAO", "Starting addReminder method");

        SQLiteDatabase db = dbUtils.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Title", reminder.getTitle());
        values.put("Description", reminder.getDescription());
        values.put("Date", reminder.getDate());
        values.put("Time", reminder.getTime());
        values.put("CategoryID", reminder.getCategoryId());

        Log.d("ReminderDAO", "Inserting reminder: " + reminder.getTitle());
        long reminderId = db.insert("Reminder", null, values);
        // Cập nhật lại ID cho reminder
        reminder.setId((int) reminderId);

        //thêm vào bảng Notification
        values.clear();
        db.insert("Notification", null, values);
        db.close();

        if (reminderId == -1) {
            Log.e("ReminderDAO", "Failed to insert reminder");
            throw new RuntimeException("Failed to insert reminder");
        } else {
            Log.d("ReminderDAO", "Reminder inserted successfully with ID: " + reminderId);
        }
        // Lên lịch thông báo cho nhắc nhở mới
        Log.d("ReminderDAO", "Scheduling notification for reminder ID: " + reminderId);
        scheduleNotification(reminder);
    }
    public void updateReminder(Reminder reminder) {
        SQLiteDatabase db = dbUtils.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Title", reminder.getTitle());
        values.put("Description", reminder.getDescription());
        values.put("Date", reminder.getDate());
        values.put("Time", reminder.getTime());
        values.put("CategoryId", reminder.getCategoryId());
        db.update("Reminder", values, "ID = ?", new String[]{String.valueOf(reminder.getId())});

        //sửa bảng Notification
        values.clear();
        values.put("Title", reminder.getTitle());
        values.put("Content", reminder.getDescription());
        values.put("Date", reminder.getDate());
        values.put("Time", reminder.getTime());
        db.update("Notification", values, "ReminderID = ?", new String[]{String.valueOf(reminder.getId())});

        db.close();
    }

    public void deleteReminder(int id) {
        SQLiteDatabase db = dbUtils.getWritableDatabase();
        db.delete("Notification", "ReminderID = ?", new String[]{String.valueOf(id)});
        db.delete("Reminder", "ID = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public String getCategoryNameById(int categoryId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String categoryName = "";

        try {
            db = dbUtils.getReadableDatabase(); // Mở cơ sở dữ liệu
            cursor = db.rawQuery("SELECT Title FROM Category WHERE ID = ?", new String[]{String.valueOf(categoryId)});

            if (cursor.moveToFirst()) {
                categoryName = cursor.getString(0);
            } else {
                Log.e("Database", "No category found with ID: " + categoryId);
            }
        } catch (Exception e) {
            Log.e("DatabaseError", "Error fetching category name", e);
        } finally {
            if (cursor != null) {
                cursor.close(); // Đóng con trỏ
            }
            if (db != null) {
                db.close(); // Đóng cơ sở dữ liệu
            }
        }

        return categoryName;
    }

    // Xem nhắc nhở theo danh mục
    public ArrayList<Reminder> filterRemindersByCategory(int id) {
        ArrayList<Reminder> filteredReminders = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        if (id == 1) {
            return getAllReminders();// Mặc định thì trả về tất cả nhắc nhở
        } else {
            try {
                db = dbUtils.getReadableDatabase();
                String query = "SELECT * FROM Reminder WHERE CategoryID = ?";
                cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

                if (cursor.moveToFirst()) {
                    do {
                        Reminder reminder = new Reminder(
                                cursor.getString(1), // Title
                                cursor.getString(2), // Description
                                cursor.getString(3), // Time
                                cursor.getString(4), // Date
                                cursor.getInt(5)      // CategoryId
                        );
                        reminder.setId(cursor.getInt(0));
                        filteredReminders.add(reminder);
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                // Xử lý ngoại lệ ở đây
                Log.e("DatabaseError", "Error filtering reminders: " + e.getMessage());
            } finally {
                // Đóng cursor và database
                if (cursor != null) {
                    cursor.close();
                }
                if (db != null) {
                    db.close();
                }
            }

            return filteredReminders;
        }


    }


    public void scheduleNotification(Reminder reminder) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Kiểm tra quyền và yêu cầu nếu cần cho API 31 trở lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                context.startActivity(intent);
                return;
            }
        }

        Intent intent = new Intent(context, ReminderBroadcastReceiver.class);
        intent.putExtra("reminderTitle", reminder.getTitle());
        intent.putExtra("reminderDescription", reminder.getDescription());
        intent.putExtra("reminderId", reminder.getId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, reminder.getId(), intent, PendingIntent.FLAG_IMMUTABLE);

        // Kiểm tra độ dài chuỗi Date và Time trước khi sử dụng substring
        String date = reminder.getDate();
        String time = reminder.getTime();

        Log.d("scheduleNotification", "Date: " + date);
        Log.d("scheduleNotification", "Time: " + time);

        if (date.length() < 10 || time.length() < 5) {
            Log.e("ReminderDAO", "Invalid date or time format");
            throw new IllegalArgumentException("Invalid date or time format");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(date.substring(6, 10)));
        calendar.set(Calendar.MONTH, Integer.parseInt(date.substring(3, 5)) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.substring(0, 2)));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0, 2)));
        calendar.set(Calendar.MINUTE, Integer.parseInt(time.substring(3, 5)));
        calendar.set(Calendar.SECOND, 0);

        try {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } catch (SecurityException e) {
            e.printStackTrace();
            Log.e("ReminderDAO", "Failed to schedule notification", e);
        }
    }
}

