package com.cabrera.inventario.dao;

import com.cabrera.inventario.models.DetalleVenta;
import com.cabrera.inventario.util.ConexionBaseDatos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// Implementa el acceso a los detalles de ventas almacenados en MySQL
public class DetalleVentaDAOImpl implements DetalleVentaDAO {

    // Obtiene la conexión activa con la base de datos
    private Connection conexion() {
        return ConexionBaseDatos.obtenerInstancia().obtenerConexion();
    }

    // Convierte el registro obtenido de MySQL en un objeto DetalleVenta
    private DetalleVenta mapear(ResultSet resultado) throws SQLException {
        return new DetalleVenta(
                resultado.getInt("id_venta"),
                resultado.getInt("id_producto"),
                resultado.getInt("cantidad"),
                resultado.getDouble("precio_unitario")
        );
    }

    // Registra un nuevo producto dentro de una venta.
    @Override
    public boolean crear(DetalleVenta detalle) {
        String sql = "INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";

        try (PreparedStatement sentencia = conexion().prepareStatement(sql)) {
            sentencia.setInt(1, detalle.getIdVenta());
            sentencia.setInt(2, detalle.getIdProducto());
            sentencia.setInt(3, detalle.getCantidad());
            sentencia.setDouble(4, detalle.getPrecioUnitario());
            return sentencia.executeUpdate() > 0;
        } catch (SQLException excepcion) {
            System.err.println("Error al crear detalle de venta: " + excepcion.getMessage());
            return false;
        }
    }

    // Busca un detalle mediante la combinación id_venta e id_producto.
    @Override
    public DetalleVenta obtenerPorClave(int venta, int producto) {
        String sql = "SELECT * FROM detalle_ventas WHERE id_venta = ? AND id_producto = ?";

        try (PreparedStatement sentencia = conexion().prepareStatement(sql)) {
            sentencia.setInt(1, venta);
            sentencia.setInt(2, producto);

            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? mapear(resultado) : null;
            }
        } catch (SQLException excepcion) {
            System.err.println("Error al buscar detalle de venta: " + excepcion.getMessage());
            return null;
        }
    }

    // Obtiene todos los productos asociados a una venta.
    @Override
    public List<DetalleVenta> obtenerPorVenta(int venta) {
        List<DetalleVenta> detalles = new ArrayList<>();
        String sql = "SELECT * FROM detalle_ventas WHERE id_venta = ? ORDER BY id_producto";

        try (PreparedStatement sentencia = conexion().prepareStatement(sql)) {
            sentencia.setInt(1, venta);

            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    detalles.add(mapear(resultado));
                }
            }
        } catch (SQLException excepcion) {
            System.err.println("Error al obtener detalles de venta: " + excepcion.getMessage());
        }
        return detalles;
    }

    // Adapta el método genérico para devolver el primer renglón de la venta.
    @Override
    public DetalleVenta obtenerPorId(int idVenta) {
        List<DetalleVenta> detalles = obtenerPorVenta(idVenta);
        return detalles.isEmpty() ? null : detalles.get(0);
    }

    // Obtiene todos los detalles registrados en todas las ventas.
    @Override
    public List<DetalleVenta> obtenerTodos() {
        List<DetalleVenta> detalles = new ArrayList<>();
        String sql = "SELECT * FROM detalle_ventas ORDER BY id_venta, id_producto";

        try (Statement sentencia = conexion().createStatement();
             ResultSet resultado = sentencia.executeQuery(sql)) {
            while (resultado.next()) {
                detalles.add(mapear(resultado));
            }
        } catch (SQLException excepcion) {
            System.err.println("Error al obtener detalles de venta: " + excepcion.getMessage());
        }
        return detalles;
    }

    // Actualiza la cantidad y el precio unitario de un detalle existente.
    @Override
    public boolean actualizar(DetalleVenta detalle) {
        String sql = "UPDATE detalle_ventas SET cantidad = ?, precio_unitario = ? WHERE id_venta = ? AND id_producto = ?";

        try (PreparedStatement sentencia = conexion().prepareStatement(sql)) {
            sentencia.setInt(1, detalle.getCantidad());
            sentencia.setDouble(2, detalle.getPrecioUnitario());
            sentencia.setInt(3, detalle.getIdVenta());
            sentencia.setInt(4, detalle.getIdProducto());
            return sentencia.executeUpdate() > 0;
        } catch (SQLException excepcion) {
            System.err.println("Error al actualizar detalle de venta: " + excepcion.getMessage());
            return false;
        }
    }

    // Elimina un producto específico dentro de una venta.
    @Override
    public boolean eliminar(int venta, int producto) {
        String sql = "DELETE FROM detalle_ventas WHERE id_venta = ? AND id_producto = ?";

        try (PreparedStatement sentencia = conexion().prepareStatement(sql)) {
            sentencia.setInt(1, venta);
            sentencia.setInt(2, producto);
            return sentencia.executeUpdate() > 0;
        } catch (SQLException excepcion) {
            System.err.println("Error al eliminar detalle de venta: " + excepcion.getMessage());
            return false;
        }
    }

    // Adapta el método genérico para eliminar todos los renglones de una venta.
    @Override
    public boolean eliminar(int idVenta) {
        String sql = "DELETE FROM detalle_ventas WHERE id_venta = ?";

        try (PreparedStatement sentencia = conexion().prepareStatement(sql)) {
            sentencia.setInt(1, idVenta);
            return sentencia.executeUpdate() > 0;
        } catch (SQLException excepcion) {
            System.err.println("Error al eliminar detalles de venta: " + excepcion.getMessage());
            return false;
        }
    }
}
