package com.example.heath_android.utils;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class ChangePassRetrofitClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = "http://10.0.2.2/"; // thay bằng IP backend thật

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
