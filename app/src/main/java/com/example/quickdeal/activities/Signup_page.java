package com.example.quickdeal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quickdeal.databinding.ActivitySignupPageBinding;
import com.example.quickdeal.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup_page extends AppCompatActivity {

    ActivitySignupPageBinding sBinding;

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

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

        // Firebase init
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        sBinding.back.setOnClickListener(v -> finish());
        sBinding.login1.setOnClickListener(v -> finish());

        sBinding.createaccount.setOnClickListener(v -> {
            if (validateInput()) {
                registerUser();
            }
        });
    }

    private void registerUser() {

        String username = sBinding.etUsername.getText().toString().trim();
        String email = sBinding.etEmail.getText().toString().trim();
        String phone = sBinding.etPhone.getText().toString().trim();
        String city = sBinding.etCity.getText().toString().trim();
        String password = sBinding.etPassword.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        String uid = firebaseUser.getUid();

                        User user = new User(uid, username, email, phone, city);

                        databaseReference.child(uid)
                                .setValue(user)
                                .addOnCompleteListener(dbTask -> {

                                    if (dbTask.isSuccessful()) {

                                        Toast.makeText(
                                                Signup_page.this,
                                                "Registration Successful",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        Intent intent =
                                                new Intent(Signup_page.this, TreeActivity.class);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        Toast.makeText(
                                                Signup_page.this,
                                                "Database Error",
                                                Toast.LENGTH_LONG
                                        ).show();
                                    }
                                });

                    } else {
                        Toast.makeText(
                                Signup_page.this,
                                task.getException().getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private boolean validateInput() {

        String username = sBinding.etUsername.getText().toString().trim();
        String email = sBinding.etEmail.getText().toString().trim();
        String phone = sBinding.etPhone.getText().toString().trim();
        String city = sBinding.etCity.getText().toString().trim();
        String password = sBinding.etPassword.getText().toString().trim();
        String confirmPassword = sBinding.etConfirmPassword.getText().toString().trim();

        if (username.isEmpty()) {
            sBinding.etUsername.setError("Username required");
            return false;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            sBinding.etEmail.setError("Valid email required");
            return false;
        }

        if (phone.length() != 10) {
            sBinding.etPhone.setError("10 digit phone required");
            return false;
        }

        if (city.isEmpty()) {
            sBinding.etCity.setError("City required");
            return false;
        }

        if (password.length() < 6) {
            sBinding.etPassword.setError("Min 6 characters");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            sBinding.etConfirmPassword.setError("Password mismatch");
            return false;
        }

        return true;
    }
}