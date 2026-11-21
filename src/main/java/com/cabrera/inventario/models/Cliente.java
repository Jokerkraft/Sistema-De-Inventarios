package com.cabrera.inventario.models;

// Representa a una persona registrada como cliente del negocio
public class Cliente {
    // Identificador asignado por MySQL
    private int idCliente;
    // Nombre y apellidos usados para identificar al cliente
    private String nombre;
    private String apellido;
    // Datos de identificación y contacto, opcionales según el esquema
    private String documento;
    private String telefono;
    private String email;
    private String direccion;
    // Permite conservar el registro sin ofrecerlo como cliente activo
    private boolean activo;

    // Constructor vacío requerido para crear y mapear clientes
    public Cliente() {
    }

    // Inicializa el cliente con sus datos personales y estado
    public Cliente(int idCliente, String nombre, String apellido, String documento, String telefono, String email, String direccion, boolean activo) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.activo = activo;
    }

    // Devuelve la clave primaria del cliente
    public int getIdCliente() {
        return idCliente;
    }

    // Asigna la clave primaria del cliente
    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    // Devuelve el nombre del cliente
    public String getNombre() {
        return nombre;
    }

    // Cambia el nombre del cliente
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Devuelve los apellidos del cliente
    public String getApellido() {
        return apellido;
    }

    // Cambia los apellidos del cliente
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    // Devuelve el documento de identificación
    public String getDocumento() {
        return documento;
    }

    // Actualiza el documento de identificación
    public void setDocumento(String documento) {
        this.documento = documento;
    }

    // Devuelve el teléfono de contacto
    public String getTelefono() {
        return telefono;
    }

    // Actualiza el teléfono de contacto
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    // Devuelve el correo electrónico de contacto
    public String getEmail() {
        return email;
    }

    // Actualiza el correo electrónico de contacto
    public void setEmail(String email) {
        this.email = email;
    }

    // Devuelve la dirección registrada
    public String getDireccion() {
        return direccion;
    }

    // Actualiza la dirección registrada
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    // Indica si el cliente se encuentra activo
    public boolean isActivo() {
        return activo;
    }

    // Cambia el estado de actividad del cliente
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
