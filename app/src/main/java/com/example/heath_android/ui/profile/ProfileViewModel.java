package com.example.heath_android.ui.profile;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.heath_android.data.repository.profile.ProfileRepository;
import com.example.heath_android.data.model.profile.ProfileRequest;
import com.example.heath_android.data.model.profile.ProfileResponse;

public class ProfileViewModel extends AndroidViewModel {
    private static final String TAG = "ProfileViewModel";

    private ProfileRepository profileRepository;

    // LiveData from repository
    private LiveData<ProfileResponse> userInfoLiveData;
    private LiveData<String> getUserInfoErrorLiveData;
    private LiveData<ProfileResponse> updateUserInfoSuccessLiveData;
    private LiveData<String> updateUserInfoErrorLiveData;
    private LiveData<Boolean> loadingLiveData;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        
        Log.d(TAG, "ProfileViewModel created");
        
        // Initialize repository
        profileRepository = ProfileRepository.getInstance(application);
        
        // Get LiveData from repository
        userInfoLiveData = profileRepository.getUserInfoLiveData();
        getUserInfoErrorLiveData = profileRepository.getUserInfoErrorLiveData();
        updateUserInfoSuccessLiveData = profileRepository.getUpdateUserInfoSuccessLiveData();
        updateUserInfoErrorLiveData = profileRepository.getUpdateUserInfoErrorLiveData();
        loadingLiveData = profileRepository.getLoadingLiveData();
    }

    // Getters for LiveData (expose repository LiveData to UI)
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

    // Business logic methods (delegate to repository)
    public void getUserInfo() {
        Log.d(TAG, "ViewModel: Getting user info");
        profileRepository.getUserInfo();
    }

    public void updateUserInfo(ProfileRequest profileRequest) {
        Log.d(TAG, "ViewModel: Updating user info");
        profileRepository.updateUserInfo(profileRequest);
    }
    
    public void refreshUserInfo() {
        Log.d(TAG, "ViewModel: Refreshing user info");
        profileRepository.refreshUserInfo();
    }
    
    public void clearErrors() {
        Log.d(TAG, "ViewModel: Clearing errors");
        profileRepository.clearErrors();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "ProfileViewModel cleared");
    }
}
