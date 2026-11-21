package com.cabrera.inventario.dao;

import com.cabrera.inventario.models.Usuario;
import com.cabrera.inventario.util.ConexionBaseDatos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Implementa las operaciones de acceso a los usuarios almacenados en MySQL
public class UsuarioDAOImp implements UsuarioDAO{

    // Convierte una fila de la tabla usuarios en un objeto Usuario.
    private Usuario mapearUsuario(ResultSet resultado) throws SQLException {
        return new Usuario(resultado.getInt("id_usuario"), resultado.getString("nombre_completo"),
                resultado.getString("username"), resultado.getString("password"),
                resultado.getString("rol"), resultado.getBoolean("activo"));
    }

    // Busca un usuario activo que coincida con el nombre y la contraseña recibidos.
    @Override
    public Usuario autenticar(String nombreUsuario, String contrasenaEncriptada) {
        Usuario usuarioLogueado = null;
        String consulta = "SELECT * FROM usuarios WHERE username = ? AND password = ? AND activo = TRUE";

        // Obtenemos la conexión afuera del try para que Java NO la cierre automáticamente
        Connection conexion = ConexionBaseDatos.obtenerInstancia().obtenerConexion();

        // Ponemos el PreparedStatement en el try
        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {

            sentencia.setString(1, nombreUsuario);
            sentencia.setString(2, contrasenaEncriptada);

            // El ResultSet también lo manejamos con su propio try
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    usuarioLogueado = mapearUsuario(resultado);
                }
            }
        } catch (SQLException excepcion) {
            System.err.println("Error al autenticar en la base de datos: " + excepcion.getMessage());
        }

        return usuarioLogueado;
    }

    // La contraseña recibida debe estar en SHA-1 antes de llamar a crear o actualizar
    @Override
    public boolean crear(Usuario entidad) {
        String sql = "INSERT INTO usuarios (nombre_completo, username, password, rol, activo) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement sentencia = ConexionBaseDatos.obtenerInstancia().obtenerConexion().prepareStatement(sql)) {
            sentencia.setString(1, entidad.getNombreCompleto());
            sentencia.setString(2, entidad.getNombreUsuario());
            sentencia.setString(3, entidad.getContrasena());
            sentencia.setString(4, entidad.getRol());
            sentencia.setBoolean(5, entidad.isActivo());
            return sentencia.executeUpdate() > 0;
        } catch (SQLException excepcion) { System.err.println("Error al crear usuario: " + excepcion.getMessage()); return false; }
    }

    // Busca un usuario por su identificador; devuelve null si no existe
    @Override
    public Usuario obtenerPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id_usuario = ?";
        try (PreparedStatement sentencia = ConexionBaseDatos.obtenerInstancia().obtenerConexion().prepareStatement(sql)) {
            sentencia.setInt(1, id);
            try (ResultSet resultado = sentencia.executeQuery()) { return resultado.next() ? mapearUsuario(resultado) : null; }
        } catch (SQLException excepcion) { System.err.println("Error al buscar usuario: " + excepcion.getMessage()); return null; }
    }

    // Recupera todos los usuarios ordenados por nombre completo
    @Override
    public List<Usuario> obtenerTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nombre_completo";
        try (PreparedStatement sentencia = ConexionBaseDatos.obtenerInstancia().obtenerConexion().prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) usuarios.add(mapearUsuario(resultado));
        } catch (SQLException excepcion) { System.err.println("Error al obtener usuarios: " + excepcion.getMessage()); }
        return usuarios;
    }

    // Actualiza los datos del usuario identificado por su ID
    @Override
    public boolean actualizar(Usuario entidad) {
        String sql = "UPDATE usuarios SET nombre_completo = ?, username = ?, password = ?, rol = ?, activo = ? WHERE id_usuario = ?";
        try (PreparedStatement sentencia = ConexionBaseDatos.obtenerInstancia().obtenerConexion().prepareStatement(sql)) {
            sentencia.setString(1, entidad.getNombreCompleto()); sentencia.setString(2, entidad.getNombreUsuario());
            sentencia.setString(3, entidad.getContrasena()); sentencia.setString(4, entidad.getRol());
            sentencia.setBoolean(5, entidad.isActivo()); sentencia.setInt(6, entidad.getIdUsuario());
            return sentencia.executeUpdate() > 0;
        } catch (SQLException excepcion) { System.err.println("Error al actualizar usuario: " + excepcion.getMessage()); return false; }
    }

    // Elimina el usuario que corresponde al identificador recibido
    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM usuarios WHERE id_usuario = ? "
                + "AND NOT EXISTS (SELECT 1 FROM movimientos WHERE id_usuario = ?)";
        try (PreparedStatement sentencia = ConexionBaseDatos.obtenerInstancia().obtenerConexion().prepareStatement(sql)) {
            sentencia.setInt(1, id);
            sentencia.setInt(2, id);
            return sentencia.executeUpdate() > 0;
        } catch (SQLException excepcion) { System.err.println("Error al eliminar usuario: " + excepcion.getMessage()); return false; }
    }
}
