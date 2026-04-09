package com.example.quickdeal.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quickdeal.R;
import com.example.quickdeal.activities.EditItem;
import com.example.quickdeal.model.Product;
import com.example.quickdeal.repository.ProductRepository;

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
            intent.putExtra("location", product.location);
            intent.putStringArrayListExtra("images", new ArrayList<>(product.images));
            context.startActivity(intent);
        });

        holder.llDeleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Delete Ad")
                    .setMessage("Are you sure you want to delete this ad?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        ProductRepository.getInstance().deleteProduct(product.getId());
                        Toast.makeText(v.getContext(), "Product Deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAdImage;
        TextView tvAdTitle, tvAdDescription, tvAdLocation, tvAdPrice;
        LinearLayout llEditButton, llDeleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAdImage = itemView.findViewById(R.id.ivProductImage);
            tvAdTitle = itemView.findViewById(R.id.tvProductTitle);
            tvAdDescription = itemView.findViewById(R.id.tvDescription);
            tvAdLocation = itemView.findViewById(R.id.tvLocation);
            tvAdPrice = itemView.findViewById(R.id.tvPrice);
            llEditButton = itemView.findViewById(R.id.llEditButton);
            llDeleteButton = itemView.findViewById(R.id.llDeleteButton);
        }

        public void bind(Product product) {
            tvAdTitle.setText(product.name);
            tvAdDescription.setText(product.description);
            tvAdPrice.setText("₹" + product.price);
            
            if (product.location != null && !product.location.isEmpty()) {
                tvAdLocation.setText(product.location);
            } else {
                tvAdLocation.setText("Location N/A");
            }

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
