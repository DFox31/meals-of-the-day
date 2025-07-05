package com.example.demo;


import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MainController {
    @FXML private TextField calDField;
    @FXML private Button addNorm;
    @FXML private Button addProd;
    @FXML private ChoiceBox<Product> chooseProd;
    @FXML private DatePicker datePicker;
    @FXML private TextField weightFullField;
    @FXML private Button addAll;

    @FXML private TableView<Product> table;
    @FXML private TableColumn<Product, String> dateCol;
    @FXML private TableColumn<Product, String> nameCol;
    @FXML private TableColumn<Product, String> calCol;
    @FXML private TableColumn<Product, String> protCol;
    @FXML private TableColumn<Product, String> fatCol;
    @FXML private TableColumn<Product, String> carbCol;
    @FXML private BarChart<String, Number> grafik;

    private final ObservableList<Product> productList = FXCollections.observableArrayList();
    private ProductListModel model = new ProductListModel();
    private DailyNorm dailyNorm;

    private final String fixedColor = "#FFA500";

    @FXML
    public void initialize() {
        dateCol.setCellValueFactory(cd -> new SimpleObjectProperty<>(
                cd.getValue().getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
        nameCol.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getName()));
        calCol.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getCcal()));
        protCol.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getProts()));
        fatCol.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getFats()));
        carbCol.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getCarbs()));
        table.setItems(productList);

        setupChart();
        productList.addListener((ListChangeListener<Product>) _ -> updateChart(true));
        loadProducts();
    }

    private void setupChart() {
        grafik.setTitle("Потребление каллорий по дням");
        CategoryAxis xAxis = (CategoryAxis) grafik.getXAxis();
        xAxis.setAutoRanging(true);
        xAxis.setAnimated(false);

        NumberAxis yAxis = (NumberAxis) grafik.getYAxis();
        yAxis.setLabel("Калории");
        yAxis.setForceZeroInRange(true);
    }

    @FXML
    public void onaddNormClick() {
        try {
            int norm = Integer.parseInt(calDField.getText().trim().replace(",", "."));
            dailyNorm = new DailyNorm(norm);
            model.setDailyNorm(dailyNorm);
            showAlert(Alert.AlertType.INFORMATION, "Успех", "Норма установлена: " + norm + " ккал");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Введите корректное число калорий");
        }
    }

    @FXML
    public void onaddProdClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("add-product-view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Добавление продукта");
            stage.setScene(new Scene(root));



            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось открыть окно добавления продукта");
        }
    }
    public void addReferenceProduct(String name, String calories, String proteins, String fats, String carbs) {
        Product newProd = new Product(name, calories, proteins, fats, carbs, LocalDate.now());
        chooseProd.getItems().add(newProd);
        model.addReferenceProduct(newProd);
        showAlert(Alert.AlertType.INFORMATION, "Успех", "Продукт добавлен: " + name);
    }

    @FXML
    public void onaddAllClick() {
        Product sel = chooseProd.getValue();
        LocalDate date = datePicker.getValue();
        String weightText = weightFullField.getText().trim().replace(",", ".");
        if (sel == null || date == null || weightText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Предупреждение", "Выберите продукт, дату и введите вес");
            return;
        }
        try {
            double weight = Double.parseDouble(weightText);
            double cal = Double.parseDouble(sel.getCcal().replace(",", ".")) * weight / 100;
            double prot = Double.parseDouble(sel.getProts().replace(",", ".")) * weight / 100;
            double fat = Double.parseDouble(sel.getFats().replace(",", ".")) * weight / 100;
            double carb = Double.parseDouble(sel.getCarbs().replace(",", ".")) * weight / 100;

            Product wp = new Product(sel.getName(), Double.toString(cal), Double.toString(prot), Double.toString(fat), Double.toString(carb), date);
            productList.add(wp);
            model.addProduct(wp);

            checkDailyNorm(date);

            weightFullField.clear();
            showAlert(Alert.AlertType.INFORMATION, "Успех", "Запись добавлена");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Введите корректный вес");
        }
    }

    private void checkDailyNorm(LocalDate date) {
        if (dailyNorm == null) return;

        double total = productList.stream()
                .filter(p -> p.getDate().equals(date))
                .mapToDouble(p -> Double.parseDouble(p.getCcal().replace(",", ".")))
                .sum();

        if (total >= dailyNorm.getNorm()) {
            showAlert(Alert.AlertType.INFORMATION, "Норма достигнута", "Поздравляем! Вы достигли дневной нормы: " + dailyNorm.getNorm() + " ккал");
        }
    }

    private void updateChart(boolean byDate) {
        grafik.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        Map<String, Double> grouped = productList.stream()
                .sorted(Comparator.comparing(Product::getDate))
                .collect(Collectors.toMap(
                        p -> byDate ? p.getDate().format(DateTimeFormatter.ofPattern("dd.MM")) : p.getName(),
                        p -> Double.parseDouble(p.getCcal().replace(",", ".")),
                        Double::sum,
                        LinkedHashMap::new
                ));

        for (Map.Entry<String, Double> entry : grouped.entrySet()) {
            XYChart.Data<String, Number> data = new XYChart.Data<>(entry.getKey(), entry.getValue());
            series.getData().add(data);

            final String valueStr = String.valueOf(Math.round(entry.getValue()));
            data.nodeProperty().addListener((_, _, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle("-fx-bar-fill: " + fixedColor + ";");
                    Text label = new Text(valueStr);
                    label.setFill(Color.BLACK);
                    StackPane stackPane = (StackPane) newNode;
                    stackPane.getChildren().add(label);
                }
            });
        }

        grafik.getData().add(series);



    }


    @FXML
    public void onSaveClick() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("products.dat"))) {
            model.setDailyNorm(dailyNorm);
            oos.writeObject(model);
            showAlert(Alert.AlertType.INFORMATION, "Успех", "Данные сохранены");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось сохранить данные");
        }
    }

    private void loadProducts() {
        File f = new File("products.dat");
        if (f.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
                model = (ProductListModel) ois.readObject();
                productList.setAll(model.getProducts());
                dailyNorm = model.getDailyNorm();
                if (dailyNorm != null) {
                    calDField.setText(String.valueOf(dailyNorm.getNorm()));
                }
                chooseProd.getItems().setAll(model.getReferenceProducts());
            } catch (IOException | ClassNotFoundException e) {
                showAlert(Alert.AlertType.WARNING, "Ошибка загрузки", "Не удалось загрузить данные");
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
