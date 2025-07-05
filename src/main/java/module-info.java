module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.demo to javafx.fxml, javafx.graphics;
    exports com.example.demo;

    // Добавьте эту строку для доступа к ресурсам
}