package com.example.heath_android;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CapNhatThongTin extends AppCompatActivity {

    EditText edtNgaySinhCapNhat, edtChieuCaoCapNhat, edtCanNangCapNhat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cap_nhat_thong_tin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edtNgaySinhCapNhat = findViewById(R.id.edtNgaySinhCapNhat);
        edtChieuCaoCapNhat = findViewById(R.id.edtChieuCaoCapNhat);
        edtCanNangCapNhat = findViewById(R.id.edtCanNangCapNhat);
        Button btnLuuThongTin1 = findViewById(R.id.btnLuuThongTin2);
        btnLuuThongTin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kiemTraThongTin();
            }
        });

        Spinner spGioiTinhCapNhat = findViewById(R.id.spGioiTinhCapNhat);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
                this,
                R.array.gender_array,
                R.layout.spinner_item
        );
        adapter1.setDropDownViewResource(R.layout.spinner_item);
        spGioiTinhCapNhat.setAdapter(adapter1);

        Spinner spTanSuatCapNhat = findViewById(R.id.spTanSuatCapNhat);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                this,
                R.array.type_array,
                R.layout.spinner_item
        );
        adapter2.setDropDownViewResource(R.layout.spinner_item);
        spTanSuatCapNhat.setAdapter(adapter2);
    }
    private void kiemTraThongTin() {
        boolean hopLe = true;

        // Reset lỗi trước đó (nếu có)
        edtNgaySinhCapNhat.setError(null);
        edtChieuCaoCapNhat.setError(null);
        edtCanNangCapNhat.setError(null);

        // Kiểm tra ngày sinh
        String ngaySinh = edtNgaySinhCapNhat.getText().toString().trim();
        if (!ngaySinh.matches("^\\d{2}/\\d{2}/\\d{4}$")) {
            edtNgaySinhCapNhat.setError("Ngày sinh phải theo định dạng dd/MM/yyyy");
            hopLe = false;
        }

        // Kiểm tra chiều cao
        String chieuCaoStr = edtChieuCaoCapNhat.getText().toString().trim();
        try {
            float chieuCao = Float.parseFloat(chieuCaoStr);
            if (chieuCao < 0) {
                edtChieuCaoCapNhat.setError("Chiều cao không được âm");
                hopLe = false;
            }
        } catch (NumberFormatException e) {
            edtChieuCaoCapNhat.setError("Chiều cao không hợp lệ");
            hopLe = false;
        }

        // Kiểm tra cân nặng
        String canNangStr = edtCanNangCapNhat.getText().toString().trim();
        try {
            float canNang = Float.parseFloat(canNangStr);
            if (canNang < 0) {
                edtCanNangCapNhat.setError("Cân nặng không được âm");
                hopLe = false;
            }
        } catch (NumberFormatException e) {
            edtCanNangCapNhat.setError("Cân nặng không hợp lệ");
            hopLe = false;
        }

        // Nếu mọi thứ hợp lệ, tiếp tục xử lý
        if (hopLe) {
            Toast.makeText(this, "Thông tin hợp lệ!", Toast.LENGTH_SHORT).show();
            // TODO: chuyển sang Activity khác hoặc xử lý dữ liệu ở đây
        }
    }
}