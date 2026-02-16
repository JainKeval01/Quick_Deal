package com.example.quickdeal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quickdeal.R;
import com.example.quickdeal.model.MyAd;

import java.util.List;

public class MyAdsAdapter extends RecyclerView.Adapter<MyAdsAdapter.ViewHolder> {

    private List<MyAd> ads;

    public MyAdsAdapter(List<MyAd> ads) {
        this.ads = ads;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_ad_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyAd ad = ads.get(position);
        holder.bind(ad);
    }

    @Override
    public int getItemCount() {
        return ads.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAdImage;
        TextView tvAdTitle, tvAdDescription, tvAdLocation, tvAdPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAdImage = itemView.findViewById(R.id.ivAdImage);
            tvAdTitle = itemView.findViewById(R.id.tvAdTitle);
            tvAdDescription = itemView.findViewById(R.id.tvAdDescription);
            tvAdLocation = itemView.findViewById(R.id.tvAdLocation);
            tvAdPrice = itemView.findViewById(R.id.tvAdPrice);
        }

        public void bind(MyAd ad) {
            tvAdTitle.setText(ad.title);
            tvAdDescription.setText(ad.description);
            tvAdLocation.setText(ad.location);
            tvAdPrice.setText(ad.price);

            Glide.with(ivAdImage.getContext())
                    .load(ad.image)
                    .placeholder(R.drawable.ic_image)
                    .error(R.drawable.ic_image)
                    .into(ivAdImage);
        }
    }
}