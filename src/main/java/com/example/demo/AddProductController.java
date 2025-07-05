package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddProductController {

    @FXML private TextField nameField;
    @FXML private TextField caloriesField;
    @FXML private TextField proteinsField;
    @FXML private TextField fatsField;
    @FXML private TextField carbsField;
    @FXML private Stage stage;
    @FXML private MainController mainController;
    @FXML public void setStage(Stage stage) {
        this.stage = stage;
    }
    @FXML public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleAdd() {
        String name = nameField.getText().trim();
        String calories = caloriesField.getText().trim().replace(",", ".");
        String proteins = proteinsField.getText().trim().replace(",", ".");
        String fats = fatsField.getText().trim().replace(",", ".");
        String carbs = carbsField.getText().trim().replace(",", ".");

        if (name.isEmpty() || calories.isEmpty() || proteins.isEmpty() || fats.isEmpty() || carbs.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Предупреждение", "Заполните все поля");
            return;
        }

        if (mainController != null) {
            mainController.addReferenceProduct(name, calories, proteins, fats, carbs);
        }
        stage.close();
    }

    @FXML
    private void handleCancel() {
        stage.close();
    }

    @FXML
    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}