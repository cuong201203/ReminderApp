package com.example.reminderapp.entity;

import java.io.Serializable;

public class Reminder {
    private int id;
    private String title;
    private String description;
    private String date;
    private String time;
    private int categoryId;
    public Reminder(){

    }
    public Reminder(int id, String title, String description, String date, String time, int categoryId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.categoryId = categoryId;
    }
    public Reminder(String title, String description, String date, String time, int categoryId) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.categoryId = categoryId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}
