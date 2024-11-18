package com.example.reminderapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.reminderapp.DBUtils.DatabaseUtils;
import com.example.reminderapp.entity.Reminder;

import java.util.ArrayList;

public class ReminderDAO {
    private DatabaseUtils dbUtils;
    public ReminderDAO(Context context){dbUtils = new DatabaseUtils(context);}
    public ArrayList<Reminder> getAllReminders() {
        ArrayList<Reminder> reminderList = new ArrayList<>();
        SQLiteDatabase db = dbUtils.getReadableDatabase();
        String query = "SELECT * FROM Reminder";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("Title"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("Description"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("Date"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("Time"));
                int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow("CategoryId"));

                reminderList.add(new Reminder(id,title,description,date,time,categoryId));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return reminderList;
    }

    public String getCategoryNameById(int categoryId) {
        SQLiteDatabase db = dbUtils.getReadableDatabase();
        String categoryName = "";

        Cursor cursor = db.rawQuery("SELECT name FROM Category WHERE id = ?", new String[]{String.valueOf(categoryId)});
        if (cursor.moveToFirst()) {
            categoryName = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return categoryName;
    }
    public void addReminder(Reminder reminder) {
        SQLiteDatabase db = dbUtils.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Title", reminder.getTitle());
        values.put("Description", reminder.getDescription());
        values.put("Date", reminder.getDate());
        values.put("Time", reminder.getTime());
        values.put("CategoryId", reminder.getCategoryId());
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



}
