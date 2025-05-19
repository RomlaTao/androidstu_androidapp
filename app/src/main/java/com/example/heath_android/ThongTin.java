package com.example.heath_android;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ThongTin extends AppCompatActivity {

    EditText edtNgaySinhNhap, edtChieuCaoNhap, edtCanNangNhap;
    Button btnLuuThongTin1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_thong_tin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edtNgaySinhNhap = findViewById(R.id.edtNgaySinhNhap);
        edtChieuCaoNhap = findViewById(R.id.edtChieuCaoNhap);
        edtCanNangNhap = findViewById(R.id.edtCanNangNhap);

        Button btnLuuThongTin1 = findViewById(R.id.btnLuuThongTin1);
        btnLuuThongTin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kiemTraThongTin();
            }
        });
        Spinner spGioiTinhNhap = findViewById(R.id.spGioiTinhNhap);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
                this,
                R.array.gender_array,
                R.layout.spinner_item
        );
        adapter1.setDropDownViewResource(R.layout.spinner_item);
        spGioiTinhNhap.setAdapter(adapter1);

        Spinner spTanSuatNhap = findViewById(R.id.spTanSuatNhap);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                this,
                R.array.type_array,
                R.layout.spinner_item
        );
        adapter2.setDropDownViewResource(R.layout.spinner_item);
        spTanSuatNhap.setAdapter(adapter2);
    }
    private void kiemTraThongTin() {
        boolean hopLe = true;

        // Reset lỗi trước đó (nếu có)
        edtNgaySinhNhap.setError(null);
        edtChieuCaoNhap.setError(null);
        edtCanNangNhap.setError(null);

        // Kiểm tra ngày sinh
        String ngaySinh = edtNgaySinhNhap.getText().toString().trim();
        if (!ngaySinh.matches("^\\d{2}/\\d{2}/\\d{4}$")) {
            edtNgaySinhNhap.setError("Ngày sinh phải theo định dạng dd/MM/yyyy");
            hopLe = false;
        }

        // Kiểm tra chiều cao
        String chieuCaoStr = edtChieuCaoNhap.getText().toString().trim();
        try {
            float chieuCao = Float.parseFloat(chieuCaoStr);
            if (chieuCao < 0) {
                edtChieuCaoNhap.setError("Chiều cao không được âm");
                hopLe = false;
            }
        } catch (NumberFormatException e) {
            edtChieuCaoNhap.setError("Chiều cao không hợp lệ");
            hopLe = false;
        }

        // Kiểm tra cân nặng
        String canNangStr = edtCanNangNhap.getText().toString().trim();
        try {
            float canNang = Float.parseFloat(canNangStr);
            if (canNang < 0) {
                edtCanNangNhap.setError("Cân nặng không được âm");
                hopLe = false;
            }
        } catch (NumberFormatException e) {
            edtCanNangNhap.setError("Cân nặng không hợp lệ");
            hopLe = false;
        }

        // Nếu mọi thứ hợp lệ, tiếp tục xử lý
        if (hopLe) {
            Toast.makeText(this, "Thông tin hợp lệ!", Toast.LENGTH_SHORT).show();
            // TODO: chuyển sang Activity khác hoặc xử lý dữ liệu ở đây
        }
    }
}