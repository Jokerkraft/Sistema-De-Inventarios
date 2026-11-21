package com.cabrera.inventario.dao;

import com.cabrera.inventario.models.MovimientoInventario;
import java.util.List;

// Define las operaciones de persistencia para el historial de existencias
public interface MovimientoInventarioDAO extends DaoGenerico<MovimientoInventario> {

    // Registra el movimiento y aplica el cambio de stock como una sola operación
    boolean registrarMovimiento(MovimientoInventario movimiento);

    // Obtiene el historial más reciente limitado al número solicitado
    List<MovimientoInventario> obtenerRecientes(int limite);
}
