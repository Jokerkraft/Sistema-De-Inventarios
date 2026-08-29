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
        String consulta = "SELECT p.id_producto, p.codigo, p.nombre, p.descripcion, " +
                "p.precio_compra, p.precio_venta, p.stock_actual " +
                "FROM productos p " +
                "ORDER BY p.id_producto";

        Connection conexion = ConexionBaseDatos.obtenerInstancia().obtenerConexion();

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
        String consultaDetalle = "INSERT INTO detalleproductos (codigo_producto, precio_venta, precio_compra, stock_actual) VALUES (?, ?, ?, ?)";

        Connection conexion = ConexionBaseDatos.obtenerInstancia().obtenerConexion();

        try {
            try (java.sql.PreparedStatement stmtProducto = conexion.prepareStatement(consultaProducto)) {
                stmtProducto.setString(1, entidad.getCodigo());
                stmtProducto.setString(2, entidad.getNombre());
                stmtProducto.setString(3, entidad.getDescripcion());
                stmtProducto.setDouble(4, entidad.getPrecioCompra());
                stmtProducto.setDouble(5, entidad.getPrecioVenta());
                stmtProducto.setInt(6, entidad.getStockActual());
                stmtProducto.executeUpdate();
            }

            try (java.sql.PreparedStatement stmtDetalle = conexion.prepareStatement(consultaDetalle)) {
                stmtDetalle.setString(1, entidad.getCodigo());
                stmtDetalle.setDouble(2, entidad.getPrecioVenta());
                stmtDetalle.setDouble(3, entidad.getPrecioCompra());
                stmtDetalle.setInt(4, entidad.getStockActual());
                stmtDetalle.executeUpdate();
            }
            return true;

        } catch (SQLException excepcion) {
            System.err.println("Error al guardar en múltiples tablas: " + excepcion.getMessage());
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
        String updateProducto = "UPDATE productos SET nombre = ?, descripcion = ?, precio_compra = ?, precio_venta = ? WHERE codigo = ?";
        String updateDetalle = "UPDATE detalleproductos SET precio_venta = ?, precio_compra = ?, stock_actual = ? WHERE codigo_producto = ?";

        Connection conexion = ConexionBaseDatos.obtenerInstancia().obtenerConexion();

        try {
            conexion.setAutoCommit(false);

            try (java.sql.PreparedStatement stmtProd = conexion.prepareStatement(updateProducto);
                 java.sql.PreparedStatement stmtDet = conexion.prepareStatement(updateDetalle)) {

                stmtProd.setString(1, entidad.getNombre());
                stmtProd.setString(2, entidad.getDescripcion());
                stmtProd.setDouble(3, entidad.getPrecioCompra());
                stmtProd.setDouble(4, entidad.getPrecioVenta());
                stmtProd.setString(5, entidad.getCodigo());
                stmtProd.executeUpdate();

                stmtDet.setDouble(1, entidad.getPrecioVenta());
                stmtDet.setDouble(2, entidad.getPrecioCompra());
                stmtDet.setInt(3, entidad.getStockActual());
                stmtDet.setString(4, entidad.getCodigo());
                stmtDet.executeUpdate();

                conexion.commit();
                return true;

            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            } finally {
                conexion.setAutoCommit(true);
            }

        } catch (SQLException excepcion) {
            System.err.println("Error al actualizar en múltiples tablas: " + excepcion.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String consultaObtenerCodigo = "SELECT codigo FROM productos WHERE id_producto = ?";
        String consultaEliminarDetalle = "DELETE FROM detalleproductos WHERE codigo_producto = ?";
        String consultaEliminarProducto = "DELETE FROM productos WHERE id_producto = ?";
        Connection conexion = ConexionBaseDatos.obtenerInstancia().obtenerConexion();

        try {
            conexion.setAutoCommit(false);

            String codigoProducto = null;
            try (java.sql.PreparedStatement sentencia = conexion.prepareStatement(consultaObtenerCodigo)) {
                sentencia.setInt(1, id);
                try (ResultSet resultado = sentencia.executeQuery()) {
                    if (resultado.next()) {
                        codigoProducto = resultado.getString("codigo");
                    }
                }
            }

            if (codigoProducto == null) {
                conexion.rollback();
                return false;
            }

            try (java.sql.PreparedStatement sentenciaDetalle = conexion.prepareStatement(consultaEliminarDetalle)) {
                sentenciaDetalle.setString(1, codigoProducto);
                sentenciaDetalle.executeUpdate();
            }

            try (java.sql.PreparedStatement sentenciaProducto = conexion.prepareStatement(consultaEliminarProducto)) {
                sentenciaProducto.setInt(1, id);
                int resultado = sentenciaProducto.executeUpdate();
                conexion.commit();
                return resultado > 0;
            }

        } catch (SQLException e) {
            try {
                conexion.rollback();
            } catch (SQLException rollbackException) {
                System.err.println("Error en rollback: " + rollbackException.getMessage());
            }
            System.err.println("Error al eliminar: " + e.getMessage());
            return false;
        } finally {
            try {
                conexion.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error al restaurar autocommit: " + e.getMessage());
            }
        }
    }
}