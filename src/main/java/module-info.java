module com.example.jdbc {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.jdbc to javafx.fxml;
    exports com.example.jdbc;
}