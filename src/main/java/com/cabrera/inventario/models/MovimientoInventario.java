package com.cabrera.inventario.models;

import java.time.LocalDateTime;

public class MovimientoInventario {
    private int idMovimiento;
    private String tipo;
    private int idProducto;
    private int cantidad;
    private String motivo;
    private LocalDateTime fecha;
    private String referencia;
    private int idUsuario;

    public MovimientoInventario() {
    }

    public MovimientoInventario(int idMovimiento, String tipo, int idProducto, int cantidad,
                               String motivo, LocalDateTime fecha, String referencia, int idUsuario) {
        this.idMovimiento = idMovimiento;
        this.tipo = tipo;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.motivo = motivo;
        this.fecha = fecha;
        this.referencia = referencia;
        this.idUsuario = idUsuario;
    }

    public int getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
}
