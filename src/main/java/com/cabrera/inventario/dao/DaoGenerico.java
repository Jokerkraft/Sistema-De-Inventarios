package com.cabrera.inventario.dao;

import java.util.List;

// Interfaza genérica para el CRUD de cualquier modelo
public interface DaoGenerico<T> {
    boolean crear(T entidad);
    T obtenerPorId(int id);
    List<T> obtenerTodos();
    boolean actualizar(T entidad);
    boolean eliminar(int id);
}
