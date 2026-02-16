package com.example.quickdeal.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quickdeal.R;
import com.example.quickdeal.model.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private List<Product> list;
    private OnFavoriteClickListener favoriteClickListener;
    private OnItemClickListener itemClickListener;
    public static final String PAYLOAD_FAVORITE = "PAYLOAD_FAVORITE";

    public interface OnFavoriteClickListener {
        void onFavoriteClick(int position, Product product);
    }

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    public ProductAdapter(List<Product> list, OnFavoriteClickListener favoriteClickListener, OnItemClickListener itemClickListener) {
        this.list = list;
        this.favoriteClickListener = favoriteClickListener;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product p = list.get(position);
        holder.bind(p);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.contains(PAYLOAD_FAVORITE)) {
            holder.updateFavoriteIcon(list.get(position));
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, location, price;
        ImageView image, ivFavorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvProductTitle);
            location = itemView.findViewById(R.id.tvLocation);
            price = itemView.findViewById(R.id.tvPrice);
            image = itemView.findViewById(R.id.ivProductImage);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
        }

        public void bind(Product p) {
            name.setText(p.name);
            location.setText(p.location);
            price.setText(p.price);
            Glide.with(image.getContext()).load(p.image).placeholder(R.drawable.loading)
                    .error(R.drawable.error).into(image);

            updateFavoriteIcon(p);

            ivFavorite.setOnClickListener(v -> {
                if (favoriteClickListener != null) {
                    favoriteClickListener.onFavoriteClick(getAdapterPosition(), p);
                }
            });

            itemView.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(p);
                }
            });
        }
        
        public void updateFavoriteIcon(Product p) {
            if (p.isFavorite) {
                ivFavorite.setImageResource(R.drawable.ic_favorite_filled);
            } else {
                ivFavorite.setImageResource(R.drawable.ic_favorite_border);
            }
        }
    }
}
