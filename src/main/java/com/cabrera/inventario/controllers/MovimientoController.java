package com.cabrera.inventario.controllers;

import com.cabrera.inventario.dao.MovimientoInventarioDAO;
import com.cabrera.inventario.dao.MovimientoInventarioDAOImpl;
import com.cabrera.inventario.dao.ProductoDAO;
import com.cabrera.inventario.dao.ProductoDAOImpl;
import com.cabrera.inventario.models.MovimientoInventario;
import com.cabrera.inventario.models.Producto;
import com.cabrera.inventario.models.Usuario;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

// Controla el registro de entradas y salidas y el reporte de movimientos recientes
public class MovimientoController {

    @FXML private ComboBox<Producto> cmbProducto;
    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextField txtCantidad;
    @FXML private TextField txtMotivo;
    @FXML private Label lblUsuario;
    @FXML private TableView<MovimientoInventario> tablaMovimientos;
    @FXML private TableColumn<MovimientoInventario, LocalDateTime> colFecha;
    @FXML private TableColumn<MovimientoInventario, String> colProducto;
    @FXML private TableColumn<MovimientoInventario, String> colTipo;
    @FXML private TableColumn<MovimientoInventario, Integer> colCantidad;
    @FXML private TableColumn<MovimientoInventario, String> colResponsable;
    @FXML private TableColumn<MovimientoInventario, String> colMotivo;

    private final ProductoDAO productoDAO = new ProductoDAOImpl();
    private final MovimientoInventarioDAO movimientoDAO = new MovimientoInventarioDAOImpl();
    private Usuario usuarioActual;

    @FXML
    public void initialize() {
        cmbTipo.setItems(FXCollections.observableArrayList("ENTRADA", "SALIDA"));
        cmbTipo.getSelectionModel().selectFirst();
        cmbProducto.setConverter(new StringConverter<>() {
            @Override
            public String toString(Producto producto) {
                return producto == null ? "" : producto.getCodigo() + " - " + producto.getNombre();
            }

            @Override
            public Producto fromString(String texto) {
                return null;
            }
        });

        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colFecha.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime fecha, boolean vacia) {
                super.updateItem(fecha, vacia);
                setText(vacia || fecha == null
                        ? null
                        : fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }
        });
        colProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colResponsable.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
        colMotivo.setCellValueFactory(new PropertyValueFactory<>("motivo"));

        cargarProductos();
        cargarMovimientos();
    }

    public void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
        lblUsuario.setText(usuario == null
                ? "Usuario no identificado"
                : usuario.getNombreCompleto() + " (" + usuario.getRol() + ")");
    }

    private void cargarProductos() {
        List<Producto> productos = productoDAO.obtenerTodos();
        cmbProducto.setItems(FXCollections.observableArrayList(productos));
        if (!productos.isEmpty()) {
            cmbProducto.getSelectionModel().selectFirst();
        }
    }

    private void cargarMovimientos() {
        tablaMovimientos.setItems(FXCollections.observableArrayList(movimientoDAO.obtenerRecientes(100)));
    }

    @FXML
    void registrarMovimiento() {
        Producto producto = cmbProducto.getValue();
        String tipo = cmbTipo.getValue();
        String motivo = txtMotivo.getText().trim();

        if (usuarioActual == null || producto == null || tipo == null || txtCantidad.getText().isBlank()) {
            mostrarAlerta("Datos incompletos", "Selecciona un producto y tipo, e ingresa una cantidad.",
                    Alert.AlertType.WARNING);
            return;
        }

        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            if (cantidad <= 0) {
                mostrarAlerta("Cantidad inválida", "La cantidad debe ser un número mayor que cero.",
                        Alert.AlertType.WARNING);
                return;
            }

            MovimientoInventario movimiento = new MovimientoInventario(
                    0, tipo, producto.getIdProducto(), cantidad, motivo, null, null, usuarioActual.getIdUsuario()
            );
            if (movimientoDAO.registrarMovimiento(movimiento)) {
                mostrarAlerta("Movimiento registrado", "El stock y el historial se actualizaron correctamente.",
                        Alert.AlertType.INFORMATION);
                txtCantidad.clear();
                txtMotivo.clear();
                cargarProductos();
                cargarMovimientos();
            } else {
                mostrarAlerta("No se pudo registrar", "Verifica que el producto exista y que haya stock suficiente.",
                        Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException excepcion) {
            mostrarAlerta("Cantidad inválida", "La cantidad debe ser un número entero.",
                    Alert.AlertType.WARNING);
        }
    }

    @FXML
    void actualizarReporte() {
        cargarProductos();
        cargarMovimientos();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
