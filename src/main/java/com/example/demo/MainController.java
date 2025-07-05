package com.example.demo;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MainController {
    @FXML private static final String CSS_PATH = "/com/example/demo/aqua.css";
    @FXML public ChoiceBox<User> chooseUser;
    @FXML public Button addUser;
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
    @FXML private final ObservableList<Product> productList = FXCollections.observableArrayList();
    @FXML private final ObservableList<User> users = FXCollections.observableArrayList();
    @FXML private ProductListModel model = new ProductListModel();
    @FXML private DailyNorm dailyNorm;
    private User currentUser;
    private ProductListModel globalModel = new ProductListModel();

    @FXML
    public void initialize() {
        dateCol.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
        nameCol.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getName()));
        calCol.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getCcal()));
        protCol.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getProts()));
        fatCol.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getFats()));
        carbCol.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getCarbs()));
        table.setItems(productList);
        loadProducts();
        setupUserManagement();
        //productList.addListener((ListChangeListener<Product>) _ -> updateChart(true));
        loadUsers();
    }

    /*@FXML
    public void onaddNormClick() {
        try {
            int norm = Integer.parseInt(calDField.getText().trim().replace(",", "."));
            dailyNorm = new DailyNorm(norm);
            model.setDailyNorm(dailyNorm);
            showAlert(Alert.AlertType.INFORMATION, "Успех", "Норма установлена: " + norm + " ккал");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Введите корректное число калорий");
        }
    }*/

    @FXML
    public void onaddProdClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("add-product-view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            Scene scene = new Scene(root);

            applyStyles(scene);

            stage.setTitle("Добавление продукта");
            stage.setScene(scene);

            AddProductController controller = loader.getController();
            controller.setStage(stage);
            controller.setMainController(this);

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

    @FXML
    public void onOpenChartClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("chart-view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            Scene scene = new Scene(root);

            applyStyles(scene);

            stage.setTitle("График потребления калорий");
            stage.setScene(scene);

            ChartViewController controller = loader.getController();
            controller.setMainController(this);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось открыть окно графика");
        }
    }

    // Метод для применения стилей к сцене
    private void applyStyles(Scene scene) {
        URL cssResource = getClass().getResource(CSS_PATH);
        if (cssResource != null) {
            scene.getStylesheets().add(cssResource.toExternalForm());
        } else {
            System.err.println("Не удалось загрузить CSS: " + CSS_PATH);
        }
    }


    public ObservableList<Product> getProductList() {
        return productList;
    }

    private void checkDailyNorm(LocalDate date) {
        if (dailyNorm == null) return;

        double total = productList.stream()
                .filter(p -> p.getDate().equals(date))
                .mapToDouble(p -> Double.parseDouble(p.getCcal().replace(",", "."))).sum();

        if (total >= dailyNorm.getNorm()) {
            showAlert(Alert.AlertType.INFORMATION, "Норма достигнута", "Поздравляем! Вы достигли дневной нормы: " + dailyNorm.getNorm() + " ккал");
        }
    }

    @FXML
    public void onSaveClick() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("products.dat"))) {
            model.setDailyNorm(dailyNorm);
            oos.writeObject(model);
            model.saveToFile(currentUser.getName() + "_products.dat");
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
                chooseProd.getItems().setAll(model.getReferenceProducts());
            } catch (IOException | ClassNotFoundException e) {
                showAlert(Alert.AlertType.WARNING, "Ошибка загрузки", "Не удалось загрузить данные");
            }
        }
    }

    public void onaddUser() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("add-user-view.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("Добавление пользователя");
                stage.setScene(new Scene(root));

                AddUserController controller = loader.getController();
                controller.setStage(stage);
                controller.setMainController(this);
                stage.show();
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось открыть окно добавления пользователя: " + e.getMessage());
            }
        }
    private void loadUsers() {
        File f = new File("users.dat");
        if (f.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
                globalModel = (ProductListModel) ois.readObject();
                users.setAll(globalModel.getUsers());
                if (!users.isEmpty()) {
                    currentUser = users.get(0);
                    chooseUser.getSelectionModel().select(currentUser);
                    loadProductsForUser(); // Загружаем данные первого пользователя
                }
            } catch (IOException | ClassNotFoundException e) {
                showAlert(Alert.AlertType.WARNING, "Ошибка загрузки", "Не удалось загрузить список пользователей: " + e.getMessage());
            }
        }
    }
    private void loadProductsForUser() {
        if (currentUser == null) return;
        File f = new File(currentUser.getName() + "_products.dat");
        if (f.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
                model = (ProductListModel) ois.readObject();
                productList.setAll(model.getProducts());
                dailyNorm = model.getDailyNorm();
                chooseProd.getItems().setAll(model.getReferenceProducts());
            } catch (IOException | ClassNotFoundException e) {
                showAlert(Alert.AlertType.WARNING, "Ошибка загрузки", "Не удалось загрузить данные пользователя: " + e.getMessage());
            }
        } else {
            productList.clear();
            chooseProd.getItems().clear();
            model = new ProductListModel();
            model.setDailyNorm(currentUser.getDailyNorm());
        }
        //updateChart(true);
    }
    private void setupUserManagement() {
        chooseUser.setItems(users);
        chooseUser.getSelectionModel().selectedItemProperty().addListener((obs, oldUser, newUser) -> {
            if (newUser != null && newUser != oldUser) {
                if (oldUser != null) {
                    try {
                        model.setDailyNorm(dailyNorm);
                        model.saveToFile(oldUser.getName() + "_products.dat");
                    } catch (IOException e) {
                        showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось сохранить данные пользователя: " + e.getMessage());
                    }
                }
                currentUser = newUser;
                model = new ProductListModel(); // Сбрасываем текущую модель
                dailyNorm = newUser.getDailyNorm();
                model.setDailyNorm(dailyNorm);
                loadProductsForUser(); // Загружаем данные нового пользователя
                //updateChart(true);
            }
        });
    }
    public void addUser(String username, DailyNorm dailyNorm) {
        if (users.stream().anyMatch(u -> u.getName().equalsIgnoreCase(username))) {
            showAlert(Alert.AlertType.WARNING, "Предупреждение", "Пользователь с таким именем уже существует");
            return;
        }

        User newUser = new User(username, dailyNorm);
        users.add(newUser);
        globalModel.addUser(newUser); // Добавляем в globalModel
        chooseUser.getSelectionModel().select(newUser);
        model = new ProductListModel(); // Создаем новую модель для нового пользователя
        this.dailyNorm = dailyNorm;
        model.setDailyNorm(dailyNorm);
        productList.clear(); // Очищаем таблицу
        chooseProd.getItems().clear(); // Очищаем список продуктов
        //updateChart(true); // Обновляем график
        try {
            globalModel.saveToFile("users.dat"); // Сохраняем список пользователей
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось сохранить список пользователей: " + e.getMessage());
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
