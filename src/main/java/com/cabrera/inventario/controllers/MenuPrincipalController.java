package com.cabrera.inventario.controllers;

import com.cabrera.inventario.dao.*;
import com.cabrera.inventario.models.*;
import com.cabrera.inventario.util.OrdenadorGenerico;
import com.cabrera.inventario.util.ServicioArchivosInventario;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.*;

import java.io.IOException;
import java.io.File;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

// Coordina el inventario principal, los reportes y la navegación a otras ventanas
public class MenuPrincipalController {

    // Controles gráficos
    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colDescripcion;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Integer> colStock;
    @FXML private Label lblSesion;
    @FXML private Label lblAlertaStock;
    @FXML private TextField txtBuscarCodigo;
    @FXML private Button btnNuevoProducto;
    @FXML private Button btnProveedores;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnImportar;

    // Instanciamos el DAO de productos
    private ProductoDAO productoDAO = new ProductoDAOImpl();
    // Usuario autenticado que realiza operaciones y movimientos de inventario.
    private Usuario usuarioActual;
    // Servicio que centraliza la importación y exportación de archivos.
    private final ServicioArchivosInventario servicioArchivos = new ServicioArchivosInventario();
    // Límite configurable para considerar un producto con existencias bajas.
    private static final int UMBRAL_STOCK_BAJO = 5;

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

    // Consulta, ordena y muestra el inventario junto con su alerta de existencias
    private void cargarDatosTabla() {
        // Ordenamos la lista con Comparator para mostrar el inventario por nombre
        List<Producto> listaNormal = OrdenadorGenerico.ordenar(
                productoDAO.obtenerTodos(),
                Comparator.comparing(Producto::getNombre, String.CASE_INSENSITIVE_ORDER)
        );

        // Con JavaFX convertirmos "ObservableList" para poder redibujar la tabla
        ObservableList<Producto> listaObservable = FXCollections.observableArrayList(listaNormal);

        // Ingresamos los datos en la vista
        tablaProductos.setItems(listaObservable);
        actualizarAlertaStock(listaNormal);
    }

    // Recibe la sesión autenticada y configura las acciones disponibles por rol
    public void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
        lblSesion.setText(usuario == null
                ? "Sesión no identificada"
                : usuario.getNombreCompleto() + " (" + usuario.getRol() + ")");

        boolean esAdministrador = usuario != null && "ADMIN".equalsIgnoreCase(usuario.getRol());
        btnNuevoProducto.setDisable(!esAdministrador);
        btnProveedores.setDisable(!esAdministrador);
        btnEliminar.setDisable(!esAdministrador);
        btnEditar.setDisable(!esAdministrador);
        btnImportar.setDisable(!esAdministrador);
    }

    // Actualiza el aviso contando productos bajo el umbral configurado
    private void actualizarAlertaStock(List<Producto> productos) {
        long productosBajos = productos.stream()
                .filter(producto -> producto.getStockActual() <= UMBRAL_STOCK_BAJO)
                .count();
        lblAlertaStock.setText(productosBajos == 0
                ? "Sin alertas de stock bajo"
                : productosBajos + " producto(s) con stock de " + UMBRAL_STOCK_BAJO + " o menos");
    }

    @FXML
    // Abre el formulario de alta y recarga el inventario al cerrar
    void abrirVentanaNuevoProducto(ActionEvent event) {
        if (!requiereAdministrador()) {
            return;
        }
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
    // Abre la gestión de proveedores si la sesión tiene permisos administrativos
    void abrirVentanaProveedores(ActionEvent event) {
        if (!requiereAdministrador()) {
            return;
        }
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
    // Abre los movimientos y recarga el stock después de cerrar la ventana
    void abrirVentanaMovimientos(ActionEvent event) {
        try {
            FXMLLoader cargador = new FXMLLoader(getClass().getResource(
                    "/com/cabrera/inventario/views/Movimientos.fxml"));
            Parent raiz = cargador.load();
            MovimientoController controlador = cargador.getController();
            controlador.setUsuarioActual(usuarioActual);

            Stage ventana = new Stage();
            ventana.setTitle("Movimientos de Inventario");
            ventana.setScene(new Scene(raiz));
            ventana.initModality(Modality.APPLICATION_MODAL);
            ventana.setResizable(true);
            ventana.showAndWait();
            cargarDatosTabla();
        } catch (IOException excepcion) {
            mostrarAlerta("Error", "No se pudo abrir la ventana de movimientos: "
                    + excepcion.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    // Guarda el inventario en el formato seleccionado por el usuario
    void exportarInventario(ActionEvent event) {
        FileChooser selector = new FileChooser();
        selector.setTitle("Exportar inventario");
        selector.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivo CSV", "*.csv"),
                new FileChooser.ExtensionFilter("Archivo JSON", "*.json")
        );
        selector.setInitialFileName("inventario.csv");
        File archivo = selector.showSaveDialog(tablaProductos.getScene().getWindow());
        if (archivo == null) {
            return;
        }

        String extension = selector.getSelectedExtensionFilter().getExtensions().get(0).substring(1);
        if (!archivo.getName().toLowerCase(Locale.ROOT).endsWith(extension)) {
            String nombre = archivo.getName();
            int punto = nombre.lastIndexOf('.');
            if (punto >= 0) {
                nombre = nombre.substring(0, punto);
            }
            archivo = new File(archivo.getParentFile(), nombre + extension);
        }

        try {
            servicioArchivos.exportar(Path.of(archivo.toURI()), productoDAO.obtenerTodos());
            mostrarAlerta("Exportación completada", "El inventario se guardó correctamente.",
                    Alert.AlertType.INFORMATION);
        } catch (IOException excepcion) {
            mostrarAlerta("Error de exportación", excepcion.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    // Importa el catálogo y registra los cambios de existencias con el usuario actual
    void importarInventario(ActionEvent event) {
        if (!requiereAdministrador()) {
            return;
        }

        FileChooser selector = new FileChooser();
        selector.setTitle("Importar inventario");
        selector.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivos CSV y JSON", "*.csv", "*.json"),
                new FileChooser.ExtensionFilter("Archivo CSV", "*.csv"),
                new FileChooser.ExtensionFilter("Archivo JSON", "*.json")
        );
        File archivo = selector.showOpenDialog(tablaProductos.getScene().getWindow());
        if (archivo == null) {
            return;
        }

        try {
            ServicioArchivosInventario.ResultadoImportacion resultado = servicioArchivos.importar(
                    Path.of(archivo.toURI()),
                    productoDAO,
                    new MovimientoInventarioDAOImpl(),
                    usuarioActual
            );
            cargarDatosTabla();
            mostrarAlerta("Importación completada",
                    "Productos nuevos: " + resultado.productosNuevos()
                            + "\nProductos actualizados: " + resultado.productosActualizados(),
                    Alert.AlertType.INFORMATION);
        } catch (IOException excepcion) {
            cargarDatosTabla();
            mostrarAlerta("Error de importación", excepcion.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Centraliza la presentación de mensajes para las acciones del menú
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // Evita ejecutar tareas administrativas con perfiles sin autorización
    private boolean requiereAdministrador() {
        if (usuarioActual != null && "ADMIN".equalsIgnoreCase(usuarioActual.getRol())) {
            return true;
        }
        mostrarAlerta("Permiso denegado", "Esta acción está disponible únicamente para el rol ADMIN.",
                Alert.AlertType.WARNING);
        return false;
    }

    @FXML
    // Busca el código introducido o escaneado y selecciona el producto encontrado
    void buscarCodigoBarras() {
        String codigo = txtBuscarCodigo.getText().trim();
        if (codigo.isEmpty()) {
            return;
        }

        Producto producto = productoDAO.obtenerPorCodigo(codigo);
        if (producto == null) {
            mostrarAlerta("Producto no encontrado", "No existe un producto con el código " + codigo + ".",
                    Alert.AlertType.INFORMATION);
            return;
        }

        for (int indice = 0; indice < tablaProductos.getItems().size(); indice++) {
            if (tablaProductos.getItems().get(indice).getIdProducto() == producto.getIdProducto()) {
                tablaProductos.getSelectionModel().select(indice);
                tablaProductos.scrollTo(indice);
                break;
            }
        }
        txtBuscarCodigo.clear();
    }

    @FXML
    // Regresa al login y limpia el usuario asociado a esta instancia
    void cerrarSesion(ActionEvent event) {
        try {
            // Cargamos la vista del Login nuevamente
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("/com/cabrera/inventario/views/Login.fxml"));
            Parent raiz = cargador.load();

            // Obtenemos la ventana actual y le cambiamos la escena
            Stage ventanaActual = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            ventanaActual.setResizable(false);
            ventanaActual.setScene(new Scene(raiz, 720, 465));
            ventanaActual.setTitle("Inicio de Sesión");
            ventanaActual.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    // Finaliza la aplicación JavaFX
    void salirDelSistema(ActionEvent event) {
        // Cerramos la aplicación de JavaFX por completo
        javafx.application.Platform.exit();
    }

    @FXML
    // Pide confirmación antes de borrar el producto seleccionado
    void eliminarProducto(ActionEvent event) {
        if (!requiereAdministrador()) {
            return;
        }
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
                } else {
                    mostrarAlerta("No se pudo eliminar",
                            "El producto puede tener movimientos o detalles asociados que deben conservarse.",
                            Alert.AlertType.ERROR);
                }
            }
        } else {
            // Si no seleccionó nada, le avisamos
            Alert alerta = new Alert(Alert.AlertType.WARNING, "Por favor, selecciona un producto de la tabla primero.");
            alerta.showAndWait();
        }
    }

    @FXML
    // Abre el formulario de edición del producto seleccionado
    void editarProducto(ActionEvent event) {
        if (!requiereAdministrador()) {
            return;
        }
        // Obtenemos el producto seleccionado
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();

        if (seleccionado != null) {
            try {
                // Cargamos la ventana de edición
                FXMLLoader cargador = new FXMLLoader(getClass().getResource("/com/cabrera/inventario/views/EditarProducto.fxml"));
                Parent raiz = cargador.load();

                // Le pasamos el producto seleccionado al nuevo controlador para que llene los campos
                com.cabrera.inventario.controllers.EditarProductoController controlador = cargador.getController();
                controlador.cargarProducto(seleccionado);

                // Mostramos la ventana modal
                Stage ventana = new Stage();
                ventana.setTitle("Editar Producto");
                ventana.setScene(new Scene(raiz));
                ventana.initModality(Modality.APPLICATION_MODAL);
                ventana.showAndWait();

                // Recargamos la tabla al cerrar
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
    @FXML
    void abrirVentanaDashboard(ActionEvent event) {
        try {
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("/com/cabrera/inventario/views/Dashboard.fxml"));
            Parent raiz = cargador.load();

            Stage ventana = new Stage();
            ventana.setTitle("Dashboard de Inventario");
            ventana.setScene(new Scene(raiz));
            ventana.initModality(Modality.APPLICATION_MODAL);
            ventana.showAndWait();
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo abrir el Dashboard: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}