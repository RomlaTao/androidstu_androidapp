package com.example.heath_android.api;
import com.example.heath_android.model.ChangePassRequest;
import com.example.heath_android.model.ChangePassResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
public interface ChangePassAuthApi {
    @POST("/auth/change-password")
    Call<ChangePassResponse> changePassword(
            @Header("Authorization") String token,
            @Body ChangePassRequest request
    );
}
