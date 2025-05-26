package com.example.heath_android.view;

import android.os.Bundle;
import android.widget.*;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.heath_android.R;
import com.example.heath_android.repository.LogInRepository;
import com.example.heath_android.viewmodel.LogInAuthViewModel;
import com.example.heath_android.model.LogInResponse;

public class DangNhap extends AppCompatActivity {

    EditText edtEmail, edtMatKhau;
    Button btnDangNhap;
    TextView tvDangKiTaiKhoan, tvQuenMatKhau;
    LogInRepository logInCallApi;
    LogInAuthViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_nhap);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edtEmail = findViewById(R.id.edtEmailDangNhap);
        edtMatKhau = findViewById(R.id.edtMatKhauDangNhap);
        btnDangNhap = findViewById(R.id.btnDangNhap);
        tvDangKiTaiKhoan = findViewById(R.id.tvDangKiTaiKhoan);
        tvQuenMatKhau = findViewById(R.id.tvQuenMatKhau);

        // ViewModel
        viewModel = new ViewModelProvider(this).get(LogInAuthViewModel.class);

        // Bắt sự kiện nút Đăng nhập
        btnDangNhap.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtMatKhau.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ email và mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.login(email, password);
        });

        // Quan sát kết quả đăng nhập
        viewModel.getResponseLiveData().observe(this, response -> {
            if (response != null) {
                Toast.makeText(this, "Đăng nhập thành công!\nToken: " + response.getToken(), Toast.LENGTH_LONG).show();
                // Chuyển sang màn hình chính (MainActivity) hoặc lưu token
                // startActivity(new Intent(this, MainActivity.class));
            } else {
                Toast.makeText(this, "Đăng nhập thất bại. Vui lòng kiểm tra lại.", Toast.LENGTH_SHORT).show();
            }
        });

        // Điều hướng sang màn hình đăng ký
        tvDangKiTaiKhoan.setOnClickListener(v -> {
            Intent intent = new Intent(this, DangKi.class);
            startActivity(intent);
        });
    }

}