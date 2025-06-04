package com.example.heath_android.data.repository.onboarding;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import com.example.heath_android.data.model.onboarding.UserInfoRequest;
import com.example.heath_android.data.model.onboarding.UserInfoResponse;
import com.example.heath_android.data.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserInfoRepository {
    private static final String TAG = "UserInfoRepository";
    private static final String BASE_URL = "http://10.0.2.2:8080/";
    private ApiService apiService;
    private MutableLiveData<UserInfoResponse> userInfoSuccessLiveData;
    private MutableLiveData<String> userInfoErrorLiveData;

    public UserInfoRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        apiService = retrofit.create(ApiService.class);
        userInfoSuccessLiveData = new MutableLiveData<>();
        userInfoErrorLiveData = new MutableLiveData<>();
    }

    public void setupUserInfo(String token, String userId, UserInfoRequest userInfoRequest) {
        String authHeader = "Bearer " + token;
        Call<UserInfoResponse> call = apiService.setupUserInfo(authHeader, userId, userInfoRequest);
        
        call.enqueue(new Callback<UserInfoResponse>() {
            @Override
            public void onResponse(Call<UserInfoResponse> call, Response<UserInfoResponse> response) {
                Log.d(TAG, "Response code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    UserInfoResponse userInfoResponse = response.body();
                    Log.d(TAG, "User info setup successful for: " + userInfoResponse.getEmail());
                    
                    // Kiểm tra có ID để xác định setup thành công
                    if (userInfoResponse.getId() != null) {
                        userInfoSuccessLiveData.postValue(userInfoResponse);
                    } else {
                        userInfoErrorLiveData.postValue("Setup thất bại: Dữ liệu không hợp lệ.");
                    }
                } else {
                    // Xử lý lỗi từ server
                    String errorMessage = "Setup thông tin thất bại.";
                    if (response.code() == 400) {
                        errorMessage = "Thông tin không hợp lệ.";
                    } else if (response.code() == 401) {
                        errorMessage = "Phiên đăng nhập đã hết hạn.";
                    } else if (response.code() == 500) {
                        errorMessage = "Lỗi server. Vui lòng thử lại sau.";
                    }
                    Log.e(TAG, "Setup failed with code: " + response.code());
                    userInfoErrorLiveData.postValue(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<UserInfoResponse> call, Throwable t) {
                Log.e(TAG, "Setup network error: " + t.getMessage());
                userInfoErrorLiveData.postValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public MutableLiveData<UserInfoResponse> getUserInfoSuccessLiveData() {
        return userInfoSuccessLiveData;
    }

    public MutableLiveData<String> getUserInfoErrorLiveData() {
        return userInfoErrorLiveData;
    }

    public void getUserInfo(String token, String userId) {
        String authHeader = "Bearer " + token;
        Call<UserInfoResponse> call = apiService.getUserInfo(authHeader, userId);
        
        call.enqueue(new Callback<UserInfoResponse>() {
            @Override
            public void onResponse(Call<UserInfoResponse> call, Response<UserInfoResponse> response) {
                Log.d(TAG, "Get user info response code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    UserInfoResponse userInfoResponse = response.body();
                    Log.d(TAG, "Get user info successful for: " + userInfoResponse.getEmail());
                    userInfoSuccessLiveData.postValue(userInfoResponse);
                } else {
                    String errorMessage = "Lấy thông tin người dùng thất bại.";
                    if (response.code() == 401) {
                        errorMessage = "Phiên đăng nhập đã hết hạn.";
                    } else if (response.code() == 404) {
                        errorMessage = "Không tìm thấy thông tin người dùng.";
                    }
                    Log.e(TAG, "Get user info failed with code: " + response.code());
                    userInfoErrorLiveData.postValue(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<UserInfoResponse> call, Throwable t) {
                Log.e(TAG, "Get user info network error: " + t.getMessage());
                userInfoErrorLiveData.postValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
} 