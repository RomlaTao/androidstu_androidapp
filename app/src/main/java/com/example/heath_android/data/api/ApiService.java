package com.example.heath_android.data.api;

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

// Schedule package imports
import com.example.heath_android.data.model.schedule.WorkoutRequest;
import com.example.heath_android.data.model.schedule.WorkoutResponse;
import com.example.heath_android.data.model.schedule.WorkoutSchedulesRequest;
import com.example.heath_android.data.model.schedule.WorkoutSchedulesResponse;
import com.example.heath_android.data.model.schedule.ScheduledWorkoutRequest;
import com.example.heath_android.data.model.schedule.ScheduledWorkoutResponse;
import com.example.heath_android.data.model.schedule.MealRequest;
import com.example.heath_android.data.model.schedule.MealResponse;
import com.example.heath_android.data.model.schedule.MealSchedulesRequest;
import com.example.heath_android.data.model.schedule.MealSchedulesResponse;
import com.example.heath_android.data.model.schedule.ScheduledMealRequest;
import com.example.heath_android.data.model.schedule.ScheduledMealResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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

    // lấy danh sách workout
    @GET("/workouts")
    Call<List<WorkoutResponse>> getWorkouts(@Header("Authorization") String token);

    // tạo workout
    @POST("/workouts")
    Call<WorkoutResponse> createWorkout(@Header("Authorization") String token, @Body WorkoutRequest workoutRequest);

    // lấy workout theo id
    @GET("/workouts/{workoutId}")
    Call<WorkoutResponse> getWorkout(@Header("Authorization") String token, @Path("workoutId") Long workoutId);

    // cập nhật workout
    @PUT("/workouts/{workoutId}")
    Call<WorkoutResponse> updateWorkout(@Header("Authorization") String token, @Path("workoutId") Long workoutId, @Body WorkoutRequest workoutRequest);

    // xóa workout
    @DELETE("/workouts/{workoutId}")
    Call<Void> deleteWorkout(@Header("Authorization") String token, @Path("workoutId") Long workoutId);

    // tạo workout schedules
    @POST("/workouts/schedules")
    Call<WorkoutSchedulesResponse> createWorkoutSchedules(@Header("Authorization") String token, @Body WorkoutSchedulesRequest workoutSchedulesRequest);

    // lấy workout schedules
    @GET("/workouts/schedules/user/{userId}")
    Call<List<WorkoutSchedulesResponse>> getWorkoutSchedules(@Header("Authorization") String token, @Path("userId") String userId);

    // cập nhập schedules theo id
    @PUT("/workouts/schedules/{scheduleId}")
    Call<WorkoutSchedulesResponse> updateWorkoutSchedules(@Header("Authorization") String token, @Path("scheduleId") Long scheduleId, @Body WorkoutSchedulesRequest workoutSchedulesRequest);

    // lấy danh sách scheduledworkout theo scheduleId
    @GET("/workouts/scheduled-workouts/schedule/{scheduleId}")
    Call<List<ScheduledWorkoutResponse>> getScheduledWorkouts(@Header("Authorization") String token, @Path("scheduleId") Long scheduleId);

    // tạo scheduledworkout
    @POST("/workouts/scheduled-workouts")
    Call<ScheduledWorkoutResponse> createScheduledWorkout(@Header("Authorization") String token, @Body ScheduledWorkoutRequest scheduledWorkoutRequest);

    // lấy scheduled workout theo id
    @GET("/workouts/scheduled-workouts/{scheduledWorkoutId}")
    Call<ScheduledWorkoutResponse> getScheduledWorkout(@Header("Authorization") String token, @Path("scheduledWorkoutId") Long scheduledWorkoutId);

    // lấy danh sách meal
    @GET("/meals")
    Call<List<MealResponse>> getMeals(@Header("Authorization") String token);

    // tạo meal
    @POST("/meals")
    Call<MealResponse> createMeal(@Header("Authorization") String token, @Body MealRequest mealRequest);

    // lấy meal theo id
    @GET("/meals/{mealId}")
    Call<MealResponse> getMeal(@Header("Authorization") String token, @Path("mealId") Long mealId);

    // cập nhật meal
    @PUT("/meals/{mealId}")
    Call<MealResponse> updateMeal(@Header("Authorization") String token, @Path("mealId") Long mealId, @Body MealRequest mealRequest);

    // xóa meal
    @DELETE("/meals/{mealId}")
    Call<Void> deleteMeal(@Header("Authorization") String token, @Path("mealId") Long mealId);

    //tạo meal schedules
    @POST("/meals/schedules")
    Call<MealSchedulesResponse> createMealSchedules(@Header("Authorization") String token, @Body MealSchedulesRequest mealSchedulesRequest);

    // lấy danh sách meal schedules
    @GET("/meals/schedules/user/{userId}")
    Call<List<MealSchedulesResponse>> getMealSchedules(@Header("Authorization") String token, @Path("userId") String userId);

    // cập nhập meal schedules theo id
    @PUT("/meals/schedules/{scheduleId}")
    Call<MealSchedulesResponse> updateMealSchedules(@Header("Authorization") String token, @Path("scheduleId") Long scheduleId, @Body MealSchedulesRequest mealSchedulesRequest);

    // lấy danh sách scheduled meal theo scheduleId
    @GET("/meals/scheduled-meals/schedule/{scheduleId}")
    Call<List<ScheduledMealResponse>> getScheduledMeals(@Header("Authorization") String token, @Path("scheduleId") Long scheduleId);

    // tạo scheduled meal
    @POST("/meals/scheduled-meals")
    Call<ScheduledMealResponse> createScheduledMeal(@Header("Authorization") String token, @Body ScheduledMealRequest scheduledMealRequest);

    // lấy scheduled meal theo id
    @GET("/meals/scheduled-meals/{scheduledMealId}")
    Call<ScheduledMealResponse> getScheduledMeal(@Header("Authorization") String token, @Path("scheduledMealId") Long scheduledMealId);

    // cập nhật scheduled workout
    @PUT("/workouts/scheduled-workouts/{scheduledWorkoutId}")
    Call<ScheduledWorkoutResponse> updateScheduledWorkout(@Header("Authorization") String token, @Path("scheduledWorkoutId") Long scheduledWorkoutId, @Body ScheduledWorkoutRequest scheduledWorkoutRequest);

    // xóa scheduled workout
    @DELETE("/workouts/scheduled-workouts/{scheduledWorkoutId}")
    Call<Void> deleteScheduledWorkout(@Header("Authorization") String token, @Path("scheduledWorkoutId") Long scheduledWorkoutId);

    // cập nhật scheduled meal
    @PUT("/meals/scheduled-meals/{scheduledMealId}")
    Call<ScheduledMealResponse> updateScheduledMeal(@Header("Authorization") String token, @Path("scheduledMealId") Long scheduledMealId, @Body ScheduledMealRequest scheduledMealRequest);

    // xóa scheduled meal
    @DELETE("/meals/scheduled-meals/{scheduledMealId}")
    Call<Void> deleteScheduledMeal(@Header("Authorization") String token, @Path("scheduledMealId") Long scheduledMealId);

    // xóa workout schedule
    @DELETE("/workouts/schedules/{scheduleId}")
    Call<Void> deleteWorkoutSchedule(@Header("Authorization") String token, @Path("scheduleId") Long scheduleId);

    // xóa meal schedule
    @DELETE("/meals/schedules/{scheduleId}")
    Call<Void> deleteMealSchedule(@Header("Authorization") String token, @Path("scheduleId") Long scheduleId);

    // lấy tất cả scheduled workouts của user
    @GET("/workouts/scheduled-workouts/user/{userId}")
    Call<List<ScheduledWorkoutResponse>> getScheduledWorkoutsByUser(@Header("Authorization") String token, @Path("userId") String userId);

    // lấy scheduled workouts theo date range
    @GET("/workouts/scheduled-workouts/user/{userId}/date-range")
    Call<List<ScheduledWorkoutResponse>> getScheduledWorkoutsByDateRange(@Header("Authorization") String token, @Path("userId") String userId, @Query("startDate") String startDate, @Query("endDate") String endDate);

    // lấy tất cả scheduled meals của user
    @GET("/meals/scheduled-meals/user/{userId}")
    Call<List<ScheduledMealResponse>> getScheduledMealsByUser(@Header("Authorization") String token, @Path("userId") String userId);

    // lấy scheduled meals theo date range
    @GET("/meals/scheduled-meals/user/{userId}/date-range")
    Call<List<ScheduledMealResponse>> getScheduledMealsByDateRange(@Header("Authorization") String token, @Path("userId") String userId, @Query("startDate") String startDate, @Query("endDate") String endDate);
}