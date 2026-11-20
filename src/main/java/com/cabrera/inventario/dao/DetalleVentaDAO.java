package com.cabrera.inventario.dao;

import com.cabrera.inventario.models.DetalleVenta;
import java.util.List;

public interface DetalleVentaDAO extends DaoGenerico<DetalleVenta> {
    List<DetalleVenta> obtenerPorVenta(int idVenta);
    DetalleVenta obtenerPorClave(int idVenta, int idProducto);
    boolean eliminar(int idVenta, int idProducto);
}
