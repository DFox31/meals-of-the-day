package com.example.demo;

import java.io.Serializable;

public class Product  implements Serializable {
    public String ccal;
    public String prots;
    public String fats;
    public String carbs;
    public String name;

    public Product(String name, String ccal, String prots, String fats, String carbs) {
        this.ccal = ccal;
        this.prots = prots;
        this.fats = fats;
        this.carbs = carbs;
        this.name = name;
    }
    public String getName() {
        return name;

    }
    public String getCcal() {
        return ccal;
    }
    public String getProts() {
        return prots;
    }
    public String getFats() {
        return fats;
    }
    public String getCarbs() {
        return carbs;
    }
    @Override
    public String toString() {
        return name;
    }

}