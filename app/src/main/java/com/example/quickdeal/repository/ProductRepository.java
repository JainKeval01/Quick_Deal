package com.example.quickdeal.repository;

import com.example.quickdeal.model.MyAd;
import com.example.quickdeal.model.Product;

import java.util.ArrayList;
import java.util.Arrays;
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
        productList.add(new Product("Gaming Console - Like New", "Mumbai, Maharashtra", "₹450,00",
                Arrays.asList(
                        "https://images.unsplash.com/photo-1606813907291-d86efa9b94db",
                        "https://images.unsplash.com/photo-1592155931584-901ac15763e3",
                        "https://images.unsplash.com/photo-1617196034599-86a3418f8c42"
                ),
                "Selling my gaming console. It is in perfect working condition and has been well taken care of. Comes with the original box, all cables, and one controller.",
                "AVAILABLE"));

        productList.add(new Product("IPhone 13 Pro Max", "Andheri East", "₹85,000",
                Arrays.asList(
                        "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9",
                        "https://images.unsplash.com/photo-1632661674596-df8be070a5c5",
                        "https://images.unsplash.com/photo-1512941937669-90a1b58e7e9c"
                ),
                "Almost new iPhone 13 Pro max, 256GB Sierra Blue. No scratches, perfect condition.",
                "AVAILABLE"));

        productList.add(new Product("Mid-Century Chair", "Queens, NY", "₹12,000",
                Arrays.asList("https://images.unsplash.com/photo-1503602642458-232111445657"),
                "Modern Wooden Design chair. Great for living rooms.",
                "SOLD"));

        activeAds = new ArrayList<>();
        if (productList.size() >= 2) {
            Product product1 = productList.get(0);
            activeAds.add(new MyAd(product1.name, product1.description, product1.location, product1.price, product1.images.get(0), "ACTIVE"));
            Product product2 = productList.get(1);
            activeAds.add(new MyAd(product2.name, product2.description, product2.location, product2.price, product2.images.get(0), "ACTIVE"));
        }

        soldAds = new ArrayList<>();
        if (productList.size() >= 3) {
            Product product3 = productList.get(2);
            soldAds.add(new MyAd(product3.name, product3.description, product3.location, product3.price, product3.images.get(0), "SOLD"));
        }
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
