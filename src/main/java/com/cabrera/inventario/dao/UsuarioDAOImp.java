package com.cabrera.inventario.dao;

import com.cabrera.inventario.models.Usuario;
import com.cabrera.inventario.util.ConexionBaseDatos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UsuarioDAOImp implements UsuarioDAO{

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
                    usuarioLogueado = new Usuario(
                            resultado.getInt("id_usuario"),
                            resultado.getString("nombre_completo"),
                            resultado.getString("username"),
                            resultado.getString("password"),
                            resultado.getString("rol")
                    );
                }
            }
        } catch (SQLException excepcion) {
            System.err.println("Error al autenticar en la base de datos: " + excepcion.getMessage());
        }

        return usuarioLogueado;
    }

    // Métodos del CRUD genéricos
    @Override
    public boolean crear(Usuario entidad) {
        return false;
    }

    @Override
    public Usuario obtenerPorId(int id) {
        return null;
    }

    @Override
    public List<Usuario> obtenerTodos() {
        return null;
    }

    @Override
    public boolean actualizar(Usuario entidad) {
        return false;
    }

    @Override
    public boolean eliminar(int id) {
        return false;
    }
}
