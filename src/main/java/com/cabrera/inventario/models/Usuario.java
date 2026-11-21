package com.cabrera.inventario.models;

// Representa una cuenta habilitada para iniciar sesión y operar en el sistema
public class Usuario {
    // Identificador de la cuenta en la base de datos
    private int idUsuario;
    // Nombre que se muestra en la interfaz y el historial de movimientos
    private String nombreCompleto;
    // Nombre único usado para autenticar la cuenta
    private String nombreUsuario; // El username para el Login
    // Hash de la contraseña guardado por el sistema.
    private String contrasena; // Aqui se guarda el hash SHA-1
    // Perfil que determina las acciones permitidas en la interfaz
    private String rol;
    // Indica si la cuenta puede autenticarse
    private boolean activo;

    // Constructor vacío (Para traer datos de la base de datos)
    // Constructor vacío utilizado al mapear usuarios desde la base de datos.
    public Usuario() {

    }

    // Crea una cuenta activa con su perfil inicial
    public Usuario(int idUsuario, String nombreCompleto, String nombreUsuario, String contrasena, String rol) {
        this(idUsuario, nombreCompleto, nombreUsuario, contrasena, rol, true);
    }

    // Crea una cuenta con todos sus datos y estado de acceso
    public Usuario(int idUsuario, String nombreCompleto, String nombreUsuario, String contrasena, String rol, boolean activo) {
        this.idUsuario = idUsuario;
        this.nombreCompleto = nombreCompleto;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.rol = rol;
        this.activo = activo;
    }

    // Devuelve el identificador de la cuenta
    public int getIdUsuario() {
        return idUsuario;
    }

    // Asigna el identificador de la cuenta
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    // Devuelve el nombre completo del usuario
    public String getNombreCompleto() {
        return nombreCompleto;
    }

    // Cambia el nombre que se muestra en la aplicación
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    // Devuelve el nombre utilizado para iniciar sesión
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    // Cambia el nombre de inicio de sesión
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    // Devuelve el hash almacenado de la contraseña
    public String getContrasena() {
        return contrasena;
    }

    // Actualiza el hash de contraseña, no la contraseña en texto plano
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    // Devuelve el perfil de autorización del usuario
    public String getRol() {
        return rol;
    }

    // Cambia el perfil de autorización
    public void setRol(String rol) {
        this.rol = rol;
    }

    // Indica si la cuenta está habilitada para autenticarse
    public boolean isActivo() {
        return activo;
    }

    // Habilita o deshabilita la cuenta.
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
