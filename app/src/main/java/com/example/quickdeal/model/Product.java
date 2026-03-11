package com.example.quickdeal.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Product implements Parcelable {
    private String id;
    public String name, price;
    public List<String> images;
    public boolean isFavorite = false;
    public String description;
    public String status; // e.g., "Available", "Sold"
    public String category;
    public String sellerId;
    public long timestamp;

    // Empty constructor required for Firebase
    public Product() {}

    public Product(String id, String name, String price, List<String> images, String description, String status, String category, String sellerId, long timestamp) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.images = images;
        this.description = description;
        this.status = status;
        this.category = category;
        this.sellerId = sellerId;
        this.timestamp = timestamp;
    }

    protected Product(Parcel in) {
        id = in.readString();
        name = in.readString();
        price = in.readString();
        images = in.createStringArrayList();
        isFavorite = in.readByte() != 0;
        description = in.readString();
        status = in.readString();
        category = in.readString();
        sellerId = in.readString();
        timestamp = in.readLong();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(price);
        dest.writeStringList(images);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
        dest.writeString(description);
        dest.writeString(status);
        dest.writeString(category);
        dest.writeString(sellerId);
        dest.writeLong(timestamp);
    }
}
