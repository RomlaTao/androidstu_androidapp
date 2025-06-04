package com.example.heath_android.data;

import com.example.heath_android.data.model.home.BMIBMRResponse;
import com.example.heath_android.data.model.home.CaloriesResponse;
import com.example.heath_android.data.model.home.CaloriesInWeekly;
import com.example.heath_android.data.model.home.CaloriesOutWeekly;
import com.example.heath_android.data.model.auth.LoginRequest;
import com.example.heath_android.data.model.auth.LoginResponse;
import com.example.heath_android.data.model.auth.SignupRequest;
import com.example.heath_android.data.model.auth.SignupResponse;
import com.example.heath_android.data.model.home.TDEEResponse;
import com.example.heath_android.data.model.onboarding.UserInfoRequest;
import com.example.heath_android.data.model.onboarding.UserInfoResponse;
import com.example.heath_android.data.model.profile.ProfileRequest;
import com.example.heath_android.data.model.profile.ProfileResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface ApiService {
    
    @POST("/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
    
    @POST("/auth/signup")
    Call<SignupResponse> signup(@Body SignupRequest signUpRequest);
    
    @PUT("users/{userId}")
    Call<UserInfoResponse> setupUserInfo(@Header("Authorization") String token, @Path("userId") String userId, @Body UserInfoRequest userInfoRequest);
    
    @GET("users/{userId}")
    Call<UserInfoResponse> getUserInfo(@Header("Authorization") String token, @Path("userId") String userId);

    @PUT("users/{userId}")
    Call<ProfileResponse> updateProfile(@Header("Authorization") String token, @Path("userId") String userId, @Body ProfileRequest profileRequest);

    @GET("users/{userId}")
    Call<ProfileResponse> getProfile(@Header("Authorization") String token, @Path("userId") String userId);
    @GET("analytics/health-analytics/health-metrics/{userID}")
    Call<BMIBMRResponse> getBMIBMRData(@Header("Authorization") String token, @Path("userID") String userID);

    @GET("/analytics/health-analytics/tdee/{userID}/strategy")
    Call<TDEEResponse> getTDEEData(@Header("Authorization") String token, @Path("userID") String userID);

    @GET("/workouts/calories-burned/daily/{userID}")
    Call<CaloriesResponse> getCaloriesData(@Header("Authorization") String token, @Path("userID") String userID, @Query("date") String date);

    @GET("/workouts/calories-burned/weekly/{userID}")
    Call<List<CaloriesOutWeekly>> getCaloriesOutWeeklyData(@Header("Authorization") String token, @Path("userID") String userID, @Query("startDate") String startDate);

    @GET("/meals/calories/weekly/{userID}")
    Call<List<CaloriesInWeekly>> getCaloriesInWeeklyData(@Header("Authorization") String token, @Path("userID") String userID, @Query("startDate") String startDate);
}