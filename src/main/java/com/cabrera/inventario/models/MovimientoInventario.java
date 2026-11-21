package com.cabrera.inventario.models;

import java.time.LocalDateTime;

// Conserva el registro de una entrada o salida de existencias y su responsable
public class MovimientoInventario {
    // Identificador único del movimiento almacenado
    private int idMovimiento;
    // Tipo de operación, normalmente ENTRADA o SALIDA
    private String tipo;
    // Producto afectado por el movimiento
    private int idProducto;
    // Unidades registradas en la operación
    private int cantidad;
    // Explicación opcional del motivo del ajuste
    private String motivo;
    // Fecha y hora del movimiento
    private LocalDateTime fecha;
    // Identificador externo o referencia relacionada con la operación
    private String referencia;
    // Usuario que efectuó el movimiento
    private int idUsuario;
    // Nombres de presentación cargados para el reporte; no forman parte del registro base
    private String nombreProducto;
    private String nombreUsuario;

    // Constructor vacío utilizado por mapeadores y herramientas de interfaz
    public MovimientoInventario() {
    }

    // Crea un movimiento con sus datos de auditoría
    public MovimientoInventario(int idMovimiento, String tipo, int idProducto, int cantidad,
                               String motivo, LocalDateTime fecha, String referencia, int idUsuario) {
        this.idMovimiento = idMovimiento;
        this.tipo = tipo;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.motivo = motivo;
        this.fecha = fecha;
        this.referencia = referencia;
        this.idUsuario = idUsuario;
    }

    // Devuelve el identificador de auditoría
    public int getIdMovimiento() {
        return idMovimiento;
    }

    // Asigna el identificador de auditoría
    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    // Devuelve si la operación agrega o retira stock
    public String getTipo() {
        return tipo;
    }

    // Cambia el tipo de operación
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    // Devuelve el identificador del producto afectado
    public int getIdProducto() {
        return idProducto;
    }

    // Asigna el producto afectado
    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    // Devuelve la cantidad absoluta registrada
    public int getCantidad() {
        return cantidad;
    }

    // Actualiza la cantidad absoluta del movimiento
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    // Devuelve el motivo del ajuste
    public String getMotivo() {
        return motivo;
    }

    // Cambia el motivo del ajuste
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    // Devuelve la marca de tiempo registrada
    public LocalDateTime getFecha() {
        return fecha;
    }

    // Asigna la marca de tiempo
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    // Devuelve la referencia externa, si existe
    public String getReferencia() {
        return referencia;
    }

    // Asigna una referencia externa al movimiento
    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    // Devuelve quién realizó la operación
    public int getIdUsuario() {
        return idUsuario;
    }

    // Asigna al usuario responsable
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    // Devuelve el nombre de producto unido para mostrarlo en el reporte
    public String getNombreProducto() {
        return nombreProducto;
    }

    // Establece el nombre de presentación del producto
    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    // Devuelve el nombre del responsable unido para mostrarlo en el reporte
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    // Establece el nombre de presentación del usuario responsable
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
}
