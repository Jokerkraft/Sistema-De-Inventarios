package com.cabrera.inventario.models;

// Representa una línea de producto incluida en una venta
public class DetalleVenta {
    // Identificador de la cabecera de venta
    private int idVenta;
    // Identificador del producto vendido
    private int idProducto;
    // Unidades vendidas; debe ser mayor que cero
    private int cantidad;
    // Precio histórico por unidad en esta venta
    private double precioUnitario;

    // Constructor vacío para mapear registros de la base de datos
    public DetalleVenta() {
    }

    // Inicializa la línea con venta, producto, unidades y precio unitario
    public DetalleVenta(int idVenta, int idProducto, int cantidad, double precioUnitario) {
        this.idVenta = idVenta;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    // Devuelve la venta a la que pertenece este detalle
    public int getIdVenta() {
        return idVenta;
    }

    // Asigna la venta propietaria de esta línea
    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    // Devuelve el producto vendido
    public int getIdProducto() {
        return idProducto;
    }

    // Cambia el producto incluido en la venta
    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    // Devuelve las unidades vendidas
    public int getCantidad() {
        return cantidad;
    }

    // Actualiza las unidades vendidas
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    // Devuelve el precio unitario
    public double getPrecioUnitario() {
        return precioUnitario;
    }

    // Actualiza el precio unitario
    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    // Calcula el subtotal de la línea a partir de cantidad y precio
    public double getSubtotal() {
        return cantidad * precioUnitario;
    }
}
