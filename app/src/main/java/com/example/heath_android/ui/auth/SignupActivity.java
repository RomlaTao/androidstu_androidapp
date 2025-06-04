package com.example.heath_android.ui.auth;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.heath_android.data.local.DatabaseInformation;
import com.example.heath_android.R;
import com.example.heath_android.data.model.auth.SignupRequest;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    EditText etName, etEmail, etPassword;
    Button btnSignup;
    ImageView btnBack;
    DatabaseInformation db;
    TextView tvLoginLink;
    AuthViewModel signupViewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        etEmail = findViewById(R.id.etEmail);
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        btnSignup = findViewById(R.id.btnSignup);
        btnBack = findViewById(R.id.btnBack);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        db = new DatabaseInformation(this);
        signupViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Observe kết quả thành công
        signupViewModel.getSignupSuccessLiveData().observe(this, user -> {
            Log.d(TAG, "Signup success observed");
            if (user != null) {
                Log.d(TAG, "User created: " + user.getEmail());
                Toast.makeText(this, "Đăng ký thành công cho: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                // Chuyển về màn hình đăng nhập
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Observe lỗi
        signupViewModel.getSignupErrorLiveData().observe(this, error -> {
            Log.d(TAG, "Signup error observed: " + error);
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        btnSignup.setOnClickListener(v -> {
            String fullName = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            Log.d(TAG, "Signup attempt: " + email);

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            } else {
                SignupRequest request = new SignupRequest(email, fullName, password);
                signupViewModel.signupUser(request);
            }
        });
        
        tvLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}