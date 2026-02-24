package com.example.quickdeal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

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
    }
}
