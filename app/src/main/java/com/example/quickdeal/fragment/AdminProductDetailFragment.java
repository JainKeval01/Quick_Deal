package com.example.quickdeal.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quickdeal.adapter.ProductImageAdapter;
import com.example.quickdeal.databinding.FragmentAdminProductDetailBinding;
import com.example.quickdeal.model.Product;
import com.example.quickdeal.model.ReportedProduct;
import com.example.quickdeal.repository.ProductRepository;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AdminProductDetailFragment extends BottomSheetDialogFragment {

    private FragmentAdminProductDetailBinding binding;
    private String productId;
    private ProductRepository productRepository;

    public static AdminProductDetailFragment newInstance(String productId) {
        AdminProductDetailFragment fragment = new AdminProductDetailFragment();
        Bundle args = new Bundle();
        args.putString("productId", productId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productId = getArguments().getString("productId");
        }
        productRepository = ProductRepository.getInstance();
        setCancelable(false); // Prevent dismissing by clicking outside
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(d -> {
            FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setDraggable(false);
                behavior.setHideable(false); // Prevent dragging down to dismiss
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminProductDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.ivClose.setOnClickListener(v -> dismiss());

        ReportedProduct reportedProduct = productRepository.getReportedProductById(productId);
        Product product = productRepository.getProductById(productId);

        if (reportedProduct != null && product != null) {
            binding.tvPrice.setText(product.price);
            binding.tvTitle.setText(product.name);
            binding.tvDescription.setText(product.description);
            binding.tvSellerName.setText(reportedProduct.getSellerName());
            binding.tvReportReason.setText("Scam or Misleading Ad");

            ProductImageAdapter adapter = new ProductImageAdapter(product.images);
            binding.viewPager.setAdapter(adapter);
        }
    }
}
