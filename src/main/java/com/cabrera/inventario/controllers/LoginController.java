package com.cabrera.inventario.controllers;

import com.cabrera.inventario.dao.UsuarioDAO;
import com.cabrera.inventario.dao.UsuarioDAOImp;
import com.cabrera.inventario.models.Usuario;
import com.cabrera.inventario.util.UtilidadSeguridad;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.IOException;

// Coordina la autenticación y la navegación entre el login y el menú principal
public class LoginController {

    // Etiquetas @FXML que conectan las variables con el diseño del SceneBuilder
    // Controles vinculados por nombre con Login.fxml
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;

    // Instanciamos el DAO para conectar con MySQL
    private UsuarioDAO usuarioDAO = new UsuarioDAOImp();

    @FXML
    // Valida las credenciales y abre el inventario cuando el acceso es correcto
    void manejarIngreso(ActionEvent evento) {
        String nombreUsuario = txtUsuario.getText();
        String contrasenaPlana = txtContrasena.getText();

        // Validamos que los campos no estén vacios
        if (nombreUsuario.isEmpty() || contrasenaPlana.isEmpty()) {
            mostrarAlerta("Error, campos vacíos", "Por favor, ingrese usuario y contraseña", Alert.AlertType.WARNING);
            return;
        }

        // Encriptamos la contraseña a SHA-1 antes de validarla
        String contrasenaEncriptada = UtilidadSeguridad.encriptarSHA1(contrasenaPlana);

        // Consultamos la base de datos através del DAO
        Usuario usuarioLogueado = usuarioDAO.autenticar(nombreUsuario, contrasenaEncriptada);

        // Validamos el resultado y mostramos los mensajes
        if (usuarioLogueado != null) {
            mostrarAlerta("Éxito", "Bienvenido al sistema, " + usuarioLogueado.getNombreCompleto(), Alert.AlertType.INFORMATION);

            try {
                // Cargamos el diseño del Menú Principal
                FXMLLoader cargador = new FXMLLoader(getClass().getResource("/com/cabrera/inventario/views/MenuPrincipal.fxml"));
                Parent raiz = cargador.load();
                MenuPrincipalController controlador = cargador.getController();
                controlador.setUsuarioActual(usuarioLogueado);

                // Obtenemos la ventana actual a partir del botón que lanzó el evento
                Stage ventanaActual = (Stage) ((Node) evento.getSource()).getScene().getWindow();

                // Creamos una nueva escena en el menú principal
                Scene nuevaEscena = new Scene(raiz, 1100, 680);

                // Mostramos la nueva escena
                ventanaActual.setTitle("Sistema de Inventarios - Menú Principal");
                ventanaActual.setResizable(true);
                ventanaActual.setScene(nuevaEscena);
                ventanaActual.centerOnScreen();
                ventanaActual.show();

            } catch (IOException e) {
                e.printStackTrace();
                mostrarAlerta("Error fatal", "No se pudo cargar la pantalla principal", Alert.AlertType.ERROR);
            }

        } else {
            // Mostramos el mensaje del error en caso de que las credenciales sean invalidas
            mostrarAlerta("Acceso Denegado", "Usuario o contraseña incorrectos. Verifique sus credenciales", Alert.AlertType.ERROR);
        }
    }

    @FXML
    // Cierra la ventana de inicio y finaliza el ciclo de JavaFX
    void salirAplicacion(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
        javafx.application.Platform.exit();
    }

    // Método auxiliar para generar las alertas en JavaFX
    // Centraliza la presentación de mensajes para el inicio de sesión
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    void abrirRegistroCuenta(ActionEvent event) {
        abrirModalCuenta(0);
    }

    @FXML
    void abrirRecuperarContrasena(ActionEvent event) {
        abrirModalCuenta(1);
    }

    // Abre la ventana de cuenta en la pestaña solicitada y espera su cierre.
    private void abrirModalCuenta(int pestana) {
        try {
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("/com/cabrera/inventario/views/Cuenta.fxml"));
            Parent raiz = cargador.load();
            CuentaController controlador = cargador.getController();
            controlador.seleccionarPestana(pestana);

            Stage modal = new Stage();
            modal.setTitle("Cuenta de Acceso");
            modal.setScene(new Scene(raiz));
            modal.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            modal.setResizable(false);
            modal.showAndWait();
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo abrir la ventana de cuentas.", Alert.AlertType.ERROR);
        }
    }
}
