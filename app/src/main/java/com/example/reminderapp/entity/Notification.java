package com.example.reminderapp.entity;

public class Notification {
    private int id;
    private String title;
    private String content;
    private String date;
    private String time;
    private int status;
    private int reminderId;

    public Notification(int id, String title, String content, String date, String time, int status, int reminderId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.time = time;
        this.status = status;
        this.reminderId = reminderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReminderId() {
        return reminderId;
    }

    public void setReminderId(int reminderId) {
        this.reminderId = reminderId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
