package com.example.quickdeal.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.quickdeal.R;
import com.example.quickdeal.adapter.ProductAdapter;
import com.example.quickdeal.databinding.FragmentHomeBinding;
import com.example.quickdeal.model.Product;
import com.example.quickdeal.repository.ProductRepository;
import java.util.List;

public class HomeFragment extends Fragment implements ProductAdapter.OnFavoriteClickListener, ProductAdapter.OnItemClickListener {
    FragmentHomeBinding hBinding;
    private ProductRepository productRepository;
    private ProductAdapter adapter;
    private List<Product> productList;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        hBinding = FragmentHomeBinding.inflate(inflater, container, false);
        productRepository = ProductRepository.getInstance();

        productList = productRepository.getProducts();

        adapter = new ProductAdapter(productList, this, this);
        hBinding.rvRecommendations.setLayoutManager(new GridLayoutManager(getContext(), 2));
        hBinding.rvRecommendations.setAdapter(adapter);
        return hBinding.getRoot();
    }

    @Override
    public void onFavoriteClick(int position, Product product) {
        product.isFavorite = !product.isFavorite;
        productRepository.toggleFavoriteStatus(product);
        adapter.notifyItemChanged(position, ProductAdapter.PAYLOAD_FAVORITE);
    }

    @Override
    public void onItemClick(Product product) {
        // For now, we do nothing. We will implement this later.
    }
}
