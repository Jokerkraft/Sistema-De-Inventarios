package com.cabrera.inventario.strategies;

import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PrecioAPIStrategy implements PrecioStrategy{
    @Override
    public double calcularPrecio(String codigoProducto, double precioBase) {
        System.out.println("Consultando precio sugerido en la API externa...");

        try {

            // Usamos FakeStoreAPI (una API pública y gratuita) para simular la búsqueda de productos

            HttpClient cliente = HttpClient.newHttpClient();
            HttpRequest peticion = HttpRequest.newBuilder()
                    .uri(URI.create("https://fakestoreapi.com/products/1")) // Simulamos buscar el producto 1
                    .GET()
                    .build();

            // Enviamos la petición y obtenemos la respuesta
            HttpResponse<String> respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofString());

            if (respuesta.statusCode() == 200) {
                // Convertimos el texto de internet a un objeto JSON
                JSONObject json = new JSONObject(respuesta.body());

                // Extraemos el precio de la API (asumiendo que viene en dólares y multiplicamos x 20 pesos)
                double precioSugerido = json.getDouble("price") * 20.0;
                System.out.println("!Precio encontrado en la API!: $" + precioSugerido);

                return precioSugerido;
            }
        } catch (Exception e) {
            System.err.println("Error al conectar con la API: " + e.getMessage());
        }

        // Si la API falla o el producto no existe, retornamos el precio base como respaldo
        return precioBase;
    }
}
