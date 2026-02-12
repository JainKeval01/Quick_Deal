package com.example.quickdeal.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.quickdeal.R;
import com.example.quickdeal.databinding.ActivityTreeBinding;

public class TreeActivity extends AppCompatActivity {

    ActivityTreeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTreeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            binding.navHostFragment.setPadding(0, insets.top, 0, 0);
            binding.bottomNavigationView.setPadding( binding.bottomNavigationView.getPaddingLeft(),
                    binding.bottomNavigationView.getPaddingTop(),
                    binding.bottomNavigationView.getPaddingRight(),
                    insets.bottom);

            return WindowInsetsCompat.CONSUMED;
        });


        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();

            NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);
        }
    }
}