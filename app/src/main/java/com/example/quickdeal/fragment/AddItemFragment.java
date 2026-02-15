package com.example.quickdeal.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.quickdeal.R;
import com.example.quickdeal.databinding.FragmentAddItemBinding;


import java.util.ArrayList;
import java.util.List;

public class AddItemFragment extends Fragment {

    FragmentAddItemBinding binding;
    private String selectedCategory = "";
    private String selectedCondition = "New";

    public AddItemFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddItemBinding.inflate(inflater, container, false);

        setupCategorySpinner();
        setupListeners();
        setupDescriptionCounter();

        return binding.getRoot();
    }

    private void setupCategorySpinner() {
        // Categories list
        List<String> categories = new ArrayList<>();
        categories.add("Select Category");
        categories.add("Cars");
        categories.add("Properties");
        categories.add("Mobiles");
        categories.add("Fashion");
        categories.add("Bikes");

        // Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                categories
        );

        binding.spinnerCategory.setAdapter(adapter);

        // Selection listener
        binding.spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // Skip "Select Category"
                    selectedCategory = categories.get(position);
                } else {
                    selectedCategory = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = "";
            }
        });
    }

    private void setupListeners() {
        // Reset button
        binding.tvReset.setOnClickListener(v -> resetForm());

        // Main photo click
        binding.cvAddMainPhoto.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Camera will open here", Toast.LENGTH_SHORT).show();
        });

        // Condition chips
        binding.chipNew.setOnClickListener(v -> selectCondition("New"));
        binding.chipUsedLikeNew.setOnClickListener(v -> selectCondition("Used - Like New"));
        binding.chipUsedGood.setOnClickListener(v -> selectCondition("Used - Good"));

        // Location selector
        binding.llLocationSelector.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Location selector will open", Toast.LENGTH_SHORT).show();
        });

        // Post Now button
        binding.btnPostNow.setOnClickListener(v -> postAd());
    }

    private void selectCondition(String condition) {
        selectedCondition = condition;

        binding.chipNew.setChecked(false);
        binding.chipUsedLikeNew.setChecked(false);
        binding.chipUsedGood.setChecked(false);

        if (condition.equals("New")) {
            binding.chipNew.setChecked(true);
        } else if (condition.equals("Used - Like New")) {
            binding.chipUsedLikeNew.setChecked(true);
        } else if (condition.equals("Used - Good")) {
            binding.chipUsedGood.setChecked(true);
        }
    }

    private void setupDescriptionCounter() {
        binding.etDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tvDescriptionCounter.setText(s.length() + "/2000");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void resetForm() {
        binding.etTitle.setText("");
        binding.spinnerCategory.setSelection(0);
        selectedCategory = "";
        selectCondition("New");
        binding.etPrice.setText("");
        binding.switchNegotiable.setChecked(false);
        binding.etDescription.setText("");
    }

    private void postAd() {
        String title = binding.etTitle.getText().toString().trim();
        String price = binding.etPrice.getText().toString().trim();
        String description = binding.etDescription.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(getContext(), "Please enter title", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedCategory.isEmpty()) {
            Toast.makeText(getContext(), "Please select category", Toast.LENGTH_SHORT).show();
            return;
        }

        if (price.isEmpty()) {
            Toast.makeText(getContext(), "Please enter price", Toast.LENGTH_SHORT).show();
            return;
        }

        if (description.isEmpty()) {
            Toast.makeText(getContext(), "Please add description", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getContext(), "Ad posted successfully!", Toast.LENGTH_SHORT).show();
        resetForm();
    }
}