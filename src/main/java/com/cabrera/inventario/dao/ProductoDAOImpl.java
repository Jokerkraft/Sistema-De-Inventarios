package com.cabrera.inventario.dao;

import com.cabrera.inventario.models.Producto;
import com.cabrera.inventario.util.ConexionBaseDatos;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ProductoDAOImpl implements ProductoDAO {

    private final Supplier<Connection> proveedorConexion;

    public ProductoDAOImpl() {
        this(() -> ConexionBaseDatos.obtenerInstancia().obtenerConexion());
    }

    // Permite probar el DAO con una conexión aislada de la base de la aplicación.
    ProductoDAOImpl(Supplier<Connection> proveedorConexion) {
        this.proveedorConexion = proveedorConexion;
    }

    @Override
    public List<Producto> obtenerTodos() {
        List<Producto> listaProductos = new ArrayList<>();
        String consulta = "SELECT p.id_producto, p.codigo, p.nombre, p.descripcion, " +
                "p.precio_compra, p.precio_venta, p.stock_actual " +
                "FROM productos p " +
                "ORDER BY p.id_producto";

        Connection conexion = proveedorConexion.get();

        try (Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(consulta)) {

            while (resultado.next()) {
                Producto producto = new Producto(
                        resultado.getInt("id_producto"),
                        resultado.getString("codigo"),
                        resultado.getString("nombre"),
                        resultado.getString("descripcion"),
                        resultado.getDouble("precio_compra"),
                        resultado.getDouble("precio_venta"),
                        resultado.getInt("stock_actual")
                );
                listaProductos.add(producto);
            }
        } catch (SQLException excepcion) {
            System.err.println("Error al obtener los productos: " + excepcion.getMessage());
        }
        return listaProductos;
    }

    @Override
    public boolean crear(Producto entidad) {
        String consultaProducto = "INSERT INTO productos (codigo, nombre, descripcion, precio_compra, precio_venta, stock_actual) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conexion = proveedorConexion.get();

        try (java.sql.PreparedStatement sentencia = conexion.prepareStatement(consultaProducto)) {
            sentencia.setString(1, entidad.getCodigo());
            sentencia.setString(2, entidad.getNombre());
            sentencia.setString(3, entidad.getDescripcion());
            sentencia.setDouble(4, entidad.getPrecioCompra());
            sentencia.setDouble(5, entidad.getPrecioVenta());
            sentencia.setInt(6, entidad.getStockActual());
            return sentencia.executeUpdate() > 0;
        } catch (SQLException excepcion) {
            System.err.println("Error al guardar producto: " + excepcion.getMessage());
            return false;
        }
    }

    @Override
    public Producto obtenerPorId(int id) {
        Producto productoEncontrado = null;
        String consulta = "SELECT p.id_producto, p.codigo, p.nombre, p.descripcion, " +
                "p.precio_compra, p.precio_venta, p.stock_actual " +
                "FROM productos p " +
                "WHERE p.id_producto = ?";
        Connection conexion = proveedorConexion.get();

        try (java.sql.PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setInt(1, id);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    productoEncontrado = new Producto(
                            resultado.getInt("id_producto"),
                            resultado.getString("codigo"),
                            resultado.getString("nombre"),
                            resultado.getString("descripcion"),
                            resultado.getDouble("precio_compra"),
                            resultado.getDouble("precio_venta"),
                            resultado.getInt("stock_actual")
                    );
                }
            }
        } catch (SQLException excepcion) {
            System.err.println("Error al buscar por ID: " + excepcion.getMessage());
        }
        return productoEncontrado;
    }

    @Override
    public boolean actualizar(Producto entidad) {
        String updateProducto = "UPDATE productos SET nombre = ?, descripcion = ?, precio_compra = ?, precio_venta = ?, stock_actual = ? WHERE id_producto = ?";

        Connection conexion = proveedorConexion.get();

        try (java.sql.PreparedStatement sentencia = conexion.prepareStatement(updateProducto)) {
            sentencia.setString(1, entidad.getNombre());
            sentencia.setString(2, entidad.getDescripcion());
            sentencia.setDouble(3, entidad.getPrecioCompra());
            sentencia.setDouble(4, entidad.getPrecioVenta());
            sentencia.setInt(5, entidad.getStockActual());
            sentencia.setInt(6, entidad.getIdProducto());
            return sentencia.executeUpdate() > 0;
        } catch (SQLException excepcion) {
            System.err.println("Error al actualizar producto: " + excepcion.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String consultaEliminarProducto = "DELETE FROM productos WHERE id_producto = ?";
        Connection conexion = proveedorConexion.get();

        try (java.sql.PreparedStatement sentencia = conexion.prepareStatement(consultaEliminarProducto)) {
            sentencia.setInt(1, id);
            return sentencia.executeUpdate() > 0;
        } catch (SQLException excepcion) {
            System.err.println("Error al eliminar producto: " + excepcion.getMessage());
            return false;
        }
    }
}
