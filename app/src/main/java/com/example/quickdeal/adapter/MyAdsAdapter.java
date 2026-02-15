package com.example.quickdeal.adapter;

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
import com.example.quickdeal.model.MyAd;

import java.util.List;

public class MyAdsAdapter extends RecyclerView.Adapter<MyAdsAdapter.ViewHolder> {

    List<MyAd> list;

    public MyAdsAdapter(List<MyAd> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public MyAdsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_ad_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdsAdapter.ViewHolder holder, int position) {
        MyAd ad = list.get(position);

        holder.title.setText(ad.title);
        holder.description.setText(ad.description);
        holder.location.setText(ad.location);
        holder.price.setText(ad.price);

        Glide.with(holder.image.getContext())
                .load(ad.image)
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .into(holder.image);

        holder.editButton.setOnClickListener(v -> {
            // Edit logic here
        });

        holder.deleteButton.setOnClickListener(v -> {
            // Delete logic here
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, location, price;
        ImageView image;
        LinearLayout editButton, deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvProductTitle);
            description = itemView.findViewById(R.id.tvDescription);
            location = itemView.findViewById(R.id.tvLocation);
            price = itemView.findViewById(R.id.tvPrice);
            image = itemView.findViewById(R.id.ivProductImage);
            editButton = itemView.findViewById(R.id.llEditButton);
            deleteButton = itemView.findViewById(R.id.llDeleteButton);
        }
    }
}