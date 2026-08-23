package com.cabrera.inventario.models;

public class Producto {

    private int idProducto;
    private String codigo;
    private String nombre;
    private double precioVenta;
    private int stockActual;

    // Constructor vacío
    public Producto() {}

    // Constructor con todos los parámetros
    public Producto(int idProducto, String codigo, String nombre, double precioVenta, int stockActual) {
        this.idProducto = idProducto;
        this.codigo = codigo;
        this.nombre = nombre;
        this.precioVenta = precioVenta;
        this.stockActual = stockActual;
    }

    // Getters y Setters

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }
}
