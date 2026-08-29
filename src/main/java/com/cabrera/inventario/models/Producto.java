package com.cabrera.inventario.models;

public class Producto {

    private int idProducto;
    private String codigo;
    private String nombre;
    private String descripcion;
    private double precioCompra;
    private double precioVenta;
    private int stockActual;

    // Constructor vacío
    public Producto() {}

    // Constructor con los parámetros usados por la aplicación
    public Producto(int idProducto, String codigo, String nombre, String descripcion, double precioVenta, int stockActual) {
        this(idProducto, codigo, nombre, descripcion, 0.0, precioVenta, stockActual);
    }

    // Constructor completo con Precio Compra
    public Producto(int idProducto, String codigo, String nombre, String descripcion, double precioCompra, double precioVenta, int stockActual) {
        this.idProducto = idProducto;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioCompra = precioCompra;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(double precioCompra) {
        this.precioCompra = precioCompra;
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
