CREATE DATABASE IF NOT EXISTS sistema_inventarios;
USE sistema_inventarios;

-- USUARIOS
CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario INT NOT NULL AUTO_INCREMENT,
    nombre_completo VARCHAR(150) NOT NULL,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(50) NOT NULL DEFAULT 'ADMIN',
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT PK_usuarios PRIMARY KEY (id_usuario),
    CONSTRAINT UQ_usuarios_username UNIQUE (username)
);

-- CATEGORÍAS
CREATE TABLE IF NOT EXISTS categorias (
    id_categoria INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT PK_categorias PRIMARY KEY (id_categoria),
    CONSTRAINT UQ_categorias_nombre UNIQUE (nombre)
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
    CONSTRAINT PK_proveedores PRIMARY KEY (id_proveedor)
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
    CONSTRAINT PK_clientes PRIMARY KEY (id_cliente)
);

-- PRODUCTOS
CREATE TABLE IF NOT EXISTS productos (
    id_producto INT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(50) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio_compra DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    precio_venta DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    stock_actual INT NOT NULL DEFAULT 0,
    id_categoria INT DEFAULT NULL,
    id_proveedor INT DEFAULT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT PK_productos PRIMARY KEY (id_producto),
    CONSTRAINT UQ_productos_codigo UNIQUE (codigo),
    CONSTRAINT CK_productos_stock CHECK (stock_actual >= 0),
    CONSTRAINT CK_productos_precio_compra CHECK (precio_compra >= 0),
    CONSTRAINT CK_productos_precio_venta CHECK (precio_venta >= 0),
    CONSTRAINT FK_productos_categoria
        FOREIGN KEY (id_categoria) REFERENCES categorias(id_categoria)
        ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT FK_productos_proveedor
        FOREIGN KEY (id_proveedor) REFERENCES proveedores(id_proveedor)
        ON UPDATE CASCADE ON DELETE SET NULL
);

-- COMPRAS
CREATE TABLE IF NOT EXISTS compras (
    id_compra INT NOT NULL AUTO_INCREMENT,
    id_proveedor INT NOT NULL,
    codigo_compra VARCHAR(50) NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
    CONSTRAINT PK_compras PRIMARY KEY (id_compra),
    CONSTRAINT UQ_compras_codigo UNIQUE (codigo_compra),
    CONSTRAINT FK_compras_proveedor
        FOREIGN KEY (id_proveedor) REFERENCES proveedores(id_proveedor)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

-- DETALLE DE COMPRAS
CREATE TABLE IF NOT EXISTS detalle_compras (
    id_compra INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    CONSTRAINT PK2_detalle_compras PRIMARY KEY (id_compra, id_producto),
    CONSTRAINT CK2_detalle_compras_cantidad CHECK (cantidad > 0),
    CONSTRAINT CK2_detalle_compras_precio CHECK (precio_unitario >= 0),
    CONSTRAINT FK2_detalle_compras_compra
        FOREIGN KEY (id_compra) REFERENCES compras(id_compra)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK2_detalle_compras_producto
        FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

-- VENTAS
CREATE TABLE IF NOT EXISTS ventas (
    id_venta INT NOT NULL AUTO_INCREMENT,
    id_cliente INT NOT NULL,
    codigo_venta VARCHAR(50) NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
    metodo_pago VARCHAR(50) NOT NULL DEFAULT 'EFECTIVO',
    CONSTRAINT PK_ventas PRIMARY KEY (id_venta),
    CONSTRAINT UQ_ventas_codigo UNIQUE (codigo_venta),
    CONSTRAINT FK_ventas_cliente
        FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

-- DETALLE DE VENTAS
CREATE TABLE IF NOT EXISTS detalle_ventas (
    id_venta INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    CONSTRAINT PK2_detalle_ventas PRIMARY KEY (id_venta, id_producto),
    CONSTRAINT CK2_detalle_ventas_cantidad CHECK (cantidad > 0),
    CONSTRAINT CK2_detalle_ventas_precio CHECK (precio_unitario >= 0),
    CONSTRAINT FK2_detalle_ventas_venta
        FOREIGN KEY (id_venta) REFERENCES ventas(id_venta)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK2_detalle_ventas_producto
        FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

-- MOVIMIENTOS DE INVENTARIO
CREATE TABLE IF NOT EXISTS movimientos (
    id_movimiento INT NOT NULL AUTO_INCREMENT,
    tipo VARCHAR(30) NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    motivo VARCHAR(150) DEFAULT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    referencia VARCHAR(100) DEFAULT NULL,
    id_usuario INT DEFAULT NULL,
    CONSTRAINT PK_movimientos PRIMARY KEY (id_movimiento),
    CONSTRAINT CK_movimientos_cantidad CHECK (cantidad <> 0),
    CONSTRAINT FK_movimientos_producto
        FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT FK_movimientos_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
        ON UPDATE CASCADE ON DELETE RESTRICT
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
