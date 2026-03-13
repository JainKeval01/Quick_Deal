package com.example.quickdeal;

import android.app.Application;
import com.cloudinary.android.MediaManager;
import java.util.HashMap;
import java.util.Map;

public class QuickDealApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initializing Cloudinary with your provided credentials
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dq0lwwkuv");
        config.put("api_key", "855852823327115");
        config.put("api_secret", "YtjYvIV1CuR6ozQikYtEn1Ww4EU");
        MediaManager.init(this, config);
    }
}
