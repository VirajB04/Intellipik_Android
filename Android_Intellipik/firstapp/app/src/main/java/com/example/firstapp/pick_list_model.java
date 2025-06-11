package com.example.firstapp;

public class pick_list_model {
    int id;
    String code;
    String name;
    int number;

    public pick_list_model(int id, String code, String name, int number) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.number = number;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }
}
