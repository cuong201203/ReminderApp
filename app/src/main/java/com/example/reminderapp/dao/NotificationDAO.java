package com.example.reminderapp.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.reminderapp.DBUtils.DatabaseUtils;
import com.example.reminderapp.entity.Notification;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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

    public void deleteAllNotifications() {
        SQLiteDatabase db = this.dbUtils.getWritableDatabase();
        db.execSQL("DELETE FROM Notification"); // Xóa toàn bộ dữ liệu trong bảng
        db.close();
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

    public ArrayList<Notification> getNotificationsByDate() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH trả về từ 0-11
        int year = calendar.get(Calendar.YEAR);

        String currentDate = String.format("%02d/%02d/%04d", day, month, year);

        SQLiteDatabase db = this.dbUtils.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Notification WHERE date = ?", new String[]{currentDate});
        ArrayList<Notification> notifications = new ArrayList<>();
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
                notifications.add(notification);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return notifications;
    }

    public ArrayList<Notification> getNotificationsByWeek() {
        ArrayList<Notification> notifications = new ArrayList<>();

        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        int currentWeek = currentCalendar.get(Calendar.WEEK_OF_YEAR);
        int currentYear = currentCalendar.get(Calendar.YEAR);

        SQLiteDatabase db = this.dbUtils.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Notification", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String dateText = cursor.getString(3);
                String[] dateParts = dateText.split("/");
                if (dateParts.length == 3) {
                    int day = Integer.parseInt(dateParts[0]);
                    int month = Integer.parseInt(dateParts[1]) - 1; // Tháng trong Calendar bắt đầu từ 0
                    int year = Integer.parseInt(dateParts[2]);

                    Calendar dateCalendar = Calendar.getInstance();
                    dateCalendar.setFirstDayOfWeek(Calendar.MONDAY);
                    dateCalendar.set(year, month, day);

                    if (dateCalendar.get(Calendar.WEEK_OF_YEAR) == currentWeek &&
                            dateCalendar.get(Calendar.YEAR) == currentYear) {
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
                    }
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return notifications;
    }

    public ArrayList<Notification> getNotificationsByMonth() {
        ArrayList<Notification> notifications = new ArrayList<>();

        // Lấy tháng và năm hiện tại
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH từ 0-11
        int currentYear = calendar.get(Calendar.YEAR);
        String monthYear = String.format("%02d/%04d", currentMonth, currentYear);

        SQLiteDatabase db = this.dbUtils.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Notification WHERE date LIKE ?", new String[]{"%/" + monthYear});
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
                notifications.add(notification);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return notifications;
    }
}
