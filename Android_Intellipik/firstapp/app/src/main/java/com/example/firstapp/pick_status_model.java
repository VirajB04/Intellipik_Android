package com.example.firstapp;

public class pick_status_model {
    public boolean isSelected = false;
    public boolean isModified = false;
    int id;
    int code;
    String date;
    String name;
    int number_of_kits;
    int kits_picked;
    String status;

    public pick_status_model(int id, int code, String date, String name, int number_of_kits, int kits_picked, String status) {
        this.id = id;
        this.code = code;
        this.date = date;
        this.name = name;
        this.number_of_kits = number_of_kits;
        this.kits_picked = kits_picked;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public int getCode() {
        return code;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public int getNumber_of_kits() {
        return number_of_kits;
    }

    public int getKits_picked() {
        return kits_picked;
    }

    public String getStatus() {
        return status;
    }
}
