package com.cabrera.inventario.dao;

import com.cabrera.inventario.models.DetalleCompra;
import com.cabrera.inventario.util.ConexionBaseDatos;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Maneja los renglones de una compra mediante su clave compuesta
public class DetalleCompraDAOImpl implements DetalleCompraDAO {
    // Obtiene la conexión JDBC compartida para las consultas de detalle
    private Connection conexion(){return ConexionBaseDatos.obtenerInstancia().obtenerConexion();}
    // Convierte una fila de la tabla de detalle al modelo correspondiente
    private DetalleCompra mapear(ResultSet r)throws SQLException{return new DetalleCompra(r.getInt("id_compra"),r.getInt("id_producto"),r.getInt("cantidad"),r.getDouble("precio_unitario"));}
    // Inserta la combinación compra-producto, clave primaria compuesta de la tabla
    @Override public boolean crear(DetalleCompra e){String sql="INSERT INTO detalle_compras (id_compra, id_producto, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";try(PreparedStatement s=conexion().prepareStatement(sql)){s.setInt(1,e.getIdCompra());s.setInt(2,e.getIdProducto());s.setInt(3,e.getCantidad());s.setDouble(4,e.getPrecioUnitario());return s.executeUpdate()>0;}catch(SQLException x){System.err.println("Error al crear detalle de compra: "+x.getMessage());return false;}}
    // Consulta un renglón usando las dos columnas de su clave compuesta
    @Override public DetalleCompra obtenerPorClave(int compra,int producto){String sql="SELECT * FROM detalle_compras WHERE id_compra = ? AND id_producto = ?";try(PreparedStatement s=conexion().prepareStatement(sql)){s.setInt(1,compra);s.setInt(2,producto);try(ResultSet r=s.executeQuery()){return r.next()?mapear(r):null;}}catch(SQLException x){System.err.println("Error al buscar detalle de compra: "+x.getMessage());return null;}}
    // Recupera los renglones de una compra específica en orden de producto
    @Override public List<DetalleCompra> obtenerPorCompra(int compra){List<DetalleCompra> detalles=new ArrayList<>();try(PreparedStatement s=conexion().prepareStatement("SELECT * FROM detalle_compras WHERE id_compra = ? ORDER BY id_producto")){s.setInt(1,compra);try(ResultSet r=s.executeQuery()){while(r.next())detalles.add(mapear(r));}}catch(SQLException x){System.err.println("Error al obtener detalles de compra: "+x.getMessage());}return detalles;}
    // Adapta DaoGenerico para devolver el primer renglón de la compra
    @Override public DetalleCompra obtenerPorId(int idCompra){List<DetalleCompra> detalles=obtenerPorCompra(idCompra);return detalles.isEmpty()?null:detalles.get(0);}
    // Lista todos los renglones sin filtrar por una cabecera
    @Override public List<DetalleCompra> obtenerTodos(){List<DetalleCompra> detalles=new ArrayList<>();try(Statement s=conexion().createStatement();ResultSet r=s.executeQuery("SELECT * FROM detalle_compras ORDER BY id_compra, id_producto")){while(r.next())detalles.add(mapear(r));}catch(SQLException x){System.err.println("Error al obtener detalles de compra: "+x.getMessage());}return detalles;}
    // Actualiza cantidad y precio sin cambiar la clave compra-producto
    @Override public boolean actualizar(DetalleCompra e){String sql="UPDATE detalle_compras SET cantidad = ?, precio_unitario = ? WHERE id_compra = ? AND id_producto = ?";try(PreparedStatement s=conexion().prepareStatement(sql)){s.setInt(1,e.getCantidad());s.setDouble(2,e.getPrecioUnitario());s.setInt(3,e.getIdCompra());s.setInt(4,e.getIdProducto());return s.executeUpdate()>0;}catch(SQLException x){System.err.println("Error al actualizar detalle de compra: "+x.getMessage());return false;}}
    // Elimina solo el renglón indicado por la compra y el producto
    @Override public boolean eliminar(int compra,int producto){try(PreparedStatement s=conexion().prepareStatement("DELETE FROM detalle_compras WHERE id_compra = ? AND id_producto = ?")){s.setInt(1,compra);s.setInt(2,producto);return s.executeUpdate()>0;}catch(SQLException x){System.err.println("Error al eliminar detalle de compra: "+x.getMessage());return false;}}
    // Adapta DaoGenerico para eliminar todos los renglones de una compra
    @Override public boolean eliminar(int idCompra){try(PreparedStatement s=conexion().prepareStatement("DELETE FROM detalle_compras WHERE id_compra = ?")){s.setInt(1,idCompra);return s.executeUpdate()>0;}catch(SQLException x){System.err.println("Error al eliminar detalles de compra: "+x.getMessage());return false;}}
}
