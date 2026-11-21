package com.cabrera.inventario.util;

import com.cabrera.inventario.dao.MovimientoInventarioDAO;
import com.cabrera.inventario.dao.ProductoDAO;
import com.cabrera.inventario.models.MovimientoInventario;
import com.cabrera.inventario.models.Producto;
import com.cabrera.inventario.models.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Comprueba importación/exportación y auditoría sin requerir una base MySQL real.
class ServicioArchivosInventarioTest {

    // Crea un directorio aislado para los archivos temporales de cada prueba.
    @TempDir
    Path carpetaTemporal;

    @Test
    // Verifica que CSV conserve comas, comillas, saltos de línea y autoría.
    void importaYExportaCsvConCamposEspecialesYAuditaElStock() throws IOException {
        verificarImportacion("csv");
    }

    @Test
    // Verifica el mismo flujo de catálogo y auditoría usando JSON.
    void importaYExportaJsonYAuditaElStock() throws IOException {
        verificarImportacion("json");
    }

    @Test
    // Confirma que una reducción de stock se registre como salida.
    void importaUnaSalidaComoMovimientoResponsable() throws IOException {
        ProductosFalsos productos = new ProductosFalsos();
        productos.productos.add(new Producto(1, "A-1", "Producto", "Descripción", 2.0, 3.0, 10));
        MovimientosFalsos movimientos = new MovimientosFalsos(productos);
        Path archivo = carpetaTemporal.resolve("salida.json");
        new ServicioArchivosInventario().exportar(
                archivo,
                List.of(new Producto(0, "A-1", "Producto", "Descripción", 2.0, 3.0, 4))
        );

        new ServicioArchivosInventario().importar(
                archivo, productos, movimientos, new Usuario(7, "Responsable", "usuario", "", "ADMIN")
        );

        assertEquals(4, productos.productos.get(0).getStockActual());
        assertEquals("SALIDA", movimientos.movimientos.get(0).getTipo());
        assertEquals(6, movimientos.movimientos.get(0).getCantidad());
        assertEquals(7, movimientos.movimientos.get(0).getIdUsuario());
    }

    @Test
    // Asegura que un archivo inválido no inserte productos ni movimientos.
    void rechazaExistenciasNegativasAntesDeModificarElCatalogo() throws IOException {
        Path archivo = carpetaTemporal.resolve("stock-negativo.json");
        java.nio.file.Files.writeString(archivo, """
                [{"codigo":"A-1","nombre":"Producto","precioCompra":1,"precioVenta":2,"stockActual":-1}]
                """);
        ProductosFalsos productos = new ProductosFalsos();
        MovimientosFalsos movimientos = new MovimientosFalsos(productos);

        assertThrows(IOException.class, () -> new ServicioArchivosInventario().importar(
                archivo, productos, movimientos, new Usuario(3, "Admin", "admin", "", "ADMIN")
        ));
        assertTrue(productos.productos.isEmpty());
        assertTrue(movimientos.movimientos.isEmpty());
    }

    // Ejecuta el mismo escenario de ida y vuelta para cada formato de archivo.
    private void verificarImportacion(String extension) throws IOException {
        ServicioArchivosInventario servicio = new ServicioArchivosInventario();
        Producto producto = new Producto(
                0, "COD-1", "Producto de prueba", "Descripción, con \"comillas\"\ny otra línea",
                12.5, 20.0, 8
        );
        Path archivo = carpetaTemporal.resolve("inventario." + extension);
        servicio.exportar(archivo, List.of(producto));

        ProductosFalsos productos = new ProductosFalsos();
        MovimientosFalsos movimientos = new MovimientosFalsos(productos);
        ServicioArchivosInventario.ResultadoImportacion resultado = servicio.importar(
                archivo,
                productos,
                movimientos,
                new Usuario(9, "Responsable", "usuario", "", "ADMIN")
        );

        assertEquals(1, resultado.productosNuevos());
        assertEquals(0, resultado.productosActualizados());
        assertEquals(producto.getDescripcion(), productos.productos.get(0).getDescripcion());
        assertEquals(8, productos.productos.get(0).getStockActual());
        assertEquals("ENTRADA", movimientos.movimientos.get(0).getTipo());
        assertEquals(9, movimientos.movimientos.get(0).getIdUsuario());
    }

    // Implementación en memoria del DAO de productos para aislar las pruebas.
    private static class ProductosFalsos implements ProductoDAO {

        private final List<Producto> productos = new ArrayList<>();
        private int siguienteId = 1;

        @Override
        public boolean crear(Producto producto) {
            producto.setIdProducto(siguienteId++);
            productos.add(producto);
            return true;
        }

        @Override
        public Producto obtenerPorId(int id) {
            return productos.stream().filter(producto -> producto.getIdProducto() == id).findFirst().orElse(null);
        }

        @Override
        public Producto obtenerPorCodigo(String codigo) {
            return productos.stream().filter(producto -> producto.getCodigo().equals(codigo)).findFirst().orElse(null);
        }

        @Override
        public List<Producto> obtenerTodos() {
            return new ArrayList<>(productos);
        }

        @Override
        public boolean actualizar(Producto producto) {
            for (int indice = 0; indice < productos.size(); indice++) {
                if (productos.get(indice).getIdProducto() == producto.getIdProducto()) {
                    productos.set(indice, producto);
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean eliminar(int id) {
            return productos.removeIf(producto -> producto.getIdProducto() == id);
        }
    }

    // Simula los ajustes de stock y conserva los movimientos creados por la prueba.
    private static class MovimientosFalsos implements MovimientoInventarioDAO {

        private final ProductosFalsos productos;
        private final List<MovimientoInventario> movimientos = new ArrayList<>();

        // Comparte el catálogo falso para aplicar los cambios de stock de prueba.
        private MovimientosFalsos(ProductosFalsos productos) {
            this.productos = productos;
        }

        @Override
        public boolean registrarMovimiento(MovimientoInventario movimiento) {
            Producto producto = productos.obtenerPorId(movimiento.getIdProducto());
            int diferencia = movimiento.getTipo().equals("ENTRADA")
                    ? movimiento.getCantidad()
                    : -movimiento.getCantidad();
            producto.setStockActual(producto.getStockActual() + diferencia);
            movimientos.add(movimiento);
            return true;
        }

        @Override
        public boolean crear(MovimientoInventario movimiento) {
            return registrarMovimiento(movimiento);
        }

        @Override
        public MovimientoInventario obtenerPorId(int id) {
            return movimientos.stream()
                    .filter(movimiento -> movimiento.getIdMovimiento() == id)
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public List<MovimientoInventario> obtenerTodos() {
            return new ArrayList<>(movimientos);
        }

        @Override
        public List<MovimientoInventario> obtenerRecientes(int limite) {
            return movimientos.stream().limit(limite).toList();
        }

        @Override
        public boolean actualizar(MovimientoInventario movimiento) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean eliminar(int id) {
            throw new UnsupportedOperationException();
        }
    }
}
