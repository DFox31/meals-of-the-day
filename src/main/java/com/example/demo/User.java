package com.example.demo;

import java.io.Serializable;

public class User  implements Serializable {
    private String name;
    private DailyNorm dailyNorm;

    public User(String name, DailyNorm dailyNorm){
        this.name = name;
        this.dailyNorm = dailyNorm;
    }
    public String getName(){
        return name;
    }
    public DailyNorm getDailyNorm() {
        return dailyNorm;
    }
    public void setDailyNorm(DailyNorm dailyNorm) {
        this.dailyNorm = dailyNorm;
    }
    @Override
    public String toString() {
        return name;
    }
}
