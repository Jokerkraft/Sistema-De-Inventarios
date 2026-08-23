module com.cabrera.inventario {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.cabrera.inventario to javafx.fxml;
    exports com.cabrera.inventario;
}