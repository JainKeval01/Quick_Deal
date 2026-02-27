package com.example.quickdeal.model;

public class User {

    public String uid;
    public String username;
    public String email;
    public String phone;
    public String city;

    // Empty constructor REQUIRED
    public User() {
    }

    public User(String uid, String username, String email, String phone, String city) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.city = city;
    }
}
