package com.cabrera.inventario.controllers;

import com.cabrera.inventario.dao.ProveedorDAO;
import com.cabrera.inventario.dao.ProveedorDAOImpl;
import com.cabrera.inventario.models.Proveedor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class ProveedoresController {

    // Controles de la tabla y columnas enlazadas con los datos de los proveedores
    @FXML private TableView<Proveedor> tablaProveedores;
    @FXML private TableColumn<Proveedor, String> colNombre;
    @FXML private TableColumn<Proveedor, String> colContacto;
    @FXML private TableColumn<Proveedor, String> colTelefono;
    @FXML private TableColumn<Proveedor, String> colEmail;
    @FXML private TableColumn<Proveedor, String> colDireccion;
    @FXML private TableColumn<Proveedor, Boolean> colActivo;

    // Campos del formulario utilizados para registrar o modificar un proveedor
    @FXML private TextField txtNombre;
    @FXML private TextField txtContacto;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private TextField txtDireccion;
    @FXML private CheckBox chkActivo;

    // DAO encargado de realizar las operaciones de persistencia de proveedores
    private final ProveedorDAO proveedorDAO = new ProveedorDAOImpl();

    // Configura la tabla y carga los proveedores al abrir la ventana.
    @FXML
    public void initialize() {
        // Vincula cada propiedad del modelo con su columna correspondiente.
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colContacto.setCellValueFactory(new PropertyValueFactory<>("contacto"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

        // Los proveedores nuevos se consideran activos por defecto
        chkActivo.setSelected(true);
        cargarTabla();

        // Cuando se selecciona un proveedor, sus datos se muestran en el formulario
        tablaProveedores.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem != null) {
                llenarFormulario(newItem);
            }
        });
    }

    // Obtiene todos los proveedores y los muestra en la tabla
    private void cargarTabla() {
        ObservableList<Proveedor> lista = FXCollections.observableArrayList(proveedorDAO.obtenerTodos());
        tablaProveedores.setItems(lista);
    }

    // Copia los datos del proveedor seleccionado a los controles del formulario
    private void llenarFormulario(Proveedor proveedor) {
        txtNombre.setText(proveedor.getNombre());
        txtContacto.setText(proveedor.getContacto());
        txtTelefono.setText(proveedor.getTelefono());
        txtEmail.setText(proveedor.getEmail());
        txtDireccion.setText(proveedor.getDireccion());
        chkActivo.setSelected(proveedor.isActivo());
    }

    // Restablece el formulario para preparar el registro de un proveedor nuevo
    private void limpiarFormulario() {
        txtNombre.clear();
        txtContacto.clear();
        txtTelefono.clear();
        txtEmail.clear();
        txtDireccion.clear();
        chkActivo.setSelected(true);
        tablaProveedores.getSelectionModel().clearSelection();
    }

    // Guarda un proveedor nuevo o actualiza el proveedor que esté seleccionado
    @FXML
    void guardarProveedor(ActionEvent event) {
        // El nombre es obligatorio y se eliminan espacios al inicio y al final.
        String nombre = txtNombre.getText() == null ? "" : txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            mostrarAlerta("Validación", "El nombre del proveedor es obligatorio.", Alert.AlertType.WARNING);
            return;
        }

        Proveedor seleccionado = tablaProveedores.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            // Crea un nuevo objeto con los datos capturados en el formulario
            Proveedor nuevo = new Proveedor(
                    0,
                    nombre,
                    txtContacto.getText() == null ? "" : txtContacto.getText().trim(),
                    txtTelefono.getText() == null ? "" : txtTelefono.getText().trim(),
                    txtEmail.getText() == null ? "" : txtEmail.getText().trim(),
                    txtDireccion.getText() == null ? "" : txtDireccion.getText().trim(),
                    chkActivo.isSelected()
            );

            // Si se registra correctamente, limpia el formulario y recarga la tabla
            if (proveedorDAO.crear(nuevo)) {
                mostrarAlerta("Éxito", "Proveedor registrado correctamente.", Alert.AlertType.INFORMATION);
                limpiarFormulario();
                cargarTabla();
            } else {
                mostrarAlerta("Error", "No se pudo guardar el proveedor.", Alert.AlertType.ERROR);
            }
            return;
        }

        // Actualiza el objeto seleccionado con los valores modificados
        seleccionado.setNombre(nombre);
        seleccionado.setContacto(txtContacto.getText() == null ? "" : txtContacto.getText().trim());
        seleccionado.setTelefono(txtTelefono.getText() == null ? "" : txtTelefono.getText().trim());
        seleccionado.setEmail(txtEmail.getText() == null ? "" : txtEmail.getText().trim());
        seleccionado.setDireccion(txtDireccion.getText() == null ? "" : txtDireccion.getText().trim());
        seleccionado.setActivo(chkActivo.isSelected());

        // Persiste los cambios y actualiza la vista si la operación fue exitosa
        if (proveedorDAO.actualizar(seleccionado)) {
            mostrarAlerta("Éxito", "Proveedor actualizado correctamente.", Alert.AlertType.INFORMATION);
            limpiarFormulario();
            cargarTabla();
        } else {
            mostrarAlerta("Error", "No se pudo actualizar el proveedor.", Alert.AlertType.ERROR);
        }
    }

    // Limpia la selección y deja el formulario listo para un proveedor nuevo
    @FXML
    void nuevoProveedor(ActionEvent event) {
        limpiarFormulario();
    }

    // Solicita confirmación y elimina el proveedor seleccionado
    @FXML
    void eliminarProveedor(ActionEvent event) {
        Proveedor seleccionado = tablaProveedores.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Advertencia", "Selecciona un proveedor antes de eliminar.", Alert.AlertType.WARNING);
            return;
        }

        // Muestra una alerta de confirmación antes de ejecutar la eliminación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Deseas eliminar al proveedor: " + seleccionado.getNombre() + "?");

        if (confirmacion.showAndWait().isPresent() && confirmacion.getResult().getButtonData().isDefaultButton()) {
            if (proveedorDAO.eliminar(seleccionado.getIdProveedor())) {
                mostrarAlerta("Éxito", "Proveedor eliminado correctamente.", Alert.AlertType.INFORMATION);
                limpiarFormulario();
                cargarTabla();
            } else {
                mostrarAlerta("Error", "No se pudo eliminar el proveedor.", Alert.AlertType.ERROR);
            }
        }
    }

    // Cierra la ventana actual desde el evento que originó la acción
    @FXML
    void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    // Centraliza la creación y presentación de alertas informativas y de error
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
