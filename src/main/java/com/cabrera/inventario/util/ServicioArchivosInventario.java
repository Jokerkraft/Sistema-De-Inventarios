package com.cabrera.inventario.util;

import com.cabrera.inventario.dao.MovimientoInventarioDAO;
import com.cabrera.inventario.dao.ProductoDAO;
import com.cabrera.inventario.models.MovimientoInventario;
import com.cabrera.inventario.models.Producto;
import com.cabrera.inventario.models.Usuario;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

// Importa y exporta el inventario en formatos CSV y JSON
public class ServicioArchivosInventario {

    private static final List<String> ENCABEZADOS_CSV = List.of(
            "codigo", "nombre", "descripcion", "precioCompra", "precioVenta", "stockActual"
    );

    // Resume los resultados para que la interfaz informe cuántas filas se procesaron
    public record ResultadoImportacion(int productosNuevos, int productosActualizados) {
    }

    // Escribe los productos en CSV o JSON según la extensión del archivo
    public void exportar(Path archivo, List<Producto> productos) throws IOException {
        String extension = obtenerExtension(archivo);
        if ("csv".equals(extension)) {
            exportarCsv(archivo, productos);
        } else if ("json".equals(extension)) {
            exportarJson(archivo, productos);
        } else {
            throw new IOException("El formato debe ser CSV o JSON.");
        }
    }

    // Lee y valida el archivo antes de modificar el catálogo o las existencias
    public ResultadoImportacion importar(
            Path archivo,
            ProductoDAO productoDAO,
            MovimientoInventarioDAO movimientoDAO,
            Usuario usuario
    ) throws IOException {
        if (usuario == null || usuario.getIdUsuario() <= 0) {
            throw new IOException("No hay un usuario válido para registrar la importación.");
        }

        String extension = obtenerExtension(archivo);
        List<Producto> productos = switch (extension) {
            case "csv" -> leerCsv(archivo);
            case "json" -> leerJson(archivo);
            default -> throw new IOException("El formato debe ser CSV o JSON.");
        };

        validarProductos(productos);
        return guardarProductos(productos, archivo, productoDAO, movimientoDAO, usuario);
    }

    // Genera un CSV con encabezados y campos escapados correctamente
    private void exportarCsv(Path archivo, List<Producto> productos) throws IOException {
        List<String> filas = new ArrayList<>();
        filas.add(String.join(",", ENCABEZADOS_CSV));

        for (Producto producto : productos) {
            filas.add(String.join(",",
                    escaparCsv(producto.getCodigo()),
                    escaparCsv(producto.getNombre()),
                    escaparCsv(producto.getDescripcion()),
                    Double.toString(producto.getPrecioCompra()),
                    Double.toString(producto.getPrecioVenta()),
                    Integer.toString(producto.getStockActual())
            ));
        }

        Files.writeString(archivo, String.join(System.lineSeparator(), filas), StandardCharsets.UTF_8);
    }

    // Serializa cada producto como un objeto dentro de un arreglo JSON
    private void exportarJson(Path archivo, List<Producto> productos) throws IOException {
        JSONArray datos = new JSONArray();
        for (Producto producto : productos) {
            JSONObject registro = new JSONObject();
            registro.put("codigo", producto.getCodigo());
            registro.put("nombre", producto.getNombre());
            registro.put("descripcion", producto.getDescripcion());
            registro.put("precioCompra", producto.getPrecioCompra());
            registro.put("precioVenta", producto.getPrecioVenta());
            registro.put("stockActual", producto.getStockActual());
            datos.put(registro);
        }
        Files.writeString(archivo, datos.toString(2), StandardCharsets.UTF_8);
    }

    // Lee filas CSV y verifica que respeten el encabezado y número de columnas
    private List<Producto> leerCsv(Path archivo) throws IOException {
        String contenido = Files.readString(archivo, StandardCharsets.UTF_8);
        if (contenido.startsWith("\uFEFF")) {
            contenido = contenido.substring(1);
        }
        List<List<String>> filas = analizarCsv(contenido);
        if (filas.isEmpty() || !filas.get(0).equals(ENCABEZADOS_CSV)) {
            throw new IOException("El CSV debe iniciar con: " + String.join(",", ENCABEZADOS_CSV));
        }

        List<Producto> productos = new ArrayList<>();
        for (int indice = 1; indice < filas.size(); indice++) {
            List<String> fila = filas.get(indice);
            if (fila.size() == 1 && fila.get(0).isBlank()) {
                continue;
            }
            if (fila.size() != ENCABEZADOS_CSV.size()) {
                throw new IOException("La fila " + (indice + 1) + " no tiene seis columnas.");
            }
            productos.add(crearProducto(fila, indice + 1));
        }
        return productos;
    }

    // Separa campos CSV respetando comas y saltos de línea dentro de comillas
    private List<List<String>> analizarCsv(String contenido) throws IOException {
        List<List<String>> filas = new ArrayList<>();
        List<String> fila = new ArrayList<>();
        StringBuilder campo = new StringBuilder();
        boolean entreComillas = false;

        for (int indice = 0; indice < contenido.length(); indice++) {
            char caracter = contenido.charAt(indice);
            if (caracter == '"') {
                if (entreComillas && indice + 1 < contenido.length() && contenido.charAt(indice + 1) == '"') {
                    campo.append('"');
                    indice++;
                } else {
                    entreComillas = !entreComillas;
                }
            } else if (caracter == ',' && !entreComillas) {
                fila.add(campo.toString());
                campo.setLength(0);
            } else if ((caracter == '\n' || caracter == '\r') && !entreComillas) {
                if (caracter == '\r' && indice + 1 < contenido.length() && contenido.charAt(indice + 1) == '\n') {
                    indice++;
                }
                fila.add(campo.toString());
                filas.add(fila);
                fila = new ArrayList<>();
                campo.setLength(0);
            } else {
                campo.append(caracter);
            }
        }

        if (entreComillas) {
            throw new IOException("El CSV contiene un campo con comillas sin cerrar.");
        }
        if (campo.length() > 0 || !fila.isEmpty()) {
            fila.add(campo.toString());
            filas.add(fila);
        }
        return filas;
    }

    // Convierte los objetos JSON del archivo en productos del dominio
    private List<Producto> leerJson(Path archivo) throws IOException {
        try {
            JSONArray datos = new JSONArray(Files.readString(archivo, StandardCharsets.UTF_8));
            List<Producto> productos = new ArrayList<>();
            for (int indice = 0; indice < datos.length(); indice++) {
                JSONObject registro = datos.getJSONObject(indice);
                List<String> valores = List.of(
                        registro.getString("codigo"),
                        registro.getString("nombre"),
                        registro.optString("descripcion", ""),
                        registro.get("precioCompra").toString(),
                        registro.get("precioVenta").toString(),
                        registro.get("stockActual").toString()
                );
                productos.add(crearProducto(valores, indice + 1));
            }
            return productos;
        } catch (RuntimeException excepcion) {
            throw new IOException("El JSON no tiene el formato esperado para productos.", excepcion);
        }
    }

    // Crea un producto y asocia errores numéricos al número del registro
    private Producto crearProducto(List<String> valores, int fila) throws IOException {
        try {
            return new Producto(
                    0,
                    valores.get(0).trim(),
                    valores.get(1).trim(),
                    valores.get(2).trim(),
                    Double.parseDouble(valores.get(3).trim()),
                    Double.parseDouble(valores.get(4).trim()),
                    Integer.parseInt(valores.get(5).trim())
            );
        } catch (NumberFormatException excepcion) {
            throw new IOException("Precio o stock inválido en la fila/registro " + fila + ".", excepcion);
        }
    }

    // Rechaza códigos repetidos, campos obligatorios vacíos y valores inválidos
    private void validarProductos(List<Producto> productos) throws IOException {
        Set<String> codigos = new HashSet<>();
        for (int indice = 0; indice < productos.size(); indice++) {
            Producto producto = productos.get(indice);
            String codigoNormalizado = producto.getCodigo().toLowerCase(Locale.ROOT);

            if (producto.getCodigo().isBlank() || producto.getNombre().isBlank()) {
                throw new IOException("El código y el nombre son obligatorios (registro " + (indice + 1) + ").");
            }
            if (!Double.isFinite(producto.getPrecioCompra()) || producto.getPrecioCompra() < 0
                    || !Double.isFinite(producto.getPrecioVenta()) || producto.getPrecioVenta() < 0
                    || producto.getStockActual() < 0) {
                throw new IOException("Precio o stock fuera de rango (registro " + (indice + 1) + ").");
            }
            if (!codigos.add(codigoNormalizado)) {
                throw new IOException("El archivo repite el código " + producto.getCodigo() + ".");
            }
        }
    }

    // Crea o actualiza cada producto y audita cualquier diferencia de stock
    private ResultadoImportacion guardarProductos(
            List<Producto> productos,
            Path archivo,
            ProductoDAO productoDAO,
            MovimientoInventarioDAO movimientoDAO,
            Usuario usuario
    ) throws IOException {
        int nuevos = 0;
        int actualizados = 0;

        for (Producto importado : productos) {
            Producto existente = productoDAO.obtenerPorCodigo(importado.getCodigo());
            boolean esNuevo = existente == null;
            int stockAnterior = esNuevo ? 0 : existente.getStockActual();

            Producto productoGuardado = new Producto(
                    esNuevo ? 0 : existente.getIdProducto(),
                    importado.getCodigo(),
                    importado.getNombre(),
                    importado.getDescripcion(),
                    importado.getPrecioCompra(),
                    importado.getPrecioVenta(),
                    esNuevo ? 0 : stockAnterior
            );

            if (!guardarDatosProducto(productoDAO, productoGuardado, esNuevo)) {
                throw new IOException("La importación se detuvo. Se procesaron "
                        + nuevos + " productos nuevos y " + actualizados + " actualizados.");
            }

            int diferenciaStock = importado.getStockActual() - stockAnterior;
            if (diferenciaStock != 0 && !registrarAjuste(
                    movimientoDAO, productoGuardado, diferenciaStock, archivo, usuario)) {
                if (esNuevo) {
                    productoDAO.eliminar(productoGuardado.getIdProducto());
                } else {
                    productoDAO.actualizar(existente);
                }
                throw new IOException("No se pudo registrar el ajuste de stock para "
                        + importado.getCodigo() + ". La importación se detuvo.");
            }

            if (esNuevo) {
                nuevos++;
            } else {
                actualizados++;
            }
        }
        return new ResultadoImportacion(nuevos, actualizados);
    }

    // Delega la escritura al DAO según se trate de alta o actualización
    private boolean guardarDatosProducto(ProductoDAO productoDAO, Producto producto, boolean esNuevo) {
        if (esNuevo) {
            return productoDAO.crear(producto);
        }
        return productoDAO.actualizar(producto);
    }

    // Representa un ajuste importado como entrada o salida del usuario responsable
    private boolean registrarAjuste(
            MovimientoInventarioDAO movimientoDAO,
            Producto producto,
            int diferencia,
            Path archivo,
            Usuario usuario
    ) {
        MovimientoInventario movimiento = new MovimientoInventario(
                0,
                diferencia > 0 ? "ENTRADA" : "SALIDA",
                producto.getIdProducto(),
                Math.abs(diferencia),
                "Ajuste por importación desde " + archivo.getFileName(),
                null,
                "IMPORTACION",
                usuario.getIdUsuario()
        );
        return movimientoDAO.registrarMovimiento(movimiento);
    }

    // Escapa comillas y separadores para mantener válidos los campos CSV
    private String escaparCsv(String valor) {
        String seguro = valor == null ? "" : valor;
        if (seguro.contains(",") || seguro.contains("\"") || seguro.contains("\n") || seguro.contains("\r")) {
            return "\"" + seguro.replace("\"", "\"\"") + "\"";
        }
        return seguro;
    }

    // Normaliza la extensión para reconocer los formatos sin distinguir mayúsculas
    private String obtenerExtension(Path archivo) {
        String nombre = archivo.getFileName().toString();
        int punto = nombre.lastIndexOf('.');
        return punto < 0 ? "" : nombre.substring(punto + 1).toLowerCase(Locale.ROOT);
    }
}
