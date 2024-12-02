package com.example.reminderapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.example.reminderapp.DBUtils.DatabaseUtils;
import com.example.reminderapp.entity.Category;

import java.util.ArrayList;

public class CategoryDAO {
    private DatabaseUtils dbUtils;

    public CategoryDAO(Context context) {
        dbUtils = new DatabaseUtils(context);
    }

    public ArrayList<Category> getAllCategories() {
        SQLiteDatabase db = dbUtils.getReadableDatabase();
        ArrayList<Category> categories = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM Category", null);
        if (cursor.moveToFirst()) {
            do {
                categories.add(new Category(cursor.getInt(0), cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return categories;
    }

    public int getTotalReminders(int categoryId) {
        int totalReminders = 0;
        SQLiteDatabase db = dbUtils.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM Reminder WHERE CategoryID = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(categoryId)});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                totalReminders = cursor.getInt(0);
            }
            cursor.close();
        }

        db.close();
        return totalReminders;
    }

    public ArrayList<Category> searchCategory(String infor) {
        ArrayList<Category> categoryList = new ArrayList<>();
        String query = "SELECT * FROM Category WHERE title LIKE \'%" + infor + "%\'";
        SQLiteDatabase db = dbUtils.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            Category category = new Category(cursor.getInt(0), cursor.getString(1));
            categoryList.add(category);
            cursor.moveToNext();
        }
        return categoryList;
    }

    // Kiểm tra tên danh mục đã tồn tại hay chưa - áp dụng cho thêm, sửa
    public boolean isCategoryTitleExists(String title, Integer excludeId) {
        SQLiteDatabase db = dbUtils.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM Category WHERE title = ?";

        // Nếu excludeId khác null, thêm điều kiện lọc để loại trừ danh mục hiện tại khi đang sửa
        if (excludeId != null)
            query += " AND id != ?";

        Cursor cursor;
        if (excludeId != null)
            cursor = db.rawQuery(query, new String[]{title, String.valueOf(excludeId)});
        else
            cursor = db.rawQuery(query, new String[]{title});

        boolean exists = false;
        if (cursor.moveToFirst())
            exists = cursor.getInt(0) > 0;
        cursor.close();
        return exists;
    }

    public void addDefaultCategory() {
        SQLiteDatabase db = dbUtils.getWritableDatabase();
        // Kiểm tra xem danh mục "Mặc định" đã tồn tại chưa
        Cursor cursor = db.rawQuery("SELECT * FROM Category WHERE title = ?", new String[]{"Mặc định"});
        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put("Title", "Mặc định");
            db.insert("Category", null, values);
        }
        cursor.close();
        db.close();
    }

    public void addCategory(Category category) {
        SQLiteDatabase db = dbUtils.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Title", category.getTitle());
        db.insert("Category", null, values);
        db.close();
    }

    public void updateCategory(Category category) {
        SQLiteDatabase db = dbUtils.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Title", category.getTitle());
        db.update("Category", values, "ID = ?", new String[]{String.valueOf(category.getId())});
        db.close();
    }

    public void deleteCategory(int id) {
        SQLiteDatabase db = dbUtils.getWritableDatabase();
        db.delete("Reminder", "CategoryID = ?", new String[]{String.valueOf(id)});
        db.delete("Category", "ID = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public ArrayList<String> getRemindersByCategory(Integer categoryId) {
        ArrayList<String> reminders = new ArrayList<>();
        SQLiteDatabase db = dbUtils.getReadableDatabase();
        String query = "SELECT title, description, date, time FROM Reminder WHERE CategoryID = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(categoryId)});

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(0);
                String description = cursor.getString(1);
                String date = cursor.getString(2);
                String time = cursor.getString(3);

                String reminderInfo = "Tên nhắc nhở: " + title + "\n" +
                        ((!TextUtils.isEmpty(description)) ? ("Mô tả: " + description + "\n") : "") +
                        "Ngày: " + date + "\n" +
                        "Giờ: " + time;

                reminders.add(reminderInfo);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return reminders;
    }

    public int getCategoryIdByTitle(String title) {
        int categoryId = -1; // Giá trị mặc định nếu không tìm thấy

        SQLiteDatabase db = dbUtils.getReadableDatabase(); // Lấy kết nối đến cơ sở dữ liệu để đọc
        String query = "SELECT id FROM Category WHERE title = ?"; // Truy vấn để tìm ID của danh mục dựa trên tiêu đề
        Cursor cursor = db.rawQuery(query, new String[]{title}); // Thực hiện truy vấn và lấy kết quả

        if (cursor.moveToFirst()) { // Nếu tìm thấy kết quả
            categoryId = cursor.getInt(0); // Lấy giá trị ID từ kết quả
        }

        cursor.close(); // Đóng kết quả truy vấn
        db.close(); // Đóng kết nối đến cơ sở dữ liệu

        return categoryId; // Trả về ID của danh mục, hoặc -1 nếu không tìm thấy
    }

}
