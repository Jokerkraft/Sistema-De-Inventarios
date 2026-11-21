package com.cabrera.inventario;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

// Configura y muestra la primera ventana de la aplicación de inventario.
public class SistemaInventarios extends Application {

    // Carga el inicio de sesión desde FXML y crea la escena principal.
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                SistemaInventarios.class.getResource(
                        "/com/cabrera/inventario/views/Login.fxml"
                )
        );

        Scene scene = new Scene(fxmlLoader.load(), 720, 420);

        stage.setTitle("Sistema de Inventarios");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();
    }
}