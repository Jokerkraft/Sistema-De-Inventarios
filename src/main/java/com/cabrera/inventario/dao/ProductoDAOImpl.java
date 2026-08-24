package com.cabrera.inventario.dao;

import com.cabrera.inventario.models.Producto;
import com.cabrera.inventario.util.ConexionBaseDatos;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAOImpl implements ProductoDAO {

    @Override
    public List<Producto> obtenerTodos() {
        List<Producto> listaProductos = new ArrayList<>();
        String consulta = "SELECT * FROM productos";

        // Obtenemos la conexión (Patrón Singleton)
        Connection conexion = ConexionBaseDatos.obtenerInstancia().obtenerConexion();

        try (Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(consulta)) {

            while (resultado.next()) {
                Producto producto = new Producto(
                        resultado.getInt("id_producto"),
                        resultado.getString("codigo"),
                        resultado.getString("nombre"),
                        resultado.getString("descripcion"),
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
        // Consulta SQL actualizada para incluir la descripción
        String consulta = "INSERT INTO productos (codigo, nombre, descripcion, precio_venta, precio_compra, stock_actual) VALUES (?, ?, ?, ?, 0.0, ?)";

        Connection conexion = ConexionBaseDatos.obtenerInstancia().obtenerConexion();

        try (java.sql.PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setString(1, entidad.getCodigo());
            sentencia.setString(2, entidad.getNombre());
            sentencia.setString(3, entidad.getDescripcion());
            sentencia.setDouble(4, entidad.getPrecioVenta());
            sentencia.setInt(5, entidad.getStockActual());
            int filasAfectadas = sentencia.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException excepcion) {
            System.err.println("Error al guardar el producto en BD: " + excepcion.getMessage());
            return false;
        }
    }

    @Override
    public Producto obtenerPorId(int id) {
        Producto productoEncontrado = null;
        String consulta = "SELECT * FROM productos WHERE id_producto = ?";
        Connection conexion = ConexionBaseDatos.obtenerInstancia().obtenerConexion();

        try (java.sql.PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setInt(1, id);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    productoEncontrado = new Producto(
                            resultado.getInt("id_producto"),
                            resultado.getString("codigo"),
                            resultado.getString("nombre"),
                            resultado.getString("descripcion"),
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
        // Consulta SQL actualizada del UPDATE
        String consulta = "UPDATE productos SET nombre = ?, descripcion = ?, precio_venta = ?, stock_actual = ? WHERE codigo = ?";
        Connection conexion = ConexionBaseDatos.obtenerInstancia().obtenerConexion();

        try (java.sql.PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setString(1, entidad.getNombre());
            sentencia.setString(2, entidad.getDescripcion());
            sentencia.setDouble(3, entidad.getPrecioVenta());
            sentencia.setInt(4, entidad.getStockActual());
            sentencia.setString(5, entidad.getCodigo()); // Usamos el código como identificador

            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String consulta = "DELETE FROM productos WHERE id_producto = ?";
        Connection conexion = ConexionBaseDatos.obtenerInstancia().obtenerConexion();

        try (java.sql.PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setInt(1, id);
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar: " + e.getMessage());
            return false;
        }
    }
}