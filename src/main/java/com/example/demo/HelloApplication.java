package com.example.demo;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.InputStream;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        //путь к CSS
        String cssPath = "/com/example/demo/aqua.css";
        InputStream cssStream = getClass().getResourceAsStream(cssPath);

        if (cssStream != null) {
            scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
            System.out.println("CSS успешно загружен");
        } else {
            System.err.println("ОШИБКА: CSS не найден по пути " + cssPath);
        }
        stage.setTitle("Похудей за 30 дней!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}