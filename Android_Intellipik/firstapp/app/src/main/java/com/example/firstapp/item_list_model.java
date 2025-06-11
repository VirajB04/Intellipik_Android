package com.example.firstapp;

public class item_list_model {
    int id;
    String code;
    String name;

    public item_list_model(int id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
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
}
