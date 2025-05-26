package com.example.heath_android.api;
import com.example.heath_android.model.LogInRequest;
import com.example.heath_android.model.LogInResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
public interface LogInAuthApi {
    @POST("/auth/login")
    Call<LogInResponse> login(@Body LogInRequest request);
}
