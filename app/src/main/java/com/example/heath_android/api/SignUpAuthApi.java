package com.example.heath_android.api;
import com.example.heath_android.model.SignUpRequest;
import com.example.heath_android.model.SignUpResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
public interface SignUpAuthApi {
    @POST("/auth/signup")
    Call<SignUpResponse> registerUser(@Body SignUpRequest request);
}
