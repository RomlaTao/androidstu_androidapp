package com.example.heath_android.util;

import android.content.Context;
import android.util.Log;

import com.example.heath_android.data.model.onboarding.UserInfoRequest;
import com.example.heath_android.data.model.onboarding.UserInfoResponse;
import com.example.heath_android.data.api.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Helper class demonstrating how ApiUrlBuilder works with ApiService
 * ApiUrlBuilder creates URLs, ApiService sends the actual requests
 */
public class ApiServiceHelper {
    private static final String TAG = "ApiServiceHelper";
    private final ApiUrlBuilder apiUrlBuilder;
    private final ApiService apiService;

    public ApiServiceHelper(Context context) {
        this.apiUrlBuilder = new ApiUrlBuilder(context);
        
        // Initialize Retrofit with base URL
        String baseUrl = "http://localhost:8080/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        this.apiService = retrofit.create(ApiService.class);
    }

    /**
     * Example: Update user info using userId from ApiUrlBuilder
     */
    public void updateUserInfo(String token, UserInfoRequest request) {
        String userId = apiUrlBuilder.getCurrentUserId();
        
        if (userId == null || userId.isEmpty()) {
            Log.e(TAG, "Cannot update user info: userId not found");
            return;
        }

        // ApiService sends the actual request to: PUT /users/{userId}
        String authHeader = "Bearer " + token;
        Call<UserInfoResponse> call = apiService.setupUserInfo(authHeader, userId, request);
        
        call.enqueue(new Callback<UserInfoResponse>() {
            @Override
            public void onResponse(Call<UserInfoResponse> call, Response<UserInfoResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "User info updated successfully for userId: " + userId);
                } else {
                    Log.e(TAG, "Failed to update user info: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UserInfoResponse> call, Throwable t) {
                Log.e(TAG, "Network error updating user info: " + t.getMessage());
            }
        });
    }

    /**
     * Example: Get user info using userId from ApiUrlBuilder
     */
    public void getUserInfo(String token) {
        String userId = apiUrlBuilder.getCurrentUserId();
        
        if (userId == null || userId.isEmpty()) {
            Log.e(TAG, "Cannot get user info: userId not found");
            return;
        }

        // ApiService sends the actual request to: GET /users/{userId}
        String authHeader = "Bearer " + token;
        Call<UserInfoResponse> call = apiService.getUserInfo(authHeader, userId);
        
        call.enqueue(new Callback<UserInfoResponse>() {
            @Override
            public void onResponse(Call<UserInfoResponse> call, Response<UserInfoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserInfoResponse userInfo = response.body();
                    Log.d(TAG, "User info retrieved successfully: " + userInfo.getEmail());
                } else {
                    Log.e(TAG, "Failed to get user info: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UserInfoResponse> call, Throwable t) {
                Log.e(TAG, "Network error getting user info: " + t.getMessage());
            }
        });
    }

    /**
     * Demonstrate URL creation for different services
     */
    public void demonstrateUrlCreation() {
        if (!apiUrlBuilder.isUserIdAvailable()) {
            Log.w(TAG, "User ID not available for URL creation");
            return;
        }

        String baseUrl = "http://localhost:8080";
        
        // Create URLs for different services
        String userServiceUrl = apiUrlBuilder.createUserServiceUrl(baseUrl);
        String authServiceUrl = apiUrlBuilder.createAuthServiceUrl(baseUrl);
        String workoutServiceUrl = apiUrlBuilder.createWorkoutServiceUrl(baseUrl);
        String mealServiceUrl = apiUrlBuilder.createMealServiceUrl(baseUrl);
        String analystServiceUrl = apiUrlBuilder.createAnalystServiceUrl(baseUrl);
        
        Log.d(TAG, "=== Service URLs ===");
        Log.d(TAG, "User Service: " + userServiceUrl);
        Log.d(TAG, "Auth Service: " + authServiceUrl);
        Log.d(TAG, "Workout Service: " + workoutServiceUrl);
        Log.d(TAG, "Meal Service: " + mealServiceUrl);
        Log.d(TAG, "Analyst Service: " + analystServiceUrl);
        
        // Example with additional path
        String workoutDetailsUrl = apiUrlBuilder.createServiceUrlWithPath(baseUrl, "workouts", "details");
        Log.d(TAG, "Workout Details: " + workoutDetailsUrl);
    }

    /**
     * Get ApiUrlBuilder instance for direct usage
     */
    public ApiUrlBuilder getApiUrlBuilder() {
        return apiUrlBuilder;
    }

    /**
     * Get ApiService instance for direct usage
     */
    public ApiService getApiService() {
        return apiService;
    }
} 