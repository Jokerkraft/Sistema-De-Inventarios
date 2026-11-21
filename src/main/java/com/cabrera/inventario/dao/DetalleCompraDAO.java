package com.cabrera.inventario.dao;

import com.cabrera.inventario.models.DetalleCompra;
import java.util.List;

// Declara consultas específicas para los productos asociados a una compra
public interface DetalleCompraDAO extends DaoGenerico<DetalleCompra> {

    // Recupera todas las líneas de detalle pertenecientes a una compra
    List<DetalleCompra> obtenerPorCompra(int idCompra);
    DetalleCompra obtenerPorClave(int idCompra, int idProducto);
    boolean eliminar(int idCompra, int idProducto);
}
