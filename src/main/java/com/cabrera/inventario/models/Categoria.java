package com.cabrera.inventario.models;

// Representa una categoría que agrupa productos relacionados
public class Categoria {
    // Identificador asignado por la base de datos
    private int idCategoria;
    // Nombre único visible en el catálogo
    private String nombre;
    // Información opcional que describe la categoría
    private String descripcion;
    // Indica si la categoría puede seguir utilizándose
    private boolean activo;

    // Constructor vacío utilizado por herramientas de persistencia y JavaFX
    public Categoria() {
    }

    // Crea una categoría con todos los valores recuperados o validados
    public Categoria(int idCategoria, String nombre, String descripcion, boolean activo) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
    }

    // Devuelve el identificador de la categoría
    public int getIdCategoria() {
        return idCategoria;
    }

    // Actualiza el identificador de la categoría
    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    // Devuelve el nombre que se muestra en el catálogo
    public String getNombre() {
        return nombre;
    }

    // Cambia el nombre de la categoría
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Devuelve la descripción opcional
    public String getDescripcion() {
        return descripcion;
    }

    // Cambia la descripción de la categoría
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // Indica si la categoría está habilitada
    public boolean isActivo() {
        return activo;
    }

    // Activa o desactiva la categoría
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
