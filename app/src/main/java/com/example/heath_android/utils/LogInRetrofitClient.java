package com.example.heath_android.utils;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class LogInRetrofitClient {
    private static final String BASE_URL = "http://192.168.53.162/"; // thay bằng URL gốc
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
