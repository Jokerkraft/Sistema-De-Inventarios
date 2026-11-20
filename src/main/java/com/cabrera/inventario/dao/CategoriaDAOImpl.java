package com.cabrera.inventario.dao;

import com.cabrera.inventario.models.Categoria;
import com.cabrera.inventario.util.ConexionBaseDatos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// Implementa las operaciones CRUD de la tabla categorias
public class CategoriaDAOImpl implements CategoriaDAO {

    // Obtiene la conexión activa con la base de datos
    private Connection obtenerConexion() {
        return ConexionBaseDatos.obtenerInstancia().obtenerConexion();
    }

    // Convierte una fila SQL en un objeto del modelo
    private Categoria mapearCategoria(ResultSet resultado) throws SQLException {
        return new Categoria(resultado.getInt("id_categoria"), resultado.getString("nombre"),
                resultado.getString("descripcion"), resultado.getBoolean("activo"));
    }

    // Registra una categoría nueva en la base de datos
    @Override
    public boolean crear(Categoria entidad) {
        String sql = "INSERT INTO categorias (nombre, descripcion, activo) VALUES (?, ?, ?)";
        try (PreparedStatement sentencia = obtenerConexion().prepareStatement(sql)) {
            sentencia.setString(1, entidad.getNombre());
            sentencia.setString(2, entidad.getDescripcion());
            sentencia.setBoolean(3, entidad.isActivo());
            return sentencia.executeUpdate() > 0;
        } catch (SQLException excepcion) {
            System.err.println("Error al crear categoría: " + excepcion.getMessage());
            return false;
        }
    }

    // Busca una categoría por su identificador
    @Override
    public Categoria obtenerPorId(int id) {
        String sql = "SELECT * FROM categorias WHERE id_categoria = ?";
        try (PreparedStatement sentencia = obtenerConexion().prepareStatement(sql)) {
            sentencia.setInt(1, id);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? mapearCategoria(resultado) : null;
            }
        } catch (SQLException excepcion) {
            System.err.println("Error al buscar categoría: " + excepcion.getMessage());
            return null;
        }
    }

    // Obtiene todas las categorías ordenadas por nombre
    @Override
    public List<Categoria> obtenerTodos() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM categorias ORDER BY nombre";
        try (Statement sentencia = obtenerConexion().createStatement(); ResultSet resultado = sentencia.executeQuery(sql)) {
            while (resultado.next()) categorias.add(mapearCategoria(resultado));
        } catch (SQLException excepcion) {
            System.err.println("Error al obtener categorías: " + excepcion.getMessage());
        }
        return categorias;
    }

    // Actualiza los datos de una categoría existente
    @Override
    public boolean actualizar(Categoria entidad) {
        String sql = "UPDATE categorias SET nombre = ?, descripcion = ?, activo = ? WHERE id_categoria = ?";
        try (PreparedStatement sentencia = obtenerConexion().prepareStatement(sql)) {
            sentencia.setString(1, entidad.getNombre());
            sentencia.setString(2, entidad.getDescripcion());
            sentencia.setBoolean(3, entidad.isActivo());
            sentencia.setInt(4, entidad.getIdCategoria());
            return sentencia.executeUpdate() > 0;
        } catch (SQLException excepcion) {
            System.err.println("Error al actualizar categoría: " + excepcion.getMessage());
            return false;
        }
    }

    // Elimina una categoría según su identificador
    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM categorias WHERE id_categoria = ?";
        try (PreparedStatement sentencia = obtenerConexion().prepareStatement(sql)) {
            sentencia.setInt(1, id);
            return sentencia.executeUpdate() > 0;
        } catch (SQLException excepcion) {
            System.err.println("Error al eliminar categoría: " + excepcion.getMessage());
            return false;
        }
    }
}
