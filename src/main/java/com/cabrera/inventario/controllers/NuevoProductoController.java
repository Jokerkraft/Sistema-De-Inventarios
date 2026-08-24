package com.cabrera.inventario.controllers;

import com.cabrera.inventario.strategies.PrecioAPIStrategy;
import com.cabrera.inventario.strategies.PrecioStrategy;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class NuevoProductoController {
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtPrecioBase;
    @FXML private TextField txtStock;

    // Instanciamos el comportamiento de la API
    private PrecioStrategy estrategiaAPI = new PrecioAPIStrategy();

    @FXML
    void obtenerPrecioSugerido(ActionEvent event) {
        try {
            // Leemos el precio base ingresado por el usuario
            double precioBase = Double.parseDouble(txtPrecioBase.getText());
            String codigo = txtCodigo.getText();

            // Usamos el patrón Strategy para consultar la API
            double precioSugerido = estrategiaAPI.calcularPrecio(codigo, precioBase);

            // Actualizamos el campo de texto con el nuevo precio
            txtPrecioBase.setText(String.format("%.2f", precioSugerido));

            mostrarAlerta("Éxito", "Precio sugerido obtenido desde la API: $" + precioSugerido, Alert.AlertType.INFORMATION);

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Por favor, ingrese un número válido en el Precio Base primero.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    void guardarProducto(ActionEvent event) {
        // Aquí usaremos el ProductoDAO para guardar en MySQL
        System.out.println("Guardando producto...");
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
