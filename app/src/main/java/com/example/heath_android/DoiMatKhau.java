package com.example.heath_android;

import android.os.Bundle;
import android.widget.*;
import android.view.View;
import androidx.activity.EdgeToEdge;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DoiMatKhau extends AppCompatActivity {

    EditText edtMatKhauHienTai, edtMatKhauMoi;
    Button btnCapNhat;
    DatabaseInformation db;

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
        btnCapNhat = findViewById(R.id.btnCapNhat);

        db = new DatabaseInformation(this);

        btnCapNhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mkHienTai = edtMatKhauHienTai.getText().toString().trim();
                String mkMoi = edtMatKhauMoi.getText().toString().trim();

                if (mkHienTai.isEmpty() || mkMoi.isEmpty()) {
                    Toast.makeText(DoiMatKhau.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Lấy tên đăng nhập hiện tại (giả sử truyền từ MainActivity hoặc lưu SharedPreferences)
                SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                String tenDangNhap = prefs.getString("tenDangNhap", null);

                if (tenDangNhap != null && db.checkUser(tenDangNhap, mkHienTai)) {
                    boolean updated = db.updatePassword(tenDangNhap, mkMoi);
                    if (updated) {
                        Toast.makeText(DoiMatKhau.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                        finish(); // quay lại giao diện đăng nhập hoặc Thông tin
                    } else {
                        Toast.makeText(DoiMatKhau.this, "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DoiMatKhau.this, "Mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}