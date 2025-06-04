package com.example.heath_android.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.google.android.material.card.MaterialCardView;

import com.example.heath_android.databinding.CardStatBinding;

public class StatCardView extends MaterialCardView {

    private CardStatBinding binding;

    public StatCardView(Context context) {
        super(context);
        init(context);
    }

    public StatCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StatCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        binding = CardStatBinding.inflate(LayoutInflater.from(context), this, true);
        setRadius(16f);
        setCardElevation(4f);
    }

    public void setData(String label, String value, String unit) {
        binding.tvLabel.setText(label);
        binding.tvValue.setText(value);
        binding.tvUnit.setText(unit);
    }
}