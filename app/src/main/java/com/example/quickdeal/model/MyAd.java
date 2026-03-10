package com.example.quickdeal.model;

import java.util.List;

public class MyAd {

    public String title;
    public String description;
    public String location;
    public String price;
    public List<String> images;
    public String category;
    public String status;

    public MyAd(String title,
                String description,
                String location,
                String price,
                List<String> images,
                String category,
                String status) {

        this.title = title;
        this.description = description;
        this.location = location;
        this.price = price;
        this.images = images;
        this.category = category;
        this.status = status;
    }
}