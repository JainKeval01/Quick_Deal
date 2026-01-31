package com.example.quickdeal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quickdeal.databinding.ActivityLoginPageBinding;

public class Login_Page extends AppCompatActivity {
    ActivityLoginPageBinding loginBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        loginBinding = ActivityLoginPageBinding.inflate(getLayoutInflater());
        setContentView(loginBinding.getRoot());

        loginBinding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(getApplication(), TreeActivity.class);
                startActivity(loginIntent);
            }
        });
    }
}