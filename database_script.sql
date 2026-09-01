CREATE DATABASE IF NOT EXISTS sistema_inventarios;
USE sistema_inventarios;

-- USUARIOS
CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario INT NOT NULL AUTO_INCREMENT,
    nombre_completo VARCHAR(150) NOT NULL,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(50) NOT NULL DEFAULT 'ADMIN',
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id_usuario)
);

-- CATEGORÍAS
CREATE TABLE IF NOT EXISTS categorias (
    id_categoria INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id_categoria),
    UNIQUE KEY uk_categoria_nombre (nombre)
);


-- PROVEEDORES
CREATE TABLE IF NOT EXISTS proveedores (
    id_proveedor INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(150) NOT NULL,
    contacto VARCHAR(150) DEFAULT NULL,
    telefono VARCHAR(50) DEFAULT NULL,
    email VARCHAR(150) DEFAULT NULL,
    direccion VARCHAR(255) DEFAULT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id_proveedor)
);


-- CLIENTES
CREATE TABLE IF NOT EXISTS clientes (
    id_cliente INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    documento VARCHAR(50) DEFAULT NULL,
    telefono VARCHAR(50) DEFAULT NULL,
    email VARCHAR(150) DEFAULT NULL,
    direccion VARCHAR(255) DEFAULT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id_cliente)
);

-- PRODUCTOS
CREATE TABLE IF NOT EXISTS productos (
    id_producto INT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio_compra DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    precio_venta DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    stock_actual INT NOT NULL DEFAULT 0,
    id_categoria INT DEFAULT NULL,
    id_proveedor INT DEFAULT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id_producto),
    CONSTRAINT chk_stock_productos CHECK (stock_actual >= 0),
    CONSTRAINT fk_producto_categoria
        FOREIGN KEY (id_categoria) REFERENCES categorias(id_categoria)
        ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_producto_proveedor
        FOREIGN KEY (id_proveedor) REFERENCES proveedores(id_proveedor)
        ON UPDATE CASCADE ON DELETE SET NULL
);

-- DETALLE PRODUCTOS
CREATE TABLE IF NOT EXISTS detalleproductos (
    id_detalle INT NOT NULL AUTO_INCREMENT,
    codigo_producto VARCHAR(50) NOT NULL,
    precio_venta DECIMAL(10,2) NOT NULL,
    precio_compra DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    stock_actual INT NOT NULL DEFAULT 0,
    PRIMARY KEY (id_detalle),
    KEY idx_detalle_codigo_producto (codigo_producto),
    CONSTRAINT fk_detalle_producto
        FOREIGN KEY (codigo_producto) REFERENCES productos(codigo)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- COMPRAS
CREATE TABLE IF NOT EXISTS compras (
    id_compra INT NOT NULL AUTO_INCREMENT,
    id_proveedor INT NOT NULL,
    codigo_compra VARCHAR(50) NOT NULL UNIQUE,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
    PRIMARY KEY (id_compra),
    CONSTRAINT fk_compra_proveedor
        FOREIGN KEY (id_proveedor) REFERENCES proveedores(id_proveedor)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

-- DETALLE DE COMPRAS
CREATE TABLE IF NOT EXISTS detalle_compras (
    id_detalle_compra INT NOT NULL AUTO_INCREMENT,
    id_compra INT NOT NULL,
    codigo_producto VARCHAR(50) NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    PRIMARY KEY (id_detalle_compra),
    CONSTRAINT fk_detalle_compra_compra
        FOREIGN KEY (id_compra) REFERENCES compras(id_compra)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_detalle_compra_producto
        FOREIGN KEY (codigo_producto) REFERENCES productos(codigo)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

-- VENTAS
CREATE TABLE IF NOT EXISTS ventas (
    id_venta INT NOT NULL AUTO_INCREMENT,
    id_cliente INT NOT NULL,
    codigo_venta VARCHAR(50) NOT NULL UNIQUE,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
    metodo_pago VARCHAR(50) DEFAULT 'EFECTIVO',
    PRIMARY KEY (id_venta),
    CONSTRAINT fk_venta_cliente
        FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

-- DETALLE DE VENTAS
CREATE TABLE IF NOT EXISTS detalle_ventas (
    id_detalle_venta INT NOT NULL AUTO_INCREMENT,
    id_venta INT NOT NULL,
    codigo_producto VARCHAR(50) NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    PRIMARY KEY (id_detalle_venta),
    CONSTRAINT fk_detalle_venta_venta
        FOREIGN KEY (id_venta) REFERENCES ventas(id_venta)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_detalle_venta_producto
        FOREIGN KEY (codigo_producto) REFERENCES productos(codigo)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

-- MOVIMIENTOS DE INVENTARIO
CREATE TABLE IF NOT EXISTS movimientos (
    id_movimiento INT NOT NULL AUTO_INCREMENT,
    tipo VARCHAR(30) NOT NULL,
    codigo_producto VARCHAR(50) NOT NULL,
    cantidad INT NOT NULL,
    motivo VARCHAR(150) DEFAULT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    referencia VARCHAR(100) DEFAULT NULL,
    id_usuario INT DEFAULT NULL,
    PRIMARY KEY (id_movimiento),
    CONSTRAINT fk_movimiento_producto
        FOREIGN KEY (codigo_producto) REFERENCES productos(codigo)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_movimiento_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
        ON UPDATE CASCADE ON DELETE SET NULL
);

-- DATOS INICIALES
INSERT INTO usuarios (nombre_completo, username, password, rol, activo)
VALUES ('Administrador', 'admin', SHA1('admin123'), 'ADMIN', TRUE)
ON DUPLICATE KEY UPDATE
    nombre_completo = VALUES(nombre_completo),
    password = VALUES(password),
    rol = VALUES(rol),
    activo = VALUES(activo);

INSERT INTO categorias (nombre, descripcion, activo)
VALUES
    ('Tecnología', 'Productos tecnológicos', TRUE),
    ('Hogar', 'Productos para el hogar', TRUE),
    ('Limpieza', 'Productos de limpieza', TRUE)
ON DUPLICATE KEY UPDATE
    descripcion = VALUES(descripcion),
    activo = VALUES(activo);

INSERT INTO proveedores (nombre, contacto, telefono, email, direccion, activo)
VALUES
    ('Distribuidora ABC', 'Juan Pérez', '555-0101', 'juan@abc.com', 'Calle 1, Ciudad', TRUE),
    ('Suministros XYZ', 'Ana Gómez', '555-0202', 'ana@xyz.com', 'Avenida 2, Ciudad', TRUE)
ON DUPLICATE KEY UPDATE
    contacto = VALUES(contacto),
    telefono = VALUES(telefono),
    email = VALUES(email),
    direccion = VALUES(direccion),
    activo = VALUES(activo);

INSERT INTO clientes (nombre, apellido, documento, telefono, email, direccion, activo)
VALUES
    ('Cliente', 'General', '00000000', '555-9999', 'cliente@demo.com', 'Sin dirección', TRUE)
ON DUPLICATE KEY UPDATE
    apellido = VALUES(apellido),
    documento = VALUES(documento),
    telefono = VALUES(telefono),
    email = VALUES(email),
    direccion = VALUES(direccion),
    activo = VALUES(activo);
