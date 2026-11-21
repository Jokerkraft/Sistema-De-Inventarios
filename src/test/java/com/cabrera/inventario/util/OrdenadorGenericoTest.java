package com.cabrera.inventario.util;

import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Verifica el comportamiento y la seguridad de uso del ordenador genérico.
class OrdenadorGenericoTest {

    @Test
    // Comprueba que se ordena una copia y no se modifica la lista de origen.
    void ordenaUnaCopiaSinModificarLaListaOriginal() {
        List<String> nombres = List.of("Zorro", "abeja", "Lobo");

        List<String> ordenados = OrdenadorGenerico.ordenar(
                nombres,
                Comparator.comparing(String::toLowerCase)
        );

        assertEquals(List.of("abeja", "Lobo", "Zorro"), ordenados);
        assertEquals(List.of("Zorro", "abeja", "Lobo"), nombres);
    }
}
