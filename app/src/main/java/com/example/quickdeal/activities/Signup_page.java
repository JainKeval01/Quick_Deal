package com.example.quickdeal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
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

    private final String[] gujaratCities = {
            "Ahmedabad", "Gandhinagar", "Surat", "Vadodara", "Rajkot",
            "Bhavnagar", "Jamnagar", "Junagadh", "Anand", "Navsari",
            "Morbi", "Nadiad", "Bharuch", "Mehsana", "Bhuj"
    };

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

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        setupCityDropdown();

        sBinding.back.setOnClickListener(v -> {
            finish();
        });
        
        sBinding.login1.setOnClickListener(v -> finish());

        sBinding.createaccount.setOnClickListener(v -> {
            if (validateInput()) {
                registerUser();
            }
        });
    }

    private void setupCityDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, gujaratCities);
        sBinding.etCity.setAdapter(adapter);
        sBinding.etCity.setOnClickListener(v -> sBinding.etCity.showDropDown());
        
        sBinding.etCity.setOnItemClickListener((parent, view, position, id) -> {
            String selection = (String) parent.getItemAtPosition(position);
            sBinding.etCity.setText(selection, false);
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
                                        Toast.makeText(Signup_page.this, "Account created successfully.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Signup_page.this, TreeActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(Signup_page.this, "Failed to save user data. Please try again.", Toast.LENGTH_LONG).show();
                                    }
                                });
                    } else {
                        Toast.makeText(Signup_page.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
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
            sBinding.etUsername.setError("Username is required");
            return false;
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            sBinding.etEmail.setError("Please enter a valid email address");
            return false;
        }
        if (phone.length() != 10) {
            sBinding.etPhone.setError("Please enter a valid 10-digit phone number");
            return false;
        }
        if (city.isEmpty()) {
            sBinding.etCity.setError("Please select a city");
            return false;
        }
        if (password.length() < 6) {
            sBinding.etPassword.setError("Password must be at least 6 characters");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            sBinding.etConfirmPassword.setError("Passwords do not match");
            return false;
        }
        return true;
    }
}
