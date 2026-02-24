package com.example.quickdeal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quickdeal.databinding.ActivityLoginPageBinding;

public class Login_Page extends AppCompatActivity {
    ActivityLoginPageBinding loginBinding;
    private static final String ADMIN_EMAIL = "admin@quickdeal.com";
    private static final String ADMIN_PASSWORD = "admin123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        loginBinding = ActivityLoginPageBinding.inflate(getLayoutInflater());
        setContentView(loginBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(loginBinding.getRoot(), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
        loginBinding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginBinding.emailEditText.getText().toString().trim();
                String password = loginBinding.passwordEditText.getText().toString().trim();

                if (email.equals(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD)) {
                    // Admin Login
                    Toast.makeText(Login_Page.this, "Admin Login Successful", Toast.LENGTH_SHORT).show();
                    Intent adminIntent = new Intent(getApplicationContext(), AdminHomeActivity.class);
                    startActivity(adminIntent);
                    finish();
                } else {
                    // Regular User Login (For now, any login is regular)
                    Intent userIntent = new Intent(getApplicationContext(), TreeActivity.class);
                    startActivity(userIntent);
                    finish();
                }
            }
        });

        loginBinding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userIntent = new Intent(getApplicationContext(), Signup_page.class);
                startActivity(userIntent);
            }
        });
    }
}
