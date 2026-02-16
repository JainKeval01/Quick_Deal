package com.example.quickdeal.model;

public class Product {
    public String name,location,price,image;
    public boolean isFavorite = false; // Default to not favorite

    public Product(String name, String location, String price, String image) {
        this.name = name;
        this.location = location;
        this.price = price;
        this.image = image;
    }
}
