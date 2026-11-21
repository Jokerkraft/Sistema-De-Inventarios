package com.cabrera.inventario.models;

import java.time.LocalDateTime;

// Representa la cabecera de una venta efectuada a un cliente
public class Venta {
    // Identificador interno asignado a la venta
    private int idVenta;
    // Cliente asociado a la operación
    private int idCliente;
    // Código legible que identifica la venta
    private String codigoVenta;
    // Fecha y hora del registro.
    private LocalDateTime fecha;
    // Estado de la venta para su seguimiento
    private String estado;
    // Forma de pago utilizada
    private String metodoPago;

    // Constructor vacío utilizado al mapear datos
    public Venta() {
    }

    // Crea la venta con todos sus datos de cabecera
    public Venta(int idVenta, int idCliente, String codigoVenta, LocalDateTime fecha, String estado, String metodoPago) {
        this.idVenta = idVenta;
        this.idCliente = idCliente;
        this.codigoVenta = codigoVenta;
        this.fecha = fecha;
        this.estado = estado;
        this.metodoPago = metodoPago;
    }

    // Devuelve el identificador interno de la venta
    public int getIdVenta() {
        return idVenta;
    }

    // Asigna el identificador interno de la venta
    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    // Devuelve el cliente relacionado con la operación
    public int getIdCliente() {
        return idCliente;
    }

    // Actualiza el cliente relacionado
    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    // Devuelve el código comercial de la venta
    public String getCodigoVenta() {
        return codigoVenta;
    }

    // Actualiza el código comercial de la venta
    public void setCodigoVenta(String codigoVenta) {
        this.codigoVenta = codigoVenta;
    }

    // Devuelve la fecha y hora de la operación
    public LocalDateTime getFecha() {
        return fecha;
    }

    // Actualiza la fecha y hora de la operación
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

    // Devuelve el método de pago registrado
    public String getMetodoPago() {
        return metodoPago;
    }

    // Actualiza el método de pago
    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
}
