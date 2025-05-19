package com.example.heath_android;

import android.os.Bundle;
import android.database.Cursor;
import android.widget.*;
import android.content.Intent;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DangNhap extends AppCompatActivity {

    EditText edtEmail, edtMatKhau;
    Button btnDangNhap, btnDangKi;
    DatabaseInformation db;
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
        btnDangKi = findViewById(R.id.btnDangKi1);

        // Khởi tạo DB
        db = new DatabaseInformation(this);

        // Sự kiện nút Đăng ký
        btnDangKi.setOnClickListener(v -> {
            Intent intent = new Intent(DangNhap.this, DangKi.class);
            startActivity(intent);
        });

        // Sự kiện nút Đăng nhập
        btnDangNhap.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtMatKhau.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(DangNhap.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isUserValid = db.checkUser(email, password);

            if (isUserValid) {
                Toast.makeText(DangNhap.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DangNhap.this, ThongTin.class);
                startActivity(intent);
            } else {
                Toast.makeText(DangNhap.this, "Email hoặc mật khẩu không chính xác!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}