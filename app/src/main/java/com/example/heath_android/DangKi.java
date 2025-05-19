package com.example.heath_android;

import android.os.Bundle;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DangKi extends AppCompatActivity {

    EditText edtTenDangNhap, edtEmail, edtMatKhau;
    Button btnDangKi;
    DatabaseInformation db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_ki);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edtTenDangNhap = findViewById(R.id.edtTenDangNhap);
        edtEmail = findViewById(R.id.edtEmailDangKi);
        edtMatKhau = findViewById(R.id.edtMatKhauDangKi);
        btnDangKi = findViewById(R.id.btnDangKi2);

        db = new DatabaseInformation(this);
        btnDangKi.setOnClickListener(v -> {
            String username = edtTenDangNhap.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String password = edtMatKhau.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            } else {
                boolean inserted = db.insertUser(username, email, password);
                if (inserted) {
                    Toast.makeText(this, "Đăng kí thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // quay về màn hình đăng nhập
                } else {
                    Toast.makeText(this, "Đăng kí thất bại!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}