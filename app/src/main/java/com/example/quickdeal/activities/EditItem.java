package com.example.quickdeal.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.quickdeal.adapter.SelectedImageAdapter;
import com.example.quickdeal.databinding.ActivityEditItemBinding;
import com.example.quickdeal.model.Product;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EditItem extends AppCompatActivity {

    private ActivityEditItemBinding binding;
    private ArrayList<String> imageUrls = new ArrayList<>(); 
    private ArrayList<Uri> newImageUris = new ArrayList<>(); 
    private ArrayList<Uri> allDisplayUris = new ArrayList<>(); 
    
    private SelectedImageAdapter imageAdapter;
    private ProgressDialog progressDialog;
    private String productId;
    private String sellerId;
    private long timestamp;
    private String location;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            if (result.getData().getClipData() != null) {
                                int count = result.getData().getClipData().getItemCount();
                                for (int i = 0; i < count; i++) {
                                    if (allDisplayUris.size() >= 10) {
                                        Toast.makeText(this, "Maximum limit of 10 images reached.", Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                    Uri uri = result.getData().getClipData().getItemAt(i).getUri();
                                    newImageUris.add(uri);
                                    allDisplayUris.add(uri);
                                }
                            } else if (result.getData().getData() != null) {
                                if (allDisplayUris.size() < 10) {
                                    Uri uri = result.getData().getData();
                                    newImageUris.add(uri);
                                    allDisplayUris.add(uri);
                                } else {
                                    Toast.makeText(this, "Maximum limit of 10 images reached.", Toast.LENGTH_SHORT).show();
                                }
                            }
                            updatePhotoUI();
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Processing Request");
        progressDialog.setCancelable(false);

        setupCategory();
        setupRecyclerView();
        loadData();

        binding.tvReset.setOnClickListener(v -> finish());
        binding.cvAddMainPhoto.setOnClickListener(v -> {
            if (allDisplayUris.size() >= 10) {
                Toast.makeText(this, "Maximum limit of 10 images reached.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            imagePickerLauncher.launch(intent);
        });

        binding.btnPostNow.setOnClickListener(v -> validateAndUpdate());
    }

    private void setupRecyclerView() {
        imageAdapter = new SelectedImageAdapter(allDisplayUris, position -> {
            Uri removedUri = allDisplayUris.get(position);
            allDisplayUris.remove(position);
            
            if (newImageUris.contains(removedUri)) {
                newImageUris.remove(removedUri);
            } else {
                imageUrls.remove(removedUri.toString());
            }
            updatePhotoUI();
        });
        binding.rvSelectedImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvSelectedImages.setAdapter(imageAdapter);
        binding.rvSelectedImages.setHasFixedSize(true);
    }

    private void setupCategory() {
        // Updated category list to be consistent with Home and Add Item screens
        List<String> categories = Arrays.asList("Electronics", "Cars", "Properties", "Mobiles", "Fashion", "Bikes", "Others");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        binding.spinnerCategory.setAdapter(adapter);
    }

    private void loadData() {
        productId = getIntent().getStringExtra("id");
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String price = getIntent().getStringExtra("price");
        String category = getIntent().getStringExtra("category");
        sellerId = getIntent().getStringExtra("sellerId");
        location = getIntent().getStringExtra("location");
        timestamp = getIntent().getLongExtra("timestamp", System.currentTimeMillis());
        
        boolean isNegotiable = getIntent().getBooleanExtra("isNegotiable", false);

        ArrayList<String> existingImages = getIntent().getStringArrayListExtra("images");
        if (existingImages != null) {
            imageUrls.addAll(existingImages);
            for (String url : existingImages) {
                allDisplayUris.add(Uri.parse(url));
            }
        }

        binding.etTitle.setText(title);
        binding.etPrice.setText(price);
        binding.etDescription.setText(description);
        binding.switchNegotiable.setChecked(isNegotiable);
        
        setSpinnerValue(category);
        updatePhotoUI();
    }

    private void updatePhotoUI() {
        binding.tvPhotoCount.setText(allDisplayUris.size() + "/10");
        imageAdapter.notifyDataSetChanged();
    }

    private void setSpinnerValue(String category) {
        if (category == null) return;
        ArrayAdapter adapter = (ArrayAdapter) binding.spinnerCategory.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase(category)) {
                binding.spinnerCategory.setSelection(i);
                break;
            }
        }
    }

    private void validateAndUpdate() {
        String title = binding.etTitle.getText().toString().trim();
        String price = binding.etPrice.getText().toString().trim();
        String description = binding.etDescription.getText().toString().trim();
        String category = binding.spinnerCategory.getSelectedItem().toString();
        boolean isNegotiable = binding.switchNegotiable.isChecked();

        if (title.isEmpty() || price.isEmpty() || description.isEmpty() || allDisplayUris.isEmpty()) {
            Toast.makeText(this, "Please provide all required details and at least one image.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Saving updates...");
        progressDialog.show();

        if (newImageUris.isEmpty()) {
            saveToFirebase(title, price, description, category, isNegotiable);
        } else {
            uploadNewImages(title, price, description, category, isNegotiable);
        }
    }

    private void uploadNewImages(String title, String price, String description, String category, boolean isNegotiable) {
        final int totalNew = newImageUris.size();
        final int[] count = {0};

        for (Uri uri : newImageUris) {
            MediaManager.get().upload(uri)
                    .option("upload_preset", "quickdeal_upload")
                    .callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {}
                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {}
                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            count[0]++;
                            imageUrls.add((String) resultData.get("secure_url"));
                            if (count[0] == totalNew) {
                                saveToFirebase(title, price, description, category, isNegotiable);
                            }
                        }
                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            count[0]++;
                            if (count[0] == totalNew) saveToFirebase(title, price, description, category, isNegotiable);
                        }
                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {}
                    }).dispatch();
        }
    }

    private void saveToFirebase(String title, String price, String description, String category, boolean isNegotiable) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("products").child(productId);
        
        Product updatedProduct = new Product(productId, title, price, imageUrls, description, "Available", category, sellerId, timestamp, isNegotiable, location);
        
        ref.setValue(updatedProduct).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(EditItem.this, "Information updated successfully.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(EditItem.this, "Failed to update information. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
