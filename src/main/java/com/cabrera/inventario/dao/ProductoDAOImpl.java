package com.cabrera.inventario.dao;

import com.cabrera.inventario.models.Producto;
import com.cabrera.inventario.util.ConexionBaseDatos;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAOImpl implements ProductoDAO{

    public List<Producto> obtenerTodos() {
        List<Producto> listaProductos = new ArrayList<>();
        String consulta = "SELECT * FROM productos";

        // Obtenemos la conexión afuera del try para que no se cierre
        Connection conexion = ConexionBaseDatos.obtenerInstancia().obtenerConexion();

        try (Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(consulta)) {

            while (resultado.next()) {
                Producto producto = new Producto(
                        resultado.getInt("id_producto"),
                        resultado.getString("codigo"),
                        resultado.getString("nombre"),
                        resultado.getDouble("precio_venta"),
                        resultado.getInt("stock_actual")
                );
                listaProductos.add(producto);
            }
        } catch (SQLException excepcion) {
            System.err.println("Error al obtener los productos: " + excepcion.getMessage());
        }
        return listaProductos;
    }


    // Métodos del CRUD
    @Override
    public boolean crear(Producto entidad) {
        return false;
    }

    @Override
    public Producto obtenerPorId(int id) {
        return null;
    }

    @Override
    public boolean actualizar(Producto entidad) {
        return false;
    }

    @Override
    public boolean eliminar(int id) {
        return false;
    }
}
