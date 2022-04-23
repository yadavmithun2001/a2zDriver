package com.example.a2zdriver;

public class ComplaintModel {
    String date,compalaint,status;

    public ComplaintModel(String date, String compalaint, String status) {
        this.date = date;
        this.compalaint = compalaint;
        this.status = status;
    }
    public ComplaintModel(){

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCompalaint() {
        return compalaint;
    }

    public void setCompalaint(String compalaint) {
        this.compalaint = compalaint;
    }
}
