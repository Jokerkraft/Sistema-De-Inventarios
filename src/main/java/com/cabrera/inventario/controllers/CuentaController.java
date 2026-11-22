package com.cabrera.inventario.controllers;

import com.cabrera.inventario.dao.UsuarioDAO;
import com.cabrera.inventario.dao.UsuarioDAOImp;
import com.cabrera.inventario.models.Usuario;
import com.cabrera.inventario.util.UtilidadSeguridad;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

// Gestiona el alta de nuevas cuentas y el restablecimiento de contraseñas
public class CuentaController {

    // Pestañas de registro y recuperación de credenciales
    @FXML private TabPane tabPaneCuentas;

    // Controles de registro
    @FXML private TextField txtRegNombre;
    @FXML private TextField txtRegUsuario;
    @FXML private PasswordField txtRegContrasena;
    @FXML private PasswordField txtRegConfirmar;

    // Controles de recuperación
    @FXML private TextField txtRecUsuario;
    @FXML private TextField txtRecNombre;
    @FXML private PasswordField txtRecNuevaContrasena;
    @FXML private PasswordField txtRecConfirmar;

    // DAO compartido para consultar y actualizar cuentas de usuario
    private final UsuarioDAO usuarioDAO = new UsuarioDAOImp();

    // Permite abrir directamente la pestaña de registro (0) o recuperación (1)
    // Selecciona la pestaña solicitada desde la pantalla de inicio de sesión
    public void seleccionarPestana(int indice) {
        tabPaneCuentas.getSelectionModel().select(indice);
    }

    @FXML
    // Valida los datos nuevos y crea la cuenta con el perfil OPERADOR
    void registrarCuenta(ActionEvent event) {
        String nombre = txtRegNombre.getText().trim();
        String username = txtRegUsuario.getText().trim();
        String pass = txtRegContrasena.getText();
        String confirmar = txtRegConfirmar.getText();

        if (nombre.isEmpty() || username.isEmpty() || pass.isEmpty()) {
            mostrarAlerta("Campos obligatorios", "Completa todos los datos para crear la cuenta.", Alert.AlertType.WARNING);
            return;
        }
        if (pass.length() < 6) {
            mostrarAlerta("Contraseña débil", "La contraseña debe tener al menos 6 caracteres.", Alert.AlertType.WARNING);
            return;
        }
        if (!pass.equals(confirmar)) {
            mostrarAlerta("Error de confirmación", "Las contraseñas no coinciden.", Alert.AlertType.WARNING);
            return;
        }
        if (usuarioDAO.obtenerPorUsername(username) != null) {
            mostrarAlerta("Usuario no disponible", "El nombre de usuario ya está registrado.", Alert.AlertType.ERROR);
            return;
        }

        // Por seguridad, las cuentas auto-registradas reciben rol OPERADOR
        String hash = UtilidadSeguridad.encriptarSHA1(pass);
        Usuario nuevo = new Usuario(0, nombre, username, hash, "OPERADOR", true);

        if (usuarioDAO.crear(nuevo)) {
            mostrarAlerta("Cuenta creada", "Usuario registrado correctamente. Ya puedes iniciar sesión.", Alert.AlertType.INFORMATION);
            cerrarVentana(event);
        } else {
            mostrarAlerta("Error", "No se pudo registrar el usuario en la base de datos.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    // Verifica la identidad declarada antes de guardar una contraseña nueva
    void restablecerContrasena(ActionEvent event) {
        String username = txtRecUsuario.getText().trim();
        String nombre = txtRecNombre.getText().trim();
        String nuevaPass = txtRecNuevaContrasena.getText();
        String confirmar = txtRecConfirmar.getText();

        if (username.isEmpty() || nombre.isEmpty() || nuevaPass.isEmpty()) {
            mostrarAlerta("Campos obligatorios", "Completa todos los campos de verificación.", Alert.AlertType.WARNING);
            return;
        }
        if (nuevaPass.length() < 6 || !nuevaPass.equals(confirmar)) {
            mostrarAlerta("Contraseña inválida", "Verifica que tenga al menos 6 caracteres y coincida.", Alert.AlertType.WARNING);
            return;
        }

        Usuario usuario = usuarioDAO.obtenerPorUsername(username);
        if (usuario == null || !usuario.getNombreCompleto().equalsIgnoreCase(nombre)) {
            mostrarAlerta("Verificación fallida", "Los datos no coinciden con ninguna cuenta activa.", Alert.AlertType.ERROR);
            return;
        }

        usuario.setContrasena(UtilidadSeguridad.encriptarSHA1(nuevaPass));
        if (usuarioDAO.actualizar(usuario)) {
            mostrarAlerta("Contraseña actualizada", "Tu contraseña se restableció correctamente.", Alert.AlertType.INFORMATION);
            cerrarVentana(event);
        } else {
            mostrarAlerta("Error", "No se pudo actualizar la contraseña.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    // Cierra la ventana de administración de cuenta
    void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    // Presenta mensajes de validación y resultados de las operaciones de cuenta
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}