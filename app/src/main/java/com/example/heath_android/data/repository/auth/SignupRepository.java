package com.example.heath_android.data.repository.auth;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import com.example.heath_android.data.model.auth.SignupRequest;
import com.example.heath_android.data.model.auth.SignupResponse;
import com.example.heath_android.data.model.User;
import com.example.heath_android.data.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignupRepository {
    private static final String TAG = "SignupRepository";
    private static final String BASE_URL = "http://10.0.2.2:8080/auth/";
    private ApiService apiService;
    private MutableLiveData<User> signupSuccessLiveData;
    private MutableLiveData<String> signupErrorLiveData;

    public SignupRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        apiService = retrofit.create(ApiService.class);
        signupSuccessLiveData = new MutableLiveData<>();
        signupErrorLiveData = new MutableLiveData<>();
    }

    public void signup(SignupRequest signupRequest) {
        Call<SignupResponse> call = apiService.signup(signupRequest);
        
        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                Log.d(TAG, "Response code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    SignupResponse signupResponse = response.body();
                    Log.d(TAG, "Signup successful for user: " + signupResponse.getEmail());
                    
                    // Kiểm tra có ID để xác định đăng ký thành công
                    if (signupResponse.getId() > 0 && signupResponse.getEmail() != null) {
                        User user = new User(signupResponse.getEmail(), signupResponse.getFullName(), null);
                        signupSuccessLiveData.postValue(user);
                    } else {
                        signupErrorLiveData.postValue("Đăng ký thất bại: Dữ liệu không hợp lệ.");
                    }
                } else {
                    // Xử lý lỗi từ server
                    String errorMessage = "Đăng ký thất bại.";
                    if (response.code() == 400) {
                        errorMessage = "Thông tin đăng ký không hợp lệ.";
                    } else if (response.code() == 409) {
                        errorMessage = "Email đã được sử dụng.";
                    } else if (response.code() == 500) {
                        errorMessage = "Lỗi server. Vui lòng thử lại sau.";
                    }
                    Log.e(TAG, "Signup failed with code: " + response.code());
                    signupErrorLiveData.postValue(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                Log.e(TAG, "Signup network error: " + t.getMessage());
                signupErrorLiveData.postValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public MutableLiveData<User> getSignupSuccessLiveData() {
        return signupSuccessLiveData;
    }

    public MutableLiveData<String> getSignupErrorLiveData() {
        return signupErrorLiveData;
    }
} 