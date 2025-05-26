package com.example.heath_android.repository;
import androidx.lifecycle.MutableLiveData;

import com.example.heath_android.api.LogInAuthApi;
import com.example.heath_android.model.LogInRequest;
import com.example.heath_android.model.LogInResponse;
import com.example.heath_android.utils.LogInRetrofitClient;
import android.util.Log;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogInRepository {
    private final LogInAuthApi logInApi;

    public LogInRepository() {
        logInApi = LogInRetrofitClient.getClient().create(LogInAuthApi.class);
    }

    public void login(String email, String password, MutableLiveData<LogInResponse> responseLiveData) {
        LogInRequest request = new LogInRequest(email, password);
        logInApi.login(request).enqueue(new Callback<LogInResponse>() {
            @Override
            public void onResponse(Call<LogInResponse> call, Response<LogInResponse> response) {
                if (response.isSuccessful()) {
                    LogInResponse body = response.body();
                    if (body != null) {
                        Log.d("API_RESPONSE", "Token: " + body.getToken());
                        responseLiveData.postValue(body);
                    } else {
                        Log.e("API_RESPONSE", "Response body is null");
                        responseLiveData.postValue(null);
                    }
                } else {
                    Log.e("API_RESPONSE", "Response failed: " + response.code());
                    responseLiveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<LogInResponse> call, Throwable t) {
                responseLiveData.postValue(null);
            }
        });
    }
}
