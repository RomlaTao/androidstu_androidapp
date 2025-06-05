package com.example.heath_android.ui.schedule.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heath_android.R;

import java.util.ArrayList;
import java.util.List;

public class WeekDayAdapter extends RecyclerView.Adapter<WeekDayAdapter.WeekDayViewHolder> {
    
    private List<String> weekDays = new ArrayList<>();
    private String selectedDate;
    private OnDayClickListener onDayClickListener;
    
    // Day names for header
    private String[] dayNames = {"T2", "T3", "T4", "T5", "T6", "T7", "CN"};
    
    public interface OnDayClickListener {
        void onDayClick(int position, String day);
    }
    
    public void setOnDayClickListener(OnDayClickListener listener) {
        this.onDayClickListener = listener;
    }
    
    public void updateDays(List<String> newDays) {
        this.weekDays.clear();
        this.weekDays.addAll(newDays);
        notifyDataSetChanged();
    }
    
    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public WeekDayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_week_day, parent, false);
        return new WeekDayViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull WeekDayViewHolder holder, int position) {
        String day = weekDays.get(position);
        String dayName = position < dayNames.length ? dayNames[position] : "";
        
        holder.tvDayName.setText(dayName);
        holder.tvDayNumber.setText(day);
        
        // Highlight selected day
        boolean isSelected = isSelectedDay(day, position);
        if (isSelected) {
            holder.layoutDay.setBackgroundResource(R.drawable.bg_day_selected);
        } else {
            holder.layoutDay.setBackgroundResource(R.drawable.bg_day_selector);
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (onDayClickListener != null) {
                onDayClickListener.onDayClick(position, day);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return weekDays.size();
    }
    
    private boolean isSelectedDay(String day, int position) {
        if (selectedDate == null) return false;
        
        try {
            // Parse selectedDate to get the day of month
            String[] dateParts = selectedDate.split("-");
            if (dateParts.length == 3) {
                int selectedDay = Integer.parseInt(dateParts[2]);
                int currentDay = Integer.parseInt(day);
                return selectedDay == currentDay;
            }
        } catch (NumberFormatException e) {
            // Fallback to simple string matching
            return selectedDate.endsWith(String.format("%02d", Integer.parseInt(day)));
        }
        return false;
    }
    
    static class WeekDayViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutDay;
        TextView tvDayName;
        TextView tvDayNumber;
        
        public WeekDayViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutDay = itemView.findViewById(R.id.layoutDay);
            tvDayName = itemView.findViewById(R.id.tvDayName);
            tvDayNumber = itemView.findViewById(R.id.tvDayNumber);
        }
    }
} 