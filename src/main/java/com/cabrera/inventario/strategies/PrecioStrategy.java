package com.cabrera.inventario.strategies;

// Define una forma intercambiable de obtener un precio sugerido
public interface PrecioStrategy {

    // Calcula una sugerencia a partir del código y del precio base del producto
    double calcularPrecio(String codigoProducto, double precioBase);
}
