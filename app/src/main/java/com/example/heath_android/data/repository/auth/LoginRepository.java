package com.example.heath_android.data.repository.auth;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import com.example.heath_android.data.model.auth.LoginRequest;
import com.example.heath_android.data.model.auth.LoginResponse;
import com.example.heath_android.data.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginRepository {
    private static final String TAG = "LoginRepository";
    private static final String BASE_URL = "http://10.0.2.2:8080/auth/";
    private ApiService apiService;
    private MutableLiveData<LoginResponse> loginLiveData;
    private MutableLiveData<String> errorLiveData;

    public LoginRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        apiService = retrofit.create(ApiService.class);
        loginLiveData = new MutableLiveData<>();
        errorLiveData = new MutableLiveData<>();
    }

    public void login(String email, String password) {
        LoginRequest loginRequest = new LoginRequest(email, password);
        Call<LoginResponse> call = apiService.login(loginRequest);
        
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.d(TAG, "Response code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    Log.d(TAG, "Login successful, token received: " + (loginResponse.getToken() != null));
                    
                    // Kiểm tra có token không để xác định thành công
                    if (loginResponse.getToken() != null && !loginResponse.getToken().isEmpty()) {
                        loginLiveData.postValue(loginResponse);
                    } else {
                        errorLiveData.postValue("Đăng nhập thất bại: Không nhận được token.");
                    }
                } else {
                    // Xử lý lỗi từ server
                    String errorMessage = "Đăng nhập thất bại.";
                    if (response.code() == 401) {
                        errorMessage = "Email hoặc mật khẩu không đúng.";
                    } else if (response.code() == 404) {
                        errorMessage = "Tài khoản không tồn tại.";
                    } else if (response.code() == 500) {
                        errorMessage = "Lỗi server. Vui lòng thử lại sau.";
                    }
                    Log.e(TAG, "Login failed with code: " + response.code());
                    errorLiveData.postValue(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e(TAG, "Login network error: " + t.getMessage());
                errorLiveData.postValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public MutableLiveData<LoginResponse> getLoginLiveData() {
        return loginLiveData;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }
} 