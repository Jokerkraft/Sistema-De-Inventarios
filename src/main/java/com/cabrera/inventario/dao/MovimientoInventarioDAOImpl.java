package com.cabrera.inventario.dao;

import com.cabrera.inventario.models.MovimientoInventario;
import com.cabrera.inventario.util.ConexionBaseDatos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// Administra movimientos y actualiza las existencias en la misma transacción
public class MovimientoInventarioDAOImpl implements MovimientoInventarioDAO {

    // Obtiene la conexión compartida con la base de datos
    private Connection conexion() {
        return ConexionBaseDatos.obtenerInstancia().obtenerConexion();
    }

    // Convierte la fila y los nombres relacionados en el modelo usado por la tabla
    private MovimientoInventario mapear(ResultSet resultado) throws SQLException {
        Timestamp fecha = resultado.getTimestamp("fecha");
        MovimientoInventario movimiento = new MovimientoInventario(
                resultado.getInt("id_movimiento"),
                resultado.getString("tipo"),
                resultado.getInt("id_producto"),
                resultado.getInt("cantidad"),
                resultado.getString("motivo"),
                fecha == null ? null : fecha.toLocalDateTime(),
                resultado.getString("referencia"),
                resultado.getInt("id_usuario")
        );

        movimiento.setNombreProducto(resultado.getString("nombre_producto"));
        movimiento.setNombreUsuario(resultado.getString("nombre_usuario"));
        return movimiento;
    }

    // Escribe NULL cuando no se recibió un identificador de usuario
    private void asignarUsuario(PreparedStatement sentencia, int posicion, int idUsuario) throws SQLException {
        if (idUsuario > 0) {
            sentencia.setInt(posicion, idUsuario);
        } else {
            sentencia.setNull(posicion, Types.INTEGER);
        }
    }

    // Registra el movimiento y el cambio de stock de forma atómica
    @Override
    public boolean registrarMovimiento(MovimientoInventario movimiento) {
        validarMovimiento(movimiento);

        Connection conexion = conexion();
        synchronized (conexion) {
            boolean autocommit = true;
            try {
                autocommit = conexion.getAutoCommit();
                conexion.setAutoCommit(false);

                int diferencia = movimiento.getTipo().equalsIgnoreCase("ENTRADA")
                        ? movimiento.getCantidad()
                        : -movimiento.getCantidad();

                if (!actualizarStock(conexion, movimiento.getIdProducto(), diferencia)
                        || !insertarMovimiento(conexion, movimiento)) {
                    conexion.rollback();
                    return false;
                }

                conexion.commit();
                return true;
            } catch (SQLException excepcion) {
                try {
                    conexion.rollback();
                } catch (SQLException errorRollback) {
                    System.err.println("Error al revertir el movimiento: " + errorRollback.getMessage());
                }
                System.err.println("Error al registrar movimiento: " + excepcion.getMessage());
                return false;
            } finally {
                try {
                    conexion.setAutoCommit(autocommit);
                } catch (SQLException excepcion) {
                    System.err.println("Error al restaurar la conexión: " + excepcion.getMessage());
                }
            }
        }
    }

    // Rechaza tipos desconocidos, cantidades no positivas y operaciones sin responsable
    private void validarMovimiento(MovimientoInventario movimiento) {
        if (movimiento == null
                || movimiento.getIdProducto() <= 0
                || movimiento.getCantidad() <= 0
                || movimiento.getIdUsuario() <= 0
                || (!"ENTRADA".equalsIgnoreCase(movimiento.getTipo())
                && !"SALIDA".equalsIgnoreCase(movimiento.getTipo()))) {
            throw new IllegalArgumentException("El movimiento debe tener producto, usuario, tipo y cantidad válidos.");
        }
    }

    // Aplica el cambio solo si el producto existe y el resultado no queda negativo
    private boolean actualizarStock(Connection conexion, int idProducto, int diferencia) throws SQLException {
        String sql = "UPDATE productos SET stock_actual = stock_actual + ? "
                + "WHERE id_producto = ? AND stock_actual + ? >= 0";

        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, diferencia);
            sentencia.setInt(2, idProducto);
            sentencia.setInt(3, diferencia);
            return sentencia.executeUpdate() == 1;
        }
    }

    // Inserta el registro de auditoría dentro de la transacción activa
    private boolean insertarMovimiento(Connection conexion, MovimientoInventario movimiento) throws SQLException {
        String sql = "INSERT INTO movimientos "
                + "(tipo, id_producto, cantidad, motivo, fecha, referencia, id_usuario) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, movimiento.getTipo().toUpperCase(Locale.ROOT));
            sentencia.setInt(2, movimiento.getIdProducto());
            sentencia.setInt(3, movimiento.getCantidad());
            sentencia.setString(4, movimiento.getMotivo());
            sentencia.setTimestamp(5, Timestamp.valueOf(
                    movimiento.getFecha() == null
                            ? java.time.LocalDateTime.now()
                            : movimiento.getFecha()
            ));
            sentencia.setString(6, movimiento.getReferencia());
            asignarUsuario(sentencia, 7, movimiento.getIdUsuario());
            return sentencia.executeUpdate() == 1;
        }
    }

    // Conserva compatibilidad con el CRUD genérico y siempre usa el flujo transaccional
    @Override
    public boolean crear(MovimientoInventario movimiento) {
        return registrarMovimiento(movimiento);
    }

    // Busca un movimiento y agrega nombres de producto y usuario para el reporte
    @Override
    public MovimientoInventario obtenerPorId(int id) {
        String sql = consultaMovimientos() + " WHERE m.id_movimiento = ?";
        try (PreparedStatement sentencia = conexion().prepareStatement(sql)) {
            sentencia.setInt(1, id);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? mapear(resultado) : null;
            }
        } catch (SQLException excepcion) {
            System.err.println("Error al buscar movimiento: " + excepcion.getMessage());
            return null;
        }
    }

    // Recupera el historial completo ordenado del movimiento más reciente al más antiguo
    @Override
    public List<MovimientoInventario> obtenerTodos() {
        List<MovimientoInventario> movimientos = new ArrayList<>();
        String sql = consultaMovimientos() + " ORDER BY m.fecha DESC, m.id_movimiento DESC";

        try (Statement sentencia = conexion().createStatement();
             ResultSet resultado = sentencia.executeQuery(sql)) {
            while (resultado.next()) {
                movimientos.add(mapear(resultado));
            }
        } catch (SQLException excepcion) {
            System.err.println("Error al obtener movimientos: " + excepcion.getMessage());
        }
        return movimientos;
    }

    // Limita la consulta del reporte a los últimos movimientos solicitados
    @Override
    public List<MovimientoInventario> obtenerRecientes(int limite) {
        if (limite <= 0) {
            throw new IllegalArgumentException("El límite debe ser mayor que cero.");
        }

        List<MovimientoInventario> movimientos = new ArrayList<>();
        String sql = consultaMovimientos() + " ORDER BY m.fecha DESC, m.id_movimiento DESC LIMIT ?";
        try (PreparedStatement sentencia = conexion().prepareStatement(sql)) {
            sentencia.setInt(1, limite);
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    movimientos.add(mapear(resultado));
                }
            }
        } catch (SQLException excepcion) {
            System.err.println("Error al obtener movimientos recientes: " + excepcion.getMessage());
        }
        return movimientos;
    }

    // Comparte los JOIN necesarios para mostrar nombres en lugar de solo identificadores
    private String consultaMovimientos() {
        return "SELECT m.*, p.nombre AS nombre_producto, u.nombre_completo AS nombre_usuario "
                + "FROM movimientos m "
                + "JOIN productos p ON p.id_producto = m.id_producto "
                + "LEFT JOIN usuarios u ON u.id_usuario = m.id_usuario";
    }

    // Los movimientos se conservan como auditoría y no se editan ni eliminan
    @Override
    public boolean actualizar(MovimientoInventario movimiento) {
        throw new UnsupportedOperationException("Los movimientos de inventario son inmutables.");
    }

    @Override
    public boolean eliminar(int id) {
        throw new UnsupportedOperationException("Los movimientos de inventario son inmutables.");
    }
}
