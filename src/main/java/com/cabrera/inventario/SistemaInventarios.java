package com.cabrera.inventario;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SistemaInventarios extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                SistemaInventarios.class.getResource(
                        "/com/cabrera/inventario/views/Login.fxml"
                )
        );

        Scene scene = new Scene(fxmlLoader.load(), 600, 400);

        stage.setTitle("Sistema de Inventarios");
        stage.setScene(scene);
        stage.show();
    }
}