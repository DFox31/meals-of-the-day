package com.example.demo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProductListModel implements Serializable {
    private List<Product> products = new ArrayList<>();

    public void addProduct(Product product){
        products.add(product);
    }
    public List<Product> getProducts() {
        return products;
    }

    public void saveToFile(String filename) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(this);
        }
    }

    public static ProductListModel loadFromFile(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (ProductListModel) in.readObject();
        }
    }
}
