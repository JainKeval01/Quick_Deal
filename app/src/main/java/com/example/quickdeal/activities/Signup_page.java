package com.example.quickdeal.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quickdeal.R;
import com.example.quickdeal.databinding.ActivitySignupPageBinding;

public class Signup_page extends AppCompatActivity {
    ActivitySignupPageBinding sBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        sBinding = ActivitySignupPageBinding.inflate(getLayoutInflater());
        setContentView(sBinding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(sBinding.getRoot(), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        sBinding.back.setOnClickListener(v -> {
            finish();
        });

        sBinding.createaccount.setOnClickListener(v -> {
            Intent userIntent = new Intent(getApplicationContext(), TreeActivity.class);
            startActivity(userIntent);
        });

    }
}
