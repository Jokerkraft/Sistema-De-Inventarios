package com.cabrera.inventario.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBaseDatos {

    // Instancia estática privada (Patrón Singleton)
    private static ConexionBaseDatos instancia;
    private Connection conexion;

    // Credenciales de la base de datos MySQL
    private static final String URL = "jdbc:mysql://localhost:3307/sistema_inventarios";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "admin";

    private ConexionBaseDatos() {
        try {
            // Establecer la conexión con JDBC
            this.conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            System.out.println("Conexión exitosa a la base de datos");
        } catch (SQLException e) {
            System.err.println("Conexión fallida a la base de datos");
        }
    }

    // Método estático para obtener la única instancia
    public static ConexionBaseDatos obtenerInstancia() {
        if (instancia == null) {
            instancia = new ConexionBaseDatos();
        }
        return instancia;
    }

    // Método para obtener el objeto Connection y hacer consultas
    public Connection obtenerConexion() {
        return conexion;
    }

    // Método para cerrar la conexión de forma segura
    public void cerrarConexion() {
        if (conexion != null) {
            try {
                conexion.close();
                System.out.println("Conexión a la base de datos cerrada");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}
