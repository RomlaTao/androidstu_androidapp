package com.example.heath_android.ui.schedule;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.Transformations;

import com.example.heath_android.data.repository.schedule.ScheduleRepository;

// Import các models mới từ API
import com.example.heath_android.data.model.schedule.WorkoutRequest;
import com.example.heath_android.data.model.schedule.WorkoutResponse;
import com.example.heath_android.data.model.schedule.MealRequest;
import com.example.heath_android.data.model.schedule.MealResponse;
import com.example.heath_android.data.model.schedule.WorkoutSchedulesRequest;
import com.example.heath_android.data.model.schedule.WorkoutSchedulesResponse;
import com.example.heath_android.data.model.schedule.MealSchedulesRequest;
import com.example.heath_android.data.model.schedule.MealSchedulesResponse;
import com.example.heath_android.data.model.schedule.ScheduledWorkoutRequest;
import com.example.heath_android.data.model.schedule.ScheduledWorkoutResponse;
import com.example.heath_android.data.model.schedule.ScheduledMealRequest;
import com.example.heath_android.data.model.schedule.ScheduledMealResponse;
import com.example.heath_android.data.model.schedule.Exercise;
import com.example.heath_android.data.model.schedule.Food;
import com.example.heath_android.data.model.schedule.DayEvent;

import com.example.heath_android.data.local.DatabaseInformation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * ScheduleViewModel - Xử lý business logic cho Schedule module
 * 
 * Chức năng chính:
 * 1. Quản lý UI state (calendar, selected dates)
 * 2. Convert API responses thành UI-friendly data
 * 3. Xử lý CRUD operations cho workouts, meals, schedules
 * 4. Cung cấp LiveData cho UI components
 * 
 * =============================================================================
 * WORKFLOW QUAN TRỌNG - Schedule Browsing & Selection:
 * =============================================================================
 * 
 * VẤN ĐỀ: User không thể biết Schedule ID trực tiếp, do đó cần workflow:
 * 
 * 1. BROWSE SCHEDULES:
 *    - User gọi browseWorkoutSchedules() hoặc browseMealSchedules()
 *    - Load danh sách schedules theo currentUserId
 *    - UI hiển thị danh sách schedules (id, name, description, startDate, endDate)
 * 
 * 2. SELECT SCHEDULE:
 *    - User chọn schedule từ danh sách UI
 *    - Gọi selectWorkoutSchedule(schedule) hoặc selectMealSchedule(schedule)
 *    - ViewModel lưu selected schedule và auto-load scheduled items
 * 
 * 3. WORK WITH SELECTED SCHEDULE:
 *    - Tạo scheduled items: createScheduledWorkoutForSelectedSchedule()
 *    - Update/delete schedule: updateWorkoutSchedule(), deleteWorkoutSchedule()
 *    - Browse scheduled items trong schedule đó
 * 
 * 4. CLEAR SELECTION:
 *    - clearWorkoutScheduleSelection(), clearMealScheduleSelection()
 *    - Về browse mode để chọn schedule khác
 * 
 * LUỒNG UI RECOMMEND:
 * Screen 1: Schedule List (browse schedules)
 * Screen 2: Schedule Detail + Scheduled Items (selected schedule)
 * Screen 3: Create/Edit Scheduled Items
 * =============================================================================
 */
public class ScheduleViewModel extends ViewModel {
    private ScheduleRepository repository;
    private DatabaseInformation databaseInformation;
    
    // ==================== UI STATE MANAGEMENT ====================
    
    // LiveData cho calendar và date selection
    private MutableLiveData<String> currentSelectedDate = new MutableLiveData<>();
    private MutableLiveData<List<String>> weekDays = new MutableLiveData<>();
    private MutableLiveData<List<String>> monthsList = new MutableLiveData<>();
    private MutableLiveData<Integer> selectedMonth = new MutableLiveData<>();
    private MutableLiveData<Integer> selectedYear = new MutableLiveData<>();
    
    // LiveData cho converted UI data
    private MutableLiveData<List<DayEvent>> convertedScheduleEvents = new MutableLiveData<>();
    private MutableLiveData<String> currentUserId = new MutableLiveData<>();
    
    // LiveData cho selected schedules - người dùng chọn từ danh sách
    private MutableLiveData<WorkoutSchedulesResponse> selectedWorkoutSchedule = new MutableLiveData<>();
    private MutableLiveData<MealSchedulesResponse> selectedMealSchedule = new MutableLiveData<>();
    private MutableLiveData<Long> selectedWorkoutScheduleId = new MutableLiveData<>();
    private MutableLiveData<Long> selectedMealScheduleId = new MutableLiveData<>();
    
    // Format cho date
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    
    /**
     * Constructor - Khởi tạo ViewModel và Repository
     */
    public ScheduleViewModel() {
        repository = ScheduleRepository.getInstance();
        databaseInformation = new DatabaseInformation(null);
        initializeCalendar();
        setupAutoRefresh();
    }
    
    /**
     * Constructor với Context - Khởi tạo ViewModel với authentication info
     * @param context Context để access SharedPreferences
     */
    public ScheduleViewModel(Context context) {
        repository = ScheduleRepository.getInstance();
        databaseInformation = new DatabaseInformation(context);
        initializeCalendar();
        initializeAuthentication();
        setupAutoRefresh();
    }
    
    /**
     * Initialize authentication info từ DatabaseInformation
     * Load userId và JWT token từ SharedPreferences
     */
    private void initializeAuthentication() {
        if (databaseInformation != null) {
            // Load userId từ SharedPreferences
            String userId = databaseInformation.getUserId();
            if (userId != null) {
                currentUserId.setValue(userId);
            }
            
            // Load và set JWT token cho Repository
            String token = databaseInformation.getToken();
            if (token != null) {
                repository.setAuthToken(token);
            }
        }
    }
    
    /**
     * Refresh authentication info từ DatabaseInformation
     * Gọi khi user login/logout hoặc cập nhật thông tin
     */
    public void refreshAuthentication() {
        initializeAuthentication();
    }
    
    /**
     * Kiểm tra authentication status
     * @return true nếu user đã login và có đầy đủ thông tin
     */
    public boolean isAuthenticated() {
        if (databaseInformation != null) {
            return databaseInformation.isLoggedIn() && 
                   databaseInformation.getUserId() != null &&
                   databaseInformation.getToken() != null;
        }
        return false;
    }
    
    /**
     * Set DatabaseInformation instance (cho testing hoặc manual setup)
     * @param context Context mới để tạo DatabaseInformation
     */
    public void setDatabaseInformation(Context context) {
        this.databaseInformation = new DatabaseInformation(context);
        initializeAuthentication();
    }
    
    /**
     * Get current user info
     * @return Array [userId, email, name] hoặc null nếu chưa login
     */
    public String[] getCurrentUserInfo() {
        if (databaseInformation != null && isAuthenticated()) {
            return new String[] {
                databaseInformation.getUserId(),
                databaseInformation.getEmail(),
                databaseInformation.getName()
            };
        }
        return null;
    }
    
    // ==================== CALENDAR MANAGEMENT ====================
    
    /**
     * Khởi tạo calendar với tháng và năm hiện tại
     * Thiết lập danh sách tháng và ngày mặc định
     */
    private void initializeCalendar() {
        // Initialize months list - Khởi tạo danh sách 12 tháng
        List<String> months = new ArrayList<>();
        String[] monthNames = {"Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
                              "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"};
        for (String month : monthNames) {
            months.add(month);
        }
        monthsList.setValue(months);
        
        // Set current date - Thiết lập ngày hiện tại
        Calendar calendar = Calendar.getInstance();
        selectedMonth.setValue(calendar.get(Calendar.MONTH));
        selectedYear.setValue(calendar.get(Calendar.YEAR));
        currentSelectedDate.setValue(dateFormat.format(calendar.getTime()));
        
        // Initialize week view for current week - Khởi tạo view tuần hiện tại
        updateWeekDays();
    }
    
    /**
     * Cập nhật danh sách ngày trong tuần dựa trên ngày hiện tại được chọn
     * Hiển thị 7 ngày trong tuần chứa ngày hiện tại (currentSelectedDate)
     */
    public void updateWeekDays() {
        Calendar calendar = Calendar.getInstance();
        String currentDate = currentSelectedDate.getValue();
        
        if (currentDate != null) {
            try {
                // Parse current selected date
                calendar.setTime(dateFormat.parse(currentDate));
            } catch (Exception e) {
                // Fallback to current system date if parsing fails
                calendar = Calendar.getInstance();
            }
        }
        
        // Tìm ngày đầu tiên của tuần chứa ngày hiện tại
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        // Điều chỉnh để tuần bắt đầu từ thứ 2 (Monday = 2)
        int daysToSubtract = (dayOfWeek == Calendar.SUNDAY) ? 6 : (dayOfWeek - Calendar.MONDAY);
        calendar.add(Calendar.DAY_OF_MONTH, -daysToSubtract);
        
        List<String> days = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            days.add(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        weekDays.setValue(days);
    }
    
    // ==================== API DATA GETTERS ====================
    
    /**
     * Lấy danh sách workout responses từ Repository
     * @return LiveData chứa danh sách WorkoutResponse từ API
     */
    public LiveData<List<WorkoutResponse>> getWorkoutResponses() {
        return repository.getWorkoutResponses();
    }
    
    /**
     * Lấy danh sách meal responses từ Repository
     * @return LiveData chứa danh sách MealResponse từ API
     */
    public LiveData<List<MealResponse>> getMealResponses() {
        return repository.getMealResponses();
    }
    
    /**
     * Lấy danh sách workout schedules từ Repository
     * @return LiveData chứa danh sách WorkoutSchedulesResponse
     */
    public LiveData<List<WorkoutSchedulesResponse>> getWorkoutSchedules() {
        return repository.getWorkoutSchedules();
    }
    
    /**
     * Lấy danh sách meal schedules từ Repository
     * @return LiveData chứa danh sách MealSchedulesResponse
     */
    public LiveData<List<MealSchedulesResponse>> getMealSchedules() {
        return repository.getMealSchedules();
    }
    
    /**
     * Lấy danh sách scheduled workouts từ Repository
     * @return LiveData chứa danh sách ScheduledWorkoutResponse
     */
    public LiveData<List<ScheduledWorkoutResponse>> getScheduledWorkouts() {
        return repository.getScheduledWorkouts();
    }
    
    /**
     * Lấy danh sách scheduled meals từ Repository
     * @return LiveData chứa danh sách ScheduledMealResponse
     */
    public LiveData<List<ScheduledMealResponse>> getScheduledMeals() {
        return repository.getScheduledMeals();
    }
    
    // ==================== SINGLE ITEM GETTERS ====================
    
    /**
     * Lấy workout hiện tại đang được chọn/xem
     * @return LiveData chứa WorkoutResponse đang được focus
     */
    public LiveData<WorkoutResponse> getCurrentWorkout() {
        return repository.getCurrentWorkout();
    }
    
    /**
     * Lấy meal hiện tại đang được chọn/xem
     * @return LiveData chứa MealResponse đang được focus
     */
    public LiveData<MealResponse> getCurrentMeal() {
        return repository.getCurrentMeal();
    }
    
    /**
     * Lấy workout schedule hiện tại đang được chọn/xem
     * @return LiveData chứa WorkoutSchedulesResponse đang được focus
     */
    public LiveData<WorkoutSchedulesResponse> getCurrentWorkoutSchedule() {
        return repository.getCurrentWorkoutSchedule();
    }
    
    /**
     * Lấy meal schedule hiện tại đang được chọn/xem
     * @return LiveData chứa MealSchedulesResponse đang được focus
     */
    public LiveData<MealSchedulesResponse> getCurrentMealSchedule() {
        return repository.getCurrentMealSchedule();
    }
    
    /**
     * Lấy scheduled workout hiện tại đang được chọn/xem
     * @return LiveData chứa ScheduledWorkoutResponse đang được focus
     */
    public LiveData<ScheduledWorkoutResponse> getCurrentScheduledWorkout() {
        return repository.getCurrentScheduledWorkout();
    }
    
    /**
     * Lấy scheduled meal hiện tại đang được chọn/xem
     * @return LiveData chứa ScheduledMealResponse đang được focus
     */
    public LiveData<ScheduledMealResponse> getCurrentScheduledMeal() {
        return repository.getCurrentScheduledMeal();
    }
    
    // ==================== LEGACY DATA GETTERS (for backward compatibility) ====================
    
    /**
     * Lấy schedule events đã được convert cho UI (legacy support)
     * @return LiveData chứa danh sách DayEvent cho compatibility
     */
    public LiveData<List<DayEvent>> getScheduleEvents() {
        return convertedScheduleEvents;
    }
    
    // ==================== REPOSITORY STATUS GETTERS ====================
    
    /**
     * Lấy error message từ Repository
     * @return LiveData chứa error message nếu có lỗi xảy ra
     */
    public LiveData<String> getErrorMessage() {
        return repository.getErrorMessage();
    }
    
    /**
     * Lấy loading state từ Repository
     * @return LiveData<Boolean> true nếu đang loading, false nếu đã xong
     */
    public LiveData<Boolean> getIsLoading() {
        return repository.getIsLoading();
    }
    
    // ==================== UI STATE GETTERS ====================
    
    /**
     * Lấy ngày hiện tại đang được chọn
     * @return LiveData chứa string ngày theo format yyyy-MM-dd
     */
    public LiveData<String> getCurrentSelectedDate() {
        return currentSelectedDate;
    }
    
    /**
     * Lấy danh sách ngày trong tuần hiện tại
     * @return LiveData chứa list 7 ngày trong tuần
     */
    public LiveData<List<String>> getWeekDays() {
        return weekDays;
    }
    
    /**
     * Lấy danh sách tên các tháng
     * @return LiveData chứa list 12 tháng tiếng Việt
     */
    public LiveData<List<String>> getMonthsList() {
        return monthsList;
    }
    
    /**
     * Lấy tháng hiện tại đang được chọn
     * @return LiveData chứa index tháng (0-11)
     */
    public LiveData<Integer> getSelectedMonth() {
        return selectedMonth;
    }
    
    /**
     * Lấy năm hiện tại đang được chọn
     * @return LiveData chứa năm (2024, 2025, ...)
     */
    public LiveData<Integer> getSelectedYear() {
        return selectedYear;
    }
    
    // ==================== SELECTED SCHEDULE GETTERS ====================
    
    /**
     * Lấy workout schedule hiện tại đang được chọn bởi user
     * @return LiveData chứa WorkoutSchedulesResponse được chọn từ danh sách
     */
    public LiveData<WorkoutSchedulesResponse> getSelectedWorkoutSchedule() {
        return selectedWorkoutSchedule;
    }
    
    /**
     * Lấy meal schedule hiện tại đang được chọn bởi user
     * @return LiveData chứa MealSchedulesResponse được chọn từ danh sách
     */
    public LiveData<MealSchedulesResponse> getSelectedMealSchedule() {
        return selectedMealSchedule;
    }
    
    /**
     * Lấy ID của workout schedule đang được chọn
     * @return LiveData chứa Long ID của workout schedule
     */
    public LiveData<Long> getSelectedWorkoutScheduleId() {
        return selectedWorkoutScheduleId;
    }
    
    /**
     * Lấy ID của meal schedule đang được chọn
     * @return LiveData chứa Long ID của meal schedule
     */
    public LiveData<Long> getSelectedMealScheduleId() {
        return selectedMealScheduleId;
    }
    
    // ==================== USER ACTIONS ====================
    
    /**
     * Chọn một ngày cụ thể và load dữ liệu cho ngày đó
     * @param date String ngày theo format yyyy-MM-dd
     */
    public void selectDate(String date) {
        currentSelectedDate.setValue(date);
        loadScheduleEventsForDate(date);
    }
    
    /**
     * Chọn tháng và cập nhật calendar
     * @param monthIndex Index của tháng (0-11)
     */
    public void selectMonth(int monthIndex) {
        selectedMonth.setValue(monthIndex);
        
        // Update current selected date to first day of selected month
        // Cập nhật ngày được chọn thành ngày đầu tiên của tháng được chọn
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, selectedYear.getValue());
        calendar.set(Calendar.MONTH, monthIndex);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        currentSelectedDate.setValue(dateFormat.format(calendar.getTime()));
        
        // Update week days sau khi đã set currentSelectedDate
        updateWeekDays();
        
        loadScheduleEventsForDate(currentSelectedDate.getValue());
    }
    
    /**
     * Chọn năm và cập nhật calendar
     * @param year Năm cần chọn
     */
    public void selectYear(int year) {
        selectedYear.setValue(year);
        
        // Update current selected date - Cập nhật ngày được chọn
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, selectedMonth.getValue());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        currentSelectedDate.setValue(dateFormat.format(calendar.getTime()));
        
        // Update week days sau khi đã set currentSelectedDate
        updateWeekDays();
        
        loadScheduleEventsForDate(currentSelectedDate.getValue());
    }
    
    /**
     * Chuyển sang tuần tiếp theo
     */
    public void nextWeek() {
        String currentDate = currentSelectedDate.getValue();
        if (currentDate != null) {
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateFormat.parse(currentDate));
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
                
                String newDate = dateFormat.format(calendar.getTime());
                currentSelectedDate.setValue(newDate);
                
                // Update month nếu cần
                int newMonth = calendar.get(Calendar.MONTH);
                int newYear = calendar.get(Calendar.YEAR);
                selectedMonth.setValue(newMonth);
                selectedYear.setValue(newYear);
                
                updateWeekDays();
                loadScheduleEventsForDate(newDate);
            } catch (Exception e) {
                updateWeekDays();
            }
        }
    }
    
    /**
     * Chuyển về tuần trước đó
     */
    public void previousWeek() {
        String currentDate = currentSelectedDate.getValue();
        if (currentDate != null) {
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateFormat.parse(currentDate));
                calendar.add(Calendar.WEEK_OF_YEAR, -1);
                
                String newDate = dateFormat.format(calendar.getTime());
                currentSelectedDate.setValue(newDate);
                
                // Update month nếu cần
                int newMonth = calendar.get(Calendar.MONTH);
                int newYear = calendar.get(Calendar.YEAR);
                selectedMonth.setValue(newMonth);
                selectedYear.setValue(newYear);
                
                updateWeekDays();
                loadScheduleEventsForDate(newDate);
            } catch (Exception e) {
                updateWeekDays();
            }
        }
    }
    
    /**
     * Lấy text hiển thị tuần hiện tại
     * @return String mô tả tuần hiện tại với khoảng ngày (vd: "16-22/12/2024")
     */
    public String getCurrentWeekText() {
        String currentDate = currentSelectedDate.getValue();
        if (currentDate != null) {
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateFormat.parse(currentDate));
                
                // Tìm ngày đầu tuần
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                int daysToSubtract = (dayOfWeek == Calendar.SUNDAY) ? 6 : (dayOfWeek - Calendar.MONDAY);
                calendar.add(Calendar.DAY_OF_MONTH, -daysToSubtract);
                
                // Ngày đầu tuần
                int startDay = calendar.get(Calendar.DAY_OF_MONTH);
                int startMonth = calendar.get(Calendar.MONTH) + 1;
                int startYear = calendar.get(Calendar.YEAR);
                
                // Ngày cuối tuần (6 ngày sau)
                calendar.add(Calendar.DAY_OF_MONTH, 6);
                int endDay = calendar.get(Calendar.DAY_OF_MONTH);
                int endMonth = calendar.get(Calendar.MONTH) + 1;
                int endYear = calendar.get(Calendar.YEAR);
                
                // Format hiển thị tuỳ theo trường hợp
                if (startYear == endYear && startMonth == endMonth) {
                    // Cùng tháng: "16-22/12/2024"
                    return String.format("%d-%d/%02d/%d", startDay, endDay, endMonth, endYear);
                } else if (startYear == endYear) {
                    // Khác tháng cùng năm: "30/11-6/12/2024"
                    return String.format("%d/%02d-%d/%02d/%d", startDay, startMonth, endDay, endMonth, endYear);
                } else {
                    // Khác năm: "30/12/2024-5/1/2025"
                    return String.format("%d/%02d/%d-%d/%02d/%d", startDay, startMonth, startYear, endDay, endMonth, endYear);
                }
            } catch (Exception e) {
                return "Tuần hiện tại";
            }
        }
        return "Tuần hiện tại";
    }
    
    // ==================== DATA LOADING METHODS ====================
    
    /**
     * Load schedule events cho một ngày cụ thể
     * Lấy tất cả schedules của user, sau đó filter scheduled items theo ngày
     * @param date String ngày theo format yyyy-MM-dd
     */
    public void loadScheduleEventsForDate(String date) {
        if (!isAuthenticated()) {
            repository.setErrorMessage("User chưa đăng nhập hoặc thiếu thông tin authentication");
            return;
        }
        
        String userId = currentUserId.getValue();
        if (userId != null) {
            // Load tất cả schedules của user
            // Backend sẽ trả về schedules với list scheduled items bên trong
            repository.getWorkoutSchedulesByUserId(userId);
            repository.getMealSchedulesByUserId(userId);
            
            // Convert scheduled items thành DayEvents cho ngày cụ thể
            convertScheduleEventsForDate(date);
        }
    }
    
    /**
     * Load tất cả workouts có sẵn từ API
     */
    public void loadAllWorkouts() {
        if (!isAuthenticated()) {
            repository.setErrorMessage("User chưa đăng nhập");
            return;
        }
        repository.getAllWorkouts();
    }
    
    /**
     * Load tất cả meals có sẵn từ API
     */
    public void loadAllMeals() {
        if (!isAuthenticated()) {
            repository.setErrorMessage("User chưa đăng nhập");
            return;
        }
        repository.getAllMeals();
    }
    
    /**
     * Load workout schedules của user hiện tại
     */
    public void loadUserWorkoutSchedules() {
        if (!isAuthenticated()) {
            repository.setErrorMessage("User chưa đăng nhập hoặc thiếu thông tin authentication");
            return;
        }
        
        String userId = currentUserId.getValue();
        if (userId != null) {
            repository.getWorkoutSchedulesByUserId(userId);
        }
    }
    
    /**
     * Load meal schedules của user hiện tại
     */
    public void loadUserMealSchedules() {
        if (!isAuthenticated()) {
            repository.setErrorMessage("User chưa đăng nhập hoặc thiếu thông tin authentication");
            return;
        }
        
        String userId = currentUserId.getValue();
        if (userId != null) {
            repository.getMealSchedulesByUserId(userId);
        }
    }
    
    /**
     * Load scheduled workouts theo schedule ID
     * @param scheduleId ID của workout schedule
     */
    public void loadScheduledWorkoutsByScheduleId(Long scheduleId) {
        repository.getScheduledWorkoutsByScheduleId(scheduleId);
    }
    
    /**
     * Load scheduled meals theo schedule ID
     * @param scheduleId ID của meal schedule
     */
    public void loadScheduledMealsByScheduleId(Long scheduleId) {
        repository.getScheduledMealsByScheduleId(scheduleId);
    }
    
    /**
     * Load một workout cụ thể theo ID
     * @param workoutId ID của workout
     */
    public void loadWorkoutById(Long workoutId) {
        repository.getWorkoutById(workoutId);
    }
    
    /**
     * Load một meal cụ thể theo ID
     * @param mealId ID của meal
     */
    public void loadMealById(Long mealId) {
        repository.getMealById(mealId);
    }
    
    // ==================== WORKOUT CRUD OPERATIONS ====================
    
    /**
     * Tạo workout mới
     * @param name Tên workout
     * @param description Mô tả workout
     * @param type Loại workout (HIIT, CARDIO, STRENGTH, etc.)
     * @param durationMinutes Thời gian tập (phút)
     * @param caloriesBurned Calories đốt cháy
     * @param exercises Danh sách bài tập
     */
    public void createWorkout(String name, String description, String type, 
                            int durationMinutes, int caloriesBurned, List<Exercise> exercises) {
        if (!isAuthenticated()) {
            repository.setErrorMessage("User chưa đăng nhập. Vui lòng đăng nhập để tạo workout.");
            return;
        }
        
        String userId = currentUserId.getValue();
        if (userId != null) {
            WorkoutRequest request = new WorkoutRequest(name, description, type, 
                                                      durationMinutes, caloriesBurned, exercises, userId);
            repository.createWorkout(request);
        }
    }
    
    /**
     * Cập nhật workout đã có
     * @param workoutId ID của workout cần cập nhật
     * @param name Tên workout mới
     * @param description Mô tả mới
     * @param type Loại workout mới
     * @param durationMinutes Thời gian mới
     * @param caloriesBurned Calories mới
     * @param exercises Danh sách bài tập mới
     */
    public void updateWorkout(Long workoutId, String name, String description, String type,
                            int durationMinutes, int caloriesBurned, List<Exercise> exercises) {
        if (!isAuthenticated()) {
            repository.setErrorMessage("User chưa đăng nhập. Vui lòng đăng nhập để cập nhật workout.");
            return;
        }
        
        String userId = currentUserId.getValue();
        if (userId != null) {
            WorkoutRequest request = new WorkoutRequest(name, description, type,
                                                      durationMinutes, caloriesBurned, exercises, userId);
            repository.updateWorkout(workoutId, request);
        }
    }
    
    /**
     * Xóa workout
     * @param workoutId ID của workout cần xóa
     */
    public void deleteWorkout(Long workoutId) {
        if (!isAuthenticated()) {
            repository.setErrorMessage("User chưa đăng nhập. Vui lòng đăng nhập để xóa workout.");
            return;
        }
        repository.deleteWorkout(workoutId);
    }
    
    // ==================== MEAL CRUD OPERATIONS ====================
    
    /**
     * Tạo meal mới
     * @param name Tên món ăn
     * @param description Mô tả món ăn
     * @param type Loại món ăn (BREAKFAST, LUNCH, DINNER, SNACK)
     * @param foods Danh sách thực phẩm
     */
    public void createMeal(String name, String description, String type, List<Food> foods) {
        if (!isAuthenticated()) {
            repository.setErrorMessage("User chưa đăng nhập. Vui lòng đăng nhập để tạo meal.");
            return;
        }
        
        String userId = currentUserId.getValue();
        if (userId != null) {
            MealRequest request = new MealRequest(name, description, type, foods, userId);
            repository.createMeal(request);
        }
    }
    
    /**
     * Cập nhật meal đã có
     * @param mealId ID của meal cần cập nhật
     * @param name Tên món ăn mới
     * @param description Mô tả mới
     * @param type Loại món ăn mới
     * @param foods Danh sách thực phẩm mới
     */
    public void updateMeal(Long mealId, String name, String description, String type, List<Food> foods) {
        if (!isAuthenticated()) {
            repository.setErrorMessage("User chưa đăng nhập. Vui lòng đăng nhập để cập nhật meal.");
            return;
        }
        
        String userId = currentUserId.getValue();
        if (userId != null) {
            MealRequest request = new MealRequest(name, description, type, foods, userId);
            repository.updateMeal(mealId, request);
        }
    }
    
    /**
     * Xóa meal
     * @param mealId ID của meal cần xóa
     */
    public void deleteMeal(Long mealId) {
        if (!isAuthenticated()) {
            repository.setErrorMessage("User chưa đăng nhập. Vui lòng đăng nhập để xóa meal.");
            return;
        }
        repository.deleteMeal(mealId);
    }
    
    // ==================== BUSINESS LOGIC & CONVERSION METHODS ====================
    
    /**
     * Convert scheduled workouts và meals thành DayEvents cho UI
     * Filter từ schedules theo ngày cụ thể
     * @param date Ngày cần convert theo format yyyy-MM-dd
     */
    private void convertScheduleEventsForDate(String date) {
        List<DayEvent> dayEvents = new ArrayList<>();
        
        // 1. Lấy scheduled workouts từ workout schedules
        List<WorkoutSchedulesResponse> workoutSchedules = repository.getWorkoutSchedules().getValue();
        if (workoutSchedules != null) {
            for (WorkoutSchedulesResponse schedule : workoutSchedules) {
                List<ScheduledWorkoutResponse> scheduledWorkouts = schedule.getScheduledWorkouts();
                if (scheduledWorkouts != null) {
                    for (ScheduledWorkoutResponse scheduledWorkout : scheduledWorkouts) {
                        if (isScheduledItemOnDate(scheduledWorkout.getScheduledDateTime(), date)) {
                            DayEvent event = convertScheduledWorkoutToDayEvent(scheduledWorkout, schedule);
                            if (event != null) {
                                dayEvents.add(event);
                            }
                        }
                    }
                }
            }
        }
        
        // 2. Lấy scheduled meals từ meal schedules
        List<MealSchedulesResponse> mealSchedules = repository.getMealSchedules().getValue();
        if (mealSchedules != null) {
            for (MealSchedulesResponse schedule : mealSchedules) {
                List<ScheduledMealResponse> scheduledMeals = schedule.getScheduledMeals();
                if (scheduledMeals != null) {
                    for (ScheduledMealResponse scheduledMeal : scheduledMeals) {
                        if (isScheduledItemOnDate(scheduledMeal.getScheduledDateTime(), date)) {
                            DayEvent event = convertScheduledMealToDayEvent(scheduledMeal, schedule);
                            if (event != null) {
                                dayEvents.add(event);
                            }
                        }
                    }
                }
            }
        }
        
        // 3. Sort theo time
        dayEvents.sort((e1, e2) -> {
            try {
                return e1.getTime().compareTo(e2.getTime());
            } catch (Exception ex) {
                return 0;
            }
        });
        
        // 4. Update LiveData
        convertedScheduleEvents.setValue(dayEvents);
    }
    
    /**
     * Kiểm tra xem scheduled item có thuộc ngày cụ thể không
     * @param scheduledDateTime ISO datetime string (yyyy-MM-ddTHH:mm:ss)
     * @param targetDate Ngày cần kiểm tra (yyyy-MM-dd)
     * @return true nếu scheduled item thuộc ngày này
     */
    private boolean isScheduledItemOnDate(String scheduledDateTime, String targetDate) {
        try {
            if (scheduledDateTime != null && targetDate != null) {
                // Extract date part từ ISO datetime
                String scheduledDate = scheduledDateTime.split("T")[0];
                return scheduledDate.equals(targetDate);
            }
        } catch (Exception e) {
            // Log error nếu cần
        }
        return false;
    }
    
    /**
     * Convert ScheduledWorkoutResponse thành DayEvent
     * @param scheduledWorkout ScheduledWorkoutResponse từ API
     * @param schedule WorkoutSchedulesResponse chứa scheduled workout này
     * @return DayEvent object hoặc null nếu lỗi
     */
    private DayEvent convertScheduledWorkoutToDayEvent(ScheduledWorkoutResponse scheduledWorkout, 
                                                      WorkoutSchedulesResponse schedule) {
        try {
            DayEvent event = new DayEvent();
            
            // Parse datetime để lấy time
            String[] dateTimeParts = parseISODateTime(scheduledWorkout.getScheduledDateTime());
            event.setTime(dateTimeParts[1]); // HH:mm
            event.setDate(dateTimeParts[0]); // yyyy-MM-dd
            
            // Get workout info từ scheduledWorkout.getWorkout()
            WorkoutResponse workout = scheduledWorkout.getWorkout();
            if (workout != null) {
                event.setName(workout.getName());
                event.setDescription(workout.getDescription());
                event.setCalories(workout.getCaloriesBurned());
                event.setType("Workout"); // hoặc workout.getType()
            } else {
                // Fallback nếu workout info không có
                event.setName("Buổi tập");
                event.setDescription("Workout ID: " + scheduledWorkout.getWorkoutId());
                event.setCalories(0);
                event.setType("Workout");
            }
            
            // Set additional info
            event.setUserId(schedule.getUserId());
            event.setStatus(scheduledWorkout.getStatus() != null ? scheduledWorkout.getStatus() : "SCHEDULED");
            
            return event;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Convert ScheduledMealResponse thành DayEvent
     * @param scheduledMeal ScheduledMealResponse từ API
     * @param schedule MealSchedulesResponse chứa scheduled meal này
     * @return DayEvent object hoặc null nếu lỗi
     */
    private DayEvent convertScheduledMealToDayEvent(ScheduledMealResponse scheduledMeal, 
                                                   MealSchedulesResponse schedule) {
        try {
            DayEvent event = new DayEvent();
            
            // Parse datetime để lấy time
            String[] dateTimeParts = parseISODateTime(scheduledMeal.getScheduledDateTime());
            event.setTime(dateTimeParts[1]); // HH:mm
            event.setDate(dateTimeParts[0]); // yyyy-MM-dd
            
            // Get meal info từ scheduledMeal.getMeal()
            MealResponse meal = scheduledMeal.getMeal();
            if (meal != null) {
                event.setName(meal.getName());
                event.setDescription(meal.getDescription());
                
                // Calculate total calories từ foods
                int totalCalories = 0;
                List<Food> foods = meal.getFoods();
                if (foods != null) {
                    for (Food food : foods) {
                        totalCalories += food.getCalories();
                    }
                }
                event.setCalories(totalCalories);
                event.setType(meal.getType()); // BREAKFAST, LUNCH, DINNER, SNACK
            } else {
                // Fallback nếu meal info không có
                event.setName("Bữa ăn");
                event.setDescription("Meal ID: " + scheduledMeal.getMealId());
                event.setCalories(0);
                event.setType("Meal");
            }
            
            // Set additional info
            event.setUserId(schedule.getUserId());
            event.setStatus(scheduledMeal.getStatus() != null ? scheduledMeal.getStatus() : "SCHEDULED");
            
            return event;
        } catch (Exception e) {
            return null;
        }
    }
    
    // ==================== AUTO REFRESH LOGIC ====================
    
    /**
     * Setup auto-refresh khi schedules được load
     * Gọi method này trong constructor để observe changes
     */
    private void setupAutoRefresh() {
        // Auto refresh events khi workout schedules thay đổi
        repository.getWorkoutSchedules().observeForever(schedules -> {
            String currentDate = currentSelectedDate.getValue();
            if (currentDate != null) {
                convertScheduleEventsForDate(currentDate);
            }
        });
        
        // Auto refresh events khi meal schedules thay đổi
        repository.getMealSchedules().observeForever(schedules -> {
            String currentDate = currentSelectedDate.getValue();
            if (currentDate != null) {
                convertScheduleEventsForDate(currentDate);
            }
        });
    }
    
    /**
     * Force refresh events cho ngày hiện tại
     * Gọi khi cần update manual hoặc sau khi tạo/sửa/xóa scheduled items
     */
    public void refreshCurrentDateEvents() {
        String currentDate = currentSelectedDate.getValue();
        if (currentDate != null) {
            convertScheduleEventsForDate(currentDate);
        }
    }
    
    // ==================== SCHEDULE CRUD OPERATIONS ====================
    
    /**
     * Tạo workout schedule mới
     * @param name Tên schedule
     * @param description Mô tả
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     */
    public void createWorkoutSchedule(String name, String description, String startDate, String endDate) {
        String userId = currentUserId.getValue();
        if (userId != null) {
            WorkoutSchedulesRequest request = new WorkoutSchedulesRequest(name, description, userId, startDate, endDate);
            repository.createWorkoutSchedule(request);
        }
    }
    
    /**
     * Tạo meal schedule mới
     * @param name Tên schedule
     * @param description Mô tả
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     */
    public void createMealSchedule(String name, String description, String startDate, String endDate) {
        String userId = currentUserId.getValue();
        if (userId != null) {
            MealSchedulesRequest request = new MealSchedulesRequest(userId, name, description, startDate, endDate);
            repository.createMealSchedule(request);
        }
    }
    
    /**
     * Cập nhật workout schedule
     * @param scheduleId ID của schedule
     * @param name Tên mới
     * @param description Mô tả mới
     * @param startDate Ngày bắt đầu mới
     * @param endDate Ngày kết thúc mới
     */
    public void updateWorkoutSchedule(Long scheduleId, String name, String description, String startDate, String endDate) {
        String userId = currentUserId.getValue();
        if (userId != null) {
            WorkoutSchedulesRequest request = new WorkoutSchedulesRequest(name, description, userId, startDate, endDate);
            repository.updateWorkoutSchedule(scheduleId, request);
        }
    }
    
    /**
     * Cập nhật meal schedule
     * @param scheduleId ID của schedule
     * @param name Tên mới
     * @param description Mô tả mới
     * @param startDate Ngày bắt đầu mới
     * @param endDate Ngày kết thúc mới
     */
    public void updateMealSchedule(Long scheduleId, String name, String description, String startDate, String endDate) {
        String userId = currentUserId.getValue();
        if (userId != null) {
            MealSchedulesRequest request = new MealSchedulesRequest(userId, name, description, startDate, endDate);
            repository.updateMealSchedule(scheduleId, request);
        }
    }
    
    /**
     * Xóa workout schedule
     * @param scheduleId ID của schedule cần xóa
     */
    public void deleteWorkoutSchedule(Long scheduleId) {
        repository.deleteWorkoutSchedule(scheduleId);
    }
    
    /**
     * Xóa meal schedule
     * @param scheduleId ID của schedule cần xóa
     */
    public void deleteMealSchedule(Long scheduleId) {
        repository.deleteMealSchedule(scheduleId);
    }
    
    // ==================== SCHEDULED WORKOUT/MEAL CRUD OPERATIONS ====================
    
    /**
     * Tạo scheduled workout (lên lịch workout cụ thể)
     * @param scheduleId ID của workout schedule
     * @param workoutId ID của workout
     * @param scheduledDateTime Thời gian lên lịch (ISO format)
     * @param status Trạng thái (SCHEDULED, COMPLETED, SKIPPED)
     * @param notes Ghi chú
     */
    public void createScheduledWorkout(Long scheduleId, Long workoutId, String scheduledDateTime, String status, String notes) {
        ScheduledWorkoutRequest request = new ScheduledWorkoutRequest(scheduleId, workoutId, scheduledDateTime, status, notes);
        repository.createScheduledWorkout(request);
    }
    
    /**
     * Tạo scheduled workout cho schedule hiện tại đang được chọn
     * @param workoutId ID của workout
     * @param scheduledDateTime Thời gian lên lịch (ISO format)
     * @param status Trạng thái (SCHEDULED, COMPLETED, SKIPPED)
     * @param notes Ghi chú
     */
    public void createScheduledWorkoutForSelectedSchedule(Long workoutId, String scheduledDateTime, String status, String notes) {
        Long scheduleId = selectedWorkoutScheduleId.getValue();
        if (scheduleId != null) {
            createScheduledWorkout(scheduleId, workoutId, scheduledDateTime, status, notes);
        }
    }
    
    /**
     * Tạo scheduled meal (lên lịch meal cụ thể)
     * @param scheduleId ID của meal schedule
     * @param mealId ID của meal
     * @param scheduledDateTime Thời gian lên lịch (ISO format)
     * @param status Trạng thái (SCHEDULED, COMPLETED, SKIPPED)
     * @param notes Ghi chú
     */
    public void createScheduledMeal(Long scheduleId, Long mealId, String scheduledDateTime, String status, String notes) {
        ScheduledMealRequest request = new ScheduledMealRequest(scheduleId, mealId, scheduledDateTime, status, notes);
        repository.createScheduledMeal(request);
    }
    
    /**
     * Tạo scheduled meal cho schedule hiện tại đang được chọn
     * @param mealId ID của meal
     * @param scheduledDateTime Thời gian lên lịch (ISO format)
     * @param status Trạng thái (SCHEDULED, COMPLETED, SKIPPED)
     * @param notes Ghi chú
     */
    public void createScheduledMealForSelectedSchedule(Long mealId, String scheduledDateTime, String status, String notes) {
        Long scheduleId = selectedMealScheduleId.getValue();
        if (scheduleId != null) {
            createScheduledMeal(scheduleId, mealId, scheduledDateTime, status, notes);
        }
    }
    
    /**
     * Cập nhật scheduled workout
     * @param scheduledWorkoutId ID của scheduled workout
     * @param scheduleId ID của workout schedule
     * @param workoutId ID của workout
     * @param scheduledDateTime Thời gian mới
     * @param status Trạng thái mới
     * @param notes Ghi chú mới
     */
    public void updateScheduledWorkout(Long scheduledWorkoutId, Long scheduleId, Long workoutId, String scheduledDateTime, String status, String notes) {
        ScheduledWorkoutRequest request = new ScheduledWorkoutRequest(scheduleId, workoutId, scheduledDateTime, status, notes);
        repository.updateScheduledWorkout(scheduledWorkoutId, request);
    }
    
    /**
     * Cập nhật scheduled meal
     * @param scheduledMealId ID của scheduled meal
     * @param scheduleId ID của meal schedule
     * @param mealId ID của meal
     * @param scheduledDateTime Thời gian mới
     * @param status Trạng thái mới
     * @param notes Ghi chú mới
     */
    public void updateScheduledMeal(Long scheduledMealId, Long scheduleId, Long mealId, String scheduledDateTime, String status, String notes) {
        ScheduledMealRequest request = new ScheduledMealRequest(scheduleId, mealId, scheduledDateTime, status, notes);
        repository.updateScheduledMeal(scheduledMealId, request);
    }
    
    /**
     * Xóa scheduled workout
     * @param scheduledWorkoutId ID của scheduled workout cần xóa
     */
    public void deleteScheduledWorkout(Long scheduledWorkoutId) {
        repository.deleteScheduledWorkout(scheduledWorkoutId);
    }
    
    /**
     * Xóa scheduled meal
     * @param scheduledMealId ID của scheduled meal cần xóa
     */
    public void deleteScheduledMeal(Long scheduledMealId) {
        repository.deleteScheduledMeal(scheduledMealId);
    }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Format ngày để hiển thị trên UI
     * @param date String ngày theo format yyyy-MM-dd
     * @return String ngày theo format dd/MM/yyyy
     */
    public String formatDateForDisplay(String date) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateFormat.parse(date));
            return displayDateFormat.format(calendar.getTime());
        } catch (Exception e) {
            return date;
        }
    }
    
    /**
     * Lấy string ngày hiện tại
     * @return String ngày hiện tại theo format yyyy-MM-dd
     */
    public String getCurrentDateString() {
        Calendar calendar = Calendar.getInstance();
        return dateFormat.format(calendar.getTime());
    }
    
    /**
     * Convert date và time thành ISO format cho API
     * @param date String ngày (yyyy-MM-dd)
     * @param time String giờ (HH:mm)
     * @return String datetime theo ISO format (yyyy-MM-ddTHH:mm:ss)
     */
    public String formatDateTimeForAPI(String date, String time) {
        try {
            String dateTimeString = date + "T" + time + ":00";
            return dateTimeString;
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Parse ISO datetime thành date và time riêng biệt
     * @param isoDateTime String ISO datetime (yyyy-MM-ddTHH:mm:ss)
     * @return Array [date, time] hoặc ["", ""] nếu lỗi
     */
    public String[] parseISODateTime(String isoDateTime) {
        try {
            String[] parts = isoDateTime.split("T");
            if (parts.length == 2) {
                String date = parts[0];
                String time = parts[1].substring(0, 5); // Lấy HH:mm
                return new String[]{date, time};
            }
        } catch (Exception e) {
            // Ignore
        }
        return new String[]{"", ""};
    }
    
    // ==================== SCHEDULE BROWSING & SELECTION ACTIONS ====================
    
    /**
     * Browse và load tất cả workout schedules của user hiện tại
     * User cần gọi method này trước để có danh sách schedules
     */
    public void browseWorkoutSchedules() {
        if (!isAuthenticated()) {
            repository.setErrorMessage("User chưa đăng nhập. Vui lòng đăng nhập để xem schedules.");
            return;
        }
        
        String userId = currentUserId.getValue();
        if (userId != null) {
            repository.getWorkoutSchedulesByUserId(userId);
            // Clear selection khi browse lại
            selectedWorkoutSchedule.setValue(null);
            selectedWorkoutScheduleId.setValue(null);
        }
    }
    
    /**
     * Browse và load tất cả meal schedules của user hiện tại
     * User cần gọi method này trước để có danh sách schedules
     */
    public void browseMealSchedules() {
        if (!isAuthenticated()) {
            repository.setErrorMessage("User chưa đăng nhập. Vui lòng đăng nhập để xem schedules.");
            return;
        }
        
        String userId = currentUserId.getValue();
        if (userId != null) {
            repository.getMealSchedulesByUserId(userId);
            // Clear selection khi browse lại
            selectedMealSchedule.setValue(null);
            selectedMealScheduleId.setValue(null);
        }
    }
    
    /**
     * Chọn một workout schedule cụ thể từ danh sách đã load
     * @param schedule WorkoutSchedulesResponse được chọn từ UI list
     */
    public void selectWorkoutSchedule(WorkoutSchedulesResponse schedule) {
        if (schedule != null) {
            selectedWorkoutSchedule.setValue(schedule);
            selectedWorkoutScheduleId.setValue(schedule.getId());
            
            // Auto load scheduled workouts cho schedule này
            loadScheduledWorkoutsByScheduleId(schedule.getId());
        }
    }
    
    /**
     * Chọn một meal schedule cụ thể từ danh sách đã load
     * @param schedule MealSchedulesResponse được chọn từ UI list
     */
    public void selectMealSchedule(MealSchedulesResponse schedule) {
        if (schedule != null) {
            selectedMealSchedule.setValue(schedule);
            selectedMealScheduleId.setValue(schedule.getId());
            
            // Auto load scheduled meals cho schedule này
            loadScheduledMealsByScheduleId(schedule.getId());
        }
    }

    /**
     * Clear selection của workout schedule
     */
    public void clearWorkoutScheduleSelection() {
        selectedWorkoutSchedule.setValue(null);
        selectedWorkoutScheduleId.setValue(null);
    }
    
    /**
     * Clear selection của meal schedule
     */
    public void clearMealScheduleSelection() {
        selectedMealSchedule.setValue(null);
        selectedMealScheduleId.setValue(null);
    }
}
