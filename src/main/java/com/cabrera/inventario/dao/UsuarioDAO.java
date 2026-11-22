package com.cabrera.inventario.dao;

import com.cabrera.inventario.models.Usuario;

// Extiende el CRUD genérico con la consulta requerida para iniciar sesión
public interface UsuarioDAO extends DaoGenerico<Usuario>{
    // Método solamente de esta interfaz para validar las credenciales
    Usuario autenticar(String nombreUsuario, String contrasenaEncriptada);
    Usuario obtenerPorUsername(String username);
}
