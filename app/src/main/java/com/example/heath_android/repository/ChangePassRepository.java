package com.example.heath_android.repository;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.heath_android.api.ChangePassAuthApi;
import com.example.heath_android.model.ChangePassRequest;
import com.example.heath_android.model.ChangePassResponse;
import com.example.heath_android.utils.ChangePassRetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class ChangePassRepository {
    private final ChangePassAuthApi authApi = ChangePassRetrofitClient.getInstance().create(ChangePassAuthApi.class);

    public void doiMatKhau(String token, ChangePassRequest request, MutableLiveData<ChangePassResponse> liveData, MutableLiveData<String> errorData) {
        authApi.changePassword("Bearer " + token, request).enqueue(new Callback<ChangePassResponse>() {
            @Override
            public void onResponse(Call<ChangePassResponse> call, Response<ChangePassResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(response.body());
                } else {
                    errorData.setValue("Mật khẩu hiện tại không đúng hoặc lỗi máy chủ");
                    Log.e("API_ERROR", "Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ChangePassResponse> call, Throwable t) {
                errorData.setValue("Không thể kết nối máy chủ: " + t.getMessage());
            }
        });
    }
}
