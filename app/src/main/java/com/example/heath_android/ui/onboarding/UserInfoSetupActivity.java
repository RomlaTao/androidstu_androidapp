package com.example.heath_android.ui.onboarding;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.heath_android.R;
import com.example.heath_android.data.local.DatabaseInformation;
import com.example.heath_android.data.model.onboarding.UserInfoRequest;
import com.example.heath_android.ui.home.HomeActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;
import java.util.Locale;

public class UserInfoSetupActivity extends AppCompatActivity {
    private static final String TAG = "UserInfoSetupActivity";

    private TextInputEditText etBirthday, etWeight, etHeight;
    private AutoCompleteTextView genderDropdown, groupDropdown;
    private MaterialButton btnFinish;
    
    private DatabaseInformation databaseInformation;
    private UserInfoViewModel userInfoViewModel;
    
    private String selectedBirthDate = "";
    private String selectedGender = "";
    private String selectedActivityLevel = "";
    
    // Activity Level mappings
    private final String[] ACTIVITY_LEVELS = {
        "SEDENTARY",           // Ít vận động
        "LIGHTLY_ACTIVE",      // Vận động nhẹ
        "MODERATELY_ACTIVE",   // Vận động vừa phải
        "VERY_ACTIVE",         // Vận động nhiều
        "EXTRA_ACTIVE"         // Vận động rất nhiều
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_info_setup);

        initViews();
        setupViewModels();
        setupObservers();
        setupListeners();
    }

    private void initViews() {
        etBirthday = findViewById(R.id.inputBirthday).findViewById(R.id.birthdayEditText);
        etWeight = findViewById(R.id.inputWeight).findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.inputHeight).findViewById(R.id.etHeight);
        genderDropdown = findViewById(R.id.genderDropdown);
        groupDropdown = findViewById(R.id.groupDropdown);
        btnFinish = findViewById(R.id.btnFinish);
        
        // Set up birthday field as clickable (not editable)
        etBirthday.setFocusable(false);
        etBirthday.setClickable(true);
    }

    private void setupViewModels() {
        databaseInformation = new DatabaseInformation(this);
        userInfoViewModel = new ViewModelProvider(this).get(UserInfoViewModel.class);
    }

    private void setupObservers() {
        // Observe success
        userInfoViewModel.getUserInfoSuccessLiveData().observe(this, userInfoResponse -> {
            Log.d(TAG, "User info setup success observed");
            if (userInfoResponse != null) {
                Log.d(TAG, "User info updated for: " + userInfoResponse.getEmail());
                
                // Update local storage with user info including userId
                databaseInformation.saveUserInfo(
                    databaseInformation.getToken(), 
                    userInfoResponse.getEmail(), 
                    userInfoResponse.getFullName(),
                    String.valueOf(userInfoResponse.getId()) // Convert long to String for userId
                );
                
                Log.d(TAG, "Saved userId: " + userInfoResponse.getId());
                
                Toast.makeText(this, "Thiết lập thông tin thành công!", Toast.LENGTH_SHORT).show();
                
                //Navigate to main app (HomeActivity)
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
                
                // For now, just finish this activity
                finish();
            }
        });

        // Observe error
        userInfoViewModel.getUserInfoErrorLiveData().observe(this, error -> {
            Log.d(TAG, "User info setup error observed: " + error);
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        // Birthday picker
        etBirthday.setOnClickListener(v -> showDatePicker());

        // Gender dropdown
        genderDropdown.setOnItemClickListener((parent, view, position, id) -> {
            selectedGender = position == 0 ? "MALE" : "FEMALE";
            Log.d(TAG, "Selected gender: " + selectedGender);
        });

        // Activity level dropdown
        groupDropdown.setOnItemClickListener((parent, view, position, id) -> {
            selectedActivityLevel = ACTIVITY_LEVELS[position];
            Log.d(TAG, "Selected activity level: " + selectedActivityLevel);
        });

        // Finish button
        btnFinish.setOnClickListener(v -> {
            if (validateInputs()) {
                submitUserInfo();
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR) - 18; // Default to 18 years ago
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, selectedYear, selectedMonth, selectedDay) -> {
                // Format date as YYYY-MM-DD
                selectedBirthDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", 
                    selectedYear, selectedMonth + 1, selectedDay);
                
                // Display date as DD/MM/YYYY
                String displayDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", 
                    selectedDay, selectedMonth + 1, selectedYear);
                etBirthday.setText(displayDate);
                
                Log.d(TAG, "Selected birth date: " + selectedBirthDate);
            },
            year, month, day
        );
        
        datePickerDialog.show();
    }

    private boolean validateInputs() {
        String weight = etWeight.getText().toString().trim();
        String height = etHeight.getText().toString().trim();

        if (selectedBirthDate.isEmpty()) {
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

    private void submitUserInfo() {
        String weight = etWeight.getText().toString().trim();
        String height = etHeight.getText().toString().trim();
        String token = databaseInformation.getToken();
        String userId = databaseInformation.getUserId();

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy thông tin user ID. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "Submitting user info for userId: " + userId + " - " + selectedBirthDate + ", " + selectedGender + 
              ", " + height + ", " + weight + ", " + selectedActivityLevel);

        UserInfoRequest request = new UserInfoRequest(
            selectedActivityLevel
        );

        userInfoViewModel.setupUserInfo(token, userId, request);
    }
}