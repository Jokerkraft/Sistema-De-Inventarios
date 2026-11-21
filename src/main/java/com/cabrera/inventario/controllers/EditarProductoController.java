package com.cabrera.inventario.controllers;

import com.cabrera.inventario.dao.ProductoDAO;
import com.cabrera.inventario.dao.ProductoDAOImpl;
import com.cabrera.inventario.models.Producto;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

// Valida y guarda los cambios del producto seleccionado en la tabla principal
public class EditarProductoController {

    // Campos que muestran el código y los datos editables del producto.
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtPrecio;

    // DAO encargado de persistir las modificaciones del catálogo
    // Instanciamos el DAO
    private ProductoDAO productoDAO = new ProductoDAOImpl();
    // Conserva el producto seleccionado para conocer su identificador y stock
    private Producto productoActual;

    // Este método recibe el producto desde la ventana principal y llena los campos
    // Prepara el formulario con los datos del producto que se va a editar
    public void cargarProducto(Producto producto) {
        this.productoActual = producto;
        txtCodigo.setText(producto.getCodigo());
        txtNombre.setText(producto.getNombre());
        txtDescripcion.setText(producto.getDescripcion());
        txtPrecio.setText(String.valueOf(producto.getPrecioVenta()));
    }

    @FXML
    // Valida los campos editables y guarda la actualización
    void guardarCambios(ActionEvent event) {
        try {
            String nombre = txtNombre.getText().trim();
            String descripcion = txtDescripcion.getText().trim();

            // Validaciones básicas
            if (nombre.isEmpty() || txtPrecio.getText().isEmpty()) {
                mostrarAlerta("Error", "El nombre y el precio son obligatorios.", Alert.AlertType.WARNING);
                return;
            }

            double precio = Double.parseDouble(txtPrecio.getText().replace(",", "."));

            if (!Double.isFinite(precio) || precio < 0) {
                mostrarAlerta("Datos Inválidos", "El precio debe ser finito y no negativo.", Alert.AlertType.WARNING);
                return;
            }

            // Conservamos el objeto original si la base de datos rechaza el cambio
            Producto productoEditado = new Producto(
                    productoActual.getIdProducto(), productoActual.getCodigo(), nombre, descripcion,
                    productoActual.getPrecioCompra(), precio, productoActual.getStockActual());

            // Llamamos al método actualizar del DAO
            if (productoDAO.actualizar(productoEditado)) {
                mostrarAlerta("Éxito", "El producto se ha actualizado correctamente.", Alert.AlertType.INFORMATION);
                cerrarVentana(event);
            } else {
                mostrarAlerta("Error", "No se pudo actualizar el producto en la base de datos.", Alert.AlertType.ERROR);
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Datos Inválidos", "El precio debe ser un número válido.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    // Cierra el diálogo de edición sin cambiar el producto
    void cerrarVentana(ActionEvent event) {
        Stage ventana = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        ventana.close();
    }

    // Presenta mensajes consistentes para validaciones y resultados del guardado
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
