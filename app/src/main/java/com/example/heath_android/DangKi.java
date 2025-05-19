package com.example.heath_android;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DangKi extends AppCompatActivity {

    EditText edtHoVaTen, edtEmail, edtMatKhau;
    Button btnDangKi;
    DatabaseInformation db;
    TextView tvDangNhapTaiKhoan;
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
        edtHoVaTen = findViewById(R.id.edtHoVaTen);
        edtEmail = findViewById(R.id.edtEmailDangKi);
        edtMatKhau = findViewById(R.id.edtMatKhauDangKi);
        btnDangKi = findViewById(R.id.btnDangKi2);
        tvDangNhapTaiKhoan = findViewById(R.id.tvDangNhapTaiKhoan);
        db = new DatabaseInformation(this);

        btnDangKi.setOnClickListener(v -> {
            String hoten = edtHoVaTen.getText().toString();
            String email = edtEmail.getText().toString();
            String matkhau = edtMatKhau.getText().toString();

            if (hoten.isEmpty() || email.isEmpty() || matkhau.isEmpty()) {
                Toast.makeText(DangKi.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            } else {
                boolean check = db.insertUser(hoten, email, matkhau);
                if (check) {
                    Toast.makeText(DangKi.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DangKi.this, DangNhap.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(DangKi.this, "Email đã tồn tại", Toast.LENGTH_SHORT).show();
                }
            }
        });
        tvDangNhapTaiKhoan.setOnClickListener(v -> {
            Intent intent = new Intent(this, DangNhap.class);
            startActivity(intent);
        });
    }
}