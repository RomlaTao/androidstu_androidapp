package com.example.heath_android.data.model.home;

public class BMIBMRResponse {
    private double bmi;
    private double bmr;

    public BMIBMRResponse() {}

    public BMIBMRResponse(double bmi, double bmr) {
        this.bmi = bmi;
        this.bmr = bmr;
    }

    // Getters and Setters
    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }

    public double getBmr() {
        return bmr;
    }

    public void setBmr(double bmr) {
        this.bmr = bmr;
    }
} 