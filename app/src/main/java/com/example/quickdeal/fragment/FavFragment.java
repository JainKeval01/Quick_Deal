package com.example.quickdeal.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.quickdeal.adapter.ProductAdapter;
import com.example.quickdeal.databinding.FragmentFavBinding;
import com.example.quickdeal.model.Product;
import com.example.quickdeal.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FavFragment extends Fragment implements ProductRepository.OnDataChangedListener, ProductAdapter.OnFavoriteClickListener, ProductAdapter.OnItemClickListener {

    private FragmentFavBinding binding;
    private ProductRepository productRepository;
    private ProductAdapter adapter;
    private final List<Product> favoriteProductsList = new ArrayList<>();

    public FavFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFavBinding.inflate(inflater, container, false);
        productRepository = ProductRepository.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        // Set listener AFTER adapter is initialized
        productRepository.setProductListener(this); 
    }
    
    @Override
    public void onResume() {
        super.onResume();
        loadFavoriteProducts();
    }

    private void setupRecyclerView() {
        adapter = new ProductAdapter(favoriteProductsList, this, this);
        binding.rvFav.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.rvFav.setAdapter(adapter);
    }

    private void loadFavoriteProducts() {
        if (adapter == null) return;
        
        List<Product> freshFavorites = productRepository.getFavoriteProducts();
        favoriteProductsList.clear();
        favoriteProductsList.addAll(freshFavorites);
        adapter.notifyDataSetChanged();
        updateUI();
    }

    private void updateUI() {
        if (binding == null) return;
        
        if (favoriteProductsList.isEmpty()) {
            binding.llEmptyState.setVisibility(View.VISIBLE);
            binding.rvFav.setVisibility(View.GONE);
            binding.tvSavedItems.setText("Saved Items (0)");
        } else {
            binding.llEmptyState.setVisibility(View.GONE);
            binding.rvFav.setVisibility(View.VISIBLE);
            binding.tvSavedItems.setText(String.format(Locale.getDefault(), "Saved Items (%d)", favoriteProductsList.size()));
        }
    }

    @Override
    public void onDataChanged(List<Product> products) {
        // Sirf tabhi load karein jab fragment visible ho aur hum kisi action ka wait na kar rahe hon
        if (isAdded()) {
            loadFavoriteProducts();
        }
    }

    @Override
    public void onError(String error) {
    }

    @Override
    public void onFavoriteClick(int position, Product product) {
        if (adapter == null || position < 0 || position >= favoriteProductsList.size()) return;

        // 1. Pehle local list se hatao (Turant response ke liye)
        favoriteProductsList.remove(position);
        adapter.notifyItemRemoved(position);
        // Position shift handle karne ke liye
        adapter.notifyItemRangeChanged(position, favoriteProductsList.size());
        
        // 2. Phir Firebase/Repo mein update karo
        productRepository.toggleFavoriteStatus(product);
        
        // 3. UI update (Empty state check)
        updateUI();
    }

    @Override
    public void onItemClick(Product product) {
        ProductDetailFragment bottomSheet = ProductDetailFragment.newInstance(product);
        bottomSheet.show(getChildFragmentManager(), "ProductDetail");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (productRepository != null) {
            productRepository.setProductListener(null);
        }
        binding = null;
        adapter = null;
    }
}
