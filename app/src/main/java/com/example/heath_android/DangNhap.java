package com.example.heath_android;

import android.os.Bundle;
import android.database.Cursor;
import android.view.View;
import android.widget.*;
import android.content.Intent;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DangNhap extends AppCompatActivity {

    EditText edtEmail, edtMatKhau;
    Button btnDangNhap;
    TextView tvDangKiTaiKhoan, tvQuenMatKhau;
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
        tvDangKiTaiKhoan = findViewById(R.id.tvDangKiTaiKhoan);
        tvQuenMatKhau = findViewById(R.id.tvQuenMatKhau);
        db = new DatabaseInformation(this);

        btnDangNhap.setOnClickListener(v -> {
            String email = edtEmail.getText().toString();
            String matkhau = edtMatKhau.getText().toString();

            if (email.isEmpty() || matkhau.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            } else {
                if (db.checkUser(email, matkhau)) {
                    Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DangNhap.this, ThongTin.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Tài khoản không tồn tại. Vui lòng đăng ký.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvDangKiTaiKhoan.setOnClickListener(v -> {
            Intent intent = new Intent(this, DangKi.class);
            startActivity(intent);
        });
        /*tvQuenMatKhau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DangNhap.this, DoiMatKhau.class);
                startActivity(intent);
            }
        });*/
    }
}