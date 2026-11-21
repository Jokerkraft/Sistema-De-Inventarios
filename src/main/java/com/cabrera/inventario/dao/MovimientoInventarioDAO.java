package com.cabrera.inventario.dao;

import com.cabrera.inventario.models.MovimientoInventario;
import java.util.List;

public interface MovimientoInventarioDAO extends DaoGenerico<MovimientoInventario> {
    boolean registrarMovimiento(MovimientoInventario movimiento);
    List<MovimientoInventario> obtenerRecientes(int limite);
}
