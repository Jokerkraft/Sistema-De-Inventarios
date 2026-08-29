package com.cabrera.inventario.models;

public class DetalleCompra {
    private int idDetalleCompra;
    private int idCompra;
    private String codigoProducto;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    public DetalleCompra() {
    }

    public DetalleCompra(int idDetalleCompra, int idCompra, String codigoProducto, int cantidad, double precioUnitario, double subtotal) {
        this.idDetalleCompra = idDetalleCompra;
        this.idCompra = idCompra;
        this.codigoProducto = codigoProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    public int getIdDetalleCompra() {
        return idDetalleCompra;
    }

    public void setIdDetalleCompra(int idDetalleCompra) {
        this.idDetalleCompra = idDetalleCompra;
    }

    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
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
