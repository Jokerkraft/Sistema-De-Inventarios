package com.cabrera.inventario.dao;

import com.cabrera.inventario.models.MovimientoInventario;
import com.cabrera.inventario.util.ConexionBaseDatos;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Registra y consulta el historial de entradas, salidas o ajustes de inventario
public class MovimientoInventarioDAOImpl implements MovimientoInventarioDAO {
    private Connection conexion(){return ConexionBaseDatos.obtenerInstancia().obtenerConexion();}
    private MovimientoInventario mapear(ResultSet r)throws SQLException{Timestamp f=r.getTimestamp("fecha");return new MovimientoInventario(r.getInt("id_movimiento"),r.getString("tipo"),r.getInt("id_producto"),r.getInt("cantidad"),r.getString("motivo"),f==null?null:f.toLocalDateTime(),r.getString("referencia"),r.getInt("id_usuario"));}
    private void usuarioOpcional(PreparedStatement s,int posicion,int idUsuario)throws SQLException{if(idUsuario>0)s.setInt(posicion,idUsuario);else s.setNull(posicion,Types.INTEGER);}
    @Override public boolean crear(MovimientoInventario e){String sql="INSERT INTO movimientos (tipo, id_producto, cantidad, motivo, fecha, referencia, id_usuario) VALUES (?, ?, ?, ?, ?, ?, ?)";try(PreparedStatement s=conexion().prepareStatement(sql)){s.setString(1,e.getTipo());s.setInt(2,e.getIdProducto());s.setInt(3,e.getCantidad());s.setString(4,e.getMotivo());s.setTimestamp(5,Timestamp.valueOf(e.getFecha()==null?java.time.LocalDateTime.now():e.getFecha()));s.setString(6,e.getReferencia());usuarioOpcional(s,7,e.getIdUsuario());return s.executeUpdate()>0;}catch(SQLException x){System.err.println("Error al crear movimiento: "+x.getMessage());return false;}}
    @Override public MovimientoInventario obtenerPorId(int id){try(PreparedStatement s=conexion().prepareStatement("SELECT * FROM movimientos WHERE id_movimiento = ?")){s.setInt(1,id);try(ResultSet r=s.executeQuery()){return r.next()?mapear(r):null;}}catch(SQLException x){System.err.println("Error al buscar movimiento: "+x.getMessage());return null;}}
    @Override public List<MovimientoInventario> obtenerTodos(){List<MovimientoInventario> movimientos=new ArrayList<>();try(Statement s=conexion().createStatement();ResultSet r=s.executeQuery("SELECT * FROM movimientos ORDER BY fecha DESC")){while(r.next())movimientos.add(mapear(r));}catch(SQLException x){System.err.println("Error al obtener movimientos: "+x.getMessage());}return movimientos;}
    @Override public boolean actualizar(MovimientoInventario e){String sql="UPDATE movimientos SET tipo = ?, id_producto = ?, cantidad = ?, motivo = ?, fecha = ?, referencia = ?, id_usuario = ? WHERE id_movimiento = ?";try(PreparedStatement s=conexion().prepareStatement(sql)){s.setString(1,e.getTipo());s.setInt(2,e.getIdProducto());s.setInt(3,e.getCantidad());s.setString(4,e.getMotivo());s.setTimestamp(5,Timestamp.valueOf(e.getFecha()==null?java.time.LocalDateTime.now():e.getFecha()));s.setString(6,e.getReferencia());usuarioOpcional(s,7,e.getIdUsuario());s.setInt(8,e.getIdMovimiento());return s.executeUpdate()>0;}catch(SQLException x){System.err.println("Error al actualizar movimiento: "+x.getMessage());return false;}}
    @Override public boolean eliminar(int id){try(PreparedStatement s=conexion().prepareStatement("DELETE FROM movimientos WHERE id_movimiento = ?")){s.setInt(1,id);return s.executeUpdate()>0;}catch(SQLException x){System.err.println("Error al eliminar movimiento: "+x.getMessage());return false;}}
}
