package com.example.heath_android.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import com.example.heath_android.R;
import com.example.heath_android.data.model.home.BMIBMRResponse;
import com.example.heath_android.data.model.home.CaloriesResponse;
import com.example.heath_android.data.model.home.CaloriesInWeekly;
import com.example.heath_android.data.model.home.CaloriesOutWeekly;
import com.example.heath_android.data.model.home.HomeResponse;
import com.example.heath_android.data.model.home.TDEEResponse;
import com.example.heath_android.ui.profile.ProfileActivity;
import com.example.heath_android.ui.schedule.ScheduleActivity;
import com.example.heath_android.util.StatCardView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private HomeViewModel viewModel;
    private ProgressBar progressBar;
    
    // UI Components for health stats
    private TextView tvUserName;
    private StatCardView bmiCardView;
    private StatCardView bmrCardView;
    private StatCardView tdeeCardView;
    private StatCardView caloriesCardView;
    
    // Chart components
    private Spinner spinnerChartType;
    private BarChart barChart;
    private PieChart pieChart;
    
    // Navigation
    private BottomNavigationView bottomNavigation;
    
    // Chart types
    private static final String CHART_TYPE_BAR = "Biểu đồ cột";
    private static final String CHART_TYPE_PIE = "Biểu đồ tròn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        
        // Apply window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.healthStatsContainer), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViewModel();
        initViews();
        setupBottomNavigation();
        observeViewModel();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(HomeViewModel.class);
    }

    private void initViews() {
        // Find progress bar (you may need to add this to your layout)
//        progressBar = findViewById(R.id.progressBar);
//        if (progressBar == null) {
//            Log.w(TAG, "ProgressBar not found in layout - consider adding one for better UX");
//        }
        
        // Find StatCardViews for health stats
        findHealthStatViews();
        
        // Initialize StatCardViews with default labels
        initializeStatCards();

        // Initialize charts
        initializeChart();
        
        // Initialize bottom navigation
        bottomNavigation = findViewById(R.id.bottom_navigation);
    }

    private void setupBottomNavigation() {
        if (bottomNavigation == null) {
            Log.w(TAG, "BottomNavigationView not found");
            return;
        }
        
        // Set Home as selected (current activity)
        bottomNavigation.setSelectedItemId(R.id.nav_home);
        
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            
            if (itemId == R.id.nav_home) {
                // Already on Home, do nothing
                return true;
            } else if (itemId == R.id.nav_profile) {
                // Navigate to Profile
                navigateToProfile();
                return true;
            } else if (itemId == R.id.nav_schedule) {
                // Navigate to Workout (when implemented)
                navigateToSchedule();
                return false;
            } else if (itemId == R.id.nav_logout) {
                Log.d(TAG, "Logout");
                Toast.makeText(this, "Settings feature coming soon!", Toast.LENGTH_SHORT).show();
                return false;
            }
            
            return false;
        });
        
        Log.d(TAG, "Bottom navigation setup completed");
    }
    
    private void navigateToProfile() {
        Log.d(TAG, "Navigating to Profile Activity");
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private void navigateToSchedule() {
        Log.d(TAG, "Navigating to Schedule Activity");
        Intent intent = new Intent(this, ScheduleActivity.class);
        startActivity(intent);
    }

    private void findHealthStatViews() {
        try {
            bmiCardView = findViewById(R.id.stat_card_bmi);
            bmrCardView = findViewById(R.id.stat_card_bmr);
            tdeeCardView = findViewById(R.id.stat_card_tdee);
            caloriesCardView = findViewById(R.id.stat_card_calories);
            
            if (bmiCardView != null && bmrCardView != null && tdeeCardView != null && caloriesCardView != null) {
                Log.d(TAG, "All StatCardViews found successfully");
            } else {
                Log.w(TAG, "Some StatCardViews not found - please check layout IDs");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error finding StatCardViews", e);
        }
    }
    
    private void initializeStatCards() {
        // Initialize cards with default labels and loading values
        if (bmiCardView != null) {
            bmiCardView.setData("BMI", "--", "Loading...");
        }
        if (bmrCardView != null) {
            bmrCardView.setData("BMR", "--", "cal");
        }
        if (tdeeCardView != null) {
            tdeeCardView.setData("TDEE", "--", "cal");
        }
        if (caloriesCardView != null) {
            caloriesCardView.setData("Calories Burned", "--", "cal");
        }
    }

    private void initializeChart() {
        // Find chart components
        spinnerChartType = findViewById(R.id.spinner_chart_type);
        barChart = findViewById(R.id.barChart);
        pieChart = findViewById(R.id.pieChart);
        
        // Setup spinner
        setupChartTypeSpinner();
        
        // Setup charts
        setupBarChart();
        setupPieChart();
    }
    
    private void setupChartTypeSpinner() {
        if (spinnerChartType == null) {
            Log.w(TAG, "Chart type spinner not found");
            return;
        }
        
        String[] chartTypes = {CHART_TYPE_BAR, CHART_TYPE_PIE};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, chartTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChartType.setAdapter(adapter);
        
        spinnerChartType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = chartTypes[position];
                switchChartType(selectedType);
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }
    
    private void switchChartType(String chartType) {
        if (barChart == null || pieChart == null) return;
        
        if (CHART_TYPE_BAR.equals(chartType)) {
            barChart.setVisibility(View.VISIBLE);
            pieChart.setVisibility(View.GONE);
            Log.d(TAG, "Switched to Bar Chart");
        } else if (CHART_TYPE_PIE.equals(chartType)) {
            barChart.setVisibility(View.GONE);
            pieChart.setVisibility(View.VISIBLE);
            Log.d(TAG, "Switched to Pie Chart");
        }
    }
    
    private void setupBarChart() {
        if (barChart == null) {
            Log.w(TAG, "BarChart not found");
            return;
        }
        
        // Configure bar chart
        barChart.getDescription().setEnabled(false);
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);
        
        // Configure X axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        
        // Configure Y axis
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f);
        
        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);
        
        // Configure legend
        Legend legend = barChart.getLegend();
        legend.setEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
    }
    
    private void setupPieChart() {
        if (pieChart == null) {
            Log.w(TAG, "PieChart not found");
            return;
        }
        
        // Configure pie chart
        pieChart.getDescription().setEnabled(false);
        pieChart.setTouchEnabled(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        
        // Configure legend
        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
    }

    private void observeViewModel() {
        // Observe user info from local data
        viewModel.getHomeData().observe(this, this::updateUserInfo);

        // Observe loading state
        viewModel.getLoading().observe(this, isLoading -> {
            if (isLoading != null) {
                Log.d(TAG, "Loading state: " + isLoading);
                if (progressBar != null) {
                    progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                }
            }
        });

        // Observe errors
        viewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Log.e(TAG, "Error observed: " + error);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                viewModel.clearError();
            }
        });

        // Observe specific health data from different endpoints
        observeHealthData();
        
        // Observe chart data
        observeChartData();
    }

    private void observeHealthData() {
        // Observe BMI/BMR data from getBMIBMRData endpoint
        viewModel.getBmiBmrData().observe(this, this::updateBMIBMRCards);
        
        // Observe TDEE data from getTDEEData endpoint
        viewModel.getTdeeData().observe(this, this::updateTDEECard);
        
        // Observe Calories data from getCaloriesData endpoint
        viewModel.getCaloriesData().observe(this, this::updateCaloriesCard);
    }

    private void observeChartData() {
        // Observe weekly calories in data
        viewModel.getCaloriesInWeeklyData().observe(this, this::updateChartsWithData);
        
        // Observe weekly calories out data (will trigger chart update when both are available)
        viewModel.getCaloriesOutWeeklyData().observe(this, caloriesOutData -> {
            // Check if both in and out data are available
            if (viewModel.hasChartData()) {
                updateChartsWithData(viewModel.getCaloriesInWeeklyData().getValue());
            }
        });
    }
    
    private void updateChartsWithData(List<CaloriesInWeekly> caloriesInData) {
        List<CaloriesOutWeekly> caloriesOutData = viewModel.getCaloriesOutWeeklyData().getValue();
        
        if (caloriesInData == null || caloriesOutData == null) {
            Log.w(TAG, "Chart data not complete yet - In: " + (caloriesInData != null ? "available" : "null") + 
                ", Out: " + (caloriesOutData != null ? "available" : "null"));
            return;
        }
        
        if (caloriesInData.isEmpty() || caloriesOutData.isEmpty()) {
            Log.w(TAG, "Chart data lists are empty - In size: " + caloriesInData.size() + 
                ", Out size: " + caloriesOutData.size());
            return;
        }
        
        Log.d(TAG, "Updating charts with calories in/out weekly data");
        
        // Update bar chart
        updateBarChart(caloriesInData, caloriesOutData);
        
        // Update pie chart
        updatePieChart(caloriesInData, caloriesOutData);
    }
    
    private void updateBarChart(List<CaloriesInWeekly> caloriesInData, List<CaloriesOutWeekly> caloriesOutData) {
        if (barChart == null) return;
        
        Log.d(TAG, "updateBarChart called with:");
        Log.d(TAG, "  caloriesInData size: " + (caloriesInData != null ? caloriesInData.size() : "null"));
        Log.d(TAG, "  caloriesOutData size: " + (caloriesOutData != null ? caloriesOutData.size() : "null"));
        
        List<BarEntry> entriesIn = new ArrayList<>();
        List<BarEntry> entriesOut = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        
        if (caloriesInData != null && caloriesOutData != null) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
            
            for (int i = 0; i < Math.min(caloriesInData.size(), caloriesOutData.size()); i++) {
                double caloriesIn = caloriesInData.get(i).getTotalCaloriesIn();
                double caloriesOut = caloriesOutData.get(i).getTotalCaloriesOut();
                String dateStr = caloriesInData.get(i).getDate();
                
                // Format date to be more user-friendly (dd/MM instead of yyyy-MM-dd)
                String formattedDate = dateStr;
                try {
                    if (dateStr != null && dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                        formattedDate = outputFormat.format(inputFormat.parse(dateStr));
                    }
                } catch (ParseException e) {
                    Log.w(TAG, "Could not parse date: " + dateStr, e);
                    // Use original date if parsing fails
                    formattedDate = dateStr;
                }
                
                Log.d(TAG, String.format("  Day %d: date=%s (formatted: %s), in=%.1f, out=%.1f", 
                    i, dateStr, formattedDate, caloriesIn, caloriesOut));
                
                entriesIn.add(new BarEntry(i, (float) caloriesIn));
                entriesOut.add(new BarEntry(i, (float) caloriesOut));
                labels.add(formattedDate);
            }
        } else {
            Log.w(TAG, "One or both chart data lists are null, cannot update bar chart");
            return;
        }
        
        Log.d(TAG, String.format("Creating bar chart with %d entries in, %d entries out, %d labels", 
            entriesIn.size(), entriesOut.size(), labels.size()));
        
        // Create data sets
        BarDataSet dataSetIn = new BarDataSet(entriesIn, "Calories In");
        dataSetIn.setColor(Color.parseColor("#4CAF50")); // Green
        dataSetIn.setValueTextSize(10f);
        
        BarDataSet dataSetOut = new BarDataSet(entriesOut, "Calories Out");
        dataSetOut.setColor(Color.parseColor("#FF5722")); // Red
        dataSetOut.setValueTextSize(10f);
        
        // Create bar data
        BarData barData = new BarData(dataSetIn, dataSetOut);
        barData.setBarWidth(0.35f);
        
        // Set data to chart
        barChart.setData(barData);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.getXAxis().setLabelCount(labels.size());
        barChart.getXAxis().setLabelRotationAngle(-45f); // Rotate labels for better readability
        barChart.groupBars(0f, 0.3f, 0f);
        barChart.invalidate();
        
        Log.d(TAG, "Bar chart updated with " + labels.size() + " days of data");
    }
    
    private void updatePieChart(List<CaloriesInWeekly> caloriesInData, List<CaloriesOutWeekly> caloriesOutData) {
        if (pieChart == null) return;
        
        Log.d(TAG, "updatePieChart called");
        
        // Calculate total calories in and out for the week
        double totalIn = 0;
        double totalOut = 0;
        String dateRange = "";
        
        if (caloriesInData != null && !caloriesInData.isEmpty()) {
            for (CaloriesInWeekly day : caloriesInData) {
                totalIn += day.getTotalCaloriesIn();
            }
            
            // Create date range string for center text
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
            
            try {
                String firstDate = caloriesInData.get(0).getDate();
                String lastDate = caloriesInData.get(caloriesInData.size() - 1).getDate();
                
                if (firstDate != null && lastDate != null) {
                    String formattedFirst = outputFormat.format(inputFormat.parse(firstDate));
                    String formattedLast = outputFormat.format(inputFormat.parse(lastDate));
                    dateRange = formattedFirst + " - " + formattedLast;
                }
            } catch (ParseException e) {
                Log.w(TAG, "Could not parse dates for pie chart center text", e);
                dateRange = "7 ngày";
            }
        }
        
        if (caloriesOutData != null) {
            for (CaloriesOutWeekly day : caloriesOutData) {
                totalOut += day.getTotalCaloriesOut();
            }
        }
        
        Log.d(TAG, String.format("Pie chart totals - In: %.1f, Out: %.1f, Date range: %s", 
            totalIn, totalOut, dateRange));
        
        // Create pie entries
        List<PieEntry> entries = new ArrayList<>();
        
        // Only add entries if there are non-zero values
        if (totalIn > 0) {
            entries.add(new PieEntry((float) totalIn, "Calories In"));
        }
        if (totalOut > 0) {
            entries.add(new PieEntry((float) totalOut, "Calories Out"));
        }
        
        // If both are zero, add small placeholder values to show the legend
        if (entries.isEmpty()) {
            entries.add(new PieEntry(1f, "Calories In"));
            entries.add(new PieEntry(1f, "Calories Out"));
            Log.d(TAG, "Added placeholder entries for pie chart");
        }
        
        // Create pie data set
        PieDataSet dataSet = new PieDataSet(entries, "Weekly Calories");
        dataSet.setColors(Color.parseColor("#4CAF50"), Color.parseColor("#FF5722"));
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setSliceSpace(2f);
        
        // Create pie data
        PieData pieData = new PieData(dataSet);
        
        // Set data to chart
        pieChart.setData(pieData);
        pieChart.setCenterText(dateRange.isEmpty() ? "Weekly\nCalories" : dateRange + "\nCalories");
        pieChart.invalidate();
        
        Log.d(TAG, String.format("Pie chart updated - Total In: %.0f, Total Out: %.0f", totalIn, totalOut));
    }

    private void updateBMIBMRCards(BMIBMRResponse bmiBmrData) {
        if (bmiBmrData == null) {
            Log.w(TAG, "BMI/BMR data is null");
            return;
        }

        Log.d(TAG, String.format("Updating BMI/BMR Cards - BMI: %.1f, BMR: %.0f", 
            bmiBmrData.getBmi(), bmiBmrData.getBmr()));

        // Update BMI Card
        if (bmiCardView != null) {
            String bmiValue = viewModel.formatBMI(bmiBmrData.getBmi());
            String bmiCategory = viewModel.getBMICategory(bmiBmrData.getBmi());
            bmiCardView.setData("BMI", bmiValue, bmiCategory);
            Log.d(TAG, "BMI Card updated: " + bmiValue + " (" + bmiCategory + ")");
        } else {
            Log.d(TAG, "BMI data from getBMIBMRData: " + bmiBmrData.getBmi());
        }

        // Update BMR Card
        if (bmrCardView != null) {
            String bmrValue = viewModel.formatBMR(bmiBmrData.getBmr());
            bmrCardView.setData("BMR", bmrValue, "cal");
            Log.d(TAG, "BMR Card updated: " + bmrValue + " cal");
        } else {
            Log.d(TAG, "BMR data from getBMIBMRData: " + bmiBmrData.getBmr() + " cal");
        }
    }

    private void updateTDEECard(TDEEResponse tdeeData) {
        if (tdeeData == null) {
            Log.w(TAG, "TDEE data is null");
            return;
        }

        Log.d(TAG, String.format("Updating TDEE Card - TDEE: %.0f", tdeeData.getTdee()));

        // Update TDEE Card
        if (tdeeCardView != null) {
            String tdeeValue = viewModel.formatTDEE(tdeeData.getTdee());
            tdeeCardView.setData("TDEE", tdeeValue, "cal");
            Log.d(TAG, "TDEE Card updated: " + tdeeValue + " cal");
        } else {
            Log.d(TAG, "TDEE data from getTDEEData: " + tdeeData.getTdee() + " cal");
        }
    }

    private void updateCaloriesCard(CaloriesResponse caloriesData) {
        if (caloriesData == null) {
            Log.w(TAG, "Calories data is null");
            return;
        }

        Log.d(TAG, String.format("Updating Calories Card - Total Calories Burned: %.0f", 
            caloriesData.getTotalCaloriesBurned()));

        // Update Calories Card
        if (caloriesCardView != null) {
            String caloriesValue = viewModel.formatCalories(caloriesData.getTotalCaloriesBurned());
            caloriesCardView.setData("Calories Burned", caloriesValue, "cal");
            Log.d(TAG, "Calories Card updated: " + caloriesValue + " cal");
        } else {
            Log.d(TAG, "Calories data from getCaloriesData: " + caloriesData.getTotalCaloriesBurned() + " cal burned");
        }
    }

    // Handle user info from local data
    private void updateUserInfo(HomeResponse homeData) {
        if (homeData == null) {
            Log.w(TAG, "HomeData is null");
            return;
        }

        Log.d(TAG, "Updating UI with user info: " + homeData.getMessage());

        // Update user info
        if (homeData.getUserInfo() != null) {
            Log.d(TAG, "User name: " + homeData.getUserInfo().getName());
            if (tvUserName != null) {
                tvUserName.setText(homeData.getUserInfo().getName());
            } else {
                Log.d(TAG, "User info updated: " + homeData.getUserInfo().getName());
            }
        }

        // Note: Health stats are handled by specific endpoint observers
        // Chart data will be handled by separate services later
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume - refreshing data");
        // Refresh both user info and health data
        viewModel.refreshData();
    }

    // Optional: Method to refresh only health data for real-time updates
    public void refreshHealthData() {
        Log.d(TAG, "Refreshing only health data");
        viewModel.refreshHealthData();
    }
    
    // Optional: Method to refresh only chart data
    public void refreshChartData() {
        Log.d(TAG, "Refreshing only chart data");
        viewModel.refreshChartData();
    }
    
    // Legacy method for compatibility
    public void refreshCardData() {
        refreshHealthData();
    }

    // Method to manually refresh chart data
    public void refreshChartsOnly() {
        Log.d(TAG, "Manually refreshing charts");
        viewModel.refreshChartData();
    }
}