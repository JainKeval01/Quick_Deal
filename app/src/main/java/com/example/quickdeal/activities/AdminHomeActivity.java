package com.example.quickdeal.activities;

import android.os.Bundle;

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

import java.util.Collections;
import java.util.List;

public class AdminHomeActivity extends AppCompatActivity implements ReportedProductAdapter.OnItemClickListener {

    private ActivityAdminHomeBinding binding;
    private ProductRepository productRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // This is the important part for handling status bar overlap
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        setSupportActionBar(binding.toolbar);

        productRepository = ProductRepository.getInstance();
        List<ReportedProduct> reportedProducts = productRepository.getReportedProducts();

        // Sort by report count (highest first)
        Collections.sort(reportedProducts, (o1, o2) -> Integer.compare(o2.reportCount, o1.reportCount));

        ReportedProductAdapter adapter = new ReportedProductAdapter(reportedProducts, this);
        binding.rvReportedAds.setLayoutManager(new LinearLayoutManager(this));
        binding.rvReportedAds.setAdapter(adapter);
    }

    @Override
    public void onItemClick(ReportedProduct product) {
        AdminProductDetailFragment bottomSheet = AdminProductDetailFragment.newInstance(product.getId());
        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
    }
}
