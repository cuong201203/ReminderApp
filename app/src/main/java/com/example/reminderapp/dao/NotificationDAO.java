package com.example.reminderapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.reminderapp.DBUtils.DatabaseUtils;
import com.example.reminderapp.entity.Notification;

import java.util.ArrayList;
public class NotificationDAO {
    private DatabaseUtils dbUtils;

    public NotificationDAO(Context context) {
        dbUtils = new DatabaseUtils(context);
    }

    //Lấy toàn bộ danh sách thông báo
    public ArrayList<Notification> getAllNotifications() {
        ArrayList<Notification> notifications = new ArrayList<>();
        SQLiteDatabase db = dbUtils.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Notification", null);

        if (cursor.moveToFirst()) {
            do {
                Notification notification = new Notification(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getInt(5),
                        cursor.getInt(6)
                );

                notifications.add(notification);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return notifications;
    }
    public void addNotification(Notification notification) {
        SQLiteDatabase db = dbUtils.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Title", notification.getTitle());
        values.put("Content", notification.getContent());
        values.put("Date", notification.getDate());
        values.put("Time", notification.getTime()); values.put("Status", notification.getStatus());
        values.put("ReminderID", notification.getReminderId());
        long result = db.insert("Notification", null, values);
        db.close();
        if (result == -1) {
            Log.e("DatabaseError", "Failed to insert notification");
        }
    }
    public void deleteNotification(int id) {
        SQLiteDatabase db = dbUtils.getWritableDatabase();
        db.delete("Notification", "ID = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public ArrayList<Notification> searchNotification(String infor) {
        ArrayList<Notification> notificationList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM Notification WHERE Title LIKE ?";
            db = dbUtils.getReadableDatabase();
            cursor = db.rawQuery(query, new String[]{"%" + infor + "%"});

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Notification notification = new Notification(
                            cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getInt(5),
                            cursor.getInt(6)
                    );
                    notificationList.add(notification);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace(); // Ghi log lỗi
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return notificationList;
    }

    public String getReminderTitleById(int reminderId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String reminderTitle = "";

        try {
            db = dbUtils.getReadableDatabase(); // Mở cơ sở dữ liệu
            cursor = db.rawQuery("SELECT Title FROM Reminder WHERE ID = ?", new String[]{String.valueOf(reminderId)});

            if (cursor.moveToFirst()) {
                reminderTitle = cursor.getString(0);
            } else {
                Log.e("Database", "No reminder found with ID: " + reminderId);
            }
        } catch (Exception e) {
            Log.e("DatabaseError", "Error fetching reminder title", e);
        } finally {
            if (cursor != null) {
                cursor.close(); // Đóng con trỏ
            }
            if (db != null) {
                db.close(); // Đóng cơ sở dữ liệu
            }
        }

        return reminderTitle;
    }
}
