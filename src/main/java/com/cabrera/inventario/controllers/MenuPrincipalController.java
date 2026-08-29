package com.cabrera.inventario.controllers;

import com.cabrera.inventario.dao.*;
import com.cabrera.inventario.models.*;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.*;

import java.io.IOException;
import java.util.List;

public class MenuPrincipalController {

    // Controles gráficos
    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colDescripcion;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Integer> colStock;

    // Instanciamos el DAO de productos
    private ProductoDAO productoDAO = new ProductoDAOImpl();

    // Método que se ejecuta automáticamente al abrir la ventana
    @FXML
    public void initialize() {
        // Enlazamos las columnas con los atributos exactos de la clase 'Producto'
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stockActual"));

        //Cargamos los datos desde MySQL a la tabla
        cargarDatosTabla();

        System.out.println("Menú principal inicializado y tabla cargada.");
    }

    private void cargarDatosTabla() {
        // Obtenemos la lista estándar de Java desde el DAO
        List<Producto> listaNormal = productoDAO.obtenerTodos();

        // Con JavaFX convertirmos "ObservableList" para poder redibujar la tabla
        ObservableList<Producto> listaObservable = FXCollections.observableArrayList(listaNormal);

        // Ingresamos los datos en la vista
        tablaProductos.setItems(listaObservable);
    }

    @FXML
    void abrirVentanaNuevoProducto(ActionEvent event) {
        try {
            // Cargamos el diseño de la nueva ventana
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("/com/cabrera/inventario/views/NuevoProducto.fxml"));
            Parent raiz = cargador.load();

            // Creamos un nuevo escenario para la ventana emergente
            Stage ventanaNuevoProducto = new Stage();
            ventanaNuevoProducto.setTitle("Registrar Producto");
            ventanaNuevoProducto.setScene(new Scene(raiz));

            // Hacemos que sea una ventana modal
            ventanaNuevoProducto.initModality(Modality.APPLICATION_MODAL);
            ventanaNuevoProducto.setResizable(false);

            // Mostramos la ventana
            ventanaNuevoProducto.showAndWait();

            // Cuando el usuario cierre esa ventana emergente, recargamos la tabla
            // por si se guardó un producto nuevo.
            cargarDatosTabla();

        } catch (IOException e) {
            System.err.println("Error al abrir la ventana: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void abrirVentanaProveedores(ActionEvent event) {
        try {
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("/com/cabrera/inventario/views/Proveedores.fxml"));
            Parent raiz = cargador.load();

            Stage ventanaProveedores = new Stage();
            ventanaProveedores.setTitle("Proveedores");
            ventanaProveedores.setScene(new Scene(raiz));
            ventanaProveedores.initModality(Modality.APPLICATION_MODAL);
            ventanaProveedores.setResizable(false);
            ventanaProveedores.showAndWait();

        } catch (IOException e) {
            System.err.println("Error al abrir la ventana de proveedores: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void cerrarSesion(ActionEvent event) {
        try {
            // Cargamos la vista del Login nuevamente
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("/com/cabrera/inventario/views/Login.fxml"));
            Parent raiz = cargador.load();

            // Obtenemos la ventana actual y le cambiamos la escena
            Stage ventanaActual = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            ventanaActual.setScene(new Scene(raiz, 600, 400));
            ventanaActual.setTitle("Inicio de Sesión");
            ventanaActual.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void salirDelSistema(ActionEvent event) {
        // Cerramos la aplicación de JavaFX por completo
        javafx.application.Platform.exit();
    }

    @FXML
    void eliminarProducto(ActionEvent event) {
        // Obtenemos el producto que el usuario seleccionó
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();

        if (seleccionado != null) {
            // Pedimos confirmación
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar Eliminación");
            confirmacion.setHeaderText("¿Estás seguro de eliminar: " + seleccionado.getNombre() + "?");

            // Si dice que sí, usamos el DAO para borrarlo
            if (confirmacion.showAndWait().get() == ButtonType.OK) {
                if (productoDAO.eliminar(seleccionado.getIdProducto())) {
                    cargarDatosTabla(); // Recargamos la tabla para que desaparezca visualmente
                }
            }
        } else {
            // Si no seleccionó nada, le avisamos
            Alert alerta = new Alert(Alert.AlertType.WARNING, "Por favor, selecciona un producto de la tabla primero.");
            alerta.showAndWait();
        }
    }

    @FXML
    void editarProducto(ActionEvent event) {
        // 1. Obtenemos el producto seleccionado
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();

        if (seleccionado != null) {
            try {
                // 2. Cargamos la ventana de edición
                FXMLLoader cargador = new FXMLLoader(getClass().getResource("/com/cabrera/inventario/views/EditarProducto.fxml"));
                Parent raiz = cargador.load();

                // 3. Le pasamos el producto seleccionado al nuevo controlador para que llene los campos
                com.cabrera.inventario.controllers.EditarProductoController controlador = cargador.getController();
                controlador.cargarProducto(seleccionado);

                // 4. Mostramos la ventana modal
                Stage ventana = new Stage();
                ventana.setTitle("Editar Producto");
                ventana.setScene(new Scene(raiz));
                ventana.initModality(Modality.APPLICATION_MODAL);
                ventana.showAndWait();

                // 5. Recargamos la tabla al cerrar
                cargarDatosTabla();

            } catch (java.io.IOException e) {
                System.err.println("Error al abrir ventana de edición: " + e.getMessage());
            }
        } else {
            // Si no seleccionó nada, mandamos alerta
            Alert alerta = new Alert(Alert.AlertType.WARNING, "Por favor, selecciona un producto de la tabla primero.");
            alerta.showAndWait();
        }
    }


}