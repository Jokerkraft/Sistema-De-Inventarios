package com.cabrera.inventario.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UtilidadSeguridad {

    // Método estático para encriptar cualquier texto a SHA-1
    public static String encriptarSHA1(String textoPlano) {
        try {
            // Instanciamos el algoritmo SHA-1
            MessageDigest resumenMensaje = MessageDigest.getInstance("SHA-1");

            // Convertimos el texto a bytes y aplicamos el algoritmo
            byte[] bytesEncriptados = resumenMensaje.digest(textoPlano.getBytes());

            // Convertimos el arreglo de bytes a una cadena hexadecimal
            StringBuilder cadenaHexadecimal = new StringBuilder();

            for (byte unByte : bytesEncriptados) {
                // Convertimos cada byte a su representación hexadecimal
                String valorHexadecimal = Integer.toHexString(0xff & unByte);

                // Si el valor tiene un solo carácter, le agregamos un cero a la izquierda
                if (valorHexadecimal.length() == 1) {
                    cadenaHexadecimal.append('0');
                }
                cadenaHexadecimal.append(valorHexadecimal);
            }

            // Retornamos el hash final de 40 caracteres
            return cadenaHexadecimal.toString();

        } catch (NoSuchAlgorithmException excepcion) {
            throw new RuntimeException("Error al inicializar el algoritmo SHA-1", excepcion);
        }
    }
}
