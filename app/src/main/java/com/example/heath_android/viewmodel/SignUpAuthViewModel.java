package com.example.heath_android.viewmodel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.heath_android.model.SignUpRequest;
import com.example.heath_android.model.SignUpResponse;
import com.example.heath_android.repository.SignUpRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class SignUpAuthViewModel extends ViewModel {
    private final SignUpRepository signUpRepository = new SignUpRepository();
    private final MutableLiveData<SignUpResponse> signUpSuccessLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> signUpErrorLiveData = new MutableLiveData<>();

    public LiveData<SignUpResponse> getSignUpSuccessLiveData() {
        return signUpSuccessLiveData;
    }

    public LiveData<String> getSignUpErrorLiveData() {
        return signUpErrorLiveData;
    }

    public void registerUser(SignUpRequest request) {
        signUpRepository.registerUser(request, new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    signUpSuccessLiveData.setValue(response.body());
                } else {
                    signUpErrorLiveData.setValue("Email đã tồn tại hoặc lỗi đăng ký");
                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                signUpErrorLiveData.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}
