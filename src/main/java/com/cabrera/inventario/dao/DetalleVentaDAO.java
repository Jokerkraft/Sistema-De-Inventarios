package com.cabrera.inventario.dao;

import com.cabrera.inventario.models.DetalleVenta;
import java.util.List;

// Declara consultas específicas para los productos asociados a una venta.
public interface DetalleVentaDAO extends DaoGenerico<DetalleVenta> {

    // Recupera todas las líneas de detalle pertenecientes a una venta
    List<DetalleVenta> obtenerPorVenta(int idVenta);
    DetalleVenta obtenerPorClave(int idVenta, int idProducto);
    boolean eliminar(int idVenta, int idProducto);
}
