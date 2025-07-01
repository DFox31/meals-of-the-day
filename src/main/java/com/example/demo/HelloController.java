package com.example.demo;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;

import java.io.File;
import java.io.IOException;

public class HelloController {
    public TextField calDField;
    @FXML
    public Button addNorm;
    @FXML
    public TextField prodField;
    @FXML
    public TextField cal100Field;
    @FXML
    public TextField prot100Field;
    @FXML
    public TextField fat100Field;
    @FXML
    public TextField carb100Field;
    @FXML
    public Button addProd;
    @FXML
    public ChoiceBox chooseProd;
    @FXML
    public TextField weightFullField;
    @FXML
    public Button addAll;
    @FXML
    public TableView table;
    @FXML
    public TableColumn <Product, String> Name;
    @FXML
    public TableColumn <Product, String> cal;
    @FXML
    public TableColumn <Product, String> prot;
    @FXML
    public TableColumn <Product, String> fat;
    @FXML
    public TableColumn <Product, String> carb;
    @FXML
    public BarChart grafik;
    @FXML
    private ProductListModel model = new ProductListModel();
<<<<<<< Updated upstream
=======
    private DailyNorm dailyNorm;

    private final String fixedColor = "#FFA500";

>>>>>>> Stashed changes
    @FXML
    public void initialize() {
        Name.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getName()));
        cal.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCcal()));
        prot.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getProts()));
        fat.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getFats()));
        carb.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCarbs()));


        try {
<<<<<<< Updated upstream
            File file = new File("products.dat");
            if (file.exists()) {
                ProductListModel loadedModel = ProductListModel.loadFromFile("products.dat");
                for (Product p : loadedModel.getProducts()) {
                    table.getItems().add(p);
                    model.addProduct(p);
=======
            int norm = Integer.parseInt(calDField.getText().trim().replace(",", "."));
            dailyNorm = new DailyNorm(norm);
            showAlert(Alert.AlertType.INFORMATION, "Успех", "Норма установлена: " + norm + " ккал");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Введите корректное число калорий");
        }
    }

    @FXML
    public void onaddProdClick() {
        String name = prodField.getText().trim();
        String calories = cal100Field.getText().trim().replace(",", ".");
        String proteins = prot100Field.getText().trim().replace(",", ".");
        String fats = fat100Field.getText().trim().replace(",", ".");
        String carbs = carb100Field.getText().trim().replace(",", ".");
        if (name.isEmpty() || calories.isEmpty() || proteins.isEmpty() || fats.isEmpty() || carbs.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Предупреждение", "Заполните все поля");
            return;
        }
        Product newProd = new Product(name, calories, proteins, fats, carbs, LocalDate.now());
        chooseProd.getItems().add(newProd);
        clearProductFields();
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
            weightFullField.clear();
            showAlert(Alert.AlertType.INFORMATION, "Успех", "Запись добавлена");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Введите корректный вес");
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
>>>>>>> Stashed changes
                }
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Не удалось загрузить продукты.");
            alert.showAndWait();
        }
    }


    @FXML
    public void onaddNormClick(){
        int norm = Integer.parseInt(calDField.getText());
        DailyNorm dailyNorm = new DailyNorm(norm);
    }

    public void onaddProdClick() {
        String prod = prodField.getText();
        String cal100 = cal100Field.getText();
        String prot100 = prot100Field.getText();
        String fat100 = fat100Field.getText();
        String carb100 = carb100Field.getText();
        if (!prod.isEmpty() && !cal100.isEmpty() && !prot100.isEmpty() && !fat100.isEmpty() && !carb100.isEmpty()) {
            Product product = new Product(prod, cal100, prot100, fat100, carb100);
            model.addProduct(product);
            chooseProd.getItems().add(product);



            prodField.clear();
            cal100Field.clear();
            prot100Field.clear();
            fat100Field.clear();
            carb100Field.clear();

        }
    }


    public void onaddAllClick() {
        Product selectedProduct = (Product) chooseProd.getSelectionModel().getSelectedItem();
        if (selectedProduct != null && !weightFullField.getText().isEmpty()) {
            int weight = Integer.parseInt(weightFullField.getText());

            // Пересчитываем значения на основе веса
            double cal = Double.parseDouble(selectedProduct.getCcal()) * weight / 100;
            double prot = Double.parseDouble(selectedProduct.getProts()) * weight / 100;
            double fat = Double.parseDouble(selectedProduct.getFats()) * weight / 100;
            double carb = Double.parseDouble(selectedProduct.getCarbs()) * weight / 100;

            // Создаем продукт с пересчитанными значениями
            Product weightedProduct = new Product(selectedProduct.getName(), String.valueOf(cal), String.valueOf(prot), String.valueOf(fat), String.valueOf(carb));

            model.addProduct(weightedProduct);
            table.getItems().add(weightedProduct);
            weightFullField.clear();
        }
    }
    @FXML
    public void onSaveClick() {
        try {
            model.saveToFile("products.dat");
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Список продуктов сохранён!");
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Ошибка при сохранении файла.");
            alert.showAndWait();
        }
    }
}
