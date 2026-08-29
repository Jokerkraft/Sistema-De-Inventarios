package com.cabrera.inventario.models;

import java.time.LocalDateTime;

public class Venta {
    private int idVenta;
    private int idCliente;
    private String codigoVenta;
    private LocalDateTime fecha;
    private double total;
    private String estado;
    private String metodoPago;

    public Venta() {
    }

    public Venta(int idVenta, int idCliente, String codigoVenta, LocalDateTime fecha, double total, String estado, String metodoPago) {
        this.idVenta = idVenta;
        this.idCliente = idCliente;
        this.codigoVenta = codigoVenta;
        this.fecha = fecha;
        this.total = total;
        this.estado = estado;
        this.metodoPago = metodoPago;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getCodigoVenta() {
        return codigoVenta;
    }

    public void setCodigoVenta(String codigoVenta) {
        this.codigoVenta = codigoVenta;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
}
