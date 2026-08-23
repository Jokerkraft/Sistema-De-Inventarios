package com.cabrera.inventario.dao;

import com.cabrera.inventario.models.Usuario;

public interface UsuarioDAO extends DaoGenerico<Usuario>{
    // Método solamente de esta interfaz para validar las credenciales
    Usuario autenticar(String nombreUsuario, String contrasenaEncriptada);
}
