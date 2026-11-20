package com.cabrera.inventario.dao;

import com.cabrera.inventario.models.Venta;
import com.cabrera.inventario.util.ConexionBaseDatos;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Administra las cabeceras de venta; los productos se gestionan por separadoz
public class VentaDAOImpl implements VentaDAO {
    private Connection conexion() { return ConexionBaseDatos.obtenerInstancia().obtenerConexion(); }
    private Venta mapear(ResultSet r) throws SQLException { Timestamp f=r.getTimestamp("fecha"); return new Venta(r.getInt("id_venta"),r.getInt("id_cliente"),r.getString("codigo_venta"),f==null?null:f.toLocalDateTime(),r.getString("estado"),r.getString("metodo_pago")); }
    @Override public boolean crear(Venta e) {
        String sql="INSERT INTO ventas (id_cliente, codigo_venta, fecha, estado, metodo_pago) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement s=conexion().prepareStatement(sql)) { s.setInt(1,e.getIdCliente());s.setString(2,e.getCodigoVenta());s.setTimestamp(3,Timestamp.valueOf(e.getFecha()==null?java.time.LocalDateTime.now():e.getFecha()));s.setString(4,e.getEstado());s.setString(5,e.getMetodoPago());return s.executeUpdate()>0; }
        catch(SQLException x){System.err.println("Error al crear venta: "+x.getMessage());return false;}
    }
    @Override public Venta obtenerPorId(int id) { try(PreparedStatement s=conexion().prepareStatement("SELECT * FROM ventas WHERE id_venta = ?")){s.setInt(1,id);try(ResultSet r=s.executeQuery()){return r.next()?mapear(r):null;}}catch(SQLException x){System.err.println("Error al buscar venta: "+x.getMessage());return null;} }
    @Override public List<Venta> obtenerTodos(){List<Venta> ventas=new ArrayList<>();try(Statement s=conexion().createStatement();ResultSet r=s.executeQuery("SELECT * FROM ventas ORDER BY fecha DESC")){while(r.next())ventas.add(mapear(r));}catch(SQLException x){System.err.println("Error al obtener ventas: "+x.getMessage());}return ventas;}
    @Override public boolean actualizar(Venta e){String sql="UPDATE ventas SET id_cliente = ?, codigo_venta = ?, fecha = ?, estado = ?, metodo_pago = ? WHERE id_venta = ?";try(PreparedStatement s=conexion().prepareStatement(sql)){s.setInt(1,e.getIdCliente());s.setString(2,e.getCodigoVenta());s.setTimestamp(3,Timestamp.valueOf(e.getFecha()==null?java.time.LocalDateTime.now():e.getFecha()));s.setString(4,e.getEstado());s.setString(5,e.getMetodoPago());s.setInt(6,e.getIdVenta());return s.executeUpdate()>0;}catch(SQLException x){System.err.println("Error al actualizar venta: "+x.getMessage());return false;}}
    @Override public boolean eliminar(int id){try(PreparedStatement s=conexion().prepareStatement("DELETE FROM ventas WHERE id_venta = ?")){s.setInt(1,id);return s.executeUpdate()>0;}catch(SQLException x){System.err.println("Error al eliminar venta: "+x.getMessage());return false;}}
}
