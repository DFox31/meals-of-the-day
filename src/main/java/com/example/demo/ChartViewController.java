package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ChartViewController {

    @FXML private BarChart<String, Number> chart;
    @FXML private CheckBox byDateCheckBox;

    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        updateChart();
    }

    @FXML
    public void updateChart() {
        if (mainController == null) return;

        chart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Потребление калорий");

        boolean byDate = byDateCheckBox.isSelected();
        Map<String, Double> grouped = mainController.getProductList().stream()
                .sorted(Comparator.comparing(Product::getDate))
                .collect(Collectors.toMap(
                        p -> byDate ? p.getDate().format(DateTimeFormatter.ofPattern("dd.MM")) : p.getName(),
                        p -> Double.parseDouble(p.getCcal().replace(",", ".")),
                        Double::sum,
                        LinkedHashMap::new
                ));

        for (Map.Entry<String, Double> entry : grouped.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        chart.getData().add(series);
    }
}