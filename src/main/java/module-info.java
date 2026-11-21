// Declara dependencias del módulo y habilita la reflexión que JavaFX necesita para FXML.
module com.cabrera.inventario {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.net.http;
    requires org.json;


    // Permite que FXMLLoader cree la aplicación y acceda a sus componentes.
    opens com.cabrera.inventario to javafx.fxml;
    // Permite que FXML conecte controles y eventos con sus controladores.
    opens com.cabrera.inventario.controllers to javafx.fxml;

    // Permite a JavaFX leer las propiedades de los modelos en controles visuales.
    opens com.cabrera.inventario.models to javafx.base;

    // Expone el punto de entrada público del módulo.
    exports com.cabrera.inventario;
}