package com.example.quickdeal.activities;

import android.os.Bundle;
import android.widget.Toast;

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

        // Handling status bar overlap
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
    }

    private void setupRecyclerView() {
        adapter = new ReportedProductAdapter(reportedProductsList, this);
        binding.rvReportedAds.setLayoutManager(new LinearLayoutManager(this));
        binding.rvReportedAds.setAdapter(adapter);
        
        // Initial load
        onReportsChanged(productRepository.getReportedProducts());
    }

    private void updateReportCount(int count) {
        if (binding.countOfReportedAds != null) {
            binding.countOfReportedAds.setText(count + " active flags requiring review");
        }
    }

    @Override
    public void onReportsChanged(List<ReportedProduct> reports) {
        reportedProductsList.clear();
        reportedProductsList.addAll(reports);
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
    public void onItemClick(ReportedProduct product) {
        AdminProductDetailFragment bottomSheet = AdminProductDetailFragment.newInstance(product.getId());
        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        productRepository.setReportsListener(null);
    }
}
