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
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    public ProfileFragment() {
        // Required empty public constructor
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

        // Standard Bottom Navigation selection logic to avoid nested stack issues
        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottomNavigationView);

        // Navigation to My Ads via Bottom Nav selection
        binding.menuMyAds.setOnClickListener(v -> {
            if (bottomNav != null) {
                bottomNav.setSelectedItemId(R.id.nav_ads);
            }
        });

        // Navigation to Wishlist via Bottom Nav selection
        binding.menuWishlist.setOnClickListener(v -> {
            if (bottomNav != null) {
                bottomNav.setSelectedItemId(R.id.nav_fav);
            }
        });

        // Logout Action
        binding.btnLogout.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Logging out...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(requireActivity(), Login_Page.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        // Other buttons placeholders
        binding.btnEditProfile.setOnClickListener(v -> 
            Toast.makeText(requireContext(), "Edit Profile clicked", Toast.LENGTH_SHORT).show());

        binding.menuSupport.setOnClickListener(v -> 
            Toast.makeText(requireContext(), "Help & Support clicked", Toast.LENGTH_SHORT).show());
            
        binding.btnEditImage.setOnClickListener(v -> 
            Toast.makeText(requireContext(), "Change Photo clicked", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
