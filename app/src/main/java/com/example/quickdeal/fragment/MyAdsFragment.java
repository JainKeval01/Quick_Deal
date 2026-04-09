package com.example.quickdeal.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.quickdeal.adapter.MyAdsAdapter;
import com.example.quickdeal.databinding.FragmentMyAdsBinding;
import com.example.quickdeal.model.Product;
import com.example.quickdeal.repository.ProductRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MyAdsFragment extends Fragment implements ProductRepository.OnDataChangedListener {

    private FragmentMyAdsBinding binding;
    private ProductRepository productRepository;
    private final List<Product> userAds = new ArrayList<>();
    private String currentUserId;

    public MyAdsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyAdsBinding.inflate(inflater, container, false);
        
        currentUserId = FirebaseAuth.getInstance().getUid();
        productRepository = ProductRepository.getInstance();
        productRepository.setProductListener(this);

        return binding.getRoot();
    }

    @Override
    public void onDataChanged(List<Product> products) {
        filterUserAds(products);
    }

    @Override
    public void onError(String error) {
        if (binding != null) {
            Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
        }
    }

    private void filterUserAds(List<Product> products) {
        userAds.clear();
        
        for (Product product : products) {
            if (product.sellerId != null && product.sellerId.equals(currentUserId)) {
                userAds.add(product);
            }
        }
        
        updateUI();
    }

    private void updateUI() {
        if (binding == null) return;
        setupRecyclerView(userAds);
    }

    private void setupRecyclerView(List<Product> ads) {
        MyAdsAdapter adapter = new MyAdsAdapter(ads);
        binding.rvMyAds.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvMyAds.setAdapter(adapter);
        checkEmptyState(ads);
    }

    private void checkEmptyState(List<Product> ads) {
        if (ads.isEmpty()) {
            binding.llEmptyState.setVisibility(View.VISIBLE);
            binding.rvMyAds.setVisibility(View.GONE);
            binding.tvEmptyTitle.setText("No Ads Found");
        } else {
            binding.llEmptyState.setVisibility(View.GONE);
            binding.rvMyAds.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        productRepository.setProductListener(null);
        binding = null;
    }
}
