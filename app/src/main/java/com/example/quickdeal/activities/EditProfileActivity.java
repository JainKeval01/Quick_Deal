package com.example.quickdeal.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quickdeal.databinding.ActivityEditProfileBinding;
import com.example.quickdeal.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String userId;

    private final String[] gujaratCities = {
            "Ahmedabad", "Gandhinagar", "Surat", "Vadodara", "Rajkot",
            "Bhavnagar", "Jamnagar", "Junagadh", "Anand", "Navsari",
            "Morbi", "Nadiad", "Bharuch", "Mehsana", "Bhuj"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        userId = mAuth.getUid();

        setupCityDropdown();
        loadUserData();

        binding.ivBack.setOnClickListener(v -> finish());
        binding.btnSaveProfile.setOnClickListener(v -> updateProfile());
    }

    private void setupCityDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, gujaratCities);
        binding.etCity.setAdapter(adapter);
        binding.etCity.setOnClickListener(v -> binding.etCity.showDropDown());
    }

    private void loadUserData() {
        if (userId == null) return;

        mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    binding.etUsername.setText(user.username);
                    binding.etPhone.setText(user.phone);
                    binding.etCity.setText(user.city);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfileActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfile() {
        String username = binding.etUsername.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String city = binding.etCity.getText().toString().trim();

        if (username.isEmpty() || phone.isEmpty() || city.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("username", username);
        updates.put("phone", phone);
        updates.put("city", city);

        mDatabase.child(userId).updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(EditProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(EditProfileActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
