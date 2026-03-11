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
import com.example.quickdeal.model.MyAd;
import com.example.quickdeal.model.Product;
import com.example.quickdeal.repository.ProductRepository;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MyAdsFragment extends Fragment implements ProductRepository.OnDataChangedListener {

    private FragmentMyAdsBinding binding;
    private ProductRepository productRepository;
    private final List<MyAd> activeAds = new ArrayList<>();
    private final List<MyAd> soldAds = new ArrayList<>();
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

        setupTabLayout();
        
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
        activeAds.clear();
        soldAds.clear();
        
        for (Product product : products) {
            if (product.sellerId != null && product.sellerId.equals(currentUserId)) {
                // Mapping Product to MyAd model
                String firstImage = (product.images != null && !product.images.isEmpty()) ? product.images.get(0) : "";
                
                MyAd ad = new MyAd(
                        product.name,
                        product.description,
                        product.price,
                        product.images, // MyAd model uses List<String> for images
                        product.category,
                        product.status
                );

                if ("Available".equalsIgnoreCase(product.status)) {
                    activeAds.add(ad);
                } else if ("Sold".equalsIgnoreCase(product.status)) {
                    soldAds.add(ad);
                }
            }
        }
        
        updateUI();
    }

    private void updateUI() {
        if (binding == null) return;
        int currentTab = binding.tabLayout.getSelectedTabPosition();
        setupRecyclerView(currentTab == 0 ? activeAds : soldAds, currentTab);
    }

    private void setupTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateUI();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupRecyclerView(List<MyAd> ads, int tabPosition) {
        MyAdsAdapter adapter = new MyAdsAdapter(ads);
        binding.rvMyAds.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvMyAds.setAdapter(adapter);
        checkEmptyState(ads, tabPosition);
    }

    private void checkEmptyState(List<MyAd> ads, int tabPosition) {
        if (ads.isEmpty()) {
            binding.llEmptyState.setVisibility(View.VISIBLE);
            binding.rvMyAds.setVisibility(View.GONE);
            binding.tvEmptyTitle.setText(tabPosition == 0 ? "No Active Ads" : "No Sold Items");
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
