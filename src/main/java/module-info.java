module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens com.example.demo to javafx.fxml, javafx.graphics;
    exports com.example.demo;

}