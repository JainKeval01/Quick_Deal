package com.example.quickdeal.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.quickdeal.adapter.MyAdsAdapter;
import com.example.quickdeal.databinding.FragmentMyAdsBinding;
import com.example.quickdeal.model.MyAd;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MyAdsFragment extends Fragment {

    FragmentMyAdsBinding binding;
    List<MyAd> activeAds = new ArrayList<>();
    List<MyAd> soldAds = new ArrayList<>();
    MyAdsAdapter adapter;

    public MyAdsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyAdsBinding.inflate(inflater, container, false);

        // Sample data - Active ads
        activeAds.add(new MyAd(
                "iPhone 13 Pro Max",
                "256GB Sierra Blue",
                "New York, NY",
                "$850",
                "https://images.unsplash.com/photo-1632661674596-df8be070a5c5",
                "ACTIVE"
        ));

        activeAds.add(new MyAd(
                "Sony PlayStation 5",
                "Disc Edition Console",
                "Brooklyn, NY",
                "$450",
                "https://images.unsplash.com/photo-1606813907291-d86efa9b94db",
                "ACTIVE"
        ));

        activeAds.add(new MyAd(
                "Mid-Century Chair",
                "Modern Wooden Design",
                "Queens, NY",
                "$120",
                "https://images.unsplash.com/photo-1503602642458-232111445657",
                "ACTIVE"
        ));



        // Setup RecyclerView with Active ads by default
        setupRecyclerView(activeAds, 0);

        // Tab selection listener - YE LOGIC IMPORTANT HAI
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: // Active tab click
                        updateAdsList(activeAds, 0);
                        break;
                    case 1: // Sold tab click
                        updateAdsList(soldAds, 1);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        return binding.getRoot();
    }

    private void setupRecyclerView(List<MyAd> ads, int tabPosition) {
        adapter = new MyAdsAdapter(ads);
        binding.rvMyAds.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvMyAds.setAdapter(adapter);

        checkEmptyState(ads, tabPosition);
    }

    // Jab tab change ho, list update ho
    private void updateAdsList(List<MyAd> ads, int tabPosition) {
        adapter = new MyAdsAdapter(ads);
        binding.rvMyAds.setAdapter(adapter);

        checkEmptyState(ads, tabPosition);
    }

    private void checkEmptyState(List<MyAd> ads, int tabPosition) {
        if (ads.isEmpty()) {
            binding.llEmptyState.setVisibility(View.VISIBLE);
            binding.rvMyAds.setVisibility(View.GONE);

            // Tab-wise empty messages
            switch (tabPosition) {
                case 0: // Active
                    binding.tvEmptyTitle.setText("No Active Ads");
                    binding.tvEmptyMessage.setText("You don't have any active ads.\nPost a new ad to start selling!");
                    break;

                case 1: // Sold
                    binding.tvEmptyTitle.setText("No Sold Items");
                    binding.tvEmptyMessage.setText("You haven't sold anything yet.\nKeep your ads active to get buyers!");
                    break;

                case 2: // Expired
                    binding.tvEmptyTitle.setText("No Expired Ads");
                    binding.tvEmptyMessage.setText("Great! All your ads are still active.\nKeep them updated for better results!");
                    break;
            }
        } else {
            binding.llEmptyState.setVisibility(View.GONE);
            binding.rvMyAds.setVisibility(View.VISIBLE);
        }
    }
}