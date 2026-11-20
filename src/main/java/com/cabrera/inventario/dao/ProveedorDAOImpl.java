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

// Implementa el acceso a los proveedores almacenados en MySQL
public class ProveedorDAOImpl implements ProveedorDAO {

    // Obtiene la conexión activa con la base de datos
    private Connection obtenerConexion() {
        return ConexionBaseDatos.obtenerInstancia().obtenerConexion();
    }

    // Convierte una fila de MySQL en un objeto Proveedor
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

    // Registra un proveedor nuevo en la base de datos
    @Override
    public boolean crear(Proveedor entidad) {
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

    // Busca un proveedor por su identificador
    @Override
    public Proveedor obtenerPorId(int id) {
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

    // Obtiene todos los proveedores registrados
    @Override
    public List<Proveedor> obtenerTodos() {
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

    // Actualiza los datos de un proveedor existente
    @Override
    public boolean actualizar(Proveedor entidad) {
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

    // Elimina un proveedor según su identificador
    @Override
    public boolean eliminar(int id) {
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
