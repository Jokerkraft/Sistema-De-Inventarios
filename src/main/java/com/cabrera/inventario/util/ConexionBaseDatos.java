package com.cabrera.inventario.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionBaseDatos {

    // Única instancia de la clase utilizada para compartir la conexión
    private static ConexionBaseDatos instancia;
    private Connection conexion;

    // Nombre del archivo que contiene los datos de acceso a MySQL
    private static final String PROPERTIES_FILE = "database.properties";

    // Constructor privado que establece la conexión al crear la instancia
    private ConexionBaseDatos() {
        try {
            // Lee la configuración de la URL, el usuario y la contraseña
            Properties propiedades = cargarPropiedades();
            String url = propiedades.getProperty("db.url");
            String usuario = propiedades.getProperty("db.user");
            String contrasena = propiedades.getProperty("db.password");

            // Abre la conexión con los datos obtenidos del archivo de propiedades
            this.conexion = DriverManager.getConnection(url, usuario, contrasena);
            System.out.println("Conexión exitosa a la base de datos");
        } catch (SQLException e) {
            System.err.println("Conexión fallida a la base de datos: " + e.getMessage());
        }
    }

    // Carga las propiedades de conexión desde el archivo database.properties
    private Properties cargarPropiedades() {
        Properties propiedades = new Properties();
        //
        try (InputStream entrada = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (entrada == null) {
                System.err.println("No se encontró el archivo " + PROPERTIES_FILE);
                return propiedades;
            }

            // Lee las claves y valores del archivo de configuración
            propiedades.load(entrada);
        } catch (Exception e) {
            System.err.println("Error al cargar propiedades: " + e.getMessage());
        }
        return propiedades;
    }

    // Devuelve la instancia única de la clase; la crea si aún no existe
    public static ConexionBaseDatos obtenerInstancia() {
        if (instancia == null) {
            instancia = new ConexionBaseDatos();
        }
        return instancia;
    }

    // Retorna la conexión activa o genera una excepción si no se pudo establecer
    public Connection obtenerConexion() {
        if (conexion == null) {
            throw new IllegalStateException("No hay conexión con la base de datos. Revisa database.properties y MySQL");
        }
        return conexion;
    }

    // Cierra la conexión y muestra un mensaje si ocurre algún error
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
