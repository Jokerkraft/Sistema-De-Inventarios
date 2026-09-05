package com.cabrera.inventario.dao;

import com.cabrera.inventario.models.Producto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProductoDAOImplTest {
    private Connection conexion;
    private ProductoDAOImpl dao;

    @BeforeEach
    void prepararBaseAislada() throws SQLException {
        conexion = DriverManager.getConnection("jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL");
        dao = new ProductoDAOImpl(() -> conexion);
        try (var sentencia = conexion.createStatement()) {
            sentencia.execute("CREATE TABLE productos (codigo VARCHAR(50) PRIMARY KEY, nombre VARCHAR(100), "
                    + "descripcion VARCHAR(255), precio_compra DECIMAL(10,2), precio_venta DECIMAL(10,2), stock_actual INT)");
            sentencia.execute("CREATE TABLE detalleproductos (codigo_producto VARCHAR(50) REFERENCES productos(codigo), "
                    + "precio_compra DECIMAL(10,2), precio_venta DECIMAL(10,2), stock_actual INT CHECK (stock_actual >= 0))");
            sentencia.execute("INSERT INTO productos VALUES ('P1', 'Original', 'Descripción original', 10, 20, 5)");
            sentencia.execute("INSERT INTO detalleproductos VALUES ('P1', 10, 20, 5)");
        }
    }

    @AfterEach
    void cerrarBase() throws SQLException {
        conexion.close();
    }

    @Test
    void actualizaAmbasTablasSinIntercambiarPrecios() throws SQLException {
        assertTrue(dao.actualizar(new Producto(1, "P1", "Editado", "Nueva descripción", 12.50, 31.75, 8)));
        comprobarValores("Editado", "Nueva descripción", 12.50, 31.75, 8);
        assertTrue(conexion.getAutoCommit());
        assertFalse(conexion.isClosed());
    }

    @Test
    void permiteGuardarSinCambiarValores() throws SQLException {
        assertTrue(dao.actualizar(new Producto(1, "P1", "Original", "Descripción original", 10, 20, 5)));
        comprobarValores("Original", "Descripción original", 10, 20, 5);
    }

    @Test
    void permiteStockCero() throws SQLException {
        assertTrue(dao.actualizar(new Producto(1, "P1", "Original", "Descripción original", 10, 20, 0)));
        comprobarValores("Original", "Descripción original", 10, 20, 0);
    }

    @Test
    void revierteProductoSiFallaLaSegundaTabla() throws SQLException {
        assertFalse(dao.actualizar(new Producto(1, "P1", "No guardar", "No guardar", 99, 150, -1)));
        comprobarValores("Original", "Descripción original", 10, 20, 5);
        assertTrue(conexion.getAutoCommit());
    }

    @Test
    void noReportaExitoParaProductoInexistente() throws SQLException {
        assertFalse(dao.actualizar(new Producto(2, "NO-EXISTE", "Otro", "Otro", 10, 20, 5)));
        comprobarValores("Original", "Descripción original", 10, 20, 5);
        assertTrue(conexion.getAutoCommit());
    }

    @Test
    void revierteSiFaltaElDetalle() throws SQLException {
        try (var sentencia = conexion.createStatement()) {
            sentencia.execute("DELETE FROM detalleproductos");
        }
        assertFalse(dao.actualizar(new Producto(1, "P1", "No guardar", "No guardar", 99, 150, 8)));
        try (var sentencia = conexion.createStatement();
             var resultado = sentencia.executeQuery("SELECT nombre, precio_venta FROM productos WHERE codigo = 'P1'")) {
            assertTrue(resultado.next());
            assertEquals("Original", resultado.getString("nombre"));
            assertEquals(20, resultado.getDouble("precio_venta"));
        }
        assertTrue(conexion.getAutoCommit());
    }

    private void comprobarValores(String nombre, String descripcion, double compra, double venta, int stock) throws SQLException {
        try (var sentencia = conexion.createStatement();
             var resultado = sentencia.executeQuery("SELECT p.nombre, p.descripcion, p.precio_compra, p.precio_venta, p.stock_actual, "
                     + "d.precio_compra AS detalle_compra, d.precio_venta AS detalle_venta, d.stock_actual AS detalle_stock "
                     + "FROM productos p JOIN detalleproductos d ON p.codigo = d.codigo_producto WHERE p.codigo = 'P1'")) {
            assertTrue(resultado.next());
            assertEquals(nombre, resultado.getString("nombre"));
            assertEquals(descripcion, resultado.getString("descripcion"));
            assertEquals(compra, resultado.getDouble("precio_compra"));
            assertEquals(venta, resultado.getDouble("precio_venta"));
            assertEquals(stock, resultado.getInt("stock_actual"));
            assertEquals(compra, resultado.getDouble("detalle_compra"));
            assertEquals(venta, resultado.getDouble("detalle_venta"));
            assertEquals(stock, resultado.getInt("detalle_stock"));
            assertFalse(resultado.next());
        }
    }
}
