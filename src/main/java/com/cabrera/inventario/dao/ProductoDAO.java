package com.cabrera.inventario.dao;

import com.cabrera.inventario.models.Producto;

// Define consultas específicas del catálogo además del CRUD genérico
public interface ProductoDAO extends DaoGenerico<Producto> {

    // Busca el producto por el código que se usa en etiquetas y escáneres
    Producto obtenerPorCodigo(String codigo);
}
