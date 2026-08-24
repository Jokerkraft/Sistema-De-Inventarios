package com.cabrera.inventario.strategies;

public interface PrecioStrategy {
    double calcularPrecio(String codigoProducto, double precioBase);
}
