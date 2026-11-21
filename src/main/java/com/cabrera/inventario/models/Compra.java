package com.cabrera.inventario.models;

import java.time.LocalDateTime;

// Representa la cabecera de una compra realizada a un proveedor
public class Compra {
    // Identificador interno de la compra
    private int idCompra;
    // Proveedor relacionado con la operación
    private int idProveedor;
    // Código legible que identifica la compra
    private String codigoCompra;
    // Fecha y hora en que se registró la operación
    private LocalDateTime fecha;
    // Estado de seguimiento de la compra
    private String estado;

    // Constructor vacío usado al mapear datos
    public Compra() {
    }

    // Crea una compra con sus datos principales
    public Compra(int idCompra, int idProveedor, String codigoCompra, LocalDateTime fecha, String estado) {
        this.idCompra = idCompra;
        this.idProveedor = idProveedor;
        this.codigoCompra = codigoCompra;
        this.fecha = fecha;
        this.estado = estado;
    }

    // Devuelve el identificador de la compra
    public int getIdCompra() {
        return idCompra;
    }

    // Asigna el identificador de la compra
    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    // Devuelve el proveedor asociado
    public int getIdProveedor() {
        return idProveedor;
    }

    // Cambia el proveedor asociado
    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    // Devuelve el código comercial de la compra
    public String getCodigoCompra() {
        return codigoCompra;
    }

    // Actualiza el código comercial de la compra
    public void setCodigoCompra(String codigoCompra) {
        this.codigoCompra = codigoCompra;
    }

    // Devuelve la fecha y hora de la compra
    public LocalDateTime getFecha() {
        return fecha;
    }

    // Actualiza la fecha y hora de la compra
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    // Devuelve el estado de seguimiento
    public String getEstado() {
        return estado;
    }

    // Cambia el estado de seguimiento
    public void setEstado(String estado) {
        this.estado = estado;
    }
}
