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
import com.example.quickdeal.model.Product;

import java.util.ArrayList;
import java.util.List;

public class MyAdsAdapter extends RecyclerView.Adapter<MyAdsAdapter.ViewHolder> {

    private List<Product> products;

    public MyAdsAdapter(List<Product> products) {
        this.products = products;
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
        Product product = products.get(position);
        holder.bind(product);

        if ("SOLD".equalsIgnoreCase(product.status)) {
            holder.llEditButton.setVisibility(View.GONE);
        } else {
            holder.llEditButton.setVisibility(View.VISIBLE);
        }

        // Edit Intent (Passing isNegotiable for saved state)
        holder.llEditButton.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, EditItem.class);
            intent.putExtra("id", product.getId());
            intent.putExtra("title", product.name);
            intent.putExtra("description", product.description);
            intent.putExtra("price", product.price);
            intent.putExtra("category", product.category);
            intent.putExtra("sellerId", product.sellerId);
            intent.putExtra("timestamp", product.timestamp);
            intent.putExtra("isNegotiable", product.isNegotiable);
            intent.putStringArrayListExtra("images", new ArrayList<>(product.images));
            context.startActivity(intent);
        });

        // Click on itemView removed as per user request (Only Home and Wishlist should open detail)
    }

    @Override
    public int getItemCount() {
        return products.size();
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

        public void bind(Product product) {
            tvAdTitle.setText(product.name);
            tvAdDescription.setText(product.description);
            tvAdPrice.setText(product.price);

            if (product.images != null && !product.images.isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(product.images.get(0))
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.error)
                        .into(ivAdImage);
            }
        }
    }
}
