package com.cabrera.inventario.dao;

import com.cabrera.inventario.models.Cliente;
import com.cabrera.inventario.util.ConexionBaseDatos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// Implementa el acceso a los clientes almacenados en MySQL
public class ClienteDAOImpl implements ClienteDAO {

    // Obtiene la conexión activa con la base de datos
    private Connection obtenerConexion() { return ConexionBaseDatos.obtenerInstancia().obtenerConexion(); }

    // Convierte una fila de MySQL en un objeto Cliente
    private Cliente mapearCliente(ResultSet resultado) throws SQLException {
        return new Cliente(resultado.getInt("id_cliente"), resultado.getString("nombre"),
                resultado.getString("apellido"), resultado.getString("documento"),
                resultado.getString("telefono"), resultado.getString("email"),
                resultado.getString("direccion"), resultado.getBoolean("activo"));
    }

    // Inserta un cliente con los datos proporcionados
    @Override
    public boolean crear(Cliente entidad) {
        String sql = "INSERT INTO clientes (nombre, apellido, documento, telefono, email, direccion, activo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement sentencia = obtenerConexion().prepareStatement(sql)) {
            sentencia.setString(1, entidad.getNombre()); sentencia.setString(2, entidad.getApellido());
            sentencia.setString(3, entidad.getDocumento()); sentencia.setString(4, entidad.getTelefono());
            sentencia.setString(5, entidad.getEmail()); sentencia.setString(6, entidad.getDireccion());
            sentencia.setBoolean(7, entidad.isActivo());
            return sentencia.executeUpdate() > 0;
        } catch (SQLException excepcion) { System.err.println("Error al crear cliente: " + excepcion.getMessage()); return false; }
    }

    // Busca un cliente por su identificador; devuelve null si no existe
    @Override
    public Cliente obtenerPorId(int id) {
        String sql = "SELECT * FROM clientes WHERE id_cliente = ?";
        try (PreparedStatement sentencia = obtenerConexion().prepareStatement(sql)) {
            sentencia.setInt(1, id);
            try (ResultSet resultado = sentencia.executeQuery()) { return resultado.next() ? mapearCliente(resultado) : null; }
        } catch (SQLException excepcion) { System.err.println("Error al buscar cliente: " + excepcion.getMessage()); return null; }
    }

    // Recupera todos los clientes y los ordena por nombre y apellido
    @Override
    public List<Cliente> obtenerTodos() {
        List<Cliente> clientes = new ArrayList<>();
        try (Statement sentencia = obtenerConexion().createStatement(); ResultSet resultado = sentencia.executeQuery("SELECT * FROM clientes ORDER BY nombre, apellido")) {
            while (resultado.next()) clientes.add(mapearCliente(resultado));
        } catch (SQLException excepcion) { System.err.println("Error al obtener clientes: " + excepcion.getMessage()); }
        return clientes;
    }

    // Actualiza los datos del cliente identificado por su ID
    @Override
    public boolean actualizar(Cliente entidad) {
        String sql = "UPDATE clientes SET nombre = ?, apellido = ?, documento = ?, telefono = ?, email = ?, direccion = ?, activo = ? WHERE id_cliente = ?";
        try (PreparedStatement sentencia = obtenerConexion().prepareStatement(sql)) {
            sentencia.setString(1, entidad.getNombre()); sentencia.setString(2, entidad.getApellido());
            sentencia.setString(3, entidad.getDocumento()); sentencia.setString(4, entidad.getTelefono());
            sentencia.setString(5, entidad.getEmail()); sentencia.setString(6, entidad.getDireccion());
            sentencia.setBoolean(7, entidad.isActivo()); sentencia.setInt(8, entidad.getIdCliente());
            return sentencia.executeUpdate() > 0;
        } catch (SQLException excepcion) { System.err.println("Error al actualizar cliente: " + excepcion.getMessage()); return false; }
    }

    // Elimina el cliente que corresponde al identificador recibido
    @Override
    public boolean eliminar(int id) {
        try (PreparedStatement sentencia = obtenerConexion().prepareStatement("DELETE FROM clientes WHERE id_cliente = ?")) {
            sentencia.setInt(1, id); return sentencia.executeUpdate() > 0;
        } catch (SQLException excepcion) { System.err.println("Error al eliminar cliente: " + excepcion.getMessage()); return false; }
    }
}
