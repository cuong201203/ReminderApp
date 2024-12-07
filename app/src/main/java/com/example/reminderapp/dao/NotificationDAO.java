package com.example.reminderapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.reminderapp.DBUtils.DatabaseUtils;
import com.example.reminderapp.entity.Notification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    public ArrayList<Notification> getNotificationsByDate() {
        // Lấy ngày hôm nay dưới dạng "dd-MM-yyyy"
        String currentDate = new SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(new Date());

        SQLiteDatabase db = this.dbUtils.getReadableDatabase();

        // Truy vấn thông báo có ngày bằng ngày hôm nay
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

        // Cấu hình Calendar với đầu tuần là Thứ Hai
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setFirstDayOfWeek(Calendar.MONDAY);

        // Tính tuần hiện tại và năm hiện tại
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        int currentYear = calendar.get(Calendar.YEAR);

        SQLiteDatabase db = this.dbUtils.getReadableDatabase();

        // Truy vấn tất cả các thông báo
        Cursor cursor = db.rawQuery("SELECT * FROM Notification", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Lấy giá trị Date từ cursor
                String dateText = cursor.getString(3); // Cột Date

                // Phân tích Date (định dạng d/M/yyyy)
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
                    Date date = dateFormat.parse(dateText);

                    // Tính tuần và năm của giá trị Date
                    Calendar dateCalendar = Calendar.getInstance(Locale.getDefault());
                    dateCalendar.setFirstDayOfWeek(Calendar.MONDAY);
                    dateCalendar.setTime(date);
                    int weekOfYear = dateCalendar.get(Calendar.WEEK_OF_YEAR);
                    int year = dateCalendar.get(Calendar.YEAR);

                    // So sánh tuần và năm
                    if (weekOfYear == currentWeek && year == currentYear) {
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
                } catch (ParseException e) {
                    Log.e("ParseError", "Invalid date format: " + dateText);
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
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH bắt đầu từ 0
        int currentYear = calendar.get(Calendar.YEAR);

        String monthYear = String.format("%d/%04d", currentMonth, currentYear);

        // Truy vấn SQLite
        SQLiteDatabase db = this.dbUtils.getReadableDatabase();

        // Câu truy vấn sử dụng LIKE để so sánh tháng và năm
        String query = "SELECT * FROM Notification WHERE date LIKE ?";
        Cursor cursor = db.rawQuery(query, new String[]{ "%" + monthYear });

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

        public void insertNotifications() {
            SQLiteDatabase db = this.dbUtils.getWritableDatabase();
            db.beginTransaction(); // Bắt đầu transaction để tăng hiệu suất
    
            try {
                // Thêm dữ liệu mẫu vào bảng Notification
                ContentValues values = new ContentValues();
    
                values.put("title", "1. Meeting");
                values.put("content", "Discuss project progress");
                values.put("date", "6/12/2024");
                values.put("time", "10:00");
                values.put("status", 1);
                values.put("reminderId", 101);
                db.insert("Notification", null, values);
    
                values.clear();
                values.put("Title", "2. Deadlinessss");
                values.put("content", "Submit final report");
                values.put("date", "30/11/2024");
                values.put("time", "15:00");
                values.put("status", 0);
                values.put("reminderId", 102);
                db.insert("Notification", null, values);
    
                values.clear();
                values.put("title", "3. Appointment");
                values.put("content", "Doctor appointment");
                values.put("date", "1/12/2024");
                values.put("time", "09:30");
                values.put("status", 1);
                values.put("reminderId", 103);
                db.insert("Notification", null, values);
    
                values.clear();
                values.put("title", "4. Workout");
                values.put("content", "Morning workout session");
                values.put("date", "6/12/2024");
                values.put("time", "06:30");
                values.put("status", 0);
                values.put("reminderId", 104);
                db.insert("Notification", null, values);
    
                values.clear();
                values.put("title", "5. Learn");
                values.put("content", "Learning IT");
                values.put("date", "2/12/2024");
                values.put("time", "06:30");
                values.put("status", 0);
                values.put("reminderId", 104);
                db.insert("Notification", null, values);
    
                values.clear();
                values.put("title", "6. Learn");
                values.put("content", "Learning Economy");
                values.put("date", "8/12/2024");
                values.put("time", "06:30");
                values.put("status", 0);
                values.put("reminderId", 104);
                db.insert("Notification", null, values);
    
                values.put("title", "7. Meeting");
                values.put("content", "Discuss project progress");
                values.put("date", "6/11/2024");
                values.put("time", "10:00");
                values.put("status", 1);
                values.put("reminderId", 101);
                db.insert("Notification", null, values);
    
                values.clear();
                values.put("title", "8. Deadline");
                values.put("content", "Submit final report");
                values.put("date", "31/12/2024");
                values.put("time", "15:00");
                values.put("status", 0);
                values.put("reminderId", 102);
                db.insert("Notification", null, values);
    
                db.setTransactionSuccessful(); // Xác nhận thành công transaction
            } catch (Exception e) {
                e.printStackTrace(); // Ghi lại lỗi nếu có
            } finally {
                db.endTransaction(); // Kết thúc transaction
                db.close(); // Đóng cơ sở dữ liệu
            }
    }
}
