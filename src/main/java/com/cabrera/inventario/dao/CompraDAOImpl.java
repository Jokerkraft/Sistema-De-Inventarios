package com.cabrera.inventario.dao;

import com.cabrera.inventario.models.Compra;
import com.cabrera.inventario.util.ConexionBaseDatos;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Administra las cabeceras de compra; los renglones se gestionan en DetalleCompraDAO
public class CompraDAOImpl implements CompraDAO {

    // Obtiene la conexión activa con la base de datos
    private Connection conexion() { return ConexionBaseDatos.obtenerInstancia().obtenerConexion(); }

    // Convierte una fila de la tabla compras en un objeto Compra
    private Compra mapear(ResultSet r) throws SQLException {
        Timestamp fecha = r.getTimestamp("fecha");
        return new Compra(r.getInt("id_compra"), r.getInt("id_proveedor"), r.getString("codigo_compra"),
                fecha == null ? null : fecha.toLocalDateTime(), r.getString("estado"));
    }

    // Registra una compra nueva y usa la fecha actual si no se especificó una
    @Override public boolean crear(Compra e) {
        String sql = "INSERT INTO compras (id_proveedor, codigo_compra, fecha, estado) VALUES (?, ?, ?, ?)";
        try (PreparedStatement s = conexion().prepareStatement(sql)) {
            s.setInt(1, e.getIdProveedor()); s.setString(2, e.getCodigoCompra());
            s.setTimestamp(3, Timestamp.valueOf(e.getFecha() == null ? java.time.LocalDateTime.now() : e.getFecha()));
            s.setString(4, e.getEstado()); return s.executeUpdate() > 0;
        } catch (SQLException x) { System.err.println("Error al crear compra: " + x.getMessage()); return false; }
    }

    // Busca una compra por su identificador; devuelve null si no existe
    @Override public Compra obtenerPorId(int id) {
        try (PreparedStatement s = conexion().prepareStatement("SELECT * FROM compras WHERE id_compra = ?")) {
            s.setInt(1, id); try (ResultSet r = s.executeQuery()) { return r.next() ? mapear(r) : null; }
        } catch (SQLException x) { System.err.println("Error al buscar compra: " + x.getMessage()); return null; }
    }

    // Recupera todas las compras, ordenadas de la más reciente a la más antigua
    @Override public List<Compra> obtenerTodos() {
        List<Compra> compras = new ArrayList<>();
        try (Statement s = conexion().createStatement(); ResultSet r = s.executeQuery("SELECT * FROM compras ORDER BY fecha DESC")) { while (r.next()) compras.add(mapear(r)); }
        catch (SQLException x) { System.err.println("Error al obtener compras: " + x.getMessage()); }
        return compras;
    }

    // Actualiza los datos de la compra identificada por su ID
    @Override public boolean actualizar(Compra e) {
        String sql = "UPDATE compras SET id_proveedor = ?, codigo_compra = ?, fecha = ?, estado = ? WHERE id_compra = ?";
        try (PreparedStatement s = conexion().prepareStatement(sql)) {
            s.setInt(1, e.getIdProveedor()); s.setString(2, e.getCodigoCompra());
            s.setTimestamp(3, Timestamp.valueOf(e.getFecha() == null ? java.time.LocalDateTime.now() : e.getFecha()));
            s.setString(4, e.getEstado()); s.setInt(5, e.getIdCompra()); return s.executeUpdate() > 0;
        } catch (SQLException x) { System.err.println("Error al actualizar compra: " + x.getMessage()); return false; }
    }

    // Elimina la compra que corresponde al identificador recibido
    @Override public boolean eliminar(int id) {
        try (PreparedStatement s = conexion().prepareStatement("DELETE FROM compras WHERE id_compra = ?")) { s.setInt(1, id); return s.executeUpdate() > 0; }
        catch (SQLException x) { System.err.println("Error al eliminar compra: " + x.getMessage()); return false; }
    }
}
