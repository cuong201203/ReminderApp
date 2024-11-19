package com.example.reminderapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.reminderapp.DBUtils.DatabaseUtils;
import com.example.reminderapp.entity.Category;
import com.example.reminderapp.entity.Reminder;

import java.util.ArrayList;

public class ReminderDAO {
    private DatabaseUtils dbUtils;
    public ReminderDAO(Context context)
    {
        dbUtils = new DatabaseUtils(context);
    }
    //Lấy toàn bộ danh sách nhắc nhở
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

    //Tìm kiếm nhắc nhở theo title
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
        SQLiteDatabase db = dbUtils.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Title", reminder.getTitle());
        values.put("Description", reminder.getDescription());
        values.put("Date", reminder.getDate());
        values.put("Time", reminder.getTime());
        values.put("CategoryID", reminder.getCategoryId());
        db.insert("Reminder", null, values);
        db.close();
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
        db.close();
    }
    public void deleteReminder(int id) {
        SQLiteDatabase db = dbUtils.getWritableDatabase();
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


}
