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

public class LoginController {

    // Etiquetas @FXML que conectan las variables con el diseño del SceneBuilder
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;

    // Instanciamos el DAO para conectar con MySQL
    private UsuarioDAO usuarioDAO = new UsuarioDAOImp();

    @FXML
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
                FXMLLoader cargador = new FXMLLoader(getClass().getResource("/com/cabrera/inventario/view/MenuPrincipal.fxml"));
                Parent raiz = cargador.load();

                // Obtenemos la ventana actual a partir del botón que lanzó el evento
                Stage ventanaActual = (Stage) ((Node) evento.getSource()).getScene().getWindow();

                // Creamos una nueva escena en el menú principal
                Scene nuevaEscena = new Scene(raiz, 800, 600);

                // Mostramos la nueva escena
                ventanaActual.setTitle("Sistema de Inventarios - Menú Principal");
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

    // Método auxiliar para generar las alertas en JavaFX
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
