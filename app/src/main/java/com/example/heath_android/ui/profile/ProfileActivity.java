package com.example.heath_android.ui.profile;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.heath_android.R;
import com.example.heath_android.data.local.DatabaseInformation;
import com.example.heath_android.data.model.profile.ProfileRequest;
import com.example.heath_android.data.model.profile.ProfileResponse;
import com.example.heath_android.ui.schedule.ScheduleActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";

    // UI Components
    private ImageButton btnEdit;
    private TextInputEditText userNameEditText, birthdayEditText, etWeight, etHeight;
    private AutoCompleteTextView genderDropdown, groupDropdown;
    private BottomNavigationView bottomNavigation;
    
    // ViewModel and Database
    private ProfileViewModel profileViewModel;
    private DatabaseInformation databaseInformation;
    
    // State variables
    private boolean isEditMode = false;
    private Date selectedBirthDate = null;
    private String selectedGender = "";
    private String selectedActivityLevel = "";
    
    // Activity Level mappings (same as UserInfoSetupActivity)
    private final String[] ACTIVITY_LEVELS = {
        "SEDENTARY",           // Ít vận động
        "LIGHTLY_ACTIVE",      // Vận động nhẹ
        "MODERATELY_ACTIVE",   // Vận động vừa phải
        "VERY_ACTIVE",         // Vận động nhiều
        "EXTRA_ACTIVE"         // Vận động rất nhiều
    };
    
    // Display names for activity levels
    private final String[] ACTIVITY_LEVEL_DISPLAY = {
        "Ít vận động",
        "Vận động nhẹ", 
        "Vận động vừa phải",
        "Vận động nhiều",
        "Vận động rất nhiều"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.profile_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupViewModels();
        setupObservers();
        setupListeners();
        
        // Load user info when activity starts
        loadUserInfo();
    }

    private void initViews() {
        btnEdit = findViewById(R.id.btnEdit);
        userNameEditText = findViewById(R.id.userNameEditText);
        birthdayEditText = findViewById(R.id.birthdayEditText);
        genderDropdown = findViewById(R.id.genderDropdown);
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        groupDropdown = findViewById(R.id.groupDropdown);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        
        // Set fields as non-editable initially
        setFieldsEditable(false);
    }

    private void setupViewModels() {
        databaseInformation = new DatabaseInformation(this);
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
    }

    private void setupObservers() {
        // Observe user info retrieval
        profileViewModel.getUserInfoLiveData().observe(this, userInfo -> {
            if (userInfo != null) {
                Log.d(TAG, "User info loaded: " + userInfo.getEmail());
                populateUserInfo(userInfo);
            }
        });
        
        // Observe user info retrieval errors
        profileViewModel.getUserInfoErrorLiveData().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Log.e(TAG, "Error loading user info: " + error);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                profileViewModel.clearErrors();
            }
        });
        
        // Observe user info update success
        profileViewModel.getUpdateUserInfoSuccessLiveData().observe(this, userInfo -> {
            if (userInfo != null) {
                Log.d(TAG, "User info updated successfully: " + userInfo.getEmail());
                Toast.makeText(this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                
                // Exit edit mode and refresh data
                setEditMode(false);
                populateUserInfo(userInfo);
            }
        });
        
        // Observe user info update errors
        profileViewModel.getUpdateUserInfoErrorLiveData().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Log.e(TAG, "Error updating user info: " + error);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                profileViewModel.clearErrors();
            }
        });
        
        // Observe loading state
        profileViewModel.getLoadingLiveData().observe(this, isLoading -> {
            // You can add a progress bar here if needed
            Log.d(TAG, "Loading state: " + isLoading);
        });
    }

    private void setupListeners() {
        // Edit button toggle
        btnEdit.setOnClickListener(v -> {
            if (isEditMode) {
                // Save changes
                if (validateInputs()) {
                    saveUserInfo();
                }
            } else {
                // Enter edit mode
                setEditMode(true);
            }
        });

        // Birthday picker (only when in edit mode)
        birthdayEditText.setOnClickListener(v -> {
            if (isEditMode) {
                showDatePicker();
            }
        });

        // Gender dropdown
        genderDropdown.setOnItemClickListener((parent, view, position, id) -> {
            if (isEditMode) {
                selectedGender = position == 0 ? "MALE" : "FEMALE";
                Log.d(TAG, "Selected gender: " + selectedGender);
            }
        });

        // Activity level dropdown
        groupDropdown.setOnItemClickListener((parent, view, position, id) -> {
            if (isEditMode) {
                selectedActivityLevel = ACTIVITY_LEVELS[position];
                Log.d(TAG, "Selected activity level: " + selectedActivityLevel);
            }
        });
        
        // Bottom navigation
        setupBottomNavigation();
    }
    
    private void setupBottomNavigation() {
        if (bottomNavigation == null) {
            Log.w(TAG, "BottomNavigationView not found");
            return;
        }
        
        // Set Profile as selected (current activity)
        bottomNavigation.setSelectedItemId(R.id.nav_profile);
        
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            
            if (itemId == R.id.nav_home) {
                // Navigate back to Home
                navigateToHome();
                return true;
            } else if (itemId == R.id.nav_profile) {
                // Already on Profile, do nothing
                return true;
            } else if (itemId == R.id.nav_schedule) {
                // Navigate to Workout (when implemented)
                navigateToSchedule();
                return false;
            } else if (itemId == R.id.nav_logout) {
                // Navigate to Settings (when implemented)
                Log.d(TAG, "Logout");
                Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show();
                return false;
            }
            
            return false;
        });
        
        Log.d(TAG, "Bottom navigation setup completed");
    }
    
    private void navigateToHome() {
        Log.d(TAG, "Navigating back to Home Activity");
        
        // Check if we're in edit mode and warn user
        if (isEditMode) {
            Toast.makeText(this, "Bạn đang ở chế độ chỉnh sửa. Hãy lưu hoặc hủy trước khi chuyển trang.", Toast.LENGTH_LONG).show();
            return;
        }
        
        // Use finish() to go back to Home instead of creating new instance
        finish();
    }

    private void navigateToSchedule() {
        Log.d(TAG, "Navigating to Schedule Activity");
        Intent intent = new Intent(this, ScheduleActivity.class);
        startActivity(intent);
    }

    private void loadUserInfo() {
        Log.d(TAG, "Loading user info...");
        profileViewModel.getUserInfo();
    }

    private void populateUserInfo(ProfileResponse userInfo) {
        Log.d(TAG, "Populating user info fields");
        
        // Populate fields with user data
        userNameEditText.setText(userInfo.getFullName());
        
        // Handle birthday (Date object)
        if (userInfo.getBirthDate() != null) {
            selectedBirthDate = userInfo.getBirthDate();
            birthdayEditText.setText(formatDateForDisplay(selectedBirthDate));
        }
        
        // Set gender
        if (userInfo.getGender() != null) {
            selectedGender = userInfo.getGender();
            String[] genderOptions = getResources().getStringArray(R.array.gender_items);
            int genderIndex = "MALE".equals(selectedGender) ? 0 : 1;
            genderDropdown.setText(genderOptions[genderIndex], false);
        }
        
        // Set weight and height
        etWeight.setText(String.valueOf(userInfo.getWeight()));
        etHeight.setText(String.valueOf(userInfo.getHeight()));
        
        // Set activity level
        if (userInfo.getInitialActivityLevel() != null) {
            selectedActivityLevel = userInfo.getInitialActivityLevel();
            int activityIndex = getActivityLevelIndex(selectedActivityLevel);
            if (activityIndex >= 0) {
                groupDropdown.setText(ACTIVITY_LEVEL_DISPLAY[activityIndex], false);
            }
        }
    }

    private void setEditMode(boolean editMode) {
        isEditMode = editMode;
        
        // Update button icon and text based on mode
        if (editMode) {
            btnEdit.setImageResource(R.drawable.check_24px);
            setFieldsEditable(true);
        } else {
            btnEdit.setImageResource(R.drawable.edit_24px);
            setFieldsEditable(false);
        }
        
        Log.d(TAG, "Edit mode: " + (editMode ? "ON" : "OFF"));
    }

    private void setFieldsEditable(boolean editable) {
        userNameEditText.setEnabled(editable);
        birthdayEditText.setEnabled(editable);
        birthdayEditText.setFocusable(false); // Always non-focusable, but clickable when enabled
        birthdayEditText.setClickable(editable);
        genderDropdown.setEnabled(editable);
        etWeight.setEnabled(editable);
        etHeight.setEnabled(editable);
        groupDropdown.setEnabled(editable);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        
        // If there's an existing date, use it
        if (selectedBirthDate != null) {
            calendar.setTime(selectedBirthDate);
        }
        
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, selectedYear, selectedMonth, selectedDay) -> {
                // Create Date object
                Calendar cal = Calendar.getInstance();
                cal.set(selectedYear, selectedMonth, selectedDay);
                selectedBirthDate = cal.getTime();
                
                // Display date as DD/MM/YYYY
                String displayDate = formatDateForDisplay(selectedBirthDate);
                birthdayEditText.setText(displayDate);
                
                Log.d(TAG, "Selected birth date: " + selectedBirthDate);
            },
            year, month, day
        );
        
        datePickerDialog.show();
    }

    private boolean validateInputs() {
        String userName = userNameEditText.getText().toString().trim();
        String weight = etWeight.getText().toString().trim();
        String height = etHeight.getText().toString().trim();

        if (userName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập họ và tên", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedBirthDate == null) {
            Toast.makeText(this, "Vui lòng chọn ngày sinh", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedGender.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (weight.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập cân nặng", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (height.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập chiều cao", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedActivityLevel.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn mức độ hoạt động", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveUserInfo() {
        String fullName = userNameEditText.getText().toString().trim();
        String weight = etWeight.getText().toString().trim();
        String height = etHeight.getText().toString().trim();

        Log.d(TAG, "Saving user info: " + selectedBirthDate + ", " + selectedGender + 
              ", " + height + ", " + weight + ", " + selectedActivityLevel);

        // Create ProfileRequest with the ProfileRequest fields
        ProfileRequest request = new ProfileRequest();
        request.setFullName(fullName);
        request.setBirthDate(selectedBirthDate);
        request.setGender(selectedGender);
        request.setWeight(Double.parseDouble(weight));
        request.setHeight(Double.parseDouble(height));
        request.setInitialActivityLevel(selectedActivityLevel);

        profileViewModel.updateUserInfo(request);
    }

    // Helper methods
    private String formatDateForDisplay(Date date) {
        if (date == null) return "";
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return displayFormat.format(date);
    }

    private int getActivityLevelIndex(String activityLevel) {
        for (int i = 0; i < ACTIVITY_LEVELS.length; i++) {
            if (ACTIVITY_LEVELS[i].equals(activityLevel)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh user info when returning to this activity
        if (!isEditMode) {
            loadUserInfo();
        }
    }
}