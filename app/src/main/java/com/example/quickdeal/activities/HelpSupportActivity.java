package com.example.quickdeal.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.quickdeal.databinding.ActivityHelpSupportBinding;

public class HelpSupportActivity extends AppCompatActivity {

    private ActivityHelpSupportBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHelpSupportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.ivBack.setOnClickListener(v -> finish());
    }
}
