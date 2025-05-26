package com.example.heath_android.view;

import android.os.Bundle;
import android.widget.*;
import android.view.View;
import androidx.activity.EdgeToEdge;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.heath_android.viewmodel.ChangePassAuthViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.heath_android.DatabaseInformation;
import com.example.heath_android.R;

public class DoiMatKhau extends AppCompatActivity {

    EditText edtMatKhauHienTai, edtMatKhauMoi;
    Button btnCapNhat;
    DatabaseInformation db;
    ChangePassAuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doi_mat_khau);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edtMatKhauHienTai = findViewById(R.id.edtMatKhauHienTai);
        edtMatKhauMoi = findViewById(R.id.edtMatKhauMoi);
        btnCapNhat = findViewById(R.id.btnCapNhatMatKhau);

        viewModel = new ViewModelProvider(this).get(ChangePassAuthViewModel.class);

        // Lấy token đã lưu
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        btnCapNhat.setOnClickListener(v -> {
            String mkHienTai = edtMatKhauHienTai.getText().toString().trim();
            String mkMoi = edtMatKhauMoi.getText().toString().trim();

            if (mkHienTai.isEmpty() || mkMoi.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (token == null) {
                Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.changePass(token, mkHienTai, mkMoi);
        });

        viewModel.responseLiveData.observe(this, response -> {
            Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
            // Cập nhật token mới nếu cần
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("jwt_token", response.getToken());
            editor.apply();
            finish();
        });

        viewModel.errorLiveData.observe(this, error -> {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        });
    }
}