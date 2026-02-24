package com.example.quickdeal.repository;

import com.example.quickdeal.model.MyAd;
import com.example.quickdeal.model.Product;
import com.example.quickdeal.model.ReportedProduct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductRepository {

    private static ProductRepository instance;
    private final List<Product> productList;
    private final List<Product> favoriteProducts = new ArrayList<>();
    private final List<MyAd> activeAds;
    private final List<MyAd> soldAds;
    private final List<ReportedProduct> reportedProducts;

    private ProductRepository() {
        // Initialize all data here in the private constructor
        productList = new ArrayList<>();
        productList.add(new Product("prod1", "Gaming Console - Like New", "Mumbai, Maharashtra", "₹450,00",
                Arrays.asList(
                        "https://images.unsplash.com/photo-1606813907291-d86efa9b94db?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8cHM1fGVufDB8fDB8fHww",
                        "https://images.unsplash.com/photo-1606144042614-b2417e99c4e3?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8cHM1fGVufDB8fDB8fHww",
                        "https://images.unsplash.com/photo-1606144170360-d2c819d4a2e9?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8NHx8cHM1fGVufDB8fDB8fHww"
                ),
                "Selling my gaming console. It is in perfect working condition and has been well taken care of. Comes with the original box, all cables, and one controller.",
                "AVAILABLE"));

        productList.add(new Product("prod2", "IPhone 13 Pro Max", "Andheri East", "₹85,000",
                Arrays.asList(
                        "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8aXBob25lfGVufDB8fDB8fHww",
                        "https://images.unsplash.com/photo-1591343411494-a9c10a10b427?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8aXBob25lfGVufDB8fDB8fHww",
                        "https://images.unsplash.com/photo-1530319067432-f5a7ad0c3f6a?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTl8fGlwaG9uZXxlbnwwfHwwfHx8MA%3D%3D"
                ),
                "Almost new iPhone 13 Pro max, 256GB Sierra Blue. No scratches, perfect condition.",
                "AVAILABLE"));

        productList.add(new Product("prod3", "Mid-Century Chair", "Queens, NY", "₹12,000",
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

        // Dummy data for reported products
        reportedProducts = new ArrayList<>();
        reportedProducts.add(new ReportedProduct("prod1", "PlayStation 5 Console", "gamer_pro_v", "8h ago", "https://images.unsplash.com/photo-1606813907291-d86efa9b94db?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8cHM1fGVufDB8fDB8fHww", 5, "Pending", false));
        reportedProducts.add(new ReportedProduct("prod2", "iPhone 13 Pro Max", "alex_smith92", "2h ago", "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8aXBob25lfGVufDB8fDB8fHww", 12, "Pending", true));
        reportedProducts.add(new ReportedProduct("prod3", "Vintage Leather Jacket", "sarah_j_88", "5h ago", "https://images.unsplash.com/photo-1551028719-00167b16eac5?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8bGVhdGhlciUyMGphY2tldHxlbnwwfHwwfHx8MA%3D%3D", 2, "Action Taken", false));

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

    public Product getProductById(String productId) {
        for (Product product : productList) {
            if (product.getId().equals(productId)) {
                return product;
            }
        }
        return null;
    }


    public List<MyAd> getActiveAds() {
        return activeAds;
    }

    public List<MyAd> getSoldAds() {
        return soldAds;
    }

    public List<ReportedProduct> getReportedProducts() {
        return reportedProducts;
    }

    public ReportedProduct getReportedProductById(String productId) {
        for (ReportedProduct product : reportedProducts) {
            if (product.getId().equals(productId)) {
                return product;
            }
        }
        return null;
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
