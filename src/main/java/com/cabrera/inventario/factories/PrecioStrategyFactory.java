package com.cabrera.inventario.factories;

import com.cabrera.inventario.strategies.PrecioAPIStrategy;
import com.cabrera.inventario.strategies.PrecioLocalStrategy;
import com.cabrera.inventario.strategies.PrecioStrategy;

// Fábrica que encapsula la creación de estrategias de cálculo de precios
public final class PrecioStrategyFactory {

    // Opciones disponibles para seleccionar una estrategia de precio
    public enum TipoEstrategia {
        API,
        LOCAL
    }

    // Evita instanciar una fábrica que solo contiene métodos estáticos
    private PrecioStrategyFactory() {
    }

    // Devuelve la implementación correspondiente según el tipo solicitado
    public static PrecioStrategy crearEstrategia(TipoEstrategia tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de estrategia no puede ser nulo.");
        }
        return switch (tipo) {
            case API -> new PrecioAPIStrategy();
            case LOCAL -> new PrecioLocalStrategy();
        };
    }
}