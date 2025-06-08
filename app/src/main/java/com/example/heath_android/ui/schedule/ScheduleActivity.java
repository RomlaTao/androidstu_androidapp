package com.example.heath_android.ui.schedule;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heath_android.R;
import com.example.heath_android.data.model.schedule.DayEvent;
import com.example.heath_android.data.model.schedule.WorkoutSchedulesResponse;
import com.example.heath_android.data.model.schedule.MealSchedulesResponse;
import com.example.heath_android.data.model.schedule.Exercise;
import com.example.heath_android.data.model.schedule.Food;
import com.example.heath_android.ui.schedule.adapter.DayContentAdapter;
import com.example.heath_android.ui.schedule.adapter.WeekDayAdapter;
import com.example.heath_android.ui.home.HomeActivity;
import com.example.heath_android.ui.profile.ProfileActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ScheduleActivity extends AppCompatActivity {
    
    private ScheduleViewModel viewModel;
    
    // UI Components
    private TextView tvCalendar;
    private Spinner spMonth;
    private RecyclerView rvWeekDays, rvContent;
    private FloatingActionButton fabAddEvent;
    private BottomNavigationView bottomNavigation;
    
    // Adapters
    private WeekDayAdapter weekDayAdapter;
    private DayContentAdapter dayContentAdapter;
    private ArrayAdapter<String> monthAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_schedule);
        
        // Apply window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        initViewModel();
        initViews();
        setupAdapters();
        setupObservers();
        setupClickListeners();
        setupBottomNavigation();
    }
    
    private void initViewModel() {
        // Sử dụng constructor mới với Context để tự động load authentication
        viewModel = new ScheduleViewModel(this);
        
        // Kiểm tra authentication status khi khởi tạo
        if (!viewModel.isAuthenticated()) {
            Toast.makeText(this, "Vui lòng đăng nhập để sử dụng chức năng lịch", Toast.LENGTH_LONG).show();
            // TODO: Redirect to login activity nếu cần
            // Intent intent = new Intent(this, LoginActivity.class);
            // startActivity(intent);
            // finish();
            return;
        }
        
        // Load initial data nếu đã authenticated
        loadInitialData();
    }
    
    /**
     * Load dữ liệu ban đầu khi activity khởi tạo
     */
    private void loadInitialData() {
        // Load base data
        viewModel.loadAllWorkouts();
        viewModel.loadAllMeals();
        
        // Load user's schedules để có thể browse
        viewModel.browseWorkoutSchedules();
        viewModel.browseMealSchedules();
        
        // Load events cho ngày hiện tại
        viewModel.loadScheduleEventsForDate(viewModel.getCurrentDateString());
    }
    
    private void initViews() {
        tvCalendar = findViewById(R.id.tvCalendar);
        spMonth = findViewById(R.id.spMonth);
        rvWeekDays = findViewById(R.id.rvWeekDays);
        rvContent = findViewById(R.id.rvContent);
        fabAddEvent = findViewById(R.id.fabAddEvent);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        
        // Initialize new UI components
        initializeNewUIComponents();
    }
    
    /**
     * Initialize các UI components mới được thêm vào layout
     */
    private void initializeNewUIComponents() {
        // User info components
        LinearLayout llUserInfo = findViewById(R.id.llUserInfo);
        ImageView ivAuthStatus = findViewById(R.id.ivAuthStatus);
        
        // Selected schedule components
        LinearLayout llSelectedSchedule = findViewById(R.id.llSelectedSchedule);
        TextView tvSelectedScheduleName = findViewById(R.id.tvSelectedScheduleName);
        TextView tvSelectedSchedulePeriod = findViewById(R.id.tvSelectedSchedulePeriod);
        ImageButton btnClearSelection = findViewById(R.id.btnClearSelection);
        
        // Calendar navigation components
        ImageButton btnPrevWeek = findViewById(R.id.btnPrevWeek);
        TextView tvCurrentWeek = findViewById(R.id.tvCurrentWeek);
        ImageButton btnNextWeek = findViewById(R.id.btnNextWeek);
        ImageButton btnYearPicker = findViewById(R.id.btnYearPicker);
        
        // Loading and empty state
        ProgressBar progressBar = findViewById(R.id.progressBar);
        LinearLayout llEmptyState = findViewById(R.id.llEmptyState);
        
        // Setup click listeners cho các components mới
        setupNewClickListeners(btnClearSelection, btnPrevWeek, btnNextWeek, btnYearPicker);
        
        // Setup observers cho authentication và selection state
        setupUIStateObservers(llUserInfo, ivAuthStatus, llSelectedSchedule,
                             tvSelectedScheduleName, tvSelectedSchedulePeriod, progressBar, llEmptyState);
    }
    
    /**
     * Setup click listeners cho các UI components mới
     */
    private void setupNewClickListeners(ImageButton btnClearSelection, ImageButton btnPrevWeek, 
                                      ImageButton btnNextWeek, ImageButton btnYearPicker) {
        
        // Clear selection button
        btnClearSelection.setOnClickListener(v -> {
            viewModel.clearWorkoutScheduleSelection();
            viewModel.clearMealScheduleSelection();
            Toast.makeText(this, "Đã bỏ chọn lịch", Toast.LENGTH_SHORT).show();
        });
        
        // Week navigation
        btnPrevWeek.setOnClickListener(v -> viewModel.previousWeek());
        btnNextWeek.setOnClickListener(v -> viewModel.nextWeek());
        
        // Year picker
        btnYearPicker.setOnClickListener(v -> showYearPickerDialog());
    }
    
    /**
     * Setup observers cho UI state
     */
    private void setupUIStateObservers(LinearLayout llUserInfo, ImageView ivAuthStatus,
                                     LinearLayout llSelectedSchedule, TextView tvSelectedScheduleName, 
                                     TextView tvSelectedSchedulePeriod, ProgressBar progressBar, 
                                     LinearLayout llEmptyState) {
        
        // Observe authentication state
        if (viewModel.isAuthenticated()) {
            String[] userInfo = viewModel.getCurrentUserInfo();
            if (userInfo != null && userInfo.length >= 2) {
                llUserInfo.setVisibility(View.VISIBLE);
                ivAuthStatus.setImageResource(android.R.drawable.presence_online);
            }
        } else {
            llUserInfo.setVisibility(View.GONE);
        }
        
        // Observe selected workout schedule
        viewModel.getSelectedWorkoutSchedule().observe(this, schedule -> {
            if (schedule != null) {
                llSelectedSchedule.setVisibility(View.VISIBLE);
                tvSelectedScheduleName.setText("🏋️ " + schedule.getName());
                tvSelectedSchedulePeriod.setText(schedule.getStartDate() + " → " + schedule.getEndDate());
                llEmptyState.setVisibility(View.GONE);
            }
        });
        
        // Observe selected meal schedule
        viewModel.getSelectedMealSchedule().observe(this, schedule -> {
            if (schedule != null) {
                llSelectedSchedule.setVisibility(View.VISIBLE);
                tvSelectedScheduleName.setText("🍽️ " + schedule.getName());
                tvSelectedSchedulePeriod.setText(schedule.getStartDate() + " → " + schedule.getEndDate());
                llEmptyState.setVisibility(View.GONE);
            }
        });
        
        // Show empty state khi không có schedule nào được chọn
        // Observe cả 2 schedule types để update UI state
        observeScheduleSelectionState(llSelectedSchedule, llEmptyState);
        
        // Observe loading state  
        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading != null && isLoading ? View.VISIBLE : View.GONE);
        });
    }
    
    /**
     * Dialog chọn năm
     */
    private void showYearPickerDialog() {
        int currentYear = viewModel.getSelectedYear().getValue() != null ? 
                         viewModel.getSelectedYear().getValue() : 2024;
        
        String[] years = new String[10];
        for (int i = 0; i < 10; i++) {
            years[i] = String.valueOf(currentYear - 5 + i);
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn năm");
        builder.setItems(years, (dialog, which) -> {
            int selectedYear = currentYear - 5 + which;
            viewModel.selectYear(selectedYear);
        });
        
        builder.show();
    }
    
    /**
     * Observe schedule selection state để update UI visibility
     */
    private void observeScheduleSelectionState(LinearLayout llSelectedSchedule, LinearLayout llEmptyState) {
        // Combine observers để track cả workout và meal selection
        viewModel.getSelectedWorkoutSchedule().observe(this, workoutSchedule -> {
            updateScheduleSelectionUI(workoutSchedule, 
                                    viewModel.getSelectedMealSchedule().getValue(),
                                    llSelectedSchedule, llEmptyState);
        });
        
        viewModel.getSelectedMealSchedule().observe(this, mealSchedule -> {
            updateScheduleSelectionUI(viewModel.getSelectedWorkoutSchedule().getValue(),
                                    mealSchedule,
                                    llSelectedSchedule, llEmptyState);
        });
    }
    
    /**
     * Update UI dựa trên schedule selection state
     */
    private void updateScheduleSelectionUI(WorkoutSchedulesResponse workoutSchedule,
                                         MealSchedulesResponse mealSchedule,
                                         LinearLayout llSelectedSchedule,
                                         LinearLayout llEmptyState) {
        boolean hasSelection = (workoutSchedule != null) || (mealSchedule != null);
        
        if (hasSelection) {
            llSelectedSchedule.setVisibility(View.VISIBLE);
            llEmptyState.setVisibility(View.GONE);
        } else {
            llSelectedSchedule.setVisibility(View.GONE);
            llEmptyState.setVisibility(View.VISIBLE);
        }
    }
    
    private void setupAdapters() {
        // Week days adapter
        weekDayAdapter = new WeekDayAdapter();
        weekDayAdapter.setOnDayClickListener(this::onDayClicked);
        rvWeekDays.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvWeekDays.setAdapter(weekDayAdapter);
        
        // Day content adapter
        dayContentAdapter = new DayContentAdapter();
        dayContentAdapter.setOnEventClickListener(this::onEventClicked);
        dayContentAdapter.setOnEventLongClickListener(this::onEventLongClicked);
        rvContent.setLayoutManager(new LinearLayoutManager(this));
        rvContent.setAdapter(dayContentAdapter);
        
        // Month spinner adapter
        monthAdapter = new ArrayAdapter<>(this, R.layout.spinner_item);
        monthAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spMonth.setAdapter(monthAdapter);
    }
    
    private void setupObservers() {
        // Observe months list
        viewModel.getMonthsList().observe(this, months -> {
            if (months != null) {
                monthAdapter.clear();
                monthAdapter.addAll(months);
                monthAdapter.notifyDataSetChanged();
            }
        });
        
        // Observe selected month
        viewModel.getSelectedMonth().observe(this, month -> {
            if (month != null && spMonth != null) {
                spMonth.setSelection(month);
            }
        });
        
        // Observe week days
        viewModel.getWeekDays().observe(this, weekDays -> {
            if (weekDays != null) {
                weekDayAdapter.updateDays(weekDays);
            }
        });
        
        // Observe schedule events (converted for UI)
        viewModel.getScheduleEvents().observe(this, events -> {
            if (events != null) {
                dayContentAdapter.updateEvents(events);
            }
        });
        
        // Observe current selected date
        viewModel.getCurrentSelectedDate().observe(this, date -> {
            if (date != null) {
                // Update UI to reflect selected date
                updateSelectedDateUI(date);
                
                // Update current week text
                TextView tvCurrentWeek = findViewById(R.id.tvCurrentWeek);
                if (tvCurrentWeek != null) {
                    tvCurrentWeek.setText(viewModel.getCurrentWeekText());
                }
            }
        });
        
        // ==================== NEW API DATA OBSERVERS ====================
        
        // Observe workout responses
        viewModel.getWorkoutResponses().observe(this, workouts -> {
            if (workouts != null) {
                // TODO: Update workout list in UI if needed
                // Can be used for dropdown/spinner in create scheduled workout dialog
            }
        });
        
        // Observe meal responses
        viewModel.getMealResponses().observe(this, meals -> {
            if (meals != null) {
                // TODO: Update meal list in UI if needed
                // Can be used for dropdown/spinner in create scheduled meal dialog
            }
        });
        
        // Observe workout schedules - cập nhật danh sách để browse
        viewModel.getWorkoutSchedules().observe(this, schedules -> {
            if (schedules != null && !schedules.isEmpty()) {
                // Hiển thị thông báo có schedules mới được load
                // UI sẽ tự cập nhật khi user mở browse dialog
            }
        });
        
        // Observe meal schedules - cập nhật danh sách để browse
        viewModel.getMealSchedules().observe(this, schedules -> {
            if (schedules != null && !schedules.isEmpty()) {
                // Hiển thị thông báo có schedules mới được load
                // UI sẽ tự cập nhật khi user mở browse dialog
            }
        });
        
        // Observe selected workout schedule
        viewModel.getSelectedWorkoutSchedule().observe(this, schedule -> {
            if (schedule != null) {
                // Show selected workout schedule info in UI
                // Enable operations on this schedule
            }
        });
        
        // Observe selected meal schedule
        viewModel.getSelectedMealSchedule().observe(this, schedule -> {
            if (schedule != null) {
                // Show selected meal schedule info in UI
                // Enable operations on this schedule
            }
        });
        
        // Observe scheduled workouts
        viewModel.getScheduledWorkouts().observe(this, scheduledWorkouts -> {
            if (scheduledWorkouts != null) {
                // Auto refresh current date events khi có scheduled workouts mới
                viewModel.refreshCurrentDateEvents();
            }
        });
        
        // Observe scheduled meals
        viewModel.getScheduledMeals().observe(this, scheduledMeals -> {
            if (scheduledMeals != null) {
                // Auto refresh current date events khi có scheduled meals mới
                viewModel.refreshCurrentDateEvents();
            }
        });
        
        // ==================== STATUS OBSERVERS ====================
        
        // Observe loading state
        viewModel.getIsLoading().observe(this, isLoading -> {
            // Show/hide loading indicator
            // You can implement a progress bar here
            if (isLoading != null && isLoading) {
                // Show loading
            } else {
                // Hide loading
            }
        });
        
        // Observe error messages
        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void setupClickListeners() {
        // Month spinner listener
        spMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.selectMonth(position);
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        // FAB click listener
        fabAddEvent.setOnClickListener(v -> showEventTypeDialog());
    }
    
    private void onDayClicked(int dayIndex, String day) {
        // Calculate the actual date based on selected month/year and day
        Integer month = viewModel.getSelectedMonth().getValue();
        Integer year = viewModel.getSelectedYear().getValue();
        
        if (month != null && year != null) {
            String date = String.format("%04d-%02d-%02d", year, month + 1, Integer.parseInt(day));
            viewModel.selectDate(date);
            
            // Refresh schedules data để đảm bảo có data mới nhất
            if (viewModel.isAuthenticated()) {
                viewModel.loadUserWorkoutSchedules();
                viewModel.loadUserMealSchedules();
            }
        }
    }
    
    private void onEventClicked(DayEvent event) {
        // Show event details or edit dialog
        showEditEventDialog(event);
    }
    
    private boolean onEventLongClicked(DayEvent event) {
        // Show delete confirmation dialog
        showDeleteEventDialog(event);
        return true;
    }
    
    private void updateSelectedDateUI(String date) {
        // Update any UI elements that show the selected date
        // You can highlight the selected day in the week view
        weekDayAdapter.setSelectedDate(date);
    }
    
    /**
     * Hàm showEventTypeDialog() hiển thị dialog chọn loại lịch chính (Lịch ăn hoặc Lịch tập)
     * Theo thiết kế backend tách biệt giữa Workout service và Meal service
     * 
     * Updated: Bao gồm cả schedule browsing workflow
     */
    private void showEventTypeDialog() {
        // Tạo dialog với nhiều lựa chọn hơn
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quản lý lịch trình");
        
        String[] options = {
            "📋 Duyệt lịch tập luyện", 
            "🍽️ Duyệt lịch ăn uống",
            "➕ Tạo mới lịch tập",
            "➕ Tạo mới lịch ăn", 
            "🏋️ Tạo bài tập",
            "🍴 Tạo thực đơn",
            "📅 Thêm event nhanh"
        };
        
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: // Duyệt lịch tập luyện
                    showWorkoutScheduleBrowseDialog();
                    break;
                case 1: // Duyệt lịch ăn uống
                    showMealScheduleBrowseDialog();
                    break;
                case 2: // Tạo mới lịch tập
                    showCreateWorkoutScheduleDialog();
                    break;
                case 3: // Tạo mới lịch ăn
                    showCreateMealScheduleDialog();
                    break;
                case 4: // Tạo bài tập
                    showCreateWorkoutDialog();
                    break;
                case 5: // Tạo thực đơn
                    showCreateMealDialog();
                    break;
                case 6: // Thêm event nhanh (legacy)
                    showQuickEventDialog();
                    break;
            }
        });
        
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
    
    /**
     * Dialog để browse và chọn workout schedules
     */
    private void showWorkoutScheduleBrowseDialog() {
        if (!viewModel.isAuthenticated()) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem lịch tập", Toast.LENGTH_SHORT).show();
            return;
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lịch tập luyện của bạn");
        
        // Refresh schedules list
        viewModel.browseWorkoutSchedules();
        
        // TODO: Implement proper list dialog with actual schedule data
        // For now, show simple options
        String[] options = {
            "📋 Xem tất cả lịch tập",
            "➕ Tạo lịch tập mới", 
            "🏋️ Lên lịch buổi tập",
            "📊 Thống kê"
        };
        
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    showWorkoutScheduleListDialog();
                    break;
                case 1:
                    showCreateWorkoutScheduleDialog();
                    break;
                case 2:
                    showCreateScheduledWorkoutDialog();
                    break;
                case 3:
                    Toast.makeText(this, "Chức năng thống kê đang phát triển", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
        
        builder.setNegativeButton("Quay lại", (dialog, which) -> showEventTypeDialog());
        builder.show();
    }
    
    /**
     * Dialog để browse và chọn meal schedules
     */
    private void showMealScheduleBrowseDialog() {
        if (!viewModel.isAuthenticated()) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem lịch ăn", Toast.LENGTH_SHORT).show();
            return;
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lịch ăn uống của bạn");
        
        // Refresh schedules list
        viewModel.browseMealSchedules();
        
        // TODO: Implement proper list dialog with actual schedule data
        String[] options = {
            "📋 Xem tất cả lịch ăn",
            "➕ Tạo lịch ăn mới",
            "🍽️ Lên lịch bữa ăn", 
            "📊 Thống kê dinh dưỡng"
        };
        
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    showMealScheduleListDialog();
                    break;
                case 1:
                    showCreateMealScheduleDialog();
                    break;
                case 2:
                    showCreateScheduledMealDialog();
                    break;
                case 3:
                    Toast.makeText(this, "Chức năng thống kê đang phát triển", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
        
        builder.setNegativeButton("Quay lại", (dialog, which) -> showEventTypeDialog());
        builder.show();
    }
    
    /**
     * Quick event dialog for simple events (legacy support)
     */
    private void showQuickEventDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm event nhanh");
        
        String[] options = {"🏋️ Buổi tập", "🍽️ Bữa ăn"};
        
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    showAddWorkoutEventDialog();
                    break;
                case 1:
                    showAddMealEventDialog();
                    break;
            }
        });
        
        builder.setNegativeButton("Quay lại", (dialog, which) -> showEventTypeDialog());
        builder.show();
    }
    
    /**
     * Dialog tạo schedule (đã được thay thế bởi showCreateWorkoutScheduleDialog và showCreateMealScheduleDialog)
     * @deprecated Sử dụng showCreateWorkoutScheduleDialog() hoặc showCreateMealScheduleDialog()
     */
    @Deprecated
    private void showCreatePlanDialog() {
        // Chuyển hướng đến dialog tạo workout schedule
        showCreateWorkoutScheduleDialog();
    }
    
    private void showCreateWorkoutDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_workout, null);
        
        EditText etWorkoutName = dialogView.findViewById(R.id.etWorkoutName);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        EditText etWorkoutType = dialogView.findViewById(R.id.etWorkoutType);
        EditText etCalories = dialogView.findViewById(R.id.etCalories);
        EditText etDuration = dialogView.findViewById(R.id.etDuration);
        EditText etDifficulty = dialogView.findViewById(R.id.etDifficulty);
        
        // Update hints for new API
        etWorkoutType.setHint("Loại (HIIT, CARDIO, STRENGTH, etc.)");
        etCalories.setHint("Calories đốt cháy");
        etDuration.setHint("Thời gian (phút)");
        etDifficulty.setHint("Danh sách bài tập (tạm thời để trống)");
        
        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Tạo bài tập")
                .setPositiveButton("Tạo", (dialog, which) -> {
                    String name = etWorkoutName.getText().toString().trim();
                    String description = etDescription.getText().toString().trim();
                    String type = etWorkoutType.getText().toString().trim();
                    String caloriesStr = etCalories.getText().toString().trim();
                    String durationStr = etDuration.getText().toString().trim();
                    
                    if (!name.isEmpty() && !type.isEmpty()) {
                        try {
                            int calories = caloriesStr.isEmpty() ? 0 : Integer.parseInt(caloriesStr);
                            int duration = durationStr.isEmpty() ? 0 : Integer.parseInt(durationStr);
                            
                            // TODO: Trong thực tế, cần dialog riêng để chọn exercises
                            // Hiện tại tạo empty list
                            java.util.List<com.example.heath_android.data.model.schedule.Exercise> exercises = new java.util.ArrayList<>();
                            
                            viewModel.createWorkout(name, description, type, duration, calories, exercises);
                            Toast.makeText(this, "Đang tạo bài tập...", Toast.LENGTH_SHORT).show();
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, "Calories và thời gian phải là số", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Vui lòng nhập tên bài tập và loại", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    
    private void showCreateMealDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_meal, null);
        
        EditText etMealName = dialogView.findViewById(R.id.etMealName);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        EditText etMealType = dialogView.findViewById(R.id.etMealType);
        EditText etCalories = dialogView.findViewById(R.id.etCalories);
        
        // Update hints for meal context (thực đơn)
        etMealName.setHint("Tên thực đơn");
        etMealType.setHint("Loại (BREAKFAST, LUNCH, DINNER, SNACK)");
        etCalories.setHint("Calories");
        
        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Tạo thực đơn")
                .setPositiveButton("Tạo", (dialog, which) -> {
                    String name = etMealName.getText().toString().trim();
                    String description = etDescription.getText().toString().trim();
                    String type = etMealType.getText().toString().trim();
                    
                    if (!name.isEmpty() && !type.isEmpty()) {
                        // TODO: Trong thực tế, cần dialog riêng để chọn foods
                        // Hiện tại tạo empty list
                        java.util.List<com.example.heath_android.data.model.schedule.Food> foods = new java.util.ArrayList<>();
                        
                        viewModel.createMeal(name, description, type, foods);
                        Toast.makeText(this, "Đang tạo thực đơn...", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Vui lòng nhập tên thực đơn và loại", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    
    /**
     * Dialog thêm scheduled meal (legacy method - đã được thay thế)
     * @deprecated Sử dụng showCreateScheduledMealDialog() với API mới
     */
    @Deprecated
    private void showAddScheduledMealDialog() {
        // Chuyển hướng đến dialog mới với API chuẩn
        showCreateScheduledMealDialog();
    }
    
    /**
     * Dialog lên lịch session (legacy method - đã được thay thế)
     * @deprecated Sử dụng showCreateScheduledWorkoutDialog() với API mới
     */
    @Deprecated
    private void showScheduleSessionDialog() {
        // Chuyển hướng đến dialog mới với API chuẩn
        showCreateScheduledWorkoutDialog();
    }
    
    private void showAddMealEventDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_event, null);
        
        EditText etTime = dialogView.findViewById(R.id.etTime);
        EditText etName = dialogView.findViewById(R.id.etName);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        EditText etCalories = dialogView.findViewById(R.id.etCalories);
        EditText etType = dialogView.findViewById(R.id.etType);
        
        // Set hints cho meal context
        etName.setHint("Tên bữa ăn");
        etType.setHint("Loại (Breakfast, Lunch, Dinner, Snack)");
        
        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Thêm bữa ăn vào lịch")
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String time = etTime.getText().toString().trim();
                    String name = etName.getText().toString().trim();
                    String description = etDescription.getText().toString().trim();
                    String calories = etCalories.getText().toString().trim();
                    String type = etType.getText().toString().trim();
                    
                    if (!time.isEmpty() && !name.isEmpty()) {
                        // TODO: Implement createDayEvent hoặc chuyển đến create scheduled meal
                        // viewModel.createDayEvent(time, name, description, calories, type.isEmpty() ? "Meal" : type);
                        Toast.makeText(this, "Tính năng này đang được phát triển. Vui lòng sử dụng 'Lên lịch bữa ăn' từ menu chính.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Vui lòng nhập thời gian và tên bữa ăn", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    
    private void showAddWorkoutEventDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_event, null);
        
        EditText etTime = dialogView.findViewById(R.id.etTime);
        EditText etName = dialogView.findViewById(R.id.etName);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        EditText etCalories = dialogView.findViewById(R.id.etCalories);
        EditText etType = dialogView.findViewById(R.id.etType);
        
        // Set hints cho workout context
        etName.setHint("Tên buổi tập");
        etType.setHint("Loại (Cardio, Strength, Yoga, etc.)");
        etCalories.setHint("Calories tiêu thụ dự kiến");
        
        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Thêm buổi tập vào lịch")
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String time = etTime.getText().toString().trim();
                    String name = etName.getText().toString().trim();
                    String description = etDescription.getText().toString().trim();
                    String calories = etCalories.getText().toString().trim();
                    String type = etType.getText().toString().trim();
                    
                    if (!time.isEmpty() && !name.isEmpty()) {
                        // TODO: Implement createDayEvent hoặc chuyển đến create scheduled workout
                        // viewModel.createDayEvent(time, name, description, calories, type.isEmpty() ? "Workout" : type);
                        Toast.makeText(this, "Tính năng này đang được phát triển. Vui lòng sử dụng 'Lên lịch buổi tập' từ menu chính.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Vui lòng nhập thời gian và tên buổi tập", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    
    private void showEditEventDialog(DayEvent event) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_event, null);
        
        EditText etTime = dialogView.findViewById(R.id.etTime);
        EditText etName = dialogView.findViewById(R.id.etName);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        EditText etCalories = dialogView.findViewById(R.id.etCalories);
        EditText etType = dialogView.findViewById(R.id.etType);
        
        // Pre-fill with existing data
        etTime.setText(event.getTime());
        etName.setText(event.getName());
        etDescription.setText(event.getDescription());
        etCalories.setText(String.valueOf(event.getCalories()));
        etType.setText(event.getType());
        
        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Chỉnh sửa sự kiện")
                .setPositiveButton("Cập nhật", (dialog, which) -> {
                    String time = etTime.getText().toString().trim();
                    String name = etName.getText().toString().trim();
                    String description = etDescription.getText().toString().trim();
                    String calories = etCalories.getText().toString().trim();
                    String type = etType.getText().toString().trim();
                    
                    if (!time.isEmpty() && !name.isEmpty()) {
                        // TODO: Implement updateDayEvent hoặc chuyển đến update scheduled meal/workout
                        // viewModel.updateDayEvent(event.getId(), time, name, description, calories, type);
                        Toast.makeText(this, "Tính năng chỉnh sửa đang được phát triển. Vui lòng xóa và tạo lại.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Vui lòng nhập thời gian và tên sự kiện", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    
    private void showDeleteEventDialog(DayEvent event) {
        String eventTypeName = event.getType() != null && event.getType().toLowerCase().contains("meal") ? "bữa ăn" : "buổi tập";
        
        new AlertDialog.Builder(this)
                .setTitle("Xóa " + eventTypeName)
                .setMessage("Bạn có chắc chắn muốn xóa " + eventTypeName + " \"" + event.getName() + "\"?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    // TODO: Implement deleteDayEvent hoặc chuyển đến delete scheduled meal/workout
                    // viewModel.deleteDayEvent(event.getId(), event.getType());
                    Toast.makeText(this, "Tính năng xóa đang được phát triển. Vui lòng sử dụng API scheduled meal/workout.", Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    
    // ==================== SCHEDULE LIST DIALOGS ====================
    
    /**
     * Dialog hiển thị danh sách workout schedules với dữ liệu thực tế
     */
    private void showWorkoutScheduleListDialog() {
        // Lấy danh sách schedules từ ViewModel
        if (viewModel.getWorkoutSchedules().getValue() == null || viewModel.getWorkoutSchedules().getValue().isEmpty()) {
            Toast.makeText(this, "Chưa có lịch tập nào. Hãy tạo lịch tập mới!", Toast.LENGTH_SHORT).show();
            showCreateWorkoutScheduleDialog();
            return;
        }
        
        // Tạo array với thông tin schedule để hiển thị
        java.util.List<com.example.heath_android.data.model.schedule.WorkoutSchedulesResponse> schedules = 
            viewModel.getWorkoutSchedules().getValue();
        
        String[] scheduleNames = new String[schedules.size()];
        for (int i = 0; i < schedules.size(); i++) {
            com.example.heath_android.data.model.schedule.WorkoutSchedulesResponse schedule = schedules.get(i);
            scheduleNames[i] = "📋 " + schedule.getName() + 
                             "\n   " + schedule.getStartDate() + " → " + schedule.getEndDate() + 
                             "\n   " + schedule.getDescription();
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn lịch tập luyện");
        
        builder.setItems(scheduleNames, (dialog, which) -> {
            // User chọn một schedule
            com.example.heath_android.data.model.schedule.WorkoutSchedulesResponse selectedSchedule = schedules.get(which);
            viewModel.selectWorkoutSchedule(selectedSchedule);
            
            Toast.makeText(this, "Đã chọn lịch: " + selectedSchedule.getName(), Toast.LENGTH_SHORT).show();
            
            // Hiển thị menu cho schedule đã chọn
            showSelectedWorkoutScheduleMenu(selectedSchedule);
        });
        
        builder.setNegativeButton("Quay lại", (dialog, which) -> showWorkoutScheduleBrowseDialog());
        builder.setNeutralButton("Tạo mới", (dialog, which) -> showCreateWorkoutScheduleDialog());
        builder.show();
    }
    
    /**
     * Dialog hiển thị danh sách meal schedules với dữ liệu thực tế
     */
    private void showMealScheduleListDialog() {
        // Lấy danh sách schedules từ ViewModel
        if (viewModel.getMealSchedules().getValue() == null || viewModel.getMealSchedules().getValue().isEmpty()) {
            Toast.makeText(this, "Chưa có lịch ăn nào. Hãy tạo lịch ăn mới!", Toast.LENGTH_SHORT).show();
            showCreateMealScheduleDialog();
            return;
        }
        
        // Tạo array với thông tin schedule để hiển thị
        java.util.List<com.example.heath_android.data.model.schedule.MealSchedulesResponse> schedules = 
            viewModel.getMealSchedules().getValue();
        
        String[] scheduleNames = new String[schedules.size()];
        for (int i = 0; i < schedules.size(); i++) {
            com.example.heath_android.data.model.schedule.MealSchedulesResponse schedule = schedules.get(i);
            scheduleNames[i] = "🍽️ " + schedule.getName() + 
                             "\n   " + schedule.getStartDate() + " → " + schedule.getEndDate() + 
                             "\n   " + schedule.getDescription();
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn lịch ăn uống");
        
        builder.setItems(scheduleNames, (dialog, which) -> {
            // User chọn một schedule
            com.example.heath_android.data.model.schedule.MealSchedulesResponse selectedSchedule = schedules.get(which);
            viewModel.selectMealSchedule(selectedSchedule);
            
            Toast.makeText(this, "Đã chọn lịch: " + selectedSchedule.getName(), Toast.LENGTH_SHORT).show();
            
            // Hiển thị menu cho schedule đã chọn
            showSelectedMealScheduleMenu(selectedSchedule);
        });
        
        builder.setNegativeButton("Quay lại", (dialog, which) -> showMealScheduleBrowseDialog());
        builder.setNeutralButton("Tạo mới", (dialog, which) -> showCreateMealScheduleDialog());
        builder.show();
    }
    
    /**
     * Menu cho workout schedule đã được chọn
     */
    private void showSelectedWorkoutScheduleMenu(com.example.heath_android.data.model.schedule.WorkoutSchedulesResponse schedule) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lịch tập: " + schedule.getName());
        
        String[] options = {
            "📅 Xem buổi tập đã lên lịch",
            "➕ Thêm buổi tập mới", 
            "✏️ Chỉnh sửa lịch tập",
            "🗑️ Xóa lịch tập",
            "📊 Thống kê"
        };
        
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    // Load và hiển thị scheduled workouts
                    viewModel.loadScheduledWorkoutsByScheduleId(schedule.getId());
                    Toast.makeText(this, "Đang tải danh sách buổi tập...", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    showCreateScheduledWorkoutDialog();
                    break;
                case 2:
                    showEditWorkoutScheduleDialog(schedule);
                    break;
                case 3:
                    showDeleteWorkoutScheduleDialog(schedule);
                    break;
                case 4:
                    Toast.makeText(this, "Tính năng thống kê đang phát triển", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
        
        builder.setNegativeButton("Quay lại", (dialog, which) -> showWorkoutScheduleListDialog());
        builder.show();
    }
    
    /**
     * Menu cho meal schedule đã được chọn
     */
    private void showSelectedMealScheduleMenu(com.example.heath_android.data.model.schedule.MealSchedulesResponse schedule) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lịch ăn: " + schedule.getName());
        
        String[] options = {
            "📅 Xem bữa ăn đã lên lịch",
            "➕ Thêm bữa ăn mới",
            "✏️ Chỉnh sửa lịch ăn", 
            "🗑️ Xóa lịch ăn",
            "📊 Thống kê dinh dưỡng"
        };
        
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    // Load và hiển thị scheduled meals
                    viewModel.loadScheduledMealsByScheduleId(schedule.getId());
                    Toast.makeText(this, "Đang tải danh sách bữa ăn...", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    showCreateScheduledMealDialog();
                    break;
                case 2:
                    showEditMealScheduleDialog(schedule);
                    break;
                case 3:
                    showDeleteMealScheduleDialog(schedule);
                    break;
                case 4:
                    Toast.makeText(this, "Tính năng thống kê đang phát triển", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
        
        builder.setNegativeButton("Quay lại", (dialog, which) -> showMealScheduleListDialog());
        builder.show();
    }
    
    // ==================== SCHEDULE EDIT/DELETE DIALOGS ====================
    
    /**
     * Dialog chỉnh sửa workout schedule
     */
    private void showEditWorkoutScheduleDialog(com.example.heath_android.data.model.schedule.WorkoutSchedulesResponse schedule) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_plan, null);
        
        EditText etPlanName = dialogView.findViewById(R.id.etPlanName);
        EditText etPlanDescription = dialogView.findViewById(R.id.etPlanDescription);
        EditText etUserId = dialogView.findViewById(R.id.etUserId);
        EditText etStartDate = dialogView.findViewById(R.id.etStartDate);
        EditText etEndDate = dialogView.findViewById(R.id.etEndDate);
        
        // Hide userId field
        etUserId.setVisibility(View.GONE);
        
        // Pre-fill với dữ liệu hiện tại
        etPlanName.setText(schedule.getName());
        etPlanDescription.setText(schedule.getDescription());
        etStartDate.setText(schedule.getStartDate());
        etEndDate.setText(schedule.getEndDate());
        
        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Chỉnh sửa lịch tập: " + schedule.getName())
                .setPositiveButton("Cập nhật", (dialog, which) -> {
                    String name = etPlanName.getText().toString().trim();
                    String description = etPlanDescription.getText().toString().trim();
                    String startDate = etStartDate.getText().toString().trim();
                    String endDate = etEndDate.getText().toString().trim();
                    
                    if (!name.isEmpty() && !startDate.isEmpty() && !endDate.isEmpty()) {
                        viewModel.updateWorkoutSchedule(schedule.getId(), name, description, startDate, endDate);
                        Toast.makeText(this, "Đang cập nhật lịch tập...", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    
    /**
     * Dialog chỉnh sửa meal schedule
     */
    private void showEditMealScheduleDialog(com.example.heath_android.data.model.schedule.MealSchedulesResponse schedule) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_plan, null);
        
        EditText etPlanName = dialogView.findViewById(R.id.etPlanName);
        EditText etPlanDescription = dialogView.findViewById(R.id.etPlanDescription);
        EditText etUserId = dialogView.findViewById(R.id.etUserId);
        EditText etStartDate = dialogView.findViewById(R.id.etStartDate);
        EditText etEndDate = dialogView.findViewById(R.id.etEndDate);
        
        // Hide userId field
        etUserId.setVisibility(View.GONE);
        
        // Pre-fill với dữ liệu hiện tại
        etPlanName.setText(schedule.getName());
        etPlanDescription.setText(schedule.getDescription());
        etStartDate.setText(schedule.getStartDate());
        etEndDate.setText(schedule.getEndDate());
        
        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Chỉnh sửa lịch ăn: " + schedule.getName())
                .setPositiveButton("Cập nhật", (dialog, which) -> {
                    String name = etPlanName.getText().toString().trim();
                    String description = etPlanDescription.getText().toString().trim();
                    String startDate = etStartDate.getText().toString().trim();
                    String endDate = etEndDate.getText().toString().trim();
                    
                    if (!name.isEmpty() && !startDate.isEmpty() && !endDate.isEmpty()) {
                        viewModel.updateMealSchedule(schedule.getId(), name, description, startDate, endDate);
                        Toast.makeText(this, "Đang cập nhật lịch ăn...", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    
    /**
     * Dialog xác nhận xóa workout schedule
     */
    private void showDeleteWorkoutScheduleDialog(com.example.heath_android.data.model.schedule.WorkoutSchedulesResponse schedule) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa lịch tập")
                .setMessage("Bạn có chắc chắn muốn xóa lịch tập \"" + schedule.getName() + "\"?\n\nTất cả buổi tập đã lên lịch trong lịch này cũng sẽ bị xóa!")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    viewModel.deleteWorkoutSchedule(schedule.getId());
                    Toast.makeText(this, "Đang xóa lịch tập...", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    
    /**
     * Dialog xác nhận xóa meal schedule
     */
    private void showDeleteMealScheduleDialog(com.example.heath_android.data.model.schedule.MealSchedulesResponse schedule) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa lịch ăn")
                .setMessage("Bạn có chắc chắn muốn xóa lịch ăn \"" + schedule.getName() + "\"?\n\nTất cả bữa ăn đã lên lịch trong lịch này cũng sẽ bị xóa!")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    viewModel.deleteMealSchedule(schedule.getId());
                    Toast.makeText(this, "Đang xóa lịch ăn...", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // ==================== NEW SCHEDULE CREATION DIALOGS ====================
    
    /**
     * Dialog tạo workout schedule mới
     */
    private void showCreateWorkoutScheduleDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_plan, null);
        
        EditText etPlanName = dialogView.findViewById(R.id.etPlanName);
        EditText etPlanDescription = dialogView.findViewById(R.id.etPlanDescription);
        EditText etUserId = dialogView.findViewById(R.id.etUserId);
        EditText etStartDate = dialogView.findViewById(R.id.etStartDate);
        EditText etEndDate = dialogView.findViewById(R.id.etEndDate);
        
        // Hide userId field since it's auto-filled from authentication
        etUserId.setVisibility(View.GONE);
        
        // Set hints for workout schedule
        etPlanName.setHint("Tên lịch tập (vd: Lịch tập Gym tháng 1)");
        etPlanDescription.setHint("Mô tả lịch tập");
        etStartDate.setHint("Ngày bắt đầu (yyyy-MM-dd)");
        etEndDate.setHint("Ngày kết thúc (yyyy-MM-dd)");
        
        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Tạo lịch tập luyện")
                .setPositiveButton("Tạo", (dialog, which) -> {
                    String name = etPlanName.getText().toString().trim();
                    String description = etPlanDescription.getText().toString().trim();
                    String startDate = etStartDate.getText().toString().trim();
                    String endDate = etEndDate.getText().toString().trim();
                    
                    if (!name.isEmpty() && !startDate.isEmpty() && !endDate.isEmpty()) {
                        viewModel.createWorkoutSchedule(name, description, startDate, endDate);
                        Toast.makeText(this, "Đang tạo lịch tập...", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Vui lòng điền đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    
    /**
     * Dialog tạo meal schedule mới
     */
    private void showCreateMealScheduleDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_plan, null);
        
        EditText etPlanName = dialogView.findViewById(R.id.etPlanName);
        EditText etPlanDescription = dialogView.findViewById(R.id.etPlanDescription);
        EditText etUserId = dialogView.findViewById(R.id.etUserId);
        EditText etStartDate = dialogView.findViewById(R.id.etStartDate);
        EditText etEndDate = dialogView.findViewById(R.id.etEndDate);
        
        // Hide userId field since it's auto-filled from authentication
        etUserId.setVisibility(View.GONE);
        
        // Set hints for meal schedule
        etPlanName.setHint("Tên lịch ăn (vd: Lịch ăn kiêng tháng 1)");
        etPlanDescription.setHint("Mô tả lịch ăn");
        etStartDate.setHint("Ngày bắt đầu (yyyy-MM-dd)");
        etEndDate.setHint("Ngày kết thúc (yyyy-MM-dd)");
        
        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Tạo lịch ăn uống")
                .setPositiveButton("Tạo", (dialog, which) -> {
                    String name = etPlanName.getText().toString().trim();
                    String description = etPlanDescription.getText().toString().trim();
                    String startDate = etStartDate.getText().toString().trim();
                    String endDate = etEndDate.getText().toString().trim();
                    
                    if (!name.isEmpty() && !startDate.isEmpty() && !endDate.isEmpty()) {
                        viewModel.createMealSchedule(name, description, startDate, endDate);
                        Toast.makeText(this, "Đang tạo lịch ăn...", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Vui lòng điền đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    
    /**
     * Dialog tạo scheduled workout (lên lịch buổi tập cụ thể)
     */
    private void showCreateScheduledWorkoutDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_schedule_session, null);
        
        EditText etWorkoutId = dialogView.findViewById(R.id.etWorkoutId);
        EditText etPlanId = dialogView.findViewById(R.id.etPlanId);
        EditText etStartTime = dialogView.findViewById(R.id.etStartTime);
        EditText etEndTime = dialogView.findViewById(R.id.etEndTime);
        EditText etStatus = dialogView.findViewById(R.id.etStatus);
        
        // Update hints for scheduled workout
        etWorkoutId.setHint("ID Bài tập (workout ID)");
        etPlanId.setHint("ID Lịch tập (schedule ID)");
        etStartTime.setHint("Thời gian (yyyy-MM-ddTHH:mm:ss)");
        etEndTime.setHint("Ghi chú");
        etStatus.setHint("Trạng thái (SCHEDULED/COMPLETED/SKIPPED)");
        
        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Lên lịch buổi tập")
                .setPositiveButton("Tạo", (dialog, which) -> {
                    String workoutId = etWorkoutId.getText().toString().trim();
                    String scheduleId = etPlanId.getText().toString().trim();
                    String scheduledDateTime = etStartTime.getText().toString().trim();
                    String notes = etEndTime.getText().toString().trim();
                    String status = etStatus.getText().toString().trim();
                    
                    if (!workoutId.isEmpty() && !scheduleId.isEmpty() && !scheduledDateTime.isEmpty()) {
                        try {
                            Long workoutIdLong = Long.parseLong(workoutId);
                            Long scheduleIdLong = Long.parseLong(scheduleId);
                            viewModel.createScheduledWorkout(scheduleIdLong, workoutIdLong, scheduledDateTime, 
                                                           status.isEmpty() ? "SCHEDULED" : status, notes);
                            Toast.makeText(this, "Đang lên lịch buổi tập...", Toast.LENGTH_SHORT).show();
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, "ID phải là số nguyên", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Vui lòng điền đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    
    /**
     * Dialog tạo scheduled meal (lên lịch bữa ăn cụ thể)
     */
    private void showCreateScheduledMealDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_schedule_session, null);
        
        EditText etMealId = dialogView.findViewById(R.id.etWorkoutId);
        EditText etScheduleId = dialogView.findViewById(R.id.etPlanId);
        EditText etScheduledDateTime = dialogView.findViewById(R.id.etStartTime);
        EditText etNotes = dialogView.findViewById(R.id.etEndTime);
        EditText etStatus = dialogView.findViewById(R.id.etStatus);
        
        // Update hints for scheduled meal
        etMealId.setHint("ID Thực đơn (meal ID)");
        etScheduleId.setHint("ID Lịch ăn (schedule ID)");
        etScheduledDateTime.setHint("Thời gian (yyyy-MM-ddTHH:mm:ss)");
        etNotes.setHint("Ghi chú");
        etStatus.setHint("Trạng thái (SCHEDULED/COMPLETED/SKIPPED)");
        
        // Pre-fill schedule ID if a meal schedule is selected
        if (viewModel.getSelectedMealSchedule().getValue() != null) {
            etScheduleId.setText(String.valueOf(viewModel.getSelectedMealSchedule().getValue().getId()));
        }
        
        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Lên lịch bữa ăn")
                .setPositiveButton("Tạo", (dialog, which) -> {
                    String mealId = etMealId.getText().toString().trim();
                    String scheduleId = etScheduleId.getText().toString().trim();
                    String scheduledDateTime = etScheduledDateTime.getText().toString().trim();
                    String notes = etNotes.getText().toString().trim();
                    String status = etStatus.getText().toString().trim();
                    
                    if (!mealId.isEmpty() && !scheduleId.isEmpty() && !scheduledDateTime.isEmpty()) {
                        try {
                            Long mealIdLong = Long.parseLong(mealId);
                            Long scheduleIdLong = Long.parseLong(scheduleId);
                            viewModel.createScheduledMeal(scheduleIdLong, mealIdLong, scheduledDateTime, 
                                                        status.isEmpty() ? "SCHEDULED" : status, notes);
                            Toast.makeText(this, "Đang lên lịch bữa ăn...", Toast.LENGTH_SHORT).show();
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, "ID phải là số nguyên", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Vui lòng điền đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    
    private void setupBottomNavigation() {
        if (bottomNavigation == null) {
            return;
        }
        
        // Set Schedule as selected (current activity)
        bottomNavigation.setSelectedItemId(R.id.nav_schedule);
        
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            
            if (itemId == R.id.nav_home) {
                // Navigate to Home
                navigateToHome();
                return true;
            } else if (itemId == R.id.nav_profile) {
                // Navigate to Profile
                navigateToProfile();
                return true;
            } else if (itemId == R.id.nav_schedule) {
                // Already on Schedule, do nothing
                return true;
            } else if (itemId == R.id.nav_logout) {
                Toast.makeText(this, "Logout feature coming soon!", Toast.LENGTH_SHORT).show();
                return false;
            }
            
            return false;
        });
    }
    
    private void navigateToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void navigateToProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
}