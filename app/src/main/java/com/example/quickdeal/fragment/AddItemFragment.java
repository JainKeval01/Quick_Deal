package com.example.quickdeal.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.quickdeal.adapter.SelectedImageAdapter;
import com.example.quickdeal.databinding.FragmentAddItemBinding;
import com.example.quickdeal.model.Product;
import com.example.quickdeal.repository.ProductRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddItemFragment extends Fragment {

    private FragmentAddItemBinding binding;
    private String selectedCategory = "";

    private final List<Uri> imageUris = new ArrayList<>();
    private final List<String> imageUrls = new ArrayList<>();

    private SelectedImageAdapter imageAdapter;
    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private ProductRepository productRepository;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {

                            if (result.getData().getClipData() != null) {

                                int count = result.getData().getClipData().getItemCount();

                                for (int i = 0; i < count; i++) {

                                    if (imageUris.size() >= 10) {
                                        Toast.makeText(getContext(),
                                                "Maximum 10 images allowed",
                                                Toast.LENGTH_SHORT).show();
                                        break;
                                    }

                                    Uri imageUri = result.getData().getClipData().getItemAt(i).getUri();
                                    imageUris.add(imageUri);
                                }

                            } else if (result.getData().getData() != null) {

                                if (imageUris.size() < 10) {
                                    imageUris.add(result.getData().getData());
                                } else {
                                    Toast.makeText(getContext(),
                                            "Maximum 10 images allowed",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }

                            updatePhotoUI();
                        }
                    });

    public AddItemFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAddItemBinding.inflate(inflater, container, false);

        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference("product_images");
        productRepository = ProductRepository.getInstance();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading Product");
        progressDialog.setCancelable(false);

        setupCategorySpinner();
        setupImageRecyclerView();
        setupListeners();

        return binding.getRoot();
    }

    private void setupCategorySpinner() {

        List<String> categories = new ArrayList<>();

        categories.add("Select Category");
        categories.add("Electronics");
        categories.add("Cars");
        categories.add("Properties");
        categories.add("Mobiles");
        categories.add("Fashion");
        categories.add("Bikes");

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        categories);

        binding.spinnerCategory.setAdapter(adapter);

        binding.spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedCategory = position > 0 ? categories.get(position) : "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupImageRecyclerView() {

        imageAdapter = new SelectedImageAdapter(imageUris, position -> {

            imageUris.remove(position);
            updatePhotoUI();

        });

        binding.rvSelectedImages.setLayoutManager(
                new LinearLayoutManager(getContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false)
        );

        binding.rvSelectedImages.setAdapter(imageAdapter);
    }

    private void setupListeners() {

        binding.cvAddMainPhoto.setOnClickListener(v -> {

            if (imageUris.size() >= 10) {

                Toast.makeText(getContext(),
                        "Maximum 10 images allowed",
                        Toast.LENGTH_SHORT).show();

                return;
            }

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

            intent.setType("image/*");

            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

            imagePickerLauncher.launch(intent);
        });

        binding.btnPostNow.setOnClickListener(v -> validateAndUpload());

        binding.tvReset.setOnClickListener(v -> resetForm());
    }

    private void updatePhotoUI() {

        binding.tvPhotoCount.setText(imageUris.size() + "/10");

        imageAdapter.notifyDataSetChanged();

        if (imageUris.size() >= 10) {

            binding.cvAddMainPhoto.setEnabled(false);
            binding.cvAddMainPhoto.setAlpha(0.5f);

        } else {

            binding.cvAddMainPhoto.setEnabled(true);
            binding.cvAddMainPhoto.setAlpha(1f);
        }
    }

    private void validateAndUpload() {

        String title = binding.etTitle.getText().toString().trim();
        String price = binding.etPrice.getText().toString().trim();
        String description = binding.etDescription.getText().toString().trim();

        if (title.isEmpty() || selectedCategory.isEmpty()
                || price.isEmpty() || description.isEmpty()
                || imageUris.size() < 1) {

            Toast.makeText(getContext(),
                    "Bhai, sab details bharo aur kam se kam 1 photo dalo",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        progressDialog.setMessage("Images upload ho rahi hain...");
        progressDialog.show();

        uploadImages(title, price, description);
    }

    private void uploadImages(String title, String price, String description) {

        imageUrls.clear();

        final int totalImages = imageUris.size();

        for (Uri uri : imageUris) {

            StorageReference fileRef = mStorage.child(UUID.randomUUID().toString());

            fileRef.putFile(uri).addOnSuccessListener(taskSnapshot -> {

                fileRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {

                    imageUrls.add(downloadUri.toString());

                    if (imageUrls.size() == totalImages) {

                        saveProduct(title, price, description);
                    }

                });

            }).addOnFailureListener(e -> {

                progressDialog.dismiss();

                Toast.makeText(getContext(),
                        "Error uploading image: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void saveProduct(String title, String price, String description) {

        progressDialog.setMessage("Product detail save ho rahi hai...");

        DatabaseReference productsRef =
                FirebaseDatabase.getInstance().getReference("products");

        String productId = productsRef.push().getKey();

        String userId = mAuth.getUid();

        long timestamp = System.currentTimeMillis();

        Product product = new Product(
                productId,
                title,
                price,
                imageUrls,
                description,
                "Available",
                selectedCategory,
                userId,
                timestamp
        );

        productRepository.addProduct(product, task -> {

            progressDialog.dismiss();

            if (task.isSuccessful()) {

                Toast.makeText(getContext(),
                        "Ad post ho gaya bhai!",
                        Toast.LENGTH_SHORT).show();

                resetForm();

            } else {

                Toast.makeText(getContext(),
                        "Save karne me error aaya",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetForm() {

        binding.etTitle.setText("");
        binding.etPrice.setText("");
        binding.etDescription.setText("");

        binding.spinnerCategory.setSelection(0);

        imageUris.clear();
        imageUrls.clear();

        updatePhotoUI();
    }
}