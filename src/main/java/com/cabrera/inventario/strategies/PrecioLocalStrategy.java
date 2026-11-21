package com.cabrera.inventario.strategies;

// Calcula una sugerencia de precio usando una regla local, sin conexión externa
public class PrecioLocalStrategy implements PrecioStrategy{

    // Aplica el margen configurado al precio base recibido
    @Override
    public double calcularPrecio(String codigoProducto, double precioBase) {
        System.out.println("Calculando precio de forma local...");
        return precioBase * 1.16;
    }
}
