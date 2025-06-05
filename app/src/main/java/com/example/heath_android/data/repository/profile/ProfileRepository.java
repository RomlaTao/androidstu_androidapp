package com.example.heath_android.data.repository.profile;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.heath_android.data.local.DatabaseInformation;
import com.example.heath_android.data.model.profile.ProfileRequest;
import com.example.heath_android.data.model.profile.ProfileResponse;
import com.example.heath_android.data.api.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileRepository {
    private static final String TAG = "ProfileRepository";
    private static final String BASE_URL = "http://10.0.2.2:8080/";
    private static ProfileRepository instance;
    
    private ApiService apiService;
    private DatabaseInformation databaseInformation;
    private Context context;

    // LiveData for user info retrieval (GET)
    private MutableLiveData<ProfileResponse> userInfoLiveData;
    private MutableLiveData<String> getUserInfoErrorLiveData;
    
    // LiveData for user info update (PUT)
    private MutableLiveData<ProfileResponse> updateUserInfoSuccessLiveData;
    private MutableLiveData<String> updateUserInfoErrorLiveData;
    
    // Loading states
    private MutableLiveData<Boolean> loadingLiveData;

    private ProfileRepository(Context context) {
        this.context = context.getApplicationContext();
        this.databaseInformation = new DatabaseInformation(this.context);
        
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
        
        // Initialize LiveData
        userInfoLiveData = new MutableLiveData<>();
        getUserInfoErrorLiveData = new MutableLiveData<>();
        updateUserInfoSuccessLiveData = new MutableLiveData<>();
        updateUserInfoErrorLiveData = new MutableLiveData<>();
        loadingLiveData = new MutableLiveData<>();
    }

    public static synchronized ProfileRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ProfileRepository(context);
        }
        return instance;
    }

    // Getters for LiveData
    public LiveData<ProfileResponse> getUserInfoLiveData() {
        return userInfoLiveData;
    }
    
    public LiveData<String> getUserInfoErrorLiveData() {
        return getUserInfoErrorLiveData;
    }
    
    public LiveData<ProfileResponse> getUpdateUserInfoSuccessLiveData() {
        return updateUserInfoSuccessLiveData;
    }
    
    public LiveData<String> getUpdateUserInfoErrorLiveData() {
        return updateUserInfoErrorLiveData;
    }
    
    public LiveData<Boolean> getLoadingLiveData() {
        return loadingLiveData;
    }

    // GET user info
    public void getUserInfo() {
        String token = databaseInformation.getToken();
        String userId = databaseInformation.getUserId();
        
        if (token == null || token.isEmpty()) {
            getUserInfoErrorLiveData.postValue("Token không hợp lệ");
            return;
        }
        
        if (userId == null || userId.isEmpty()) {
            getUserInfoErrorLiveData.postValue("User ID không hợp lệ");
            return;
        }
        
        Log.d(TAG, "Getting user info for userId: " + userId);
        loadingLiveData.postValue(true);
        
        String authHeader = "Bearer " + token;
        Call<ProfileResponse> call = apiService.getProfile(authHeader, userId);
        
        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                loadingLiveData.postValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ProfileResponse userInfo = response.body();
                    Log.d(TAG, "Get user info successful for: " + userInfo.getEmail());
                    userInfoLiveData.postValue(userInfo);
                } else {
                    String errorMessage = "Lấy thông tin người dùng thất bại.";
                    if (response.code() == 401) {
                        errorMessage = "Phiên đăng nhập đã hết hạn.";
                    } else if (response.code() == 404) {
                        errorMessage = "Không tìm thấy thông tin người dùng.";
                    } else if (response.code() == 500) {
                        errorMessage = "Lỗi server. Vui lòng thử lại sau.";
                    }
                    Log.e(TAG, "Get user info failed with code: " + response.code());
                    getUserInfoErrorLiveData.postValue(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                loadingLiveData.postValue(false);
                Log.e(TAG, "Get user info network error: " + t.getMessage(), t);
                getUserInfoErrorLiveData.postValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // PUT update user info
    public void updateUserInfo(ProfileRequest profileRequest) {
        String token = databaseInformation.getToken();
        String userId = databaseInformation.getUserId();
        
        if (token == null || token.isEmpty()) {
            updateUserInfoErrorLiveData.postValue("Token không hợp lệ");
            return;
        }
        
        if (userId == null || userId.isEmpty()) {
            updateUserInfoErrorLiveData.postValue("User ID không hợp lệ");
            return;
        }
        
        Log.d(TAG, "Updating user info for userId: " + userId);
        loadingLiveData.postValue(true);
        
        String authHeader = "Bearer " + token;
        Call<ProfileResponse> call = apiService.updateProfile(authHeader, userId, profileRequest);
        
        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                loadingLiveData.postValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ProfileResponse updatedUserInfo = response.body();
                    Log.d(TAG, "User info updated successfully for: " + updatedUserInfo.getEmail());
                    
                    // Update local database/storage
                    updateLocalUserInfo(updatedUserInfo);
                    
                    updateUserInfoSuccessLiveData.postValue(updatedUserInfo);
                } else {
                    String errorMessage = "Cập nhật thông tin thất bại.";
                    if (response.code() == 400) {
                        errorMessage = "Thông tin không hợp lệ.";
                    } else if (response.code() == 401) {
                        errorMessage = "Phiên đăng nhập đã hết hạn.";
                    } else if (response.code() == 500) {
                        errorMessage = "Lỗi server. Vui lòng thử lại sau.";
                    }
                    Log.e(TAG, "Update user info failed with code: " + response.code());
                    updateUserInfoErrorLiveData.postValue(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                loadingLiveData.postValue(false);
                Log.e(TAG, "Update user info network error: " + t.getMessage(), t);
                updateUserInfoErrorLiveData.postValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Update local user info in database/storage
    private void updateLocalUserInfo(ProfileResponse userInfo) {
        try {
            String token = databaseInformation.getToken();
            databaseInformation.saveUserInfo(
                token,
                userInfo.getEmail(),
                userInfo.getFullName(),
                String.valueOf(userInfo.getId())
            );
            Log.d(TAG, "Local user info updated successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error updating local user info", e);
        }
    }

    // Clear error messages
    public void clearErrors() {
        getUserInfoErrorLiveData.postValue(null);
        updateUserInfoErrorLiveData.postValue(null);
    }

    // Refresh user info from both API and local storage
    public void refreshUserInfo() {
        Log.d(TAG, "Refreshing user info");
        getUserInfo();
    }
}