package com.example.quickdeal.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quickdeal.R;
import com.example.quickdeal.activities.EditItem;
import com.example.quickdeal.model.MyAd;

import java.util.ArrayList;
import java.util.List;

public class MyAdsAdapter extends RecyclerView.Adapter<MyAdsAdapter.ViewHolder> {

    private List<MyAd> ads;

    public MyAdsAdapter(List<MyAd> ads) {
        this.ads = ads;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_ad_card, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MyAd ad = ads.get(position);

        holder.bind(ad);

        if ("SOLD".equalsIgnoreCase(ad.status)) {
            holder.llEditButton.setVisibility(View.GONE);
        } else {
            holder.llEditButton.setVisibility(View.VISIBLE);
        }

        holder.llEditButton.setOnClickListener(v -> {

            Context context = v.getContext();

            Intent intent = new Intent(context, EditItem.class);

            intent.putExtra("title", ad.title);
            intent.putExtra("description", ad.description);
            intent.putExtra("price", ad.price);
            intent.putExtra("location", ad.location);
            intent.putExtra("category", ad.category);
            intent.putStringArrayListExtra("images", new ArrayList<>(ad.images));

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return ads.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivAdImage;
        TextView tvAdTitle, tvAdDescription, tvAdLocation, tvAdPrice;
        LinearLayout llEditButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivAdImage = itemView.findViewById(R.id.ivProductImage);
            tvAdTitle = itemView.findViewById(R.id.tvProductTitle);
            tvAdDescription = itemView.findViewById(R.id.tvDescription);
            tvAdLocation = itemView.findViewById(R.id.tvLocation);
            tvAdPrice = itemView.findViewById(R.id.tvPrice);
            llEditButton = itemView.findViewById(R.id.llEditButton);
        }

        public void bind(MyAd ad) {

            tvAdTitle.setText(ad.title);
            tvAdDescription.setText(ad.description);
            tvAdLocation.setText(ad.location);
            tvAdPrice.setText(ad.price);

            if (ad.images != null && !ad.images.isEmpty()) {

                Glide.with(itemView.getContext())
                        .load(ad.images.get(0))
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.error)
                        .into(ivAdImage);
            }
        }
    }
}