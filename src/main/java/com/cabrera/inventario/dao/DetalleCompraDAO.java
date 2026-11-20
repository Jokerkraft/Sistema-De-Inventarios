package com.cabrera.inventario.dao;

import com.cabrera.inventario.models.DetalleCompra;
import java.util.List;

public interface DetalleCompraDAO extends DaoGenerico<DetalleCompra> {
    List<DetalleCompra> obtenerPorCompra(int idCompra);
    DetalleCompra obtenerPorClave(int idCompra, int idProducto);
    boolean eliminar(int idCompra, int idProducto);
}
