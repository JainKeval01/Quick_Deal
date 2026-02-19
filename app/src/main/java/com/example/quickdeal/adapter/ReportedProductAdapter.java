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
import com.example.quickdeal.model.ReportedProduct;

import java.util.List;

public class ReportedProductAdapter extends RecyclerView.Adapter<ReportedProductAdapter.ViewHolder> {

    private List<ReportedProduct> list;
    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClick(ReportedProduct product);
    }

    public ReportedProductAdapter(List<ReportedProduct> list, OnItemClickListener itemClickListener) {
        this.list = list;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reported_ad, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReportedProduct p = list.get(position);
        holder.bind(p);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductTitle, tvReportedBy, tvHighPriority, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductTitle = itemView.findViewById(R.id.tvProductTitle);
            tvReportedBy = itemView.findViewById(R.id.tvReportedBy);
            tvHighPriority = itemView.findViewById(R.id.tvHighPriority);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }

        public void bind(ReportedProduct p) {
            tvProductTitle.setText(p.name);
            tvReportedBy.setText("By: " + p.reportedBy + " • " + p.timeAgo);

            if (p.isHighPriority) {
                tvHighPriority.setVisibility(View.VISIBLE);
            } else {
                tvHighPriority.setVisibility(View.GONE);
            }

            tvStatus.setText(p.status);

            Glide.with(ivProductImage.getContext()).load(p.imageUrl).into(ivProductImage);

            itemView.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(p);
                }
            });
        }
    }
}
