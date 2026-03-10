package com.example.quickdeal.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.quickdeal.R;
import com.example.quickdeal.databinding.ActivityEditItemBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditItem extends AppCompatActivity {

    ActivityEditItemBinding binding;
    ArrayList<String> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupCategory();
        loadData();

        binding.tvReset.setOnClickListener(v -> finish());

        binding.btnPostNow.setOnClickListener(v -> {

            String newTitle = binding.etTitle.getText().toString();
            String newPrice = binding.etPrice.getText().toString();
            String newDescription = binding.etDescription.getText().toString();

            if (newTitle.isEmpty() || newPrice.isEmpty() || newDescription.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Item Updated", Toast.LENGTH_SHORT).show();

            finish();
        });
    }

    private void setupCategory() {

        List<String> categories = Arrays.asList(
                "Mobiles",
                "Electronics",
                "Vehicles",
                "Furniture",
                "Fashion",
                "Books",
                "Sports",
                "Others"
        );

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item,
                        categories);

        binding.spinnerCategory.setAdapter(adapter);
    }

    private void loadData() {

        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String price = getIntent().getStringExtra("price");
        String category = getIntent().getStringExtra("category");

        images = getIntent().getStringArrayListExtra("images");

        binding.etTitle.setText(title);
        binding.etPrice.setText(price);
        binding.etDescription.setText(description);

        setSpinnerValue(category);

        showImages();
    }

    private void setSpinnerValue(String category) {

        if (category == null) return;

        ArrayAdapter adapter = (ArrayAdapter) binding.spinnerCategory.getAdapter();

        for (int i = 0; i < adapter.getCount(); i++) {

            if (adapter.getItem(i).toString().equals(category)) {

                binding.spinnerCategory.setSelection(i);
                break;
            }
        }
    }

    private void showImages() {

        if (images == null) return;

        binding.imageContainer.removeAllViews();

        for (String url : images) {

            ImageView image = new ImageView(this);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(300, 300);

            params.setMargins(16,0,16,0);

            image.setLayoutParams(params);
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);

            Glide.with(this)
                    .load(url)
                    .placeholder(R.drawable.loading)
                    .into(image);

            binding.imageContainer.addView(image);
        }
    }
}