package com.example.quickdeal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quickdeal.databinding.ActivityLoginPageBinding;
import com.google.firebase.auth.FirebaseAuth;

public class Login_Page extends AppCompatActivity {

    ActivityLoginPageBinding loginBinding;

    private static final String ADMIN_EMAIL = "admin@quickdeal.com";
    private static final String ADMIN_PASSWORD = "admin123";

    FirebaseAuth mAuth;

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

        mAuth = FirebaseAuth.getInstance();

        loginBinding.loginButton.setOnClickListener(v -> {

            String email = loginBinding.emailEditText.getText().toString().trim();
            String password = loginBinding.passwordEditText.getText().toString().trim();

            if (validateInput(email, password)) {

                // ✅ ADMIN LOGIN CHECK
                if (email.equals(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD)) {

                    Toast.makeText(Login_Page.this,
                            "Admin Login Successful",
                            Toast.LENGTH_SHORT).show();

                    Intent adminIntent =
                            new Intent(Login_Page.this, AdminHomeActivity.class);
                    startActivity(adminIntent);
                    finish();

                } else {
                    // ✅ FIREBASE USER LOGIN
                    loginUser(email, password);
                }
            }
        });

        loginBinding.signUp.setOnClickListener(v -> {
            Intent userIntent =
                    new Intent(Login_Page.this, Signup_page.class);
            startActivity(userIntent);
        });
    }

    private void loginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        Toast.makeText(Login_Page.this,
                                "Login Successful",
                                Toast.LENGTH_SHORT).show();

                        Intent userIntent =
                                new Intent(Login_Page.this, TreeActivity.class);
                        startActivity(userIntent);
                        finish();

                    } else {
                        Toast.makeText(Login_Page.this,
                                "Invalid Email or Password",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean validateInput(String email, String password) {

        if (email.isEmpty()) {
            loginBinding.emailEditText.setError("Email is required");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginBinding.emailEditText.setError("Valid email required");
            return false;
        }

        if (password.isEmpty()) {
            loginBinding.passwordEditText.setError("Password is required");
            return false;
        }

        if (password.length() < 6) {
            loginBinding.passwordEditText.setError("Minimum 6 characters required");
            return false;
        }

        return true;
    }
}