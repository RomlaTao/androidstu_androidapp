package com.example.heath_android.ui.auth;

import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.heath_android.R;
import com.example.heath_android.data.local.DatabaseInformation;
import com.example.heath_android.data.model.auth.LoginResponse;
import com.example.heath_android.ui.home.HomeActivity;
import com.example.heath_android.util.ApiUrlBuilder;
import com.example.heath_android.util.JsonTestHelper;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    EditText etEmail, etPassword;
    Button btnLogin;
    TextView tvSignup;
    DatabaseInformation databaseInformation;
    AuthViewModel viewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.tvSignup);

        // Database for local storage
        databaseInformation = new DatabaseInformation(this);
        
        // ViewModel
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Test JSON mapping (for debugging)
        JsonTestHelper.testLoginResponseMapping();

        // Bắt sự kiện nút Đăng nhập
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            Log.d(TAG, "Login attempt: " + email);

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ email và mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Call login method from ViewModel
            viewModel.login(email, password);
        });

        // Quan sát kết quả đăng nhập thành công
        viewModel.getResponseLiveData().observe(this, response -> {
            Log.d(TAG, "Login success observed");
            if (response != null && response.getToken() != null && !response.getToken().isEmpty()) {
                Log.d(TAG, "Token received, login successful");
                
                // Debug: Log complete response
                logLoginResponse(response);
                
                // Get userId from login response (using new method)
                String userId = response.getUserId();
                if (userId != null && !userId.isEmpty()) {
                    // Save user info including userId to local database
                    databaseInformation.saveUserInfo(response.getToken(), "", "", userId);
                    Log.d(TAG, "Saved userId: " + userId);
                    
                    // Demo: Show how ApiUrlBuilder can now be used with saved userId
                    demonstrateApiUrlUsage();
                } else {
                    // Save only token if userId is not available
                    databaseInformation.saveUserInfo(response.getToken(), "", "");
                    Log.w(TAG, "UserId not available in login response");
                }
                
                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_LONG).show();
                
                // Check if user info is initialized to decide navigation
                if (response.isUserInfoInitialized()) {
                    Log.d(TAG, "User info already initialized, navigating to main app");
                    // Navigate to main app (HomeActivity)
                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                    //For now, just finish this activity
                    finish();
                } else {
                    Log.d(TAG, "User info not initialized, navigating to setup");
                    // Navigate to UserInfoSetupActivity for first-time setup
                    Intent intent = new Intent(this, com.example.heath_android.ui.onboarding.UserInfoSetupActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        // Quan sát lỗi đăng nhập
        viewModel.getLoginErrorLiveData().observe(this, error -> {
            Log.d(TAG, "Login error observed: " + error);
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        // Điều hướng sang màn hình đăng ký
        tvSignup.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Demonstrate how ApiUrlBuilder can be used after successful login
     * This shows that userId is now available for creating service URLs
     */
    private void demonstrateApiUrlUsage() {
        ApiUrlBuilder apiUrlBuilder = new ApiUrlBuilder(this);
        
        if (!apiUrlBuilder.isUserIdAvailable()) {
            Log.w(TAG, "Demo: UserId not available for URL creation");
            return;
        }

        String baseUrl = "http://localhost:8080";
        
        // Create service URLs now that userId is available
        String userServiceUrl = apiUrlBuilder.createUserServiceUrl(baseUrl);
        String authServiceUrl = apiUrlBuilder.createAuthServiceUrl(baseUrl);
        String workoutServiceUrl = apiUrlBuilder.createWorkoutServiceUrl(baseUrl);
        String mealServiceUrl = apiUrlBuilder.createMealServiceUrl(baseUrl);
        String analystServiceUrl = apiUrlBuilder.createAnalystServiceUrl(baseUrl);
        
        Log.d(TAG, "=== URLs Available After Login ===");
        Log.d(TAG, "User Service: " + userServiceUrl);
        Log.d(TAG, "Auth Service: " + authServiceUrl);
        Log.d(TAG, "Workout Service: " + workoutServiceUrl);
        Log.d(TAG, "Meal Service: " + mealServiceUrl);
        Log.d(TAG, "Analyst Service: " + analystServiceUrl);
        
        // Example: Now can use these URLs for API calls
        Log.d(TAG, "Ready for CRUD operations with userId: " + apiUrlBuilder.getCurrentUserId());
    }

    /**
     * Debug method to log complete login response for troubleshooting
     */
    private void logLoginResponse(LoginResponse response) {
        Log.d(TAG, "=== Login Response Debug ===");
        Log.d(TAG, "Token: " + (response.getToken() != null ? "***TOKEN_PRESENT***" : "NULL"));
        Log.d(TAG, "ExpiresIn: " + response.getExpiresIn());
        Log.d(TAG, "UserId: " + response.getUserId());
        Log.d(TAG, "IsUserInfoInitialized: " + response.isUserInfoInitialized());
        Log.d(TAG, "=== End Debug ===");
    }
}