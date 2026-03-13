package com.example.quickdeal.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quickdeal.R;
import com.example.quickdeal.activities.Login_Page;
import com.example.quickdeal.databinding.FragmentProfileBinding;
import com.example.quickdeal.model.Product;
import com.example.quickdeal.model.User;
import com.example.quickdeal.repository.ProductRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ProfileFragment extends Fragment implements ProductRepository.OnDataChangedListener {

    private FragmentProfileBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ProductRepository productRepository;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        // Fixed node name to "Users" to match Signup_page
        mDatabase = FirebaseDatabase.getInstance().getReference();
        productRepository = ProductRepository.getInstance();
        productRepository.setProductListener(this);

        loadUserProfile();
        setupListeners();
    }

    private void loadUserProfile() {
        String userId = mAuth.getUid();
        if (userId == null) return;

        // Fetching from "Users" (Capital U)
        mDatabase.child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null && binding != null) {
                    binding.tvProfileName.setText(user.username);
                    binding.tvProfileEmail.setText(user.email);
                    binding.tvProfileLocation.setText(user.city != null ? user.city : "No Location Set");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Profile load fail: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupListeners() {
        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottomNavigationView);

        binding.menuMyAds.setOnClickListener(v -> {
            if (bottomNav != null) {
                bottomNav.setSelectedItemId(R.id.nav_ads);
            }
        });

        binding.menuWishlist.setOnClickListener(v -> {
            if (bottomNav != null) {
                bottomNav.setSelectedItemId(R.id.nav_fav);
            }
        });

        binding.btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(requireActivity(), Login_Page.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        binding.btnEditProfile.setOnClickListener(v -> 
            Toast.makeText(requireContext(), "Edit Profile clicked", Toast.LENGTH_SHORT).show());
            
        binding.btnEditImage.setOnClickListener(v -> 
            Toast.makeText(requireContext(), "Change Photo clicked", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDataChanged(List<Product> products) {
        if (binding == null) return;
        
        String userId = mAuth.getUid();
        int activeCount = 0;
        int soldCount = 0;

        for (Product p : products) {
            if (p.sellerId != null && p.sellerId.equals(userId)) {
                if ("Available".equalsIgnoreCase(p.status)) {
                    activeCount++;
                } else if ("Sold".equalsIgnoreCase(p.status)) {
                    soldCount++;
                }
            }
        }

        binding.tvActiveAdsCount.setText(String.valueOf(activeCount));
        binding.tvSoldItemsCount.setText(String.valueOf(soldCount));
    }

    @Override
    public void onError(String error) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        productRepository.setProductListener(null);
        binding = null;
    }
}
