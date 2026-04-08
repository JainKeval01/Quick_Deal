package com.example.quickdeal.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ReportedProduct implements Parcelable {

    public String reportId;
    public String productId;
    public String productName;
    public String reporterId;
    public String reporterName;
    public String reason;
    public String timeAgo;
    public String imageUrl;
    public int reportCount;
    public String status; // Pending, Action Taken
    public boolean isHighPriority;

    // Firebase requires an empty constructor
    public ReportedProduct() {}

    public ReportedProduct(String reportId, String productId, String productName, String reporterId, String reporterName, String reason, String timeAgo, String imageUrl, int reportCount, String status, boolean isHighPriority) {
        this.reportId = reportId;
        this.productId = productId;
        this.productName = productName;
        this.reporterId = reporterId;
        this.reporterName = reporterName;
        this.reason = reason;
        this.timeAgo = timeAgo;
        this.imageUrl = imageUrl;
        this.reportCount = reportCount;
        this.status = status;
        this.isHighPriority = isHighPriority;
    }

    protected ReportedProduct(Parcel in) {
        reportId = in.readString();
        productId = in.readString();
        productName = in.readString();
        reporterId = in.readString();
        reporterName = in.readString();
        reason = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(reportId);
        dest.writeString(productId);
        dest.writeString(productName);
        dest.writeString(reporterId);
        dest.writeString(reporterName);
        dest.writeString(reason);
        dest.writeString(timeAgo);
        dest.writeString(imageUrl);
        dest.writeInt(reportCount);
        dest.writeString(status);
        dest.writeByte((byte) (isHighPriority ? 1 : 0));
    }
}
