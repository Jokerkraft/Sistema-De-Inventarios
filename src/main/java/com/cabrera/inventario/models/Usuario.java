package com.cabrera.inventario.models;

public class Usuario {
    private int idUsuario;
    private String nombreCompleto;
    private String nombreUsuario; // El username para el Login
    private String contrasena; // Aqui se guarda el hash SHA-1
    private String rol;

    // Constructor vacío (Para traer datos de la base de datos)
    public Usuario() {

    }

    // Constructor con todos los parámetros
    public Usuario(int idUsuario, String nombreCompleto, String nombreUsuario, String contrasena, String rol) {
        this.idUsuario = idUsuario;
        this.nombreCompleto = nombreCompleto;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    // Getters y Setters
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
