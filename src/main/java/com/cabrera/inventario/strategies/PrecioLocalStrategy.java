package com.cabrera.inventario.strategies;

public class PrecioLocalStrategy implements PrecioStrategy{
    @Override
    public double calcularPrecio(String codigoProducto, double precioBase) {
        System.out.println("Calculando precio de forma local...");
        return precioBase * 1.16;
    }
}
