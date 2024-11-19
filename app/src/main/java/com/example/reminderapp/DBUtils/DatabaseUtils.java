package com.example.reminderapp.DBUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseUtils extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ReminderApp.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseUtils(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createCategoryTable = "CREATE TABLE Category (ID INTEGER PRIMARY KEY AUTOINCREMENT, Title TEXT NOT NULL);";
        String createReminderTable = "CREATE TABLE Reminder (ID INTEGER PRIMARY KEY AUTOINCREMENT, Title TEXT NOT NULL, Description TEXT, Date TEXT, Time TEXT, CategoryID INTEGER, FOREIGN KEY (CategoryID) REFERENCES Category(ID));";
        String createNotificationTable = "CREATE TABLE Notification (ID INTEGER PRIMARY KEY AUTOINCREMENT, Title TEXT NOT NULL, Content TEXT, Date TEXT, Time TEXT, Status INTEGER, ReminderID INTEGER, FOREIGN KEY (ReminderID) REFERENCES Reminder(ID));";

        db.execSQL(createCategoryTable);
        db.execSQL(createReminderTable);
        db.execSQL(createNotificationTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Notification");
        db.execSQL("DROP TABLE IF EXISTS Reminder");
        db.execSQL("DROP TABLE IF EXISTS Category");
        onCreate(db);
    }

}
