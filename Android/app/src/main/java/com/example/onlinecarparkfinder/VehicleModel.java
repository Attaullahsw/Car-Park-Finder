package com.example.onlinecarparkfinder;

public class VehicleModel {

    private String id;
    private String name;
    private String number;

    public VehicleModel(String id, String name, String number) {
        this.id = id;
        this.name = name;
        this.number = number;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }
}
