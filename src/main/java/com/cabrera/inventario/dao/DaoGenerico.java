package com.cabrera.inventario.dao;

import java.util.List;

// Contrato genérico que evita repetir las operaciones CRUD de cada entidad
public interface DaoGenerico<T> {

    // Inserta una nueva entidad y reporta si MySQL aceptó la operación
    boolean crear(T entidad);

    // Busca una entidad por su identificador principal
    T obtenerPorId(int id);

    // Recupera todas las entidades disponibles de este tipo
    List<T> obtenerTodos();

    // Persiste los cambios de una entidad existente
    boolean actualizar(T entidad);

    // Elimina la entidad asociada al identificador recibido
    boolean eliminar(int id);
}
