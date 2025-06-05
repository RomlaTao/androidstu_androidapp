package com.example.heath_android.data.repository.home;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.heath_android.data.local.DatabaseInformation;
import com.example.heath_android.data.api.ApiService;
import com.example.heath_android.data.model.home.BMIBMRResponse;
import com.example.heath_android.data.model.home.CaloriesResponse;
import com.example.heath_android.data.model.home.CaloriesInWeekly;
import com.example.heath_android.data.model.home.CaloriesOutWeekly;
import com.example.heath_android.data.model.home.HomeResponse;
import com.example.heath_android.data.model.home.TDEEResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;

public class HomeRepository {
    private static final String TAG = "HomeRepository";
    private static HomeRepository instance;

    private static final String BASE_URL = "http://10.0.2.2:8080/";
    private Context context;
    private DatabaseInformation databaseInformation;

    private ApiService apiService;
    
    // LiveData for user info (basic user data)
    private MutableLiveData<HomeResponse> homeDataLiveData;
    private MutableLiveData<Boolean> loadingLiveData;
    private MutableLiveData<String> errorLiveData;
    
    // LiveData for specific health data
    private MutableLiveData<BMIBMRResponse> bmiBmrDataLiveData;
    private MutableLiveData<TDEEResponse> tdeeDataLiveData;
    private MutableLiveData<CaloriesResponse> caloriesDataLiveData;
    
    // LiveData for chart data - now using List directly
    private MutableLiveData<List<CaloriesInWeekly>> caloriesInWeeklyLiveData;
    private MutableLiveData<List<CaloriesOutWeekly>> caloriesOutWeeklyLiveData;

    private HomeRepository(Context context) {
        this.context = context.getApplicationContext();
        this.databaseInformation = new DatabaseInformation(this.context);
        
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
        
        // Initialize LiveData
        homeDataLiveData = new MutableLiveData<>();
        loadingLiveData = new MutableLiveData<>();
        errorLiveData = new MutableLiveData<>();
        
        // Initialize LiveData for specific health data
        bmiBmrDataLiveData = new MutableLiveData<>();
        tdeeDataLiveData = new MutableLiveData<>();
        caloriesDataLiveData = new MutableLiveData<>();
        
        // Initialize LiveData for chart data
        caloriesInWeeklyLiveData = new MutableLiveData<>();
        caloriesOutWeeklyLiveData = new MutableLiveData<>();
    }

    public static synchronized HomeRepository getInstance(Context context) {
        if (instance == null) {
            instance = new HomeRepository(context);
        }
        return instance;
    }

    // Getters
    public LiveData<HomeResponse> getHomeData() {
        return homeDataLiveData;
    }

    public LiveData<Boolean> getLoading() {
        return loadingLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }
    
    // Getters for specific health data
    public LiveData<BMIBMRResponse> getBmiBmrData() {
        return bmiBmrDataLiveData;
    }
    
    public LiveData<TDEEResponse> getTdeeData() {
        return tdeeDataLiveData;
    }
    
    public LiveData<CaloriesResponse> getCaloriesData() {
        return caloriesDataLiveData;
    }
    
    // Getters for chart data - now returning List directly
    public LiveData<List<CaloriesInWeekly>> getCaloriesInWeeklyData() {
        return caloriesInWeeklyLiveData;
    }
    
    public LiveData<List<CaloriesOutWeekly>> getCaloriesOutWeeklyData() {
        return caloriesOutWeeklyLiveData;
    }

    // Fetch basic user info (create fallback since no specific endpoint mentioned)
    public void fetchUserInfo() {
        String userId = databaseInformation.getUserId();
        String token = databaseInformation.getToken();
        
        if (userId == null || userId.isEmpty()) {
            Log.e(TAG, "UserId not available, cannot fetch user info");
            errorLiveData.postValue("Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại.");
            return;
        }
        
        if (token == null || token.isEmpty()) {
            Log.e(TAG, "Token not available, cannot fetch user info");
            errorLiveData.postValue("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.");
            return;
        }

        Log.d(TAG, "Creating fallback user info for userId: " + userId);
        
        // Create user info from local data since no specific user info endpoint is mentioned
        HomeResponse userInfoResponse = createUserInfoFromLocal();
        homeDataLiveData.postValue(userInfoResponse);
    }

    // Fetch health data from specific endpoints
    public void fetchHealthData() {
        String userId = databaseInformation.getUserId();
        String token = databaseInformation.getToken();
        
        if (userId == null || userId.isEmpty() || token == null || token.isEmpty()) {
            Log.e(TAG, "UserId or Token not available, cannot fetch health data");
            errorLiveData.postValue("Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại.");
            return;
        }

        Log.d(TAG, "Fetching health data for userId: " + userId);
        loadingLiveData.postValue(true);
        
        // Fetch specific health data from different endpoints
        fetchBMIBMRData(userId, token);
        fetchTDEEData(userId, token);
        fetchCaloriesData(userId, token);
    }
    
    // Fetch chart data from weekly endpoints
    public void fetchChartData() {
        String userId = databaseInformation.getUserId();
        String token = databaseInformation.getToken();
        
        if (userId == null || userId.isEmpty() || token == null || token.isEmpty()) {
            Log.e(TAG, "UserId or Token not available, cannot fetch chart data");
            errorLiveData.postValue("Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại.");
            return;
        }

        Log.d(TAG, "Fetching chart data for userId: " + userId);
        
        // Fetch weekly calories data for charts
        fetchCaloriesInWeeklyData(userId, token);
        fetchCaloriesOutWeeklyData(userId, token);
    }

    // Fetch BMI and BMR data from getBMIBMRData endpoint
    private void fetchBMIBMRData(String userId, String token) {
        String authHeader = "Bearer " + token;
        Call<BMIBMRResponse> call = apiService.getBMIBMRData(authHeader, userId);
        
        call.enqueue(new Callback<BMIBMRResponse>() {
            @Override
            public void onResponse(Call<BMIBMRResponse> call, Response<BMIBMRResponse> response) {
                Log.d(TAG, "BMI/BMR data response code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    BMIBMRResponse bmiBmrResponse = response.body();
                    Log.d(TAG, String.format("BMI/BMR data fetched successfully - BMI: %.1f, BMR: %.0f", 
                        bmiBmrResponse.getBmi(), bmiBmrResponse.getBmr()));
                    bmiBmrDataLiveData.postValue(bmiBmrResponse);
                } else {
                    Log.e(TAG, "Failed to fetch BMI/BMR data: " + response.code());
                    errorLiveData.postValue("Không thể tải dữ liệu BMI/BMR");
                    // Create fallback data
                    BMIBMRResponse fallback = new BMIBMRResponse(0, 0);
                    Log.d(TAG, "Using fallback BMI/BMR data");
                    bmiBmrDataLiveData.postValue(fallback);
                }
                checkLoadingComplete();
            }

            @Override
            public void onFailure(Call<BMIBMRResponse> call, Throwable t) {
                Log.e(TAG, "BMI/BMR data network error: " + t.getMessage());
                errorLiveData.postValue("Lỗi kết nối khi tải BMI/BMR: " + t.getMessage());
                // Create fallback data
                BMIBMRResponse fallback = new BMIBMRResponse(0, 0);
                Log.d(TAG, "Using fallback BMI/BMR data due to network error");
                bmiBmrDataLiveData.postValue(fallback);
                checkLoadingComplete();
            }
        });
    }

    // Fetch TDEE data from getTDEEData endpoint
    private void fetchTDEEData(String userId, String token) {
        String authHeader = "Bearer " + token;
        Call<TDEEResponse> call = apiService.getTDEEData(authHeader, userId);
        
        call.enqueue(new Callback<TDEEResponse>() {
            @Override
            public void onResponse(Call<TDEEResponse> call, Response<TDEEResponse> response) {
                Log.d(TAG, "TDEE data response code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    TDEEResponse tdeeResponse = response.body();
                    Log.d(TAG, String.format("TDEE data fetched successfully - TDEE: %.0f", 
                        tdeeResponse.getTdee()));
                    tdeeDataLiveData.postValue(tdeeResponse);
                } else {
                    Log.e(TAG, "Failed to fetch TDEE data: " + response.code());
                    errorLiveData.postValue("Không thể tải dữ liệu TDEE");
                    // Create fallback data
                    TDEEResponse fallback = new TDEEResponse(0);
                    Log.d(TAG, "Using fallback TDEE data");
                    tdeeDataLiveData.postValue(fallback);
                }
                checkLoadingComplete();
            }

            @Override
            public void onFailure(Call<TDEEResponse> call, Throwable t) {
                Log.e(TAG, "TDEE data network error: " + t.getMessage());
                errorLiveData.postValue("Lỗi kết nối khi tải TDEE: " + t.getMessage());
                // Create fallback data
                TDEEResponse fallback = new TDEEResponse(0);
                Log.d(TAG, "Using fallback TDEE data due to network error");
                tdeeDataLiveData.postValue(fallback);
                checkLoadingComplete();
            }
        });
    }

    // Fetch Calories data from getCaloriesData endpoint
    private void fetchCaloriesData(String userId, String token) {
        String authHeader = "Bearer " + token;
        
        // Use current date in yyyy-MM-dd format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        
        Call<CaloriesResponse> call = apiService.getCaloriesData(authHeader, userId, currentDate);
        
        call.enqueue(new Callback<CaloriesResponse>() {
            @Override
            public void onResponse(Call<CaloriesResponse> call, Response<CaloriesResponse> response) {
                Log.d(TAG, "Calories data response code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    CaloriesResponse caloriesResponse = response.body();
                    Log.d(TAG, String.format("Calories data fetched successfully - Total Calories Burned: %.0f", 
                        caloriesResponse.getTotalCaloriesBurned()));
                    caloriesDataLiveData.postValue(caloriesResponse);
                } else {
                    Log.e(TAG, "Failed to fetch Calories data: " + response.code());
                    errorLiveData.postValue("Không thể tải dữ liệu Calories");
                    // Create fallback data
                    CaloriesResponse fallback = new CaloriesResponse(0);
                    Log.d(TAG, "Using fallback Calories data");
                    caloriesDataLiveData.postValue(fallback);
                }
                checkLoadingComplete();
            }

            @Override
            public void onFailure(Call<CaloriesResponse> call, Throwable t) {
                Log.e(TAG, "Calories data network error: " + t.getMessage());
                errorLiveData.postValue("Lỗi kết nối khi tải Calories: " + t.getMessage());
                // Create fallback data
                CaloriesResponse fallback = new CaloriesResponse(0);
                Log.d(TAG, "Using fallback Calories data due to network error");
                caloriesDataLiveData.postValue(fallback);
                checkLoadingComplete();
            }
        });
    }

    // Fetch weekly calories in data
    private void fetchCaloriesInWeeklyData(String userId, String token) {
        String authHeader = "Bearer " + token;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_YEAR, -6);
        String startDate = dateFormat.format(calendar.getTime());
        
        Log.d(TAG, "Fetching Calories In Weekly data for userId: " + userId + ", startDate: " + startDate);
        
        Call<List<CaloriesInWeekly>> call = apiService.getCaloriesInWeeklyData(authHeader, userId, startDate);
        
        call.enqueue(new Callback<List<CaloriesInWeekly>>() {
            @Override
            public void onResponse(Call<List<CaloriesInWeekly>> call, Response<List<CaloriesInWeekly>> response) {
                Log.d(TAG, "Calories In Weekly response code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    List<CaloriesInWeekly> caloriesInList = response.body();
                    Log.d(TAG, "Calories In Weekly data fetched successfully - " + caloriesInList.size() + " days");
                    
                    // Debug: Log each day's data
                    for (int i = 0; i < caloriesInList.size(); i++) {
                        CaloriesInWeekly day = caloriesInList.get(i);
                        Log.d(TAG, "Day " + i + ": date=" + day.getDate() + ", calories=" + day.getTotalCaloriesIn());
                    }
                    
                    caloriesInWeeklyLiveData.postValue(caloriesInList);
                } else {
                    Log.e(TAG, "Failed to fetch Calories In Weekly data: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error body: " + errorBody);
                        } catch (Exception e) {
                            Log.e(TAG, "Could not read error body", e);
                        }
                    }
                    errorLiveData.postValue("Không thể tải dữ liệu biểu đồ Calories In");
                    // Create fallback data
                    caloriesInWeeklyLiveData.postValue(createFallbackCaloriesInWeekly());
                }
            }

            @Override
            public void onFailure(Call<List<CaloriesInWeekly>> call, Throwable t) {
                Log.e(TAG, "Calories In Weekly network error: " + t.getMessage(), t);
                errorLiveData.postValue("Lỗi kết nối khi tải Calories In Weekly: " + t.getMessage());
                // Create fallback data
                caloriesInWeeklyLiveData.postValue(createFallbackCaloriesInWeekly());
            }
        });
    }

    // Fetch weekly calories out data
    private void fetchCaloriesOutWeeklyData(String userId, String token) {
        String authHeader = "Bearer " + token;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_YEAR, -6);
        String startDate = dateFormat.format(calendar.getTime());
        
        Log.d(TAG, "Fetching Calories Out Weekly data for userId: " + userId + ", startDate: " + startDate);
        
        Call<List<CaloriesOutWeekly>> call = apiService.getCaloriesOutWeeklyData(authHeader, userId, startDate);
        
        call.enqueue(new Callback<List<CaloriesOutWeekly>>() {
            @Override
            public void onResponse(Call<List<CaloriesOutWeekly>> call, Response<List<CaloriesOutWeekly>> response) {
                Log.d(TAG, "Calories Out Weekly response code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    List<CaloriesOutWeekly> caloriesOutList = response.body();
                    Log.d(TAG, "Calories Out Weekly data fetched successfully - " + caloriesOutList.size() + " days");
                    
                    // Debug: Log each day's data
                    for (int i = 0; i < caloriesOutList.size(); i++) {
                        CaloriesOutWeekly day = caloriesOutList.get(i);
                        Log.d(TAG, "Day " + i + ": date=" + day.getDate() + ", calories=" + day.getTotalCaloriesOut());
                    }
                    
                    caloriesOutWeeklyLiveData.postValue(caloriesOutList);
                } else {
                    Log.e(TAG, "Failed to fetch Calories Out Weekly data: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error body: " + errorBody);
                        } catch (Exception e) {
                            Log.e(TAG, "Could not read error body", e);
                        }
                    }
                    errorLiveData.postValue("Không thể tải dữ liệu biểu đồ Calories Out");
                    // Create fallback data
                    caloriesOutWeeklyLiveData.postValue(createFallbackCaloriesOutWeekly());
                }
            }

            @Override
            public void onFailure(Call<List<CaloriesOutWeekly>> call, Throwable t) {
                Log.e(TAG, "Calories Out Weekly network error: " + t.getMessage(), t);
                errorLiveData.postValue("Lỗi kết nối khi tải Calories Out Weekly: " + t.getMessage());
                // Create fallback data
                caloriesOutWeeklyLiveData.postValue(createFallbackCaloriesOutWeekly());
            }
        });
    }

    // Helper to check if all API calls are complete
    private void checkLoadingComplete() {
        // This is a simple approach - in production you might want to track individual loading states
        loadingLiveData.postValue(false);
    }

    // Create user info from local data
    private HomeResponse createUserInfoFromLocal() {
        HomeResponse response = new HomeResponse();
        response.setSuccess(true);
        response.setMessage("Thông tin người dùng từ dữ liệu local");

        // Create user info from local database
        HomeResponse.UserInfo userInfo = new HomeResponse.UserInfo();
        String userName = databaseInformation.getName();
        userInfo.setName(userName.isEmpty() ? "Người dùng" : userName);
        userInfo.setAvatarUrl(""); // No avatar from local data
        response.setUserInfo(userInfo);

        Log.d(TAG, "Created user info from local data: " + userName);
        return response;
    }
    
    // Create fallback calories in weekly data - now returns List with actual dates
    private List<CaloriesInWeekly> createFallbackCaloriesInWeekly() {
        List<CaloriesInWeekly> fallbackData = new ArrayList<>();
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        
        // Create 7 days of fallback data starting from 6 days ago
        calendar.add(Calendar.DAY_OF_YEAR, -6);
        
        for (int i = 0; i < 7; i++) {
            String date = dateFormat.format(calendar.getTime());
            CaloriesInWeekly dayData = new CaloriesInWeekly(date, 0);
            fallbackData.add(dayData);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        
        Log.d(TAG, "Created fallback calories in weekly data with actual dates");
        return fallbackData;
    }
    
    // Create fallback calories out weekly data - now returns List with actual dates
    private List<CaloriesOutWeekly> createFallbackCaloriesOutWeekly() {
        List<CaloriesOutWeekly> fallbackData = new ArrayList<>();
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        
        // Create 7 days of fallback data starting from 6 days ago
        calendar.add(Calendar.DAY_OF_YEAR, -6);
        
        for (int i = 0; i < 7; i++) {
            String date = dateFormat.format(calendar.getTime());
            CaloriesOutWeekly dayData = new CaloriesOutWeekly(date, 0);
            fallbackData.add(dayData);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        
        Log.d(TAG, "Created fallback calories out weekly data with actual dates");
        return fallbackData;
    }

    public void clearError() {
        errorLiveData.postValue(null);
    }

    // Refresh all data including charts
    public void refreshData() {
        Log.d(TAG, "Refreshing all data");
        fetchUserInfo();  // Fetch user info from local data
        fetchHealthData(); // Fetch health data from specific endpoints
        fetchChartData();  // Fetch chart data from weekly endpoints
    }

    // Method to refresh only health data (for real-time updates)
    public void refreshHealthData() {
        Log.d(TAG, "Refreshing only health data");
        fetchHealthData();
    }
    
    // Method to refresh only chart data
    public void refreshChartData() {
        Log.d(TAG, "Refreshing only chart data");
        fetchChartData();
    }

    // Method to refresh only user info
    public void refreshUserInfo() {
        Log.d(TAG, "Refreshing only user info");
        fetchUserInfo();
    }
    
    // Legacy method names for compatibility
    public void fetchCardData() {
        fetchHealthData();
    }
    
    public void refreshCardData() {
        refreshHealthData();
    }
}
