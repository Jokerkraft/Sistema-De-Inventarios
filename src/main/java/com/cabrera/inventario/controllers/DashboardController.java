package com.cabrera.inventario.controllers;

import com.cabrera.inventario.dao.MovimientoInventarioDAO;
import com.cabrera.inventario.dao.MovimientoInventarioDAOImpl;
import com.cabrera.inventario.dao.ProductoDAO;
import com.cabrera.inventario.dao.ProductoDAOImpl;
import com.cabrera.inventario.models.MovimientoInventario;
import com.cabrera.inventario.models.Producto;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.List;
import java.util.Locale;

// Calcula indicadores clave y alimenta las gráficas del inventario
public class DashboardController {

    // Indicadores resumidos del catálogo y sus existencias
    @FXML private Label lblTotalProductos;
    @FXML private Label lblUnidadesTotales;
    @FXML private Label lblValorInventario;
    @FXML private Label lblStockBajo;
    // Gráficas que comparan niveles de stock y volumen de movimientos
    @FXML private PieChart graficaEstadoStock;
    @FXML private BarChart<String, Number> graficaMovimientos;

    // DAOs que proporcionan los datos persistidos para el panel
    private final ProductoDAO productoDAO = new ProductoDAOImpl();
    private final MovimientoInventarioDAO movimientoDAO = new MovimientoInventarioDAOImpl();
    // Cantidad máxima para clasificar existencias como bajas.
    private static final int UMBRAL_STOCK_BAJO = 5;

    @FXML
    // Carga los indicadores cuando JavaFX termina de construir el dashboard
    public void initialize() {
        cargarIndicadores();
    }

    @FXML
    // Consulta productos y movimientos y actualiza cada indicador visual
    public void cargarIndicadores() {
        List<Producto> productos = productoDAO.obtenerTodos();
        List<MovimientoInventario> movimientos = movimientoDAO.obtenerTodos();

        int totalProductos = productos.size();
        int unidadesTotales = productos.stream().mapToInt(Producto::getStockActual).sum();
        double valorTotal = productos.stream()
                .mapToDouble(p -> p.getPrecioVenta() * p.getStockActual())
                .sum();

        long agotados = productos.stream().filter(p -> p.getStockActual() == 0).count();
        long stockBajo = productos.stream()
                .filter(p -> p.getStockActual() > 0 && p.getStockActual() <= UMBRAL_STOCK_BAJO)
                .count();
        long stockOptimo = productos.stream().filter(p -> p.getStockActual() > UMBRAL_STOCK_BAJO).count();

        lblTotalProductos.setText(String.valueOf(totalProductos));
        lblUnidadesTotales.setText(String.valueOf(unidadesTotales));
        lblValorInventario.setText(String.format(Locale.US, "$%,.2f", valorTotal));
        lblStockBajo.setText(String.valueOf(agotados + stockBajo));

        // Gráfica circular: estado de existencias
        graficaEstadoStock.setData(FXCollections.observableArrayList(
                new PieChart.Data("Stock Óptimo (" + stockOptimo + ")", stockOptimo),
                new PieChart.Data("Stock Bajo (" + stockBajo + ")", stockBajo),
                new PieChart.Data("Agotado (" + agotados + ")", agotados)
        ));

        // Gráfica de barras: volumen de entradas vs salidas
        int totalEntradas = movimientos.stream()
                .filter(m -> "ENTRADA".equalsIgnoreCase(m.getTipo()))
                .mapToInt(MovimientoInventario::getCantidad)
                .sum();
        int totalSalidas = movimientos.stream()
                .filter(m -> "SALIDA".equalsIgnoreCase(m.getTipo()))
                .mapToInt(MovimientoInventario::getCantidad)
                .sum();

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.getData().add(new XYChart.Data<>("Entradas", totalEntradas));
        serie.getData().add(new XYChart.Data<>("Salidas", totalSalidas));
        graficaMovimientos.getData().setAll(serie);
    }

    @FXML
    // Cierra la ventana del dashboard
    void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}