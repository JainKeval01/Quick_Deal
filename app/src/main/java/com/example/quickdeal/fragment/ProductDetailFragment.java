package com.example.quickdeal.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quickdeal.R;
import com.example.quickdeal.adapter.ProductImageAdapter;
import com.example.quickdeal.databinding.FragmentProductDetailBinding;
import com.example.quickdeal.model.Product;
import com.example.quickdeal.repository.ProductRepository;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ProductDetailFragment extends BottomSheetDialogFragment {

    private FragmentProductDetailBinding binding;
    private Product product;
    private ProductRepository productRepository;

    public static ProductDetailFragment newInstance(Product product) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable("product", product);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            product = getArguments().getParcelable("product");
        }
        productRepository = ProductRepository.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setDraggable(false);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProductDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (product != null) {
            setupViewPager();
            populateUI();
            updateWishlistButton();
        }

        binding.ivClose.setOnClickListener(v -> dismiss());

        binding.btnWishlist.setOnClickListener(v -> {
            product.isFavorite = !product.isFavorite;
            productRepository.toggleFavoriteStatus(product);
            updateWishlistButton();
        });
    }

    private void setupViewPager() {
        ProductImageAdapter adapter = new ProductImageAdapter(product.images);
        binding.viewPager.setAdapter(adapter);
    }

    private void populateUI() {
        binding.tvPrice.setText(product.price);
        binding.tvTitle.setText(product.name);
        binding.tvDescription.setText(product.description);
        binding.tvStatus.setText(product.status);

        if ("AVAILABLE".equalsIgnoreCase(product.status)) {
            binding.tvStatus.setBackgroundResource(R.drawable.bg_status_green);
        } else {
            binding.tvStatus.setBackgroundResource(R.drawable.bg_status_red);
        }
    }

    private void updateWishlistButton() {
        if (product.isFavorite) {
            binding.btnWishlist.setIconResource(R.drawable.ic_favorite_filled);
            binding.btnWishlist.setText("Wishlisted");
        } else {
            binding.btnWishlist.setIconResource(R.drawable.ic_wishlist_add);
            binding.btnWishlist.setText("Wishlist");
        }
    }
}
