package com.example.heath_android.data.repository.schedule;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.heath_android.data.api.ApiService;

// Import các response và request models từ schedule package
import com.example.heath_android.data.model.schedule.WorkoutResponse;
import com.example.heath_android.data.model.schedule.WorkoutRequest;
import com.example.heath_android.data.model.schedule.WorkoutSchedulesResponse;
import com.example.heath_android.data.model.schedule.WorkoutSchedulesRequest;
import com.example.heath_android.data.model.schedule.ScheduledWorkoutResponse;
import com.example.heath_android.data.model.schedule.ScheduledWorkoutRequest;
import com.example.heath_android.data.model.schedule.MealResponse;
import com.example.heath_android.data.model.schedule.MealRequest;
import com.example.heath_android.data.model.schedule.MealSchedulesResponse;
import com.example.heath_android.data.model.schedule.MealSchedulesRequest;
import com.example.heath_android.data.model.schedule.ScheduledMealResponse;
import com.example.heath_android.data.model.schedule.ScheduledMealRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ScheduleRepository {
    private static final String BASE_URL = "http://10.0.2.2:8080/";
    private static ScheduleRepository instance;
    private ApiService apiService;
    
    // ==================== API RESPONSE LIVEDATA ====================
    // Main data lists from API
    private MutableLiveData<List<WorkoutResponse>> workoutResponses = new MutableLiveData<>();
    private MutableLiveData<List<MealResponse>> mealResponses = new MutableLiveData<>();
    private MutableLiveData<List<WorkoutSchedulesResponse>> workoutSchedules = new MutableLiveData<>();
    private MutableLiveData<List<MealSchedulesResponse>> mealSchedules = new MutableLiveData<>();
    private MutableLiveData<List<ScheduledWorkoutResponse>> scheduledWorkouts = new MutableLiveData<>();
    private MutableLiveData<List<ScheduledMealResponse>> scheduledMeals = new MutableLiveData<>();
    
    // ==================== SINGLE ITEM LIVEDATA ====================
    // Current selected/focused items
    private MutableLiveData<WorkoutResponse> currentWorkout = new MutableLiveData<>();
    private MutableLiveData<MealResponse> currentMeal = new MutableLiveData<>();
    private MutableLiveData<WorkoutSchedulesResponse> currentWorkoutSchedule = new MutableLiveData<>();
    private MutableLiveData<MealSchedulesResponse> currentMealSchedule = new MutableLiveData<>();
    private MutableLiveData<ScheduledWorkoutResponse> currentScheduledWorkout = new MutableLiveData<>();
    private MutableLiveData<ScheduledMealResponse> currentScheduledMeal = new MutableLiveData<>();
    
    // ==================== STATUS LIVEDATA ====================
    // App state management
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    
    // ==================== AUTHENTICATION ====================
    private String authToken = "";

    private ScheduleRepository() {
        setupRetrofit();
    }

    public static synchronized ScheduleRepository getInstance() {
        if (instance == null) {
            instance = new ScheduleRepository();
        }
        return instance;
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        apiService = retrofit.create(ApiService.class);
    }

    // ==================== MAIN DATA GETTERS ====================
    
    public LiveData<List<WorkoutResponse>> getWorkoutResponses() {
        return workoutResponses;
    }

    public LiveData<List<MealResponse>> getMealResponses() {
        return mealResponses;
    }

    public LiveData<List<WorkoutSchedulesResponse>> getWorkoutSchedules() {
        return workoutSchedules;
    }

    public LiveData<List<MealSchedulesResponse>> getMealSchedules() {
        return mealSchedules;
    }

    public LiveData<List<ScheduledWorkoutResponse>> getScheduledWorkouts() {
        return scheduledWorkouts;
    }

    public LiveData<List<ScheduledMealResponse>> getScheduledMeals() {
        return scheduledMeals;
    }

    // ==================== SINGLE ITEM GETTERS ====================
    
    public LiveData<WorkoutResponse> getCurrentWorkout() {
        return currentWorkout;
    }

    public LiveData<MealResponse> getCurrentMeal() {
        return currentMeal;
    }

    public LiveData<WorkoutSchedulesResponse> getCurrentWorkoutSchedule() {
        return currentWorkoutSchedule;
    }

    public LiveData<MealSchedulesResponse> getCurrentMealSchedule() {
        return currentMealSchedule;
    }

    public LiveData<ScheduledWorkoutResponse> getCurrentScheduledWorkout() {
        return currentScheduledWorkout;
    }

    public LiveData<ScheduledMealResponse> getCurrentScheduledMeal() {
        return currentScheduledMeal;
    }

    // ==================== STATUS GETTERS ====================
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    // ==================== AUTHENTICATION METHODS ====================
    
    /**
     * Set auth token cho tất cả API calls
     * @param token JWT token từ authentication
     */
    public void setAuthToken(String token) {
        this.authToken = "Bearer " + token;
    }
    
    /**
     * Set error message manually (được gọi từ ViewModel)
     * @param message Error message string
     */
    public void setErrorMessage(String message) {
        errorMessage.setValue(message);
    }

    // ==================== WORKOUT API METHODS ====================
    
    // Lấy danh sách workout
    public void getAllWorkouts() {
        isLoading.setValue(true);
        Call<List<WorkoutResponse>> call = apiService.getWorkouts(authToken);
        call.enqueue(new Callback<List<WorkoutResponse>>() {
            @Override
            public void onResponse(Call<List<WorkoutResponse>> call, Response<List<WorkoutResponse>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    workoutResponses.setValue(response.body());
                } else {
                    errorMessage.setValue("Không thể tải danh sách bài tập");
                }
            }

            @Override
            public void onFailure(Call<List<WorkoutResponse>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Tạo workout mới
    public void createWorkout(WorkoutRequest workoutRequest) {
        isLoading.setValue(true);
        Call<WorkoutResponse> call = apiService.createWorkout(authToken, workoutRequest);
        call.enqueue(new Callback<WorkoutResponse>() {
            @Override
            public void onResponse(Call<WorkoutResponse> call, Response<WorkoutResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    getAllWorkouts(); // Refresh list
                } else {
                    errorMessage.setValue("Không thể tạo bài tập");
                }
            }

            @Override
            public void onFailure(Call<WorkoutResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Lấy workout theo ID
    public void getWorkoutById(Long workoutId) {
        isLoading.setValue(true);
        Call<WorkoutResponse> call = apiService.getWorkout(authToken, workoutId);
        call.enqueue(new Callback<WorkoutResponse>() {
            @Override
            public void onResponse(Call<WorkoutResponse> call, Response<WorkoutResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    currentWorkout.setValue(response.body());
                } else {
                    errorMessage.setValue("Không thể tải thông tin bài tập");
                }
            }

            @Override
            public void onFailure(Call<WorkoutResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Cập nhật workout
    public void updateWorkout(Long workoutId, WorkoutRequest workoutRequest) {
        isLoading.setValue(true);
        Call<WorkoutResponse> call = apiService.updateWorkout(authToken, workoutId, workoutRequest);
        call.enqueue(new Callback<WorkoutResponse>() {
            @Override
            public void onResponse(Call<WorkoutResponse> call, Response<WorkoutResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    getAllWorkouts(); // Refresh list
                    if (response.body() != null) {
                        currentWorkout.setValue(response.body());
                    }
                } else {
                    errorMessage.setValue("Không thể cập nhật bài tập");
                }
            }

            @Override
            public void onFailure(Call<WorkoutResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Xóa workout
    public void deleteWorkout(Long workoutId) {
        isLoading.setValue(true);
        Call<Void> call = apiService.deleteWorkout(authToken, workoutId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    getAllWorkouts(); // Refresh list
                } else {
                    errorMessage.setValue("Không thể xóa bài tập");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Tạo workout schedule
    public void createWorkoutSchedule(WorkoutSchedulesRequest request) {
        isLoading.setValue(true);
        Call<WorkoutSchedulesResponse> call = apiService.createWorkoutSchedules(authToken, request);
        call.enqueue(new Callback<WorkoutSchedulesResponse>() {
            @Override
            public void onResponse(Call<WorkoutSchedulesResponse> call, Response<WorkoutSchedulesResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    // Set created schedule as current và refresh list
                    if (response.body() != null) {
                        currentWorkoutSchedule.setValue(response.body());
                        // Refresh schedules list
                        if (request.getUserId() != null) {
                            getWorkoutSchedulesByUserId(request.getUserId());
                        }
                    }
                } else {
                    errorMessage.setValue("Không thể tạo lịch tập");
                }
            }

            @Override
            public void onFailure(Call<WorkoutSchedulesResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Lấy workout schedules của user
    public void getWorkoutSchedulesByUserId(String userId) {
        isLoading.setValue(true);
        Call<List<WorkoutSchedulesResponse>> call = apiService.getWorkoutSchedules(authToken, userId);
        call.enqueue(new Callback<List<WorkoutSchedulesResponse>>() {
            @Override
            public void onResponse(Call<List<WorkoutSchedulesResponse>> call, Response<List<WorkoutSchedulesResponse>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    workoutSchedules.setValue(response.body());
                } else {
                    errorMessage.setValue("Không thể tải lịch tập");
                }
            }

            @Override
            public void onFailure(Call<List<WorkoutSchedulesResponse>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Cập nhật workout schedule
    public void updateWorkoutSchedule(Long scheduleId, WorkoutSchedulesRequest request) {
        isLoading.setValue(true);
        Call<WorkoutSchedulesResponse> call = apiService.updateWorkoutSchedules(authToken, scheduleId, request);
        call.enqueue(new Callback<WorkoutSchedulesResponse>() {
            @Override
            public void onResponse(Call<WorkoutSchedulesResponse> call, Response<WorkoutSchedulesResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        currentWorkoutSchedule.setValue(response.body());
                        // Refresh schedules list
                        if (request.getUserId() != null) {
                            getWorkoutSchedulesByUserId(request.getUserId());
                        }
                    }
                } else {
                    errorMessage.setValue("Không thể cập nhật lịch tập");
                }
            }

            @Override
            public void onFailure(Call<WorkoutSchedulesResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Lấy scheduled workouts theo schedule ID
    public void getScheduledWorkoutsByScheduleId(Long scheduleId) {
        isLoading.setValue(true);
        Call<List<ScheduledWorkoutResponse>> call = apiService.getScheduledWorkouts(authToken, scheduleId);
        call.enqueue(new Callback<List<ScheduledWorkoutResponse>>() {
            @Override
            public void onResponse(Call<List<ScheduledWorkoutResponse>> call, Response<List<ScheduledWorkoutResponse>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    scheduledWorkouts.setValue(response.body());
                } else {
                    errorMessage.setValue("Không thể tải lịch tập chi tiết");
                }
            }

            @Override
            public void onFailure(Call<List<ScheduledWorkoutResponse>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Tạo scheduled workout
    public void createScheduledWorkout(ScheduledWorkoutRequest request) {
        isLoading.setValue(true);
        Call<ScheduledWorkoutResponse> call = apiService.createScheduledWorkout(authToken, request);
        call.enqueue(new Callback<ScheduledWorkoutResponse>() {
            @Override
            public void onResponse(Call<ScheduledWorkoutResponse> call, Response<ScheduledWorkoutResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        currentScheduledWorkout.setValue(response.body());
                    }
                } else {
                    errorMessage.setValue("Không thể lên lịch tập");
                }
            }

            @Override
            public void onFailure(Call<ScheduledWorkoutResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Lấy scheduled workout theo ID
    public void getScheduledWorkoutById(Long scheduledWorkoutId) {
        isLoading.setValue(true);
        Call<ScheduledWorkoutResponse> call = apiService.getScheduledWorkout(authToken, scheduledWorkoutId);
        call.enqueue(new Callback<ScheduledWorkoutResponse>() {
            @Override
            public void onResponse(Call<ScheduledWorkoutResponse> call, Response<ScheduledWorkoutResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    currentScheduledWorkout.setValue(response.body());
                } else {
                    errorMessage.setValue("Không thể tải thông tin lịch tập");
                }
            }

            @Override
            public void onFailure(Call<ScheduledWorkoutResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Cập nhật scheduled workout
    public void updateScheduledWorkout(Long scheduledWorkoutId, ScheduledWorkoutRequest request) {
        isLoading.setValue(true);
        Call<ScheduledWorkoutResponse> call = apiService.updateScheduledWorkout(authToken, scheduledWorkoutId, request);
        call.enqueue(new Callback<ScheduledWorkoutResponse>() {
            @Override
            public void onResponse(Call<ScheduledWorkoutResponse> call, Response<ScheduledWorkoutResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        currentScheduledWorkout.setValue(response.body());
                    }
                } else {
                    errorMessage.setValue("Không thể cập nhật lịch tập");
                }
            }

            @Override
            public void onFailure(Call<ScheduledWorkoutResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Xóa scheduled workout
    public void deleteScheduledWorkout(Long scheduledWorkoutId) {
        isLoading.setValue(true);
        Call<Void> call = apiService.deleteScheduledWorkout(authToken, scheduledWorkoutId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    // Có thể refresh scheduled workouts list
                } else {
                    errorMessage.setValue("Không thể xóa lịch tập");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Xóa workout schedule
    public void deleteWorkoutSchedule(Long scheduleId) {
        isLoading.setValue(true);
        Call<Void> call = apiService.deleteWorkoutSchedule(authToken, scheduleId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    // Clear current selection nếu đang xem schedule này
                    WorkoutSchedulesResponse current = currentWorkoutSchedule.getValue();
                    if (current != null && current.getId().equals(scheduleId)) {
                        currentWorkoutSchedule.setValue(null);
                    }
                    // Note: Không thể refresh list vì không có userId
                    // UI sẽ cần gọi browseWorkoutSchedules() sau khi delete
                } else {
                    errorMessage.setValue("Không thể xóa lịch tập");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Lấy tất cả scheduled workouts của user
    public void getScheduledWorkoutsByUserId(String userId) {
        isLoading.setValue(true);
        Call<List<ScheduledWorkoutResponse>> call = apiService.getScheduledWorkoutsByUser(authToken, userId);
        call.enqueue(new Callback<List<ScheduledWorkoutResponse>>() {
            @Override
            public void onResponse(Call<List<ScheduledWorkoutResponse>> call, Response<List<ScheduledWorkoutResponse>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    scheduledWorkouts.setValue(response.body());
                } else {
                    errorMessage.setValue("Không thể tải lịch tập của user");
                }
            }

            @Override
            public void onFailure(Call<List<ScheduledWorkoutResponse>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Lấy scheduled workouts theo date range
    public void getScheduledWorkoutsByDateRange(String userId, String startDate, String endDate) {
        isLoading.setValue(true);
        Call<List<ScheduledWorkoutResponse>> call = apiService.getScheduledWorkoutsByDateRange(authToken, userId, startDate, endDate);
        call.enqueue(new Callback<List<ScheduledWorkoutResponse>>() {
            @Override
            public void onResponse(Call<List<ScheduledWorkoutResponse>> call, Response<List<ScheduledWorkoutResponse>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    scheduledWorkouts.setValue(response.body());
                } else {
                    errorMessage.setValue("Không thể tải lịch tập theo khoảng thời gian");
                }
            }

            @Override
            public void onFailure(Call<List<ScheduledWorkoutResponse>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // ==================== MEAL API METHODS ====================
    
    // Lấy danh sách meal
    public void getAllMeals() {
        isLoading.setValue(true);
        Call<List<MealResponse>> call = apiService.getMeals(authToken);
        call.enqueue(new Callback<List<MealResponse>>() {
            @Override
            public void onResponse(Call<List<MealResponse>> call, Response<List<MealResponse>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    mealResponses.setValue(response.body());
                } else {
                    errorMessage.setValue("Không thể tải danh sách món ăn");
                }
            }

            @Override
            public void onFailure(Call<List<MealResponse>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Tạo meal mới
    public void createMeal(MealRequest mealRequest) {
        isLoading.setValue(true);
        Call<MealResponse> call = apiService.createMeal(authToken, mealRequest);
        call.enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    getAllMeals(); // Refresh list
                } else {
                    errorMessage.setValue("Không thể tạo món ăn");
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Lấy meal theo ID
    public void getMealById(Long mealId) {
        isLoading.setValue(true);
        Call<MealResponse> call = apiService.getMeal(authToken, mealId);
        call.enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    currentMeal.setValue(response.body());
                } else {
                    errorMessage.setValue("Không thể tải thông tin món ăn");
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Cập nhật meal
    public void updateMeal(Long mealId, MealRequest mealRequest) {
        isLoading.setValue(true);
        Call<MealResponse> call = apiService.updateMeal(authToken, mealId, mealRequest);
        call.enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    getAllMeals(); // Refresh list
                    if (response.body() != null) {
                        currentMeal.setValue(response.body());
                    }
                } else {
                    errorMessage.setValue("Không thể cập nhật món ăn");
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Xóa meal
    public void deleteMeal(Long mealId) {
        isLoading.setValue(true);
        Call<Void> call = apiService.deleteMeal(authToken, mealId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    getAllMeals(); // Refresh list
                } else {
                    errorMessage.setValue("Không thể xóa món ăn");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Tạo meal schedule
    public void createMealSchedule(MealSchedulesRequest request) {
        isLoading.setValue(true);
        Call<MealSchedulesResponse> call = apiService.createMealSchedules(authToken, request);
        call.enqueue(new Callback<MealSchedulesResponse>() {
            @Override
            public void onResponse(Call<MealSchedulesResponse> call, Response<MealSchedulesResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    // Set created schedule as current và refresh list
                    if (response.body() != null) {
                        currentMealSchedule.setValue(response.body());
                        // Refresh schedules list
                        if (request.getUserId() != null) {
                            getMealSchedulesByUserId(request.getUserId());
                        }
                    }
                } else {
                    errorMessage.setValue("Không thể tạo lịch ăn");
                }
            }

            @Override
            public void onFailure(Call<MealSchedulesResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Lấy meal schedules của user
    public void getMealSchedulesByUserId(String userId) {
        isLoading.setValue(true);
        Call<List<MealSchedulesResponse>> call = apiService.getMealSchedules(authToken, userId);
        call.enqueue(new Callback<List<MealSchedulesResponse>>() {
            @Override
            public void onResponse(Call<List<MealSchedulesResponse>> call, Response<List<MealSchedulesResponse>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    mealSchedules.setValue(response.body());
                } else {
                    errorMessage.setValue("Không thể tải lịch ăn");
                }
            }

            @Override
            public void onFailure(Call<List<MealSchedulesResponse>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Cập nhật meal schedule
    public void updateMealSchedule(Long scheduleId, MealSchedulesRequest request) {
        isLoading.setValue(true);
        Call<MealSchedulesResponse> call = apiService.updateMealSchedules(authToken, scheduleId, request);
        call.enqueue(new Callback<MealSchedulesResponse>() {
            @Override
            public void onResponse(Call<MealSchedulesResponse> call, Response<MealSchedulesResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        currentMealSchedule.setValue(response.body());
                        // Refresh schedules list
                        if (request.getUserId() != null) {
                            getMealSchedulesByUserId(request.getUserId());
                        }
                    }
                } else {
                    errorMessage.setValue("Không thể cập nhật lịch ăn");
                }
            }

            @Override
            public void onFailure(Call<MealSchedulesResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Lấy scheduled meals theo schedule ID
    public void getScheduledMealsByScheduleId(Long scheduleId) {
        isLoading.setValue(true);
        Call<List<ScheduledMealResponse>> call = apiService.getScheduledMeals(authToken, scheduleId);
        call.enqueue(new Callback<List<ScheduledMealResponse>>() {
            @Override
            public void onResponse(Call<List<ScheduledMealResponse>> call, Response<List<ScheduledMealResponse>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    scheduledMeals.setValue(response.body());
                } else {
                    errorMessage.setValue("Không thể tải lịch ăn chi tiết");
                }
            }

            @Override
            public void onFailure(Call<List<ScheduledMealResponse>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Tạo scheduled meal
    public void createScheduledMeal(ScheduledMealRequest request) {
        isLoading.setValue(true);
        Call<ScheduledMealResponse> call = apiService.createScheduledMeal(authToken, request);
        call.enqueue(new Callback<ScheduledMealResponse>() {
            @Override
            public void onResponse(Call<ScheduledMealResponse> call, Response<ScheduledMealResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        currentScheduledMeal.setValue(response.body());
                    }
                } else {
                    errorMessage.setValue("Không thể lên lịch ăn");
                }
            }

            @Override
            public void onFailure(Call<ScheduledMealResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Lấy scheduled meal theo ID
    public void getScheduledMealById(Long scheduledMealId) {
        isLoading.setValue(true);
        Call<ScheduledMealResponse> call = apiService.getScheduledMeal(authToken, scheduledMealId);
        call.enqueue(new Callback<ScheduledMealResponse>() {
            @Override
            public void onResponse(Call<ScheduledMealResponse> call, Response<ScheduledMealResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    currentScheduledMeal.setValue(response.body());
                } else {
                    errorMessage.setValue("Không thể tải thông tin lịch ăn");
                }
            }

            @Override
            public void onFailure(Call<ScheduledMealResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Cập nhật scheduled meal
    public void updateScheduledMeal(Long scheduledMealId, ScheduledMealRequest request) {
        isLoading.setValue(true);
        Call<ScheduledMealResponse> call = apiService.updateScheduledMeal(authToken, scheduledMealId, request);
        call.enqueue(new Callback<ScheduledMealResponse>() {
            @Override
            public void onResponse(Call<ScheduledMealResponse> call, Response<ScheduledMealResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        currentScheduledMeal.setValue(response.body());
                    }
                } else {
                    errorMessage.setValue("Không thể cập nhật lịch ăn");
                }
            }

            @Override
            public void onFailure(Call<ScheduledMealResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Xóa scheduled meal
    public void deleteScheduledMeal(Long scheduledMealId) {
        isLoading.setValue(true);
        Call<Void> call = apiService.deleteScheduledMeal(authToken, scheduledMealId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    // Có thể refresh scheduled meals list
                } else {
                    errorMessage.setValue("Không thể xóa lịch ăn");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Xóa meal schedule
    public void deleteMealSchedule(Long scheduleId) {
        isLoading.setValue(true);
        Call<Void> call = apiService.deleteMealSchedule(authToken, scheduleId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    // Clear current selection nếu đang xem schedule này
                    MealSchedulesResponse current = currentMealSchedule.getValue();
                    if (current != null && current.getId().equals(scheduleId)) {
                        currentMealSchedule.setValue(null);
                    }
                    // Note: Không thể refresh list vì không có userId
                    // UI sẽ cần gọi browseMealSchedules() sau khi delete
                } else {
                    errorMessage.setValue("Không thể xóa lịch ăn");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Lấy tất cả scheduled meals của user
    public void getScheduledMealsByUserId(String userId) {
        isLoading.setValue(true);
        Call<List<ScheduledMealResponse>> call = apiService.getScheduledMealsByUser(authToken, userId);
        call.enqueue(new Callback<List<ScheduledMealResponse>>() {
            @Override
            public void onResponse(Call<List<ScheduledMealResponse>> call, Response<List<ScheduledMealResponse>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    scheduledMeals.setValue(response.body());
                } else {
                    errorMessage.setValue("Không thể tải lịch ăn của user");
                }
            }

            @Override
            public void onFailure(Call<List<ScheduledMealResponse>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Lấy scheduled meals theo date range
    public void getScheduledMealsByDateRange(String userId, String startDate, String endDate) {
        isLoading.setValue(true);
        Call<List<ScheduledMealResponse>> call = apiService.getScheduledMealsByDateRange(authToken, userId, startDate, endDate);
        call.enqueue(new Callback<List<ScheduledMealResponse>>() {
            @Override
            public void onResponse(Call<List<ScheduledMealResponse>> call, Response<List<ScheduledMealResponse>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    scheduledMeals.setValue(response.body());
                } else {
                    errorMessage.setValue("Không thể tải lịch ăn theo khoảng thời gian");
                }
            }

            @Override
            public void onFailure(Call<List<ScheduledMealResponse>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

}
