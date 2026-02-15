package com.example.quickdeal.model;

public class MyAd {
    public String title;
    public String description;
    public String location;
    public String price;
    public String image;
    public String status;      // "ACTIVE", "SOLD", "EXPIRED", "PENDING"

    public MyAd(String title, String description, String location, String price,
                String image, String status) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.price = price;
        this.image = image;
        this.status = status;
    }
}