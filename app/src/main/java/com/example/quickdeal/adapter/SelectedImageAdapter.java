package com.example.quickdeal.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickdeal.R;
import java.util.List;

public class SelectedImageAdapter extends RecyclerView.Adapter<SelectedImageAdapter.ViewHolder> {

    private final List<Uri> imageUris;
    private final OnImageRemoveListener removeListener;

    public interface OnImageRemoveListener {
        void onRemove(int position);
    }

    public SelectedImageAdapter(List<Uri> imageUris, OnImageRemoveListener removeListener) {
        this.imageUris = imageUris;
        this.removeListener = removeListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.ivImage.setImageURI(imageUris.get(position));
        holder.ivRemove.setOnClickListener(v -> {
            if (removeListener != null) {
                removeListener.onRemove(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage, ivRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivSelectedImage);
            ivRemove = itemView.findViewById(R.id.ivRemoveImage);
        }
    }
}
