package com.example.quickdeal.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.example.quickdeal.R;
import com.example.quickdeal.adapter.ProductImageAdapter;
import com.example.quickdeal.databinding.FragmentProductDetailBinding;
import com.example.quickdeal.model.Product;
import com.example.quickdeal.model.ReportedProduct;
import com.example.quickdeal.model.User;
import com.example.quickdeal.repository.ProductRepository;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProductDetailFragment extends BottomSheetDialogFragment {

    private FragmentProductDetailBinding binding;
    private Product product;
    private ProductRepository productRepository;
    private String currentUserName = "Unknown";
    private String sellerPhone;
    private String currentUserId;

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
        currentUserId = FirebaseAuth.getInstance().getUid();
        fetchCurrentUserName();
    }

    private void fetchCurrentUserName() {
        if (currentUserId == null) return;
        FirebaseDatabase.getInstance().getReference("Users").child(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) currentUserName = user.username;
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
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
            loadSellerInfo();

            if (product.sellerId != null && product.sellerId.equals(currentUserId)) {
                binding.report.setVisibility(View.GONE);
                binding.btnChat.setVisibility(View.GONE);
                binding.icCall.setVisibility(View.GONE);
                binding.btnWishlist.setVisibility(View.GONE);
            }
        }

        binding.ivClose.setOnClickListener(v -> dismiss());

        binding.btnWishlist.setOnClickListener(v -> {
            product.isFavorite = !product.isFavorite;
            productRepository.toggleFavoriteStatus(product);
            updateWishlistButton();
        });

        binding.report.setOnClickListener(v -> showReportDialog());

        binding.btnChat.setOnClickListener(v -> {
            if (sellerPhone != null && !sellerPhone.isEmpty()) {
                openWhatsApp();
            } else {
                Toast.makeText(getContext(), "Seller contact info not available", Toast.LENGTH_SHORT).show();
            }
        });

        binding.icCall.setOnClickListener(v -> {
            if (sellerPhone != null && !sellerPhone.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + sellerPhone));
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Seller phone number not available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openWhatsApp() {
        String message = "Hi, I'm interested in your product: " + product.name +
                "\nPrice: ₹" + product.price;

        String url = "https://api.whatsapp.com/send?phone=" + sellerPhone + "&text=" + Uri.encode(message);
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getContext(), "WhatsApp not installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadSellerInfo() {
        if (product.sellerId == null) return;

        FirebaseDatabase.getInstance().getReference("Users")
                .child(product.sellerId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null && binding != null) {
                            binding.tvSellerName.setText(user.username);
                            sellerPhone = user.phone;
                            
                            // Corrected variable name to profileImageUrl as per User model
                            if (user.profileImageUrl != null && !user.profileImageUrl.isEmpty()) {
                                Glide.with(requireContext())
                                        .load(user.profileImageUrl)
                                        .placeholder(R.drawable.ic_profile)
                                        .error(R.drawable.ic_profile)
                                        .into(binding.ivSellerProfile);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void showReportDialog() {
        final String[] reportReasons = {
                "Scam or Misleading Ad",
                "Prohibited or Banned Item",
                "Offensive Content",
                "Item is already sold or unavailable",
                "Incorrect category or information"
        };
        final int[] checkedItem = {-1};

        new AlertDialog.Builder(requireContext())
                .setTitle("Report this Ad")
                .setSingleChoiceItems(reportReasons, checkedItem[0], (dialog, which) -> {
                    checkedItem[0] = which;
                })
                .setPositiveButton("Submit", (dialog, which) -> {
                    if (checkedItem[0] != -1) {
                        submitReport(reportReasons[checkedItem[0]]);
                    } else {
                        Toast.makeText(getContext(), "Please select a reason", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void submitReport(String reason) {
        String userId = FirebaseAuth.getInstance().getUid();
        String imageUrl = (product.images != null && !product.images.isEmpty()) ? product.images.get(0) : "";
        String reportId = FirebaseDatabase.getInstance().getReference("reports").push().getKey();

        ReportedProduct report = new ReportedProduct(
                reportId,
                product.getId(),
                product.name,
                userId,
                currentUserName,
                reason,
                "Just now",
                imageUrl,
                1,
                "Pending",
                false
        );

        productRepository.reportProduct(report, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Product reported", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to report product", Toast.LENGTH_SHORT).show();
            }
            dismiss();
        });
    }

    private void setupViewPager() {
        if (product.images != null) {
            ProductImageAdapter adapter = new ProductImageAdapter(product.images);
            binding.viewPager.setAdapter(adapter);
        }
    }

    private void populateUI() {
        binding.tvPrice.setText("₹" + product.price);
        binding.tvTitle.setText(product.name);
        binding.tvDescription.setText(product.description);
        
        if (product.status != null) {
            binding.tvStatus.setText(product.status.toUpperCase());
            
            if (product.status.equalsIgnoreCase("New")) {
                binding.tvStatus.setBackgroundResource(R.drawable.bg_status_green);
                binding.tvStatus.setTextColor(Color.WHITE);
            } else if (product.status.equalsIgnoreCase("Used - Like New")) {
                binding.tvStatus.setBackgroundResource(R.drawable.bg_status_yellow);
                binding.tvStatus.setTextColor(Color.BLACK); 
            } else {
                binding.tvStatus.setBackgroundResource(R.drawable.bg_status_red);
                binding.tvStatus.setTextColor(Color.WHITE);
            }
        }

        if (product.isNegotiable) {
            binding.tvNegotiable.setText("Price is Negotiable");
        } else {
            binding.tvNegotiable.setText("Fixed Price");
        }

        if (product.location != null && !product.location.isEmpty()) {
            binding.tvLocation.setText(product.location);
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
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
