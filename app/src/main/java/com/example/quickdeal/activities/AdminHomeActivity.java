package com.example.quickdeal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.quickdeal.adapter.ReportedProductAdapter;
import com.example.quickdeal.databinding.ActivityAdminHomeBinding;
import com.example.quickdeal.fragment.AdminProductDetailFragment;
import com.example.quickdeal.model.ReportedProduct;
import com.example.quickdeal.repository.ProductRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminHomeActivity extends AppCompatActivity implements ReportedProductAdapter.OnItemClickListener, ProductRepository.OnReportsChangedListener {

    private ActivityAdminHomeBinding binding;
    private ProductRepository productRepository;
    private ReportedProductAdapter adapter;
    private final List<ReportedProduct> reportedProductsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        setSupportActionBar(binding.toolbar);

        productRepository = ProductRepository.getInstance();
        productRepository.setReportsListener(this);

        setupRecyclerView();
        setupRefresh();
        setupAdminActions();
    }

    private void setupAdminActions() {
        binding.ivProfile.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout from Admin panel?")
                    .setPositiveButton("Logout", (dialog, which) -> {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(AdminHomeActivity.this, Login_Page.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void setupRecyclerView() {
        adapter = new ReportedProductAdapter(reportedProductsList, this);
        binding.rvReportedAds.setLayoutManager(new LinearLayoutManager(this));
        binding.rvReportedAds.setAdapter(adapter);
        onReportsChanged(productRepository.getReportedProducts());
    }

    private void setupRefresh() {
        binding.swipeRefresh.setOnRefreshListener(() -> {
            onReportsChanged(productRepository.getReportedProducts());
            binding.swipeRefresh.setRefreshing(false);
        });
    }

    private void updateReportCount(int count) {
        if (binding.countOfReportedAds != null) {
            binding.countOfReportedAds.setText(count + " active flags requiring review");
        }
    }

    @Override
    public void onReportsChanged(List<ReportedProduct> reports) {
        reportedProductsList.clear();
        
        for (ReportedProduct report : reports) {
            if (productRepository.getProductById(report.productId) != null) {
                reportedProductsList.add(report);
            }
        }
        
        Collections.sort(reportedProductsList, (o1, o2) -> Integer.compare(o2.reportCount, o1.reportCount));
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        updateReportCount(reportedProductsList.size());
    }

    @Override
    public void onError(String error) {
        Toast.makeText(this, "Error fetching reports: " + error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(ReportedProduct report) {
        if ("Pending".equalsIgnoreCase(report.status)) {
            FirebaseDatabase.getInstance().getReference("reports")
                    .child(report.reportId)
                    .child("status")
                    .setValue("Visited");
        }

        AdminProductDetailFragment bottomSheet = AdminProductDetailFragment.newInstance(report.productId, report.reason);
        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        productRepository.setReportsListener(null);
    }
}
