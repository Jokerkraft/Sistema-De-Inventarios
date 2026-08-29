package com.cabrera.inventario.dao;

import com.cabrera.inventario.models.Proveedor;
import com.cabrera.inventario.util.ConexionBaseDatos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAOImpl implements ProveedorDAO {

    private static final String SQL_CREAR_TABLA = "CREATE TABLE IF NOT EXISTS proveedores (" +
            "id_proveedor INT NOT NULL AUTO_INCREMENT, " +
            "nombre VARCHAR(150) NOT NULL, " +
            "contacto VARCHAR(150) DEFAULT NULL, " +
            "telefono VARCHAR(50) DEFAULT NULL, " +
            "email VARCHAR(150) DEFAULT NULL, " +
            "direccion VARCHAR(255) DEFAULT NULL, " +
            "activo BOOLEAN NOT NULL DEFAULT TRUE, " +
            "PRIMARY KEY (id_proveedor) " +
            ")";

    private Connection obtenerConexion() {
        return ConexionBaseDatos.obtenerInstancia().obtenerConexion();
    }

    private void asegurarTablaExiste() {
        try (Statement sentencia = obtenerConexion().createStatement()) {
            sentencia.executeUpdate(SQL_CREAR_TABLA);
        } catch (SQLException excepcion) {
            System.err.println("Error al asegurar la tabla de proveedores: " + excepcion.getMessage());
        }
    }

    private Proveedor mapearProveedor(ResultSet resultado) throws SQLException {
        return new Proveedor(
                resultado.getInt("id_proveedor"),
                resultado.getString("nombre"),
                resultado.getString("contacto"),
                resultado.getString("telefono"),
                resultado.getString("email"),
                resultado.getString("direccion"),
                resultado.getBoolean("activo")
        );
    }

    @Override
    public boolean crear(Proveedor entidad) {
        asegurarTablaExiste();
        String consulta = "INSERT INTO proveedores (nombre, contacto, telefono, email, direccion, activo) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement sentencia = obtenerConexion().prepareStatement(consulta)) {
            sentencia.setString(1, entidad.getNombre());
            sentencia.setString(2, entidad.getContacto());
            sentencia.setString(3, entidad.getTelefono());
            sentencia.setString(4, entidad.getEmail());
            sentencia.setString(5, entidad.getDireccion());
            sentencia.setBoolean(6, entidad.isActivo());
            return sentencia.executeUpdate() > 0;
        } catch (SQLException excepcion) {
            System.err.println("Error al crear proveedor: " + excepcion.getMessage());
            return false;
        }
    }

    @Override
    public Proveedor obtenerPorId(int id) {
        asegurarTablaExiste();
        String consulta = "SELECT * FROM proveedores WHERE id_proveedor = ?";

        try (PreparedStatement sentencia = obtenerConexion().prepareStatement(consulta)) {
            sentencia.setInt(1, id);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return mapearProveedor(resultado);
                }
            }
        } catch (SQLException excepcion) {
            System.err.println("Error al buscar proveedor por ID: " + excepcion.getMessage());
        }
        return null;
    }

    @Override
    public List<Proveedor> obtenerTodos() {
        asegurarTablaExiste();
        List<Proveedor> proveedores = new ArrayList<>();
        String consulta = "SELECT * FROM proveedores ORDER BY id_proveedor";

        try (Statement sentencia = obtenerConexion().createStatement();
             ResultSet resultado = sentencia.executeQuery(consulta)) {

            while (resultado.next()) {
                proveedores.add(mapearProveedor(resultado));
            }
        } catch (SQLException excepcion) {
            System.err.println("Error al obtener proveedores: " + excepcion.getMessage());
        }
        return proveedores;
    }

    @Override
    public boolean actualizar(Proveedor entidad) {
        asegurarTablaExiste();
        String consulta = "UPDATE proveedores SET nombre = ?, contacto = ?, telefono = ?, email = ?, direccion = ?, activo = ? WHERE id_proveedor = ?";

        try (PreparedStatement sentencia = obtenerConexion().prepareStatement(consulta)) {
            sentencia.setString(1, entidad.getNombre());
            sentencia.setString(2, entidad.getContacto());
            sentencia.setString(3, entidad.getTelefono());
            sentencia.setString(4, entidad.getEmail());
            sentencia.setString(5, entidad.getDireccion());
            sentencia.setBoolean(6, entidad.isActivo());
            sentencia.setInt(7, entidad.getIdProveedor());
            return sentencia.executeUpdate() > 0;
        } catch (SQLException excepcion) {
            System.err.println("Error al actualizar proveedor: " + excepcion.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        asegurarTablaExiste();
        String consulta = "DELETE FROM proveedores WHERE id_proveedor = ?";

        try (PreparedStatement sentencia = obtenerConexion().prepareStatement(consulta)) {
            sentencia.setInt(1, id);
            return sentencia.executeUpdate() > 0;
        } catch (SQLException excepcion) {
            System.err.println("Error al eliminar proveedor: " + excepcion.getMessage());
            return false;
        }
    }
}
