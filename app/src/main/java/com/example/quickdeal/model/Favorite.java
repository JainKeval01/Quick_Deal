package com.example.quickdeal.model;

public class Favorite {
    public String favId;
    public String userId;
    public String productId;

    public Favorite() {
    }

    public Favorite(String favId, String userId, String productId) {
        this.favId = favId;
        this.userId = userId;
        this.productId = productId;
    }
}
