package com.example.heath_android.ui.home;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.heath_android.data.model.home.BMIBMRResponse;
import com.example.heath_android.data.model.home.CaloriesInWeekly;
import com.example.heath_android.data.model.home.CaloriesOutWeekly;
import com.example.heath_android.data.model.home.CaloriesResponse;
import com.example.heath_android.data.model.home.HomeResponse;
import com.example.heath_android.data.model.home.TDEEResponse;
import com.example.heath_android.data.repository.home.HomeRepository;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private static final String TAG = "HomeViewModel";
    private HomeRepository homeRepository;
    
    // LiveData for user info from local data
    private MediatorLiveData<HomeResponse> homeDataLiveData;
    private LiveData<Boolean> loadingLiveData;
    private LiveData<String> errorLiveData;
    
    // LiveData for specific health data from different endpoints
    private LiveData<BMIBMRResponse> bmiBmrDataLiveData;
    private LiveData<TDEEResponse> tdeeDataLiveData;
    private LiveData<CaloriesResponse> caloriesDataLiveData;
    
    // LiveData for chart data - now using List directly
    private LiveData<List<CaloriesInWeekly>> caloriesInWeeklyLiveData;
    private LiveData<List<CaloriesOutWeekly>> caloriesOutWeeklyLiveData;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        Log.d(TAG, "HomeViewModel created");
        homeRepository = HomeRepository.getInstance(application);
        initializeLiveData();
        loadInitialData();
    }

    private void initializeLiveData() {
        homeDataLiveData = new MediatorLiveData<>();
        
        // Add repository data sources for user info
        homeDataLiveData.addSource(homeRepository.getHomeData(), homeData -> {
            homeDataLiveData.setValue(homeData);
        });
        
        loadingLiveData = homeRepository.getLoading();
        errorLiveData = homeRepository.getError();
        
        // Initialize LiveData for specific health data
        bmiBmrDataLiveData = homeRepository.getBmiBmrData();
        tdeeDataLiveData = homeRepository.getTdeeData();
        caloriesDataLiveData = homeRepository.getCaloriesData();
        
        // Initialize LiveData for chart data
        caloriesInWeeklyLiveData = homeRepository.getCaloriesInWeeklyData();
        caloriesOutWeeklyLiveData = homeRepository.getCaloriesOutWeeklyData();
    }

    // Load initial data
    private void loadInitialData() {
        // Fetch user info from local data
        homeRepository.fetchUserInfo();
        // Fetch health data from specific endpoints
        homeRepository.fetchHealthData();
        // Fetch chart data from weekly endpoints
        homeRepository.fetchChartData();
    }

    // Getters for LiveData
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

    // Refresh methods
    public void refreshData() {
        Log.d(TAG, "Refreshing all data from ViewModel");
        homeRepository.refreshData();
    }

    public void refreshHealthData() {
        Log.d(TAG, "Refreshing only health data from ViewModel");
        homeRepository.refreshHealthData();
    }
    
    public void refreshChartData() {
        Log.d(TAG, "Refreshing only chart data from ViewModel");
        homeRepository.refreshChartData();
    }

    public void refreshUserInfo() {
        Log.d(TAG, "Refreshing only user info from ViewModel");
        homeRepository.refreshUserInfo();
    }
    
    // Legacy method names for compatibility
    public void fetchCardData() {
        refreshHealthData();
    }

    public void refreshCardData() {
        refreshHealthData();
    }

    public void clearError() {
        homeRepository.clearError();
    }

    // Utility methods for UI formatting
    public String formatBMI(double bmi) {
        if (bmi <= 0) return "--";
        return String.format("%.1f", bmi);
    }

    public String formatCalories(double calories) {
        if (calories <= 0) return "--";
        return String.format("%.0f", calories);
    }

    public String formatTDEE(double tdee) {
        if (tdee <= 0) return "--";
        return String.format("%.0f", tdee);
    }

    public String formatBMR(double bmr) {
        if (bmr <= 0) return "--";
        return String.format("%.0f", bmr);
    }

    public String getBMICategory(double bmi) {
        if (bmi <= 0) return "Không xác định";
        if (bmi < 18.5) return "Thiếu cân";
        if (bmi < 25) return "Bình thường";
        if (bmi < 30) return "Thừa cân";
        return "Béo phì";
    }

    // Convenience methods to get current values from LiveData
    public double getCurrentBMI() {
        BMIBMRResponse data = bmiBmrDataLiveData.getValue();
        return data != null ? data.getBmi() : 0.0;
    }
    
    public double getCurrentBMR() {
        BMIBMRResponse data = bmiBmrDataLiveData.getValue();
        return data != null ? data.getBmr() : 0.0;
    }
    
    public double getCurrentTDEE() {
        TDEEResponse data = tdeeDataLiveData.getValue();
        return data != null ? data.getTdee() : 0.0;
    }
    
    public double getCurrentTotalCaloriesBurned() {
        CaloriesResponse data = caloriesDataLiveData.getValue();
        return data != null ? data.getTotalCaloriesBurned() : 0.0;
    }

    // Check if health data is available
    public boolean hasHealthData() {
        return bmiBmrDataLiveData.getValue() != null || 
               tdeeDataLiveData.getValue() != null || 
               caloriesDataLiveData.getValue() != null;
    }
    
    // Check if chart data is available - now checking List directly
    public boolean hasChartData() {
        List<CaloriesInWeekly> inData = caloriesInWeeklyLiveData.getValue();
        List<CaloriesOutWeekly> outData = caloriesOutWeeklyLiveData.getValue();
        return inData != null && outData != null && !inData.isEmpty() && !outData.isEmpty();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "HomeViewModel cleared");
        // Clean up if needed
    }
}
