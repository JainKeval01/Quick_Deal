package com.example.quickdeal.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ReportedProduct implements Parcelable {

    private String id;
    public String name;
    public String reportedBy;
    public String timeAgo;
    public String imageUrl;
    public int reportCount;
    public String status; // Pending, Action Taken
    public boolean isHighPriority;

    // Firebase requires an empty constructor
    public ReportedProduct() {}

    public ReportedProduct(String id, String name, String reportedBy, String timeAgo, String imageUrl, int reportCount, String status, boolean isHighPriority) {
        this.id = id;
        this.name = name;
        this.reportedBy = reportedBy;
        this.timeAgo = timeAgo;
        this.imageUrl = imageUrl;
        this.reportCount = reportCount;
        this.status = status;
        this.isHighPriority = isHighPriority;
    }


    protected ReportedProduct(Parcel in) {
        id = in.readString();
        name = in.readString();
        reportedBy = in.readString();
        timeAgo = in.readString();
        imageUrl = in.readString();
        reportCount = in.readInt();
        status = in.readString();
        isHighPriority = in.readByte() != 0;
    }

    public static final Creator<ReportedProduct> CREATOR = new Creator<ReportedProduct>() {
        @Override
        public ReportedProduct createFromParcel(Parcel in) {
            return new ReportedProduct(in);
        }

        @Override
        public ReportedProduct[] newArray(int size) {
            return new ReportedProduct[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getSellerName() {
        return reportedBy;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(reportedBy);
        dest.writeString(timeAgo);
        dest.writeString(imageUrl);
        dest.writeInt(reportCount);
        dest.writeString(status);
        dest.writeByte((byte) (isHighPriority ? 1 : 0));
    }
}
