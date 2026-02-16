package com.example.quickdeal.repository;

import com.example.quickdeal.model.MyAd;
import com.example.quickdeal.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductRepository {

    private static ProductRepository instance;
    private final List<Product> productList;
    private final List<Product> favoriteProducts = new ArrayList<>();
    private final List<MyAd> activeAds;
    private final List<MyAd> soldAds;

    private ProductRepository() {
        // Initialize all data here in the private constructor
        productList = new ArrayList<>();
        productList.add(new Product("IPhone", "Andheri East", "₹54,999", "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9"));
        productList.add(new Product("IPhone", "Andheri East", "₹54,999", "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9"));
        productList.add(new Product("IPhone", "Andheri East", "₹54,999", "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9"));
        productList.add(new Product("IPhone", "Andheri East", "₹54,999", "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9"));
        productList.add(new Product("IPhone", "Andheri East", "₹54,999", "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9"));
        productList.add(new Product("IPhone", "Andheri East", "₹54,999", "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9"));

        activeAds = new ArrayList<>();
        activeAds.add(new MyAd(
                "iPhone 13 Pro Max",
                "256GB Sierra Blue",
                "New York, NY",
                "₹85,000",
                "https://images.unsplash.com/photo-1632661674596-df8be070a5c5",
                "ACTIVE"
        ));
        activeAds.add(new MyAd(
                "Sony PlayStation 5",
                "Disc Edition Console",
                "Brooklyn, NY",
                "₹45,000",
                "https://images.unsplash.com/photo-1606813907291-d86efa9b94db",
                "ACTIVE"
        ));

        soldAds = new ArrayList<>();
        soldAds.add(new MyAd(
                "Mid-Century Chair",
                "Modern Wooden Design",
                "Queens, NY",
                "₹12,000",
                "https://images.unsplash.com/photo-1503602642458-232111445657",
                "SOLD"
        ));
    }

    public static synchronized ProductRepository getInstance() {
        if (instance == null) {
            instance = new ProductRepository();
        }
        return instance;
    }

    public List<Product> getProducts() {
        return productList;
    }

    public List<MyAd> getActiveAds() {
        return activeAds;
    }

    public List<MyAd> getSoldAds() {
        return soldAds;
    }
    
    public void toggleFavoriteStatus(Product product) {
        if (product.isFavorite) {
            if (!favoriteProducts.contains(product)) {
                favoriteProducts.add(product);
            }
        } else {
            favoriteProducts.remove(product);
        }
    }

    public List<Product> getFavoriteProducts() {
        return favoriteProducts;
    }
}
