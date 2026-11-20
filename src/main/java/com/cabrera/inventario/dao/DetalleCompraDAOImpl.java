package com.cabrera.inventario.dao;

import com.cabrera.inventario.models.DetalleCompra;
import com.cabrera.inventario.util.ConexionBaseDatos;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Maneja los renglones de una compra mediante su clave compuesta
public class DetalleCompraDAOImpl implements DetalleCompraDAO {
    private Connection conexion(){return ConexionBaseDatos.obtenerInstancia().obtenerConexion();}
    private DetalleCompra mapear(ResultSet r)throws SQLException{return new DetalleCompra(r.getInt("id_compra"),r.getInt("id_producto"),r.getInt("cantidad"),r.getDouble("precio_unitario"));}
    @Override public boolean crear(DetalleCompra e){String sql="INSERT INTO detalle_compras (id_compra, id_producto, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";try(PreparedStatement s=conexion().prepareStatement(sql)){s.setInt(1,e.getIdCompra());s.setInt(2,e.getIdProducto());s.setInt(3,e.getCantidad());s.setDouble(4,e.getPrecioUnitario());return s.executeUpdate()>0;}catch(SQLException x){System.err.println("Error al crear detalle de compra: "+x.getMessage());return false;}}
    @Override public DetalleCompra obtenerPorClave(int compra,int producto){String sql="SELECT * FROM detalle_compras WHERE id_compra = ? AND id_producto = ?";try(PreparedStatement s=conexion().prepareStatement(sql)){s.setInt(1,compra);s.setInt(2,producto);try(ResultSet r=s.executeQuery()){return r.next()?mapear(r):null;}}catch(SQLException x){System.err.println("Error al buscar detalle de compra: "+x.getMessage());return null;}}
    @Override public List<DetalleCompra> obtenerPorCompra(int compra){List<DetalleCompra> detalles=new ArrayList<>();try(PreparedStatement s=conexion().prepareStatement("SELECT * FROM detalle_compras WHERE id_compra = ? ORDER BY id_producto")){s.setInt(1,compra);try(ResultSet r=s.executeQuery()){while(r.next())detalles.add(mapear(r));}}catch(SQLException x){System.err.println("Error al obtener detalles de compra: "+x.getMessage());}return detalles;}
    // Adapta DaoGenerico para devolver el primer renglón de la compra
    @Override public DetalleCompra obtenerPorId(int idCompra){List<DetalleCompra> detalles=obtenerPorCompra(idCompra);return detalles.isEmpty()?null:detalles.get(0);}
    @Override public List<DetalleCompra> obtenerTodos(){List<DetalleCompra> detalles=new ArrayList<>();try(Statement s=conexion().createStatement();ResultSet r=s.executeQuery("SELECT * FROM detalle_compras ORDER BY id_compra, id_producto")){while(r.next())detalles.add(mapear(r));}catch(SQLException x){System.err.println("Error al obtener detalles de compra: "+x.getMessage());}return detalles;}
    @Override public boolean actualizar(DetalleCompra e){String sql="UPDATE detalle_compras SET cantidad = ?, precio_unitario = ? WHERE id_compra = ? AND id_producto = ?";try(PreparedStatement s=conexion().prepareStatement(sql)){s.setInt(1,e.getCantidad());s.setDouble(2,e.getPrecioUnitario());s.setInt(3,e.getIdCompra());s.setInt(4,e.getIdProducto());return s.executeUpdate()>0;}catch(SQLException x){System.err.println("Error al actualizar detalle de compra: "+x.getMessage());return false;}}
    @Override public boolean eliminar(int compra,int producto){try(PreparedStatement s=conexion().prepareStatement("DELETE FROM detalle_compras WHERE id_compra = ? AND id_producto = ?")){s.setInt(1,compra);s.setInt(2,producto);return s.executeUpdate()>0;}catch(SQLException x){System.err.println("Error al eliminar detalle de compra: "+x.getMessage());return false;}}
    // Adapta DaoGenerico para eliminar todos los renglones de una compra
    @Override public boolean eliminar(int idCompra){try(PreparedStatement s=conexion().prepareStatement("DELETE FROM detalle_compras WHERE id_compra = ?")){s.setInt(1,idCompra);return s.executeUpdate()>0;}catch(SQLException x){System.err.println("Error al eliminar detalles de compra: "+x.getMessage());return false;}}
}
