package com.example.quickdeal.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.quickdeal.R;
import com.example.quickdeal.adapter.ProductAdapter;
import com.example.quickdeal.databinding.FragmentFavBinding;
import com.example.quickdeal.model.Product;
import com.example.quickdeal.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FavFragment extends Fragment implements ProductAdapter.OnFavoriteClickListener, ProductAdapter.OnItemClickListener {

    private FragmentFavBinding binding;
    private ProductRepository productRepository;
    private ProductAdapter adapter;
    private List<Product> favoriteProductsList = new ArrayList<>();

    public FavFragment() {
        // Required empty public constructor
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
        List<Product> freshFavorites = productRepository.getFavoriteProducts();
        favoriteProductsList.clear();
        favoriteProductsList.addAll(freshFavorites);
        adapter.notifyDataSetChanged();
        updateUI();
    }

    private void updateUI() {
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
    public void onFavoriteClick(int position, Product product) {
        product.isFavorite = !product.isFavorite;
        productRepository.toggleFavoriteStatus(product);

        favoriteProductsList.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, favoriteProductsList.size());
        
        updateUI();
    }

    @Override
    public void onItemClick(Product product) {
        // For now, we do nothing. We will implement this later.
    }
}
