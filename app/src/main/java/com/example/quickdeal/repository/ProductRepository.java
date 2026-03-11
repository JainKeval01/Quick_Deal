package com.example.quickdeal.repository;

import androidx.annotation.NonNull;

import com.example.quickdeal.model.Product;
import com.example.quickdeal.model.ReportedProduct;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductRepository {

    private static ProductRepository instance;
    private final DatabaseReference mDatabase;
    private final DatabaseReference mReportsDatabase;
    private final List<Product> allProducts = new ArrayList<>();
    private final List<Product> favoriteProducts = new ArrayList<>();
    private final List<ReportedProduct> reportedProducts = new ArrayList<>();
    private OnDataChangedListener productListener;
    private OnReportsChangedListener reportsListener;

    public interface OnDataChangedListener {
        void onDataChanged(List<Product> products);
        void onError(String error);
    }

    public interface OnReportsChangedListener {
        void onReportsChanged(List<ReportedProduct> reports);
        void onError(String error);
    }

    private ProductRepository() {
        mDatabase = FirebaseDatabase.getInstance().getReference("products");
        mReportsDatabase = FirebaseDatabase.getInstance().getReference("reports");
        startListening();
    }

    public static synchronized ProductRepository getInstance() {
        if (instance == null) {
            instance = new ProductRepository();
        }
        return instance;
    }

    private void startListening() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allProducts.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    if (product != null) {
                        allProducts.add(product);
                    }
                }
                if (productListener != null) {
                    productListener.onDataChanged(new ArrayList<>(allProducts));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (productListener != null) productListener.onError(error.getMessage());
            }
        });

        mReportsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reportedProducts.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    ReportedProduct report = postSnapshot.getValue(ReportedProduct.class);
                    if (report != null) {
                        reportedProducts.add(report);
                    }
                }
                if (reportsListener != null) {
                    reportsListener.onReportsChanged(new ArrayList<>(reportedProducts));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (reportsListener != null) reportsListener.onError(error.getMessage());
            }
        });
    }

    public void setProductListener(OnDataChangedListener listener) {
        this.productListener = listener;
        if (listener != null && !allProducts.isEmpty()) {
            listener.onDataChanged(new ArrayList<>(allProducts));
        }
    }

    public void setReportsListener(OnReportsChangedListener listener) {
        this.reportsListener = listener;
        if (listener != null && !reportedProducts.isEmpty()) {
            listener.onReportsChanged(new ArrayList<>(reportedProducts));
        }
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(allProducts);
    }

    public List<ReportedProduct> getReportedProducts() {
        return new ArrayList<>(reportedProducts);
    }

    public List<Product> getFavoriteProducts() {
        return new ArrayList<>(favoriteProducts);
    }

    public void addProduct(Product product, OnCompleteListener<Void> completionListener) {
        String id = product.getId();
        if (id == null || id.isEmpty()) {
            id = mDatabase.push().getKey();
            // We might need to update the product object with the generated ID if it's null
            // But usually, it's passed from the fragment.
        }
        if (id != null) {
            mDatabase.child(id).setValue(product).addOnCompleteListener(completionListener);
        }
    }

    public void reportProduct(ReportedProduct report, OnCompleteListener<Void> completionListener) {
        String reportId = mReportsDatabase.push().getKey();
        if (reportId != null) {
            mReportsDatabase.child(reportId).setValue(report).addOnCompleteListener(completionListener);
        }
    }

    public void toggleFavoriteStatus(Product product) {
        // This is local for now as per current code, usually would be in DB per user
        if (product.isFavorite) {
            if (!favoriteProducts.contains(product)) {
                favoriteProducts.add(product);
            }
        } else {
            favoriteProducts.remove(product);
        }
    }

    public Product getProductById(String id) {
        for (Product p : allProducts) {
            if (p.getId() != null && p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    public List<Product> getProductsByCategory(String category) {
        if (category == null || category.equalsIgnoreCase("All") || category.isEmpty()) {
            return getAllProducts();
        }
        List<Product> filtered = new ArrayList<>();
        for (Product p : allProducts) {
            if (p.category != null && p.category.equalsIgnoreCase(category)) {
                filtered.add(p);
            }
        }
        return filtered;
    }

    public List<Product> getProductsByUser(String userId) {
        List<Product> userProducts = new ArrayList<>();
        for (Product p : allProducts) {
            if (p.sellerId != null && p.sellerId.equals(userId)) {
                userProducts.add(p);
            }
        }
        return userProducts;
    }
    
    public void deleteProduct(String productId) {
        mDatabase.child(productId).removeValue();
        // Also remove reports related to this product if needed
    }
}
