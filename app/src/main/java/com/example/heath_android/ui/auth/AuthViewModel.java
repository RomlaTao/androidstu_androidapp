package com.example.heath_android.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.heath_android.data.model.auth.LoginResponse;
import com.example.heath_android.data.model.auth.SignupRequest;
import com.example.heath_android.data.model.User;
import com.example.heath_android.data.repository.auth.LoginRepository;
import com.example.heath_android.data.repository.auth.SignupRepository;

public class AuthViewModel extends ViewModel {
    private LoginRepository loginRepository;
    private SignupRepository signupRepository;
    
    // LiveData cho đăng nhập
    private MutableLiveData<LoginResponse> responseLiveData;
    private MutableLiveData<String> loginErrorLiveData;
    
    // LiveData cho đăng ký
    private MutableLiveData<User> signupSuccessLiveData;
    private MutableLiveData<String> signupErrorLiveData;

    public AuthViewModel() {
        loginRepository = new LoginRepository();
        signupRepository = new SignupRepository();
        
        responseLiveData = new MutableLiveData<>();
        loginErrorLiveData = new MutableLiveData<>();
        signupSuccessLiveData = new MutableLiveData<>();
        signupErrorLiveData = new MutableLiveData<>();
        
        // Observe repository data
        setupRepositoryObservers();
    }

    private void setupRepositoryObservers() {
        // Login observers
        loginRepository.getLoginLiveData().observeForever(loginResponse -> {
            responseLiveData.postValue(loginResponse);
        });
        
        loginRepository.getErrorLiveData().observeForever(error -> {
            loginErrorLiveData.postValue(error);
        });
        
        // Signup observers
        signupRepository.getSignupSuccessLiveData().observeForever(user -> {
            signupSuccessLiveData.postValue(user);
        });
        
        signupRepository.getSignupErrorLiveData().observeForever(error -> {
            signupErrorLiveData.postValue(error);
        });
    }

    // Login methods
    public void login(String email, String password) {
        loginRepository.login(email, password);
    }

    public LiveData<LoginResponse> getResponseLiveData() {
        return responseLiveData;
    }

    public LiveData<String> getLoginErrorLiveData() {
        return loginErrorLiveData;
    }

    // Signup methods
    public void signupUser(SignupRequest signupRequest) {
        signupRepository.signup(signupRequest);
    }

    public LiveData<User> getSignupSuccessLiveData() {
        return signupSuccessLiveData;
    }

    public LiveData<String> getSignupErrorLiveData() {
        return signupErrorLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Clean up observers if needed
    }
}
