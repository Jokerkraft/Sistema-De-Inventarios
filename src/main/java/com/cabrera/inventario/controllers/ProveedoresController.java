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

    @FXML private TableView<Proveedor> tablaProveedores;
    @FXML private TableColumn<Proveedor, String> colNombre;
    @FXML private TableColumn<Proveedor, String> colContacto;
    @FXML private TableColumn<Proveedor, String> colTelefono;
    @FXML private TableColumn<Proveedor, String> colEmail;
    @FXML private TableColumn<Proveedor, String> colDireccion;
    @FXML private TableColumn<Proveedor, Boolean> colActivo;

    @FXML private TextField txtNombre;
    @FXML private TextField txtContacto;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private TextField txtDireccion;
    @FXML private CheckBox chkActivo;

    private final ProveedorDAO proveedorDAO = new ProveedorDAOImpl();

    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colContacto.setCellValueFactory(new PropertyValueFactory<>("contacto"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

        chkActivo.setSelected(true);
        cargarTabla();

        tablaProveedores.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem != null) {
                llenarFormulario(newItem);
            }
        });
    }

    private void cargarTabla() {
        ObservableList<Proveedor> lista = FXCollections.observableArrayList(proveedorDAO.obtenerTodos());
        tablaProveedores.setItems(lista);
    }

    private void llenarFormulario(Proveedor proveedor) {
        txtNombre.setText(proveedor.getNombre());
        txtContacto.setText(proveedor.getContacto());
        txtTelefono.setText(proveedor.getTelefono());
        txtEmail.setText(proveedor.getEmail());
        txtDireccion.setText(proveedor.getDireccion());
        chkActivo.setSelected(proveedor.isActivo());
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtContacto.clear();
        txtTelefono.clear();
        txtEmail.clear();
        txtDireccion.clear();
        chkActivo.setSelected(true);
        tablaProveedores.getSelectionModel().clearSelection();
    }

    @FXML
    void guardarProveedor(ActionEvent event) {
        String nombre = txtNombre.getText() == null ? "" : txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            mostrarAlerta("Validación", "El nombre del proveedor es obligatorio.", Alert.AlertType.WARNING);
            return;
        }

        Proveedor seleccionado = tablaProveedores.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            Proveedor nuevo = new Proveedor(
                    0,
                    nombre,
                    txtContacto.getText() == null ? "" : txtContacto.getText().trim(),
                    txtTelefono.getText() == null ? "" : txtTelefono.getText().trim(),
                    txtEmail.getText() == null ? "" : txtEmail.getText().trim(),
                    txtDireccion.getText() == null ? "" : txtDireccion.getText().trim(),
                    chkActivo.isSelected()
            );

            if (proveedorDAO.crear(nuevo)) {
                mostrarAlerta("Éxito", "Proveedor registrado correctamente.", Alert.AlertType.INFORMATION);
                limpiarFormulario();
                cargarTabla();
            } else {
                mostrarAlerta("Error", "No se pudo guardar el proveedor.", Alert.AlertType.ERROR);
            }
            return;
        }

        seleccionado.setNombre(nombre);
        seleccionado.setContacto(txtContacto.getText() == null ? "" : txtContacto.getText().trim());
        seleccionado.setTelefono(txtTelefono.getText() == null ? "" : txtTelefono.getText().trim());
        seleccionado.setEmail(txtEmail.getText() == null ? "" : txtEmail.getText().trim());
        seleccionado.setDireccion(txtDireccion.getText() == null ? "" : txtDireccion.getText().trim());
        seleccionado.setActivo(chkActivo.isSelected());

        if (proveedorDAO.actualizar(seleccionado)) {
            mostrarAlerta("Éxito", "Proveedor actualizado correctamente.", Alert.AlertType.INFORMATION);
            limpiarFormulario();
            cargarTabla();
        } else {
            mostrarAlerta("Error", "No se pudo actualizar el proveedor.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void nuevoProveedor(ActionEvent event) {
        limpiarFormulario();
    }

    @FXML
    void eliminarProveedor(ActionEvent event) {
        Proveedor seleccionado = tablaProveedores.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Advertencia", "Selecciona un proveedor antes de eliminar.", Alert.AlertType.WARNING);
            return;
        }

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

    @FXML
    void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
