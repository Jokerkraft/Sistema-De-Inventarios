package com.cabrera.inventario.controllers;

import com.cabrera.inventario.dao.*;
import com.cabrera.inventario.models.Producto;
import com.cabrera.inventario.strategies.PrecioStrategy;
import com.cabrera.inventario.strategies.PrecioAPIStrategy;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NuevoProductoController {

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtPrecioBase;
    @FXML private TextField txtStock;

    // Instanciamos el comportamiento de la API
    private PrecioStrategy estrategiaAPI = new PrecioAPIStrategy();

    // Instanciamos el DAO de productos para guardarlos en MySQL
    private ProductoDAO productoDAO = new ProductoDAOImpl();

    @FXML
    void obtenerPrecioSugerido(ActionEvent event) {
        try {
            double precioBase = Double.parseDouble(txtPrecioBase.getText());
            String codigo = txtCodigo.getText();

            double precioSugerido = estrategiaAPI.calcularPrecio(codigo, precioBase);

            // Actualizamos el campo de texto. Usamos replace para evitar errores de comas y puntos decimales
            txtPrecioBase.setText(String.format("%.2f", precioSugerido).replace(",", "."));

            mostrarAlerta("Éxito", "Precio sugerido obtenido desde la API: $" + precioSugerido, Alert.AlertType.INFORMATION);

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Por favor, ingrese un número válido en el Precio Base primero.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    void guardarProducto(ActionEvent event) {
        try {
            String codigo = txtCodigo.getText().trim();
            String nombre = txtNombre.getText().trim();
            String descripcion = txtDescripcion.getText().trim();

            // Validamos que los campos no estén vacíos
            if (codigo.isEmpty() || nombre.isEmpty() || txtPrecioBase.getText().isEmpty() || txtStock.getText().isEmpty()) {
                mostrarAlerta("Error de Validación", "Todos los campos son obligatorios.", Alert.AlertType.WARNING);
                return;
            }

            double precio = Double.parseDouble(txtPrecioBase.getText());
            int stock = Integer.parseInt(txtStock.getText());

            // Validamos que no exista stock negativo
            if (stock < 0) {
                mostrarAlerta("Error de Validación", "El stock actual no puede ser un valor negativo.", Alert.AlertType.WARNING);
                return;
            }

            // Validación de duplicados en la base de datos
            for (Producto p: productoDAO.obtenerTodos()) {
                if (p.getCodigo().equalsIgnoreCase(codigo) || p.getNombre().equalsIgnoreCase(nombre)) {
                    mostrarAlerta("Producto Duplicado", "Ya existe un producto registrado con ese Código o Nombre.", Alert.AlertType.ERROR);
                    return;
                }
            }

            // Creamos el objeto si pasa todas las validaciones
            Producto nuevoProducto = new Producto(0, codigo, nombre, descripcion, precio, stock);

            // Guardamos en la base de datos usando el DAO
            if (productoDAO.crear(nuevoProducto)) {
                mostrarAlerta("Éxito", "El producto se ha registrado correctamente.", Alert.AlertType.INFORMATION);
                cerrarVentana(event);
            } else {
                mostrarAlerta("Error en Base de Datos", "No se pudo guardar. Verifica que el código no esté repetido.", Alert.AlertType.ERROR);
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Datos Inválidos", "El precio y el stock deben ser valores numéricos.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    void cerrarVentana(ActionEvent event) {
        // Cerramos esta ventana emergente
        Stage ventana = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        ventana.close();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}