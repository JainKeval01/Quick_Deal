package com.example.quickdeal.model;

import java.util.List;

public class MyAd {

    public String title;
    public String description;
    public String price;
    public List<String> images;
    public String category;
    public String status;

    public MyAd(String title,
                String description,
                String price,
                List<String> images,
                String category,
                String status) {

        this.title = title;
        this.description = description;
        this.price = price;
        this.images = images;
        this.category = category;
        this.status = status;
    }
}