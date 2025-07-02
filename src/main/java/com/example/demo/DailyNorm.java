package com.example.demo;

import java.io.Serializable;

public class DailyNorm implements Serializable {
    public int dailyNorm;
    public DailyNorm (int dailyNorm){
        this.dailyNorm = dailyNorm;
    }
    public int getDailyNorm(){
        return dailyNorm;
    }
}