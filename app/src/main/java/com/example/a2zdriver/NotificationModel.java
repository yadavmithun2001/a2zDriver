package com.example.a2zdriver;

public class NotificationModel {
    String date,notification;

    public NotificationModel(String date, String notification) {
        this.date = date;
        this.notification = notification;
    }
    public NotificationModel(){

    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
