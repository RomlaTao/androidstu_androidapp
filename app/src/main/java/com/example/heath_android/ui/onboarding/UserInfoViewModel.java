package com.example.heath_android.ui.onboarding;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.heath_android.data.model.onboarding.UserInfoRequest;
import com.example.heath_android.data.model.onboarding.UserInfoResponse;
import com.example.heath_android.data.repository.onboarding.UserInfoRepository;

public class UserInfoViewModel extends ViewModel {
    private UserInfoRepository userInfoRepository;
    
    // LiveData cho setup user info
    private MutableLiveData<UserInfoResponse> userInfoSuccessLiveData;
    private MutableLiveData<String> userInfoErrorLiveData;

    public UserInfoViewModel() {
        userInfoRepository = new UserInfoRepository();
        
        userInfoSuccessLiveData = new MutableLiveData<>();
        userInfoErrorLiveData = new MutableLiveData<>();
        
        // Observe repository data
        setupRepositoryObservers();
    }

    private void setupRepositoryObservers() {
        // UserInfo observers
        userInfoRepository.getUserInfoSuccessLiveData().observeForever(userInfoResponse -> {
            userInfoSuccessLiveData.postValue(userInfoResponse);
        });
        
        userInfoRepository.getUserInfoErrorLiveData().observeForever(error -> {
            userInfoErrorLiveData.postValue(error);
        });
    }

    // UserInfo methods
    public void setupUserInfo(String token, String userId, UserInfoRequest userInfoRequest) {
        userInfoRepository.setupUserInfo(token, userId, userInfoRequest);
    }

    public void getUserInfo(String token, String userId) {
        userInfoRepository.getUserInfo(token, userId);
    }

    public LiveData<UserInfoResponse> getUserInfoSuccessLiveData() {
        return userInfoSuccessLiveData;
    }

    public LiveData<String> getUserInfoErrorLiveData() {
        return userInfoErrorLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Clean up observers if needed
    }
} 