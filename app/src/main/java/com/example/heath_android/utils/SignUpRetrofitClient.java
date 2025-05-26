package com.example.heath_android.utils;

import com.example.heath_android.api.SignUpAuthApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class SignUpRetrofitClient {
    private static Retrofit retrofit = null;

    public static SignUpAuthApi getAuthApi() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.53.162/")  // Nếu dùng Android emulator, đổi IP nếu khác
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(SignUpAuthApi.class);
    }
}
