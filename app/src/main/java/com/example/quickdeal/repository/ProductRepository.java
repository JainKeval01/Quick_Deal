package com.example.quickdeal.repository;

import androidx.annotation.NonNull;

import com.example.quickdeal.model.Product;
import com.example.quickdeal.model.ReportedProduct;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductRepository {

    private static ProductRepository instance;
    private final DatabaseReference mDatabase;
    private final DatabaseReference mReportsDatabase;
    private final DatabaseReference mFavoritesDatabase;
    
    private final List<Product> allProducts = new ArrayList<>();
    private final Set<String> favoriteProductIds = new HashSet<>();
    private final List<ReportedProduct> reportedProducts = new ArrayList<>();
    
    private OnDataChangedListener productListener;
    private OnReportsChangedListener reportsListener;
    
    private ValueEventListener productValueListener;
    private ValueEventListener reportsValueListener;
    private ValueEventListener favoritesValueListener;

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
        mFavoritesDatabase = FirebaseDatabase.getInstance().getReference("Favorites");
        startListening();
    }

    public static synchronized ProductRepository getInstance() {
        if (instance == null) {
            instance = new ProductRepository();
        }
        return instance;
    }

    public static synchronized void resetInstance() {
        if (instance != null) {
            instance.stopListening();
            instance = null;
        }
    }

    private void startListening() {
        productValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allProducts.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    if (product != null) {
                        allProducts.add(product);
                    }
                }
                notifyProductListeners();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (productListener != null) productListener.onError(error.getMessage());
            }
        };
        mDatabase.addValueEventListener(productValueListener);

        reportsValueListener = new ValueEventListener() {
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
        };
        mReportsDatabase.addValueEventListener(reportsValueListener);

        String currentUid = FirebaseAuth.getInstance().getUid();
        if (currentUid != null) {
            favoritesValueListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    favoriteProductIds.clear();
                    for (DataSnapshot favSnapshot : snapshot.getChildren()) {
                        favoriteProductIds.add(favSnapshot.getKey());
                    }
                    notifyProductListeners();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            };
            mFavoritesDatabase.child(currentUid).addValueEventListener(favoritesValueListener);
        }
    }

    private void updateProductFavoriteFlags() {
        for (Product p : allProducts) {
            p.isFavorite = favoriteProductIds.contains(p.getId());
        }
    }

    private void notifyProductListeners() {
        updateProductFavoriteFlags();
        if (productListener != null) {
            productListener.onDataChanged(new ArrayList<>(allProducts));
        }
    }

    public void stopListening() {
        if (productValueListener != null) mDatabase.removeEventListener(productValueListener);
        if (reportsValueListener != null) mReportsDatabase.removeEventListener(reportsValueListener);
        String currentUid = FirebaseAuth.getInstance().getUid();
        if (favoritesValueListener != null && currentUid != null) {
            mFavoritesDatabase.child(currentUid).removeEventListener(favoritesValueListener);
        }
    }

    public void setProductListener(OnDataChangedListener listener) {
        this.productListener = listener;
        if (listener != null) {
            notifyProductListeners();
        }
    }

    public void setReportsListener(OnReportsChangedListener listener) {
        this.reportsListener = listener;
        if (listener != null) {
            listener.onReportsChanged(new ArrayList<>(reportedProducts));
        }
    }

    public List<ReportedProduct> getReportedProducts() {
        return new ArrayList<>(reportedProducts);
    }

    public List<Product> getFavoriteProducts() {
        List<Product> favs = new ArrayList<>();
        for (Product p : allProducts) {
            if (favoriteProductIds.contains(p.getId())) {
                favs.add(p);
            }
        }
        return favs;
    }

    public void toggleFavoriteStatus(Product product) {
        String currentUid = FirebaseAuth.getInstance().getUid();
        if (currentUid == null) return;

        DatabaseReference userFavRef = mFavoritesDatabase.child(currentUid).child(product.getId());

        if (favoriteProductIds.contains(product.getId())) {
            userFavRef.removeValue();
        } else {
            userFavRef.setValue(true);
        }
    }

    public void addProduct(Product product, OnCompleteListener<Void> completionListener) {
        String id = product.getId();
        if (id == null || id.isEmpty()) {
            id = mDatabase.push().getKey();
        }
        if (id != null) {
            mDatabase.child(id).setValue(product).addOnCompleteListener(completionListener);
        }
    }

    public void reportProduct(ReportedProduct report, OnCompleteListener<Void> completionListener) {
        if (report.reportId != null) {
            mReportsDatabase.child(report.reportId).setValue(report).addOnCompleteListener(completionListener);
        } else {
            String reportId = mReportsDatabase.push().getKey();
            if (reportId != null) {
                report.reportId = reportId;
                mReportsDatabase.child(reportId).setValue(report).addOnCompleteListener(completionListener);
            }
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

    public void deleteProduct(String productId) {
        mDatabase.child(productId).removeValue();
        mReportsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot reportSnapshot : snapshot.getChildren()) {
                    ReportedProduct report = reportSnapshot.getValue(ReportedProduct.class);
                    if (report != null && productId.equals(report.productId)) {
                        reportSnapshot.getRef().removeValue();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
