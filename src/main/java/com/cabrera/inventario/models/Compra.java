package com.cabrera.inventario.models;

import java.time.LocalDateTime;

public class Compra {
    private int idCompra;
    private int idProveedor;
    private String codigoCompra;
    private LocalDateTime fecha;
    private String estado;

    public Compra() {
    }

    public Compra(int idCompra, int idProveedor, String codigoCompra, LocalDateTime fecha, String estado) {
        this.idCompra = idCompra;
        this.idProveedor = idProveedor;
        this.codigoCompra = codigoCompra;
        this.fecha = fecha;
        this.estado = estado;
    }

    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getCodigoCompra() {
        return codigoCompra;
    }

    public void setCodigoCompra(String codigoCompra) {
        this.codigoCompra = codigoCompra;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
