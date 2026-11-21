package com.cabrera.inventario.models;

// Representa una línea de producto incluida en una compra
public class DetalleCompra {
    // Identificador de la cabecera de compra
    private int idCompra;
    // Identificador del producto comprado
    private int idProducto;
    // Unidades compradas; debe ser mayor que cero
    private int cantidad;
    // Costo histórico por unidad en esta compra
    private double precioUnitario;

    // Constructor vacío para mapear registros de la base de datos
    public DetalleCompra() {
    }

    // Inicializa la línea con compra, producto, unidades y costo unitario
    public DetalleCompra(int idCompra, int idProducto, int cantidad, double precioUnitario) {
        this.idCompra = idCompra;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    // Devuelve la compra a la que pertenece este detalle
    public int getIdCompra() {
        return idCompra;
    }

    // Asigna la compra propietaria de esta línea
    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    // Devuelve el producto incluido
    public int getIdProducto() {
        return idProducto;
    }

    // Cambia el producto incluido
    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    // Devuelve las unidades compradas
    public int getCantidad() {
        return cantidad;
    }

    // Actualiza las unidades compradas
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    // Devuelve el costo unitario
    public double getPrecioUnitario() {
        return precioUnitario;
    }

    // Actualiza el costo unitario
    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    // Calcula el subtotal sin duplicar ni almacenar el importe derivado
    public double getSubtotal() {
        return cantidad * precioUnitario;
    }
}
