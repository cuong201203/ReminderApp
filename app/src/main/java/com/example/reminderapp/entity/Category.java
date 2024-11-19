package com.example.reminderapp.entity;

import java.util.ArrayList;

public class Category {
    private int id;
    private String title;
    private ArrayList<Reminder> listReminder = null;
    // Dùng khi thêm dữ liệu mới vì id tự tăng
    public Category(String title) {
        this.title = title;
    }

    // Dùng khi lấy dữ liệu từ db hoặc trường hợp đặc biệt
    public Category(int id, String title) {
        this.id = id;
        this.title = title;
        listReminder = new ArrayList<Reminder>();
    }

    public boolean checkAdd(Reminder r){
        for (Reminder r1: listReminder) {
            if(r1.getId()==r.getId()) return true;
        }
        return false;
    }
    public boolean add(Reminder r){
        if(!checkAdd(r)){
            listReminder.add(r);
            return true;
        } return false;
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
    public ArrayList<Reminder> getListReminder(){
        return listReminder;
    }
    @Override
    public String toString() {
        return this.title;
    }
}
