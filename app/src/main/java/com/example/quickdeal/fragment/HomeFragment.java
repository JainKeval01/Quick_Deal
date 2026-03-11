package com.example.quickdeal.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.quickdeal.adapter.ProductAdapter;
import com.example.quickdeal.databinding.FragmentHomeBinding;
import com.example.quickdeal.model.Product;
import com.example.quickdeal.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements ProductRepository.OnDataChangedListener, ProductAdapter.OnFavoriteClickListener, ProductAdapter.OnItemClickListener {
    private FragmentHomeBinding binding;
    private ProductAdapter adapter;
    private final List<Product> productList = new ArrayList<>();
    private ProductRepository productRepository;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        
        productRepository = ProductRepository.getInstance();
        productRepository.setProductListener(this);

        setupRecyclerView();
        setupCategoryListeners();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new ProductAdapter(productList, this, this);
        binding.rvRecommendations.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.rvRecommendations.setAdapter(adapter);
    }

    @Override
    public void onDataChanged(List<Product> products) {
        updateUIWithProducts(products);
    }

    @Override
    public void onError(String error) {
        if (binding != null) {
            binding.progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUIWithProducts(List<Product> products) {
        if (binding == null) return;
        
        productList.clear();
        productList.addAll(products);
        adapter.notifyDataSetChanged();
        binding.progressBar.setVisibility(View.GONE);
        
        if (productList.isEmpty()) {
            binding.tvNoProducts.setVisibility(View.VISIBLE);
        } else {
            binding.tvNoProducts.setVisibility(View.GONE);
        }
    }

    private void setupCategoryListeners() {
        binding.llAll.setOnClickListener(v -> updateUIWithProducts(productRepository.getAllProducts()));
        binding.llElectronics.setOnClickListener(v -> filterByCategory("Electronics"));
        binding.llCars.setOnClickListener(v -> filterByCategory("Cars"));
        binding.llProperties.setOnClickListener(v -> filterByCategory("Properties"));
        binding.llMobiles.setOnClickListener(v -> filterByCategory("Mobiles"));
        binding.llFashion.setOnClickListener(v -> filterByCategory("Fashion"));
        binding.llBikes.setOnClickListener(v -> filterByCategory("Bikes"));
    }

    private void filterByCategory(String category) {
        binding.progressBar.setVisibility(View.VISIBLE);
        List<Product> filteredList = productRepository.getProductsByCategory(category);
        updateUIWithProducts(filteredList);
    }

    @Override
    public void onFavoriteClick(int position, Product product) {
        product.isFavorite = !product.isFavorite;
        productRepository.toggleFavoriteStatus(product);
        adapter.notifyItemChanged(position, ProductAdapter.PAYLOAD_FAVORITE);
    }

    @Override
    public void onItemClick(Product product) {
        ProductDetailFragment bottomSheet = ProductDetailFragment.newInstance(product);
        bottomSheet.show(getChildFragmentManager(), "ProductDetail");
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        productRepository.setProductListener(null);
        binding = null;
    }
}
