package com.cabrera.inventario.controllers;

import com.cabrera.inventario.models.Producto;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class MenuiPrincipalController {

    // Controles gráficos
    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Integer> colStock;

    // Este método se ejecuta automáticamente al abrir la ventana
    @FXML
    public void initialize() {
        // TODO: Aquí configuraremos las columnas para que lean los atributos de 'Producto'
        // y cargaremos los datos desde MySQL usando el DAO.
        System.out.println("Menú principal inicializado correctamente.");
    }
}
