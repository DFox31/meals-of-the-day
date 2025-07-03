package com.example.demo;

import java.io.Serializable;

public class DailyNorm implements Serializable {
    private int norm;

    public DailyNorm(int norm) {
        this.norm = norm;
    }

    public int getNorm() {
        return norm;
    }
}