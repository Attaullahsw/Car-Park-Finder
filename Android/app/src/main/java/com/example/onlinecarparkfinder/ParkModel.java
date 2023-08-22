package com.example.onlinecarparkfinder;

public class ParkModel {

    String id;
    String name;
    String no_of_slot;
    String available_slot;
    String price_per_hour;
    String contact;
    String email;
    String lat;
    String lon;

    public ParkModel(String id, String name, String no_of_slot, String available_slot, String price_per_hour, String contact, String email, String lat, String lon) {
        this.id = id;
        this.name = name;
        this.no_of_slot = no_of_slot;
        this.available_slot = available_slot;
        this.price_per_hour = price_per_hour;
        this.contact = contact;
        this.email = email;
        this.lat = lat;
        this.lon = lon;
    }
}
