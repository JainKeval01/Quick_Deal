package com.example.quickdeal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quickdeal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    // Check if Admin
                    if ("admin@quickdeal.com".equals(currentUser.getEmail())) {
                        startActivity(new Intent(MainActivity.this, AdminHomeActivity.class));
                    } else {
                        startActivity(new Intent(MainActivity.this, TreeActivity.class));
                    }
                } else {
                    startActivity(new Intent(MainActivity.this, Login_Page.class));
                }
                finish();
            }
        }, 2000);
    }
}
