package com.cabrera.inventario.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionBaseDatos {

    private static ConexionBaseDatos instancia;
    private Connection conexion;
    private static final String PROPERTIES_FILE = "database.properties";

    private ConexionBaseDatos() {
        try {
            Properties propiedades = cargarPropiedades();
            String url = propiedades.getProperty("db.url");
            String usuario = propiedades.getProperty("db.user");
            String contrasena = propiedades.getProperty("db.password");

            this.conexion = DriverManager.getConnection(url, usuario, contrasena);
            System.out.println("Conexión exitosa a la base de datos");
        } catch (SQLException e) {
            System.err.println("Conexión fallida a la base de datos: " + e.getMessage());
        }
    }

    private Properties cargarPropiedades() {
        Properties propiedades = new Properties();
        try (InputStream entrada = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (entrada == null) {
                System.err.println("No se encontró el archivo " + PROPERTIES_FILE);
                return propiedades;
            }
            propiedades.load(entrada);
        } catch (Exception e) {
            System.err.println("Error al cargar propiedades: " + e.getMessage());
        }
        return propiedades;
    }

    public static ConexionBaseDatos obtenerInstancia() {
        if (instancia == null) {
            instancia = new ConexionBaseDatos();
        }
        return instancia;
    }

    public Connection obtenerConexion() {
        return conexion;
    }

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
