package com.cabrera.inventario.models;

// Representa a una empresa o persona que suministra productos al inventario
public class Proveedor {
    // Identificador asignado por la base de datos
    private int idProveedor;
    // Nombre comercial o razón social
    private String nombre;
    // Persona de contacto del proveedor
    private String contacto;
    // Medios para contactar al proveedor
    private String telefono;
    private String email;
    private String direccion;
    // Permite desactivar un proveedor sin borrar su historial
    private boolean activo;

    // Constructor vacío utilizado al crear o mapear proveedores
    public Proveedor() {
    }

    // Inicializa el proveedor con sus datos de contacto y estado
    public Proveedor(int idProveedor, String nombre, String contacto, String telefono, String email, String direccion, boolean activo) {
        this.idProveedor = idProveedor;
        this.nombre = nombre;
        this.contacto = contacto;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.activo = activo;
    }

    // Devuelve la clave primaria del proveedor
    public int getIdProveedor() {
        return idProveedor;
    }

    // Asigna la clave primaria del proveedor
    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    // Devuelve el nombre comercial del proveedor
    public String getNombre() {
        return nombre;
    }

    // Actualiza el nombre comercial del proveedor
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Devuelve el nombre de la persona de contacto
    public String getContacto() {
        return contacto;
    }

    // Actualiza la persona de contacto
    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    // Devuelve el teléfono de contacto
    public String getTelefono() {
        return telefono;
    }

    // Actualiza el teléfono de contacto
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    // Devuelve el correo de contacto
    public String getEmail() {
        return email;
    }

    // Actualiza el correo de contacto
    public void setEmail(String email) {
        this.email = email;
    }

    // Devuelve la dirección del proveedor
    public String getDireccion() {
        return direccion;
    }

    // Actualiza la dirección del proveedor
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    // Indica si el proveedor puede seleccionarse en nuevas operaciones
    public boolean isActivo() {
        return activo;
    }

    // Activa o desactiva al proveedor
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
