-- MIGRACIÓN ÚNICA: esquema anterior -> esquema normalizado.
-- Ejecuta una copia de seguridad antes de usarla.
-- Requiere MySQL 8.0.16 o posterior para CHECK constraints.

USE sistema_inventarios;

-- Los totales y subtotales se calculan desde cantidad * precio_unitario.
ALTER TABLE compras DROP COLUMN total;
ALTER TABLE ventas
    DROP COLUMN total,
    MODIFY metodo_pago VARCHAR(50) NOT NULL DEFAULT 'EFECTIVO';

-- Verificación previa: cada detalle debe referenciar un producto existente.
-- Ambas consultas deben devolver 0 filas antes de continuar.
SELECT dc.*
FROM detalle_compras dc
LEFT JOIN productos p ON p.codigo = dc.codigo_producto
WHERE p.id_producto IS NULL;

SELECT dv.*
FROM detalle_ventas dv
LEFT JOIN productos p ON p.codigo = dv.codigo_producto
WHERE p.id_producto IS NULL;

-- El catálogo normalizado usa productos como fuente única de precios y stock.
-- Solo elimina detalleproductos después de verificar que no contiene valores que
-- quieras conservar como historial; sus columnas duplican productos.
DROP TABLE IF EXISTS detalleproductos;

-- DETALLES DE COMPRA: código comercial -> clave interna y clave compuesta.
ALTER TABLE detalle_compras
    DROP FOREIGN KEY fk_detalle_compra_producto,
    ADD COLUMN id_producto INT NULL AFTER id_compra;

UPDATE detalle_compras dc
JOIN productos p ON p.codigo = dc.codigo_producto
SET dc.id_producto = p.id_producto;

ALTER TABLE detalle_compras
    MODIFY id_producto INT NOT NULL,
    MODIFY id_detalle_compra INT NOT NULL,
    DROP FOREIGN KEY fk_detalle_compra_compra,
    DROP PRIMARY KEY,
    DROP COLUMN id_detalle_compra,
    DROP COLUMN codigo_producto,
    DROP COLUMN subtotal,
    ADD CONSTRAINT PK2_detalle_compras PRIMARY KEY (id_compra, id_producto),
    ADD CONSTRAINT CK2_detalle_compras_cantidad CHECK (cantidad > 0),
    ADD CONSTRAINT CK2_detalle_compras_precio CHECK (precio_unitario >= 0),
    ADD CONSTRAINT FK2_detalle_compras_compra
        FOREIGN KEY (id_compra) REFERENCES compras(id_compra)
        ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT FK2_detalle_compras_producto
        FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- DETALLES DE VENTA: código comercial -> clave interna y clave compuesta.
ALTER TABLE detalle_ventas
    DROP FOREIGN KEY fk_detalle_venta_producto,
    ADD COLUMN id_producto INT NULL AFTER id_venta;

UPDATE detalle_ventas dv
JOIN productos p ON p.codigo = dv.codigo_producto
SET dv.id_producto = p.id_producto;

ALTER TABLE detalle_ventas
    MODIFY id_producto INT NOT NULL,
    MODIFY id_detalle_venta INT NOT NULL,
    DROP FOREIGN KEY fk_detalle_venta_venta,
    DROP PRIMARY KEY,
    DROP COLUMN id_detalle_venta,
    DROP COLUMN codigo_producto,
    DROP COLUMN subtotal,
    ADD CONSTRAINT PK2_detalle_ventas PRIMARY KEY (id_venta, id_producto),
    ADD CONSTRAINT CK2_detalle_ventas_cantidad CHECK (cantidad > 0),
    ADD CONSTRAINT CK2_detalle_ventas_precio CHECK (precio_unitario >= 0),
    ADD CONSTRAINT FK2_detalle_ventas_venta
        FOREIGN KEY (id_venta) REFERENCES ventas(id_venta)
        ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT FK2_detalle_ventas_producto
        FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- MOVIMIENTOS: código comercial -> clave interna.
ALTER TABLE movimientos
    DROP FOREIGN KEY fk_movimiento_producto,
    ADD COLUMN id_producto INT NULL AFTER tipo;

UPDATE movimientos m
JOIN productos p ON p.codigo = m.codigo_producto
SET m.id_producto = p.id_producto;

ALTER TABLE movimientos
    MODIFY id_producto INT NOT NULL,
    DROP COLUMN codigo_producto,
    ADD CONSTRAINT CK_movimientos_cantidad CHECK (cantidad <> 0),
    ADD CONSTRAINT FK_movimientos_producto
        FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
        ON UPDATE CASCADE ON DELETE RESTRICT;

-- Los nombres se alinean con el esquema nuevo.
ALTER TABLE productos
    DROP FOREIGN KEY fk_producto_categoria,
    DROP FOREIGN KEY fk_producto_proveedor,
    DROP CHECK chk_stock_productos,
    DROP PRIMARY KEY,
    ADD CONSTRAINT PK_productos PRIMARY KEY (id_producto),
    ADD CONSTRAINT CK_productos_stock CHECK (stock_actual >= 0),
    ADD CONSTRAINT CK_productos_precio_compra CHECK (precio_compra >= 0),
    ADD CONSTRAINT CK_productos_precio_venta CHECK (precio_venta >= 0),
    ADD CONSTRAINT FK_productos_categoria
        FOREIGN KEY (id_categoria) REFERENCES categorias(id_categoria)
        ON UPDATE CASCADE ON DELETE SET NULL,
    ADD CONSTRAINT FK_productos_proveedor
        FOREIGN KEY (id_proveedor) REFERENCES proveedores(id_proveedor)
        ON UPDATE CASCADE ON DELETE SET NULL;
