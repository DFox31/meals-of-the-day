package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddUserController {
    @FXML public TextField userNameField;
    @FXML public Button addUser;
    @FXML public Button cancel;
    @FXML
    private TextField calDField;
    @FXML private Button addNorm;
    @FXML
    private Stage stage;
    private MainController mainController;
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }



    public void onAddUserClick() {

        String username = userNameField.getText();
        String normText = calDField.getText();

        if (username.isEmpty() || normText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Предупреждение", "Заполните все поля");
            return;
        }


        if (username.contains("/") || username.contains("\\") || username.contains(":") || username.contains("*") ||
                username.contains("?") || username.contains("\"") || username.contains("<") || username.contains(">") ||
                username.contains("|")) {
            showAlert(Alert.AlertType.WARNING, "Предупреждение", "Имя пользователя содержит недопустимые символы");
            return;
        }

        try {
            int norm = Integer.parseInt(normText);
            if (norm <= 0) {
                showAlert(Alert.AlertType.WARNING, "Предупреждение", "Норма калорий должна быть больше 0");
                return;
            }
            stage.close();
            if (mainController != null) {
                mainController.addUser(username, new DailyNorm(norm));
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Введите корректное число калорий");
        }

    }

    public void onCancelClick() {
        stage.close();
    }
    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

}
