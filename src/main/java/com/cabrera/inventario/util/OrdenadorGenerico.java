package com.cabrera.inventario.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Ordena copias de listas de cualquier tipo usando el criterio recibido
public final class OrdenadorGenerico {

    private OrdenadorGenerico() {
    }

    public static <T> List<T> ordenar(List<T> elementos, Comparator<? super T> comparador) {
        List<T> elementosOrdenados = new ArrayList<>(elementos);
        elementosOrdenados.sort(comparador);
        return elementosOrdenados;
    }
}
