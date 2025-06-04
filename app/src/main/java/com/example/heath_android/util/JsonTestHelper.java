package com.example.heath_android.util;

import android.util.Log;
import com.example.heath_android.data.model.auth.LoginResponse;
import com.google.gson.Gson;

/**
 * Helper class to test JSON mapping for LoginResponse
 */
public class JsonTestHelper {
    private static final String TAG = "JsonTestHelper";
    
    /**
     * Test JSON mapping with sample backend response
     */
    public static void testLoginResponseMapping() {
        String sampleJson = "{\n" +
                "    \"token\": \"eyJhbGciOiJIUzI1NiJ9.sample\",\n" +
                "    \"expiresIn\": 3600000,\n" +
                "    \"userId\": \"14c22e32-7948-456c-8ba1-be9333987edf\",\n" +
                "    \"isUserInfoInitialized\": false\n" +
                "}";
        
        try {
            Gson gson = new Gson();
            LoginResponse response = gson.fromJson(sampleJson, LoginResponse.class);
            
            Log.d(TAG, "=== JSON Mapping Test ===");
            Log.d(TAG, "Original JSON: " + sampleJson);
            Log.d(TAG, "Mapped Token: " + response.getToken());
            Log.d(TAG, "Mapped ExpiresIn: " + response.getExpiresIn());
            Log.d(TAG, "Mapped UserId: " + response.getUserId());
            Log.d(TAG, "Mapped IsUserInfoInitialized: " + response.isUserInfoInitialized());
            Log.d(TAG, "=== Test Complete ===");
            
            if (response.getUserId() != null && !response.getUserId().isEmpty()) {
                Log.d(TAG, "✅ JSON mapping successful - userId found!");
            } else {
                Log.e(TAG, "❌ JSON mapping failed - userId is null or empty!");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "❌ JSON mapping failed with exception: " + e.getMessage());
        }
    }
} 