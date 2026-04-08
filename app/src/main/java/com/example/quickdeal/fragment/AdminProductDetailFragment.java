package com.example.quickdeal.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quickdeal.adapter.ProductImageAdapter;
import com.example.quickdeal.databinding.FragmentAdminProductDetailBinding;
import com.example.quickdeal.model.Product;
import com.example.quickdeal.model.User;
import com.example.quickdeal.repository.ProductRepository;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminProductDetailFragment extends BottomSheetDialogFragment {

    private FragmentAdminProductDetailBinding binding;
    private String productId;
    private String reportReason;
    private ProductRepository productRepository;
    private String sellerPhone = "";

    public static AdminProductDetailFragment newInstance(String productId, String reportReason) {
        AdminProductDetailFragment fragment = new AdminProductDetailFragment();
        Bundle args = new Bundle();
        args.putString("productId", productId);
        args.putString("reportReason", reportReason);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productId = getArguments().getString("productId");
            reportReason = getArguments().getString("reportReason");
        }
        productRepository = ProductRepository.getInstance();
        setCancelable(false);
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
                behavior.setHideable(false);
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

        Product product = productRepository.getProductById(productId);

        if (product != null) {
            binding.tvPrice.setText("₹" + product.price);
            binding.tvTitle.setText(product.name);
            binding.tvDescription.setText(product.description);
            binding.tvReportReason.setText(reportReason != null ? reportReason : "No reason provided");
            loadSellerInfo(product.sellerId);

            if (product.images != null && !product.images.isEmpty()) {
                ProductImageAdapter adapter = new ProductImageAdapter(product.images);
                binding.viewPager.setAdapter(adapter);
            }
        } else {
            Toast.makeText(getContext(), "Product not found", Toast.LENGTH_SHORT).show();
            dismiss();
        }

        binding.btnBanProduct.setOnClickListener(v -> {
            productRepository.deleteProduct(productId);
            Toast.makeText(getContext(), "Ad Deleted", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        binding.btnChat.setOnClickListener(v -> {
            if (!sellerPhone.isEmpty()) {
                String url = "https://api.whatsapp.com/send?phone=+91" + sellerPhone;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            } else {
                Toast.makeText(getContext(), "Seller phone not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSellerInfo(String sellerId) {
        if (sellerId == null) return;

        FirebaseDatabase.getInstance().getReference("Users").child(sellerId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null && binding != null) {
                            binding.tvSellerName.setText(user.username);
                            sellerPhone = user.phone;
                            if (user.city != null) {
                                binding.tvLocation.setText(user.city);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}
