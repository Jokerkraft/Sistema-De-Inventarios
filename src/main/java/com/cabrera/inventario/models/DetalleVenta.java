package com.cabrera.inventario.models;

public class DetalleVenta {
    private int idDetalleVenta;
    private int idVenta;
    private String codigoProducto;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    public DetalleVenta() {
    }

    public DetalleVenta(int idDetalleVenta, int idVenta, String codigoProducto, int cantidad, double precioUnitario, double subtotal) {
        this.idDetalleVenta = idDetalleVenta;
        this.idVenta = idVenta;
        this.codigoProducto = codigoProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    public int getIdDetalleVenta() {
        return idDetalleVenta;
    }

    public void setIdDetalleVenta(int idDetalleVenta) {
        this.idDetalleVenta = idDetalleVenta;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}
