package com.example.quickdeal.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Product implements Parcelable {
    public String name, location, price;
    public List<String> images; // Changed to a list for multiple images
    public boolean isFavorite = false;
    public String description;
    public String status; // e.g., "Available", "Sold"

    public Product(String name, String location, String price, List<String> images, String description, String status) {
        this.name = name;
        this.location = location;
        this.price = price;
        this.images = images;
        this.description = description;
        this.status = status;
    }

    protected Product(Parcel in) {
        name = in.readString();
        location = in.readString();
        price = in.readString();
        images = in.createStringArrayList();
        isFavorite = in.readByte() != 0;
        description = in.readString();
        status = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(location);
        dest.writeString(price);
        dest.writeStringList(images);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
        dest.writeString(description);
        dest.writeString(status);
    }
}
