package com.example.heath_android.ui.schedule.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heath_android.R;
import com.example.heath_android.data.model.schedule.DayEvent;

import java.util.ArrayList;
import java.util.List;

public class DayContentAdapter extends RecyclerView.Adapter<DayContentAdapter.DayContentViewHolder> {
    
    private List<DayEvent> events = new ArrayList<>();
    private OnEventClickListener onEventClickListener;
    private OnEventLongClickListener onEventLongClickListener;
    
    public interface OnEventClickListener {
        void onEventClick(DayEvent event);
    }
    
    public interface OnEventLongClickListener {
        boolean onEventLongClick(DayEvent event);
    }
    
    public void setOnEventClickListener(OnEventClickListener listener) {
        this.onEventClickListener = listener;
    }
    
    public void setOnEventLongClickListener(OnEventLongClickListener listener) {
        this.onEventLongClickListener = listener;
    }
    
    public void updateEvents(List<DayEvent> newEvents) {
        this.events.clear();
        if (newEvents != null) {
            this.events.addAll(newEvents);
        }
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public DayContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_day_content, parent, false);
        return new DayContentViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull DayContentViewHolder holder, int position) {
        DayEvent event = events.get(position);
        
        // Bind all fields from layout
        holder.tvTime.setText(event.getTime() != null ? event.getTime() : "");
        holder.tvType.setText(event.getType() != null ? event.getType() : "Event");
        holder.tvEventName.setText(event.getName() != null ? event.getName() : "Unnamed Event");
        holder.tvDescription.setText(event.getDescription() != null ? event.getDescription() : "No description");
        
        // Format calories display
        String caloriesText = event.getCalories() > 0 ? event.getCalories() + " kcal" : "N/A";
        holder.tvCalories.setText(caloriesText);
        
        // Status display
        String status = event.getStatus();
        if (status != null && !status.isEmpty()) {
            holder.tvStatus.setVisibility(View.VISIBLE);
            switch (status.toUpperCase()) {
                case "COMPLETED":
                    holder.tvStatus.setText("Đã hoàn thành");
                    holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(android.R.color.holo_green_dark));
                    break;
                case "SKIPPED":
                    holder.tvStatus.setText("Đã bỏ qua");
                    holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(android.R.color.holo_red_dark));
                    break;
                case "SCHEDULED":
                default:
                    holder.tvStatus.setText("Đã lên lịch");
                    holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(android.R.color.holo_orange_dark));
                    break;
            }
        } else {
            holder.tvStatus.setVisibility(View.GONE);
        }
        
        // Set click listeners
        holder.itemView.setOnClickListener(v -> {
            if (onEventClickListener != null) {
                onEventClickListener.onEventClick(event);
            }
        });
        
        holder.itemView.setOnLongClickListener(v -> {
            if (onEventLongClickListener != null) {
                return onEventLongClickListener.onEventLongClick(event);
            }
            return false;
        });
        
        // Action button click
        holder.btnEventAction.setOnClickListener(v -> {
            if (onEventClickListener != null) {
                onEventClickListener.onEventClick(event);
            }
        });
        
        // Style based on event type
        setEventTypeStyle(holder, event.getType());
    }
    
    @Override
    public int getItemCount() {
        return events.size();
    }
    
    private void setEventTypeStyle(DayContentViewHolder holder, String type) {
        // Customize appearance based on event type
        if (type != null) {
            switch (type.toLowerCase()) {
                case "lunch":
                case "dinner":
                case "breakfast":
                case "snack":
                case "meal":
                    // Meal events - green theme
                    holder.tvType.setBackgroundColor(holder.itemView.getContext().getColor(android.R.color.holo_green_dark));
                    holder.tvType.setTextColor(holder.itemView.getContext().getColor(android.R.color.white));
                    break;
                case "workout":
                case "exercise":
                    // Workout events - blue theme
                    holder.tvType.setBackgroundColor(holder.itemView.getContext().getColor(android.R.color.holo_blue_dark));
                    holder.tvType.setTextColor(holder.itemView.getContext().getColor(android.R.color.white));
                    break;
                default:
                    // Default events - gray theme
                    holder.tvType.setBackgroundColor(holder.itemView.getContext().getColor(android.R.color.darker_gray));
                    holder.tvType.setTextColor(holder.itemView.getContext().getColor(android.R.color.white));
                    break;
            }
        }
    }
    
    static class DayContentViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime;
        TextView tvType;
        TextView tvEventName;
        TextView tvDescription;
        TextView tvCalories;
        TextView tvStatus;
        ImageButton btnEventAction;
        
        public DayContentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvType = itemView.findViewById(R.id.tvType);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvCalories = itemView.findViewById(R.id.tvCalories);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnEventAction = itemView.findViewById(R.id.btnEventAction);
        }
    }
} 