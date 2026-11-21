package com.cabrera.inventario.models;

// Representa un producto del catálogo y sus existencias actuales
public class Producto {

    // Identificador único generado por MySQL
    private int idProducto;
    // Código único que se imprime en la etiqueta del producto
    private String codigo;
    // Nombre y descripción que identifican el artículo
    private String nombre;
    private String descripcion;
    // Precios de referencia guardados en el catálogo
    private double precioCompra;
    private double precioVenta;
    // Existencias disponibles; los cambios se registran mediante movimientos
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

    // Devuelve el identificador de base de datos
    public int getIdProducto() {
        return idProducto;
    }

    // Actualiza el identificador de base de datos
    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    // Devuelve el código que identifica al producto
    public String getCodigo() {
        return codigo;
    }

    // Cambia el código del producto
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    // Devuelve el nombre visible del producto
    public String getNombre() {
        return nombre;
    }

    // Actualiza el nombre del producto
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Devuelve la descripción del producto
    public String getDescripcion() {
        return descripcion;
    }

    // Actualiza la descripción del producto
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // Devuelve el precio de adquisición registrado
    public double getPrecioCompra() {
        return precioCompra;
    }

    // Actualiza el precio de adquisición
    public void setPrecioCompra(double precioCompra) {
        this.precioCompra = precioCompra;
    }

    // Devuelve el precio de venta registrado
    public double getPrecioVenta() {
        return precioVenta;
    }

    // Actualiza el precio de venta
    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    // Devuelve las unidades disponibles en inventario
    public int getStockActual() {
        return stockActual;
    }

    // Cambia el stock en memoria; para persistir ajustes se debe usar un movimiento
    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }
}
