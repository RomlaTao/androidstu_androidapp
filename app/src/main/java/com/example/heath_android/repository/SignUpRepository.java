package com.example.heath_android.repository;
import com.example.heath_android.utils.SignUpRetrofitClient;
import com.example.heath_android.model.SignUpRequest;
import com.example.heath_android.model.SignUpResponse;

import retrofit2.Call;
import retrofit2.Callback;
public class SignUpRepository {
    public void registerUser(SignUpRequest request, Callback<SignUpResponse> callback) {
        Call<SignUpResponse> call = SignUpRetrofitClient.getAuthApi().registerUser(request);
        call.enqueue(callback);
    }
}
