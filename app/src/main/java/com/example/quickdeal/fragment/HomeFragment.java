package com.example.quickdeal.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
    private final List<Product> fullProductList = new ArrayList<>();
    private ProductRepository productRepository;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        
        // 1. Initialize UI components first
        setupRecyclerView();
        setupCategoryListeners();
        setupSearch();

        // 2. Then get repository and set listener safely
        productRepository = ProductRepository.getInstance();
        productRepository.setProductListener(this);

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new ProductAdapter(productList, this, this);
        binding.rvRecommendations.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.rvRecommendations.setAdapter(adapter);
    }

    @Override
    public void onDataChanged(List<Product> products) {
        fullProductList.clear();
        fullProductList.addAll(products);
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
        if (binding == null || adapter == null) return;
        
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
        binding.llAll.setOnClickListener(v -> updateUIWithProducts(fullProductList));
        binding.llElectronics.setOnClickListener(v -> filterByCategory("Electronics"));
        binding.llCars.setOnClickListener(v -> filterByCategory("Cars"));
        binding.llProperties.setOnClickListener(v -> filterByCategory("Properties"));
        binding.llMobiles.setOnClickListener(v -> filterByCategory("Mobiles"));
        binding.llFashion.setOnClickListener(v -> filterByCategory("Fashion"));
        binding.llBikes.setOnClickListener(v -> filterByCategory("Bikes"));
    }

    private void filterByCategory(String category) {
        if (binding == null) return;
        binding.progressBar.setVisibility(View.VISIBLE);
        List<Product> filteredList = new ArrayList<>();
        for (Product p : fullProductList) {
            if (p.category != null && p.category.equalsIgnoreCase(category)) {
                filteredList.add(p);
            }
        }
        updateUIWithProducts(filteredList);
    }

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterSearch(String query) {
        String lowerQuery = query.toLowerCase();
        List<Product> filteredList = new ArrayList<>();
        for (Product product : fullProductList) {
            boolean matchesName = product.name != null && product.name.toLowerCase().contains(lowerQuery);
            boolean matchesDesc = product.description != null && product.description.toLowerCase().contains(lowerQuery);
            boolean matchesCat = product.category != null && product.category.toLowerCase().contains(lowerQuery);
            
            if (matchesName || matchesDesc || matchesCat) {
                filteredList.add(product);
            }
        }
        updateUIWithProducts(filteredList);
    }

    @Override
    public void onFavoriteClick(int position, Product product) {
        if (adapter == null || productRepository == null) return;
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
        if (productRepository != null) {
            productRepository.setProductListener(null);
        }
        binding = null;
        adapter = null;
    }
}
