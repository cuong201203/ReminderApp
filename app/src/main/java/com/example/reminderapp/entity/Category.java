package com.example.reminderapp.entity;

public class Category {
    private int id;
    private String title;

    // Dùng khi thêm dữ liệu mới vì id tự tăng
    public Category(String title) {
        this.title = title;
    }

    // Dùng khi lấy dữ liệu từ db hoặc trường hợp đặc biệt
    public Category(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

//    @Override
//    public String toString() {
//        return this.title;
//    }
}
