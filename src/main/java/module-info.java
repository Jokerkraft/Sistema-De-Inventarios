module com.cabrera.inventario {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.net.http;
    requires org.json;


    opens com.cabrera.inventario to javafx.fxml;
    opens com.cabrera.inventario.controllers to javafx.fxml;

    opens com.cabrera.inventario.models to javafx.base;

    exports com.cabrera.inventario;
}