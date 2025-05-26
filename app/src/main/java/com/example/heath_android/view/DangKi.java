package com.example.heath_android.view;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.heath_android.viewmodel.SignUpAuthViewModel;
import androidx.lifecycle.ViewModelProvider;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.heath_android.repository.SignUpRepository;
import com.example.heath_android.DatabaseInformation;
import com.example.heath_android.R;
import com.example.heath_android.model.SignUpRequest;
import com.example.heath_android.model.SignUpResponse;

public class DangKi extends AppCompatActivity {

    EditText edtHoVaTen, edtEmail, edtMatKhau;
    Button btnDangKi;
    ImageView btnQuayLaiDangNhap;
    DatabaseInformation db;
    TextView tvDangNhapTaiKhoan;
    SignUpAuthViewModel signUpViewModel;
    SignUpRepository signUpCallApi;
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
        edtHoVaTen = findViewById(R.id.edtEmailDangKi);
        edtEmail = findViewById(R.id.edtHoVaTen);
        edtMatKhau = findViewById(R.id.edtMatKhauDangKi);
        btnDangKi = findViewById(R.id.btnDangKi2);
        btnQuayLaiDangNhap = findViewById(R.id.btnQuayLaiDangNhap);
        tvDangNhapTaiKhoan = findViewById(R.id.tvDangNhapTaiKhoan);

        db = new DatabaseInformation(this);
        signUpViewModel = new ViewModelProvider(this).get(SignUpAuthViewModel.class);

        // Observe kết quả thành công
        signUpViewModel.getSignUpSuccessLiveData().observe(this, user -> {
            Toast.makeText(this, "Đăng ký thành công: " + user.getEmail(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(DangKi.this, DangNhap.class));
            finish();
        });

        // Observe lỗi
        signUpViewModel.getSignUpErrorLiveData().observe(this, error -> {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        });

        btnDangKi.setOnClickListener(v -> {
            String hoten = edtHoVaTen.getText().toString();
            String email = edtEmail.getText().toString();
            String matkhau = edtMatKhau.getText().toString();

            if (hoten.isEmpty() || email.isEmpty() || matkhau.isEmpty()) {
                Toast.makeText(DangKi.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            } else {
                SignUpRequest request = new SignUpRequest(hoten, email, matkhau);
                signUpViewModel.registerUser(request);
            }
        });
        tvDangNhapTaiKhoan.setOnClickListener(v -> {
            Intent intent = new Intent(this, DangNhap.class);
            startActivity(intent);
        });
        btnQuayLaiDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}