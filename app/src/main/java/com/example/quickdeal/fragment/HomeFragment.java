package com.example.quickdeal.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.quickdeal.R;
import com.example.quickdeal.adapter.ProductAdapter;
import com.example.quickdeal.databinding.FragmentHomeBinding;
import com.example.quickdeal.model.Product;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    FragmentHomeBinding hBinding;
    List<Product> productList=new ArrayList<>();
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        hBinding = FragmentHomeBinding.inflate(inflater, container, false);

        productList.add(new Product("IPhone","Andheri East","₹54,999","https://images.unsplash.com/photo-1511707171634-5f897ff02aa9"));
        productList.add(new Product("IPhone","Andheri East","₹54,999","https://images.unsplash.com/photo-1511707171634-5f897ff02aa9"));
        productList.add(new Product("IPhone","Andheri East","₹54,999","https://images.unsplash.com/photo-1511707171634-5f897ff02aa9"));
        productList.add(new Product("IPhone","Andheri East","₹54,999","https://images.unsplash.com/photo-1511707171634-5f897ff02aa9"));
        productList.add(new Product("IPhone","Andheri East","₹54,999","https://images.unsplash.com/photo-1511707171634-5f897ff02aa9"));
        productList.add(new Product("IPhone","Andheri East","₹54,999","https://images.unsplash.com/photo-1511707171634-5f897ff02aa9"));

        ProductAdapter adapter=new ProductAdapter(productList);
        hBinding.rvRecommendations.setLayoutManager(new GridLayoutManager(getContext(),2));
        hBinding.rvRecommendations.setAdapter(adapter);
        return hBinding.getRoot();
    }
}