package com.cabrera.inventario.controllers;

import com.cabrera.inventario.dao.*;
import com.cabrera.inventario.factories.PrecioStrategyFactory;
import com.cabrera.inventario.models.Producto;
import com.cabrera.inventario.strategies.PrecioStrategy;
import com.cabrera.inventario.strategies.PrecioAPIStrategy;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

// Controla la validación y el alta de productos desde el formulario JavaFX
public class NuevoProductoController {

    // Controles del formulario enlazados desde NuevoProducto.fxml
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtPrecioBase;

    //
    private final PrecioStrategy estrategiaPrecio =
            PrecioStrategyFactory.crearEstrategia(PrecioStrategyFactory.TipoEstrategia.API);

    // Instanciamos el DAO de productos para guardarlos en MySQL
    private ProductoDAO productoDAO = new ProductoDAOImpl();

    @FXML
    // Obtiene una referencia de precio usando la estrategia configurada
    void obtenerPrecioSugerido(ActionEvent event) {
        try {
            double precioBase = Double.parseDouble(txtPrecioBase.getText());
            String codigo = txtCodigo.getText();

            double precioSugerido = estrategiaPrecio.calcularPrecio(codigo, precioBase);

            // Actualizamos el campo de texto. Usamos replace para evitar errores de comas y puntos decimales
            txtPrecioBase.setText(String.format("%.2f", precioSugerido).replace(",", "."));

            mostrarAlerta("Éxito", "Precio sugerido obtenido desde la API: $" + precioSugerido, Alert.AlertType.INFORMATION);

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Por favor, ingrese un número válido en el Precio Base primero.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    // Valida los datos y crea un producto con existencias iniciales en cero
    void guardarProducto(ActionEvent event) {
        try {
            String codigo = txtCodigo.getText().trim();
            String nombre = txtNombre.getText().trim();
            String descripcion = txtDescripcion.getText().trim();

            // Validamos que los campos no estén vacíos
            if (codigo.isEmpty() || nombre.isEmpty() || txtPrecioBase.getText().isEmpty()) {
                mostrarAlerta("Error de Validación", "Código, nombre y precio son obligatorios.", Alert.AlertType.WARNING);
                return;
            }

            double precio = Double.parseDouble(txtPrecioBase.getText());

            if (!Double.isFinite(precio) || precio < 0) {
                mostrarAlerta("Error de Validación", "El precio debe ser un número finito y no negativo.",
                        Alert.AlertType.WARNING);
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
            // Las existencias se agregan después mediante un movimiento de entrada
            Producto nuevoProducto = new Producto(0, codigo, nombre, descripcion, precio, 0);

            // Guardamos en la base de datos usando el DAO
            if (productoDAO.crear(nuevoProducto)) {
                mostrarAlerta("Éxito", "El producto se ha registrado correctamente.", Alert.AlertType.INFORMATION);
                cerrarVentana(event);
            } else {
                mostrarAlerta("Error en Base de Datos", "No se pudo guardar. Verifica que el código no esté repetido.", Alert.AlertType.ERROR);
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Datos Inválidos", "El precio debe ser un número válido.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    // Cierra el formulario de alta
    void cerrarVentana(ActionEvent event) {
        // Cerramos esta ventana emergente
        Stage ventana = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        ventana.close();
    }

    // Muestra un mensaje de validación o resultado al usuario
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}