package com.example.quickdeal.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.quickdeal.adapter.SelectedImageAdapter;
import com.example.quickdeal.databinding.FragmentAddItemBinding;
import com.example.quickdeal.model.Product;
import com.example.quickdeal.model.User;
import com.example.quickdeal.repository.ProductRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddItemFragment extends Fragment {

    private FragmentAddItemBinding binding;
    private String selectedCategory = "";

    private final List<Uri> imageUris = new ArrayList<>();
    private final List<String> imageUrls = new ArrayList<>();

    private SelectedImageAdapter imageAdapter;
    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private ProductRepository productRepository;
    private String userCity = "Unknown";

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                            if (result.getData().getClipData() != null) {
                                int count = result.getData().getClipData().getItemCount();
                                for (int i = 0; i < count; i++) {
                                    if (imageUris.size() >= 10) {
                                        Toast.makeText(getContext(), "Maximum 10 images allowed", Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                    Uri imageUri = result.getData().getClipData().getItemAt(i).getUri();
                                    imageUris.add(imageUri);
                                }
                            } else if (result.getData().getData() != null) {
                                if (imageUris.size() < 10) {
                                    imageUris.add(result.getData().getData());
                                } else {
                                    Toast.makeText(getContext(), "Maximum 10 images allowed", Toast.LENGTH_SHORT).show();
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
        productRepository = ProductRepository.getInstance();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading Product");
        progressDialog.setCancelable(false);

        fetchUserCity();
        setupCategorySpinner();
        setupImageRecyclerView();
        setupListeners();

        return binding.getRoot();
    }

    private void fetchUserCity() {
        String uid = mAuth.getUid();
        if (uid == null) return;

        FirebaseDatabase.getInstance().getReference("Users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null && user.city != null) {
                            userCity = user.city;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void setupCategorySpinner() {
        String[] categories = {"Electronics", "Cars", "Properties", "Mobiles", "Fashion", "Bikes", "Others"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, categories);
        binding.spinnerCategory.setAdapter(adapter);
        
        binding.spinnerCategory.setOnClickListener(v -> binding.spinnerCategory.showDropDown());
        
        binding.spinnerCategory.setOnItemClickListener((parent, view, position, id) -> {
            selectedCategory = categories[position];
        });
    }

    private void setupImageRecyclerView() {
        imageAdapter = new SelectedImageAdapter(imageUris, position -> {
            imageUris.remove(position);
            updatePhotoUI();
        });
        binding.rvSelectedImages.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvSelectedImages.setAdapter(imageAdapter);
    }

    private void setupListeners() {
        binding.cvAddMainPhoto.setOnClickListener(v -> {
            if (imageUris.size() >= 10) {
                Toast.makeText(getContext(), "Maximum 10 images allowed", Toast.LENGTH_SHORT).show();
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
        boolean isNegotiable = binding.switchNegotiable.isChecked();

        if (title.isEmpty() || selectedCategory.isEmpty() || price.isEmpty() || description.isEmpty() || imageUris.size() < 1) {
            Toast.makeText(getContext(), "Bhai, sab details bharo aur kam se kam 1 photo dalo", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Images Cloudinary pe upload ho rahi hain...");
        progressDialog.show();
        uploadImagesToCloudinary(title, price, description, isNegotiable);
    }

    private void uploadImagesToCloudinary(String title, String price, String description, boolean isNegotiable) {
        imageUrls.clear();
        final int totalImages = imageUris.size();
        final int[] uploadCount = {0};

        for (Uri uri : imageUris) {
            MediaManager.get().upload(uri)
                    .option("upload_preset", "quickdeal_upload")
                    .callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {}

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {}

                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            uploadCount[0]++;
                            String url = (String) resultData.get("secure_url");
                            imageUrls.add(url);

                            if (uploadCount[0] == totalImages) {
                                saveProductToFirebase(title, price, description, isNegotiable);
                            }
                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            uploadCount[0]++;
                            Log.e("CloudinaryError", "Error: " + error.getDescription());
                            
                            if (uploadCount[0] == totalImages) {
                                if (imageUrls.size() > 0) {
                                    saveProductToFirebase(title, price, description, isNegotiable);
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "Bhai, images upload nahi ho payi: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {}
                    }).dispatch();
        }
    }

    private void saveProductToFirebase(String title, String price, String description, boolean isNegotiable) {
        progressDialog.setMessage("Product detail save ho rahi hai...");
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("products");
        String productId = productsRef.push().getKey();
        String userId = mAuth.getUid();
        long timestamp = System.currentTimeMillis();

        // Storing all data including the user's city
        Product product = new Product(productId, title, price, imageUrls, description, "Available", selectedCategory, userId, timestamp, isNegotiable, userCity);

        productRepository.addProduct(product, task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Ad post ho gaya bhai!", Toast.LENGTH_SHORT).show();
                resetForm();
            } else {
                Toast.makeText(getContext(), "Database me save karne me error aaya", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetForm() {
        binding.etTitle.setText("");
        binding.etPrice.setText("");
        binding.etDescription.setText("");
        binding.spinnerCategory.setText("");
        selectedCategory = "";
        binding.switchNegotiable.setChecked(false);
        imageUris.clear();
        imageUrls.clear();
        updatePhotoUI();
    }
}
