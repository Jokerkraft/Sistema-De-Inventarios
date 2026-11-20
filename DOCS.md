# Documentación — Sistema de Inventarios

## 1. Control de versiones

El proyecto usa Git y GitHub. La rama principal actual es `main` y el
repositorio conserva ramas de trabajo como `develop`, `feature/login`,
`feature/dao-login`, `feature/database-connection`, `feature/main-menu` y
`feature/mvc-architecture`.

Los commits siguen mensajes convencionales cuando aplica:

- `feat`: funcionalidad nueva.
- `fix`: corrección de un error.
- `refactor`: cambio estructural sin modificar el objetivo de la función.
- `docs`: documentación.
- `chore`: tareas de mantenimiento.

## 2. Autenticación y seguridad

### Inicio de sesión basado en base de datos

La tabla `usuarios` almacena el nombre completo, `username`, hash de
contraseña, rol y estado activo. `LoginController` recibe el usuario y la
contraseña desde la vista, valida que no estén vacíos y delega la consulta a
`UsuarioDAOImp`.

El acceso solo se concede si existe un usuario con el `username`, contraseña y
estado activo correspondientes.

### Hash de contraseña

Clase: `com.cabrera.inventario.util.UtilidadSeguridad`

Proceso actual:

1. La persona escribe sus credenciales en `Login.fxml`.
2. `LoginController` calcula SHA-1 de la contraseña introducida.
3. `UsuarioDAOImp` compara ese resultado con `usuarios.password`.
4. Si los datos coinciden, se abre el menú principal.

> SHA-1 se mantiene porque es el mecanismo actual del proyecto y del dato
> inicial. Para una versión real debe sustituirse por BCrypt o Argon2.

El sistema actual no usa JWT, tokens persistentes ni autorización por rol. El
rol se guarda para extender la aplicación posteriormente.

## 3. Patrones de diseño implementados

### 3.1 Strategy Pattern

Ubicación: `strategies/`

Interfaz: `PrecioStrategy`

Implementaciones:

1. `PrecioLocalStrategy`: aplica un multiplicador local de 1.16 al precio base.
2. `PrecioAPIStrategy`: consulta FakeStoreAPI y usa el precio recibido como una
   sugerencia; si la consulta falla, conserva el precio base.

`NuevoProductoController` utiliza actualmente `PrecioAPIStrategy` para el botón
de precio sugerido. La interfaz permite sustituir el cálculo sin cambiar el
controlador.

### 3.2 Data Access Object (DAO)

Ubicación: `dao/`

Interfaz genérica: `DaoGenerico<T>`

Define operaciones comunes:

- `crear(T entidad)`
- `obtenerPorId(int id)`
- `obtenerTodos()`
- `actualizar(T entidad)`
- `eliminar(int id)`

Implementaciones activas:

- `ProductoDAOImpl`: consulta y administra productos.
- `ProveedorDAOImpl`: administra proveedores.
- `UsuarioDAOImp`: autentica usuarios.

Existen interfaces para clientes, categorías, compras, ventas, detalles y
movimientos. Sus implementaciones y pantallas son trabajo pendiente.

### 3.3 Singleton Pattern

Ubicación: `util/ConexionBaseDatos.java`

`ConexionBaseDatos` conserva una única instancia de conexión JDBC a MySQL.
Carga la configuración desde `database.properties` y la expone a los DAO.

## 4. Programación genérica

`DaoGenerico<T>` usa el parámetro `T` para describir el tipo de entidad que
administra cada DAO. Esto evita repetir las mismas firmas CRUD para
`Producto`, `Proveedor`, `Usuario` y las entidades que se implementarán.

## 5. Arquitectura MVC con JavaFX

El proyecto sigue MVC para una aplicación de escritorio. No utiliza Spring Boot
ni endpoints REST.

### Modelo

Ubicación: `models/`

Representa las entidades del dominio:

- `Usuario`, `Producto`, `Proveedor`, `Categoria` y `Cliente`.
- `Compra` y `DetalleCompra`.
- `Venta` y `DetalleVenta`.
- `MovimientoInventario`.

Los modelos de detalle usan una clave compuesta lógica formada por la operación
y el producto: `(id_compra, id_producto)` o `(id_venta, id_producto)`.
`getSubtotal()` calcula el importe desde cantidad y precio unitario; no guarda
un valor duplicado.

### Vista

Ubicación: `src/main/resources/com/cabrera/inventario/views/`

Vistas disponibles:

- `Login.fxml`: acceso al sistema.
- `MenuPrincipal.fxml`: tabla del inventario y navegación principal.
- `NuevoProducto.fxml`: alta de productos.
- `EditarProducto.fxml`: actualización de producto seleccionado.
- `Proveedores.fxml`: mantenimiento de proveedores.

### Controlador

Ubicación: `controllers/`

- `LoginController`: valida acceso y abre el menú principal.
- `MenuPrincipalController`: carga productos, abre ventanas de alta, edición y
  proveedores; también elimina productos y cierra sesión.
- `NuevoProductoController`: valida y registra productos; solicita precio
  sugerido mediante Strategy.
- `EditarProductoController`: actualiza el producto seleccionado y valida
  precio y stock.
- `ProveedoresController`: crea, modifica, lista y elimina proveedores.

### Flujo principal

```text
Vista FXML → Controller → DAO → JDBC → MySQL
```

## 6. Funcionalidades

### 6.1 Autenticación

- Inicio de sesión con usuario activo almacenado en MySQL.
- Validación de campos vacíos.
- Cierre de sesión que vuelve a la pantalla de acceso.
- Salida completa de JavaFX.

### 6.2 Gestión de productos

- Listado de código, nombre, descripción, precio de venta y stock.
- Registro de productos.
- Validación de campos obligatorios, duplicados y stock negativo.
- Consulta opcional de un precio sugerido.
- Edición de nombre, descripción, precio y stock.
- Eliminación con confirmación.

El producto mantiene una sola fuente para stock y precios actuales: la tabla
`productos`.

### 6.3 Gestión de proveedores

- Listado de proveedores.
- Alta, edición y eliminación.
- Datos de contacto: teléfono, correo y dirección.
- Estado activo/inactivo.

### 6.4 Funcionalidades preparadas, aún no implementadas en la interfaz

- CRUD de categorías y clientes.
- Registro de compras y sus detalles.
- Registro de ventas y sus detalles.
- Movimientos de inventario y auditoría por usuario.
- Aplicación de permisos según rol.

## 7. Base de datos

### Motor

MySQL mediante Docker, o MySQL compatible instalado localmente.

### Tablas Principales

| Tabla | Responsabilidad |
| --- | --- |
| `usuarios` | Credenciales, rol y estado de acceso. |
| `categorias` | Clasificación de productos. |
| `proveedores` | Datos de los proveedores. |
| `clientes` | Datos de los clientes. |
| `productos` | Catálogo, precios de referencia y stock actual. |
| `compras` | Encabezado de compras a proveedor. |
| `detalle_compras` | Productos, cantidad y costo histórico de cada compra. |
| `ventas` | Encabezado de ventas a cliente. |
| `detalle_ventas` | Productos, cantidad y precio histórico de cada venta. |
| `movimientos` | Entradas, salidas y ajustes de stock. |

### Nomenclatura de constraints

- `PK_`: clave primaria simple, por ejemplo `PK_productos`.
- `FK_`: clave foránea simple, por ejemplo `FK_productos_categoria`.
- `PK2_`: clave primaria compuesta, por ejemplo `PK2_detalle_ventas`.
- `FK2_`: clave foránea de una tabla con clave compuesta, por ejemplo
  `FK2_detalle_ventas_producto`.
- `UQ_`: valor único, por ejemplo `UQ_productos_codigo`.
- `CK_` y `CK2_`: reglas de validación, como stock no negativo y cantidad
  mayor que cero.

### Scripts

- `database_script.sql`: crea una base nueva ya normalizada.
- `database_migration_normalizacion.sql`: transforma el esquema anterior;
  requiere respaldo porque elimina la tabla redundante `detalleproductos`.

## 8. Configuración y ejecución

### Variables de entorno para MySQL con Docker

Archivo `.env`:

```properties
MYSQL_DATABASE=sistema_inventarios
MYSQL_USER=inventarios_app
MYSQL_PASSWORD=contraseña
MYSQL_ROOT_PASSWORD=cambia_esta_clave_root
MYSQL_PORT=3307
```

### Conexión de la aplicación

Archivo no versionado: `src/main/resources/database.properties`

```properties
db.url=jdbc:mysql://localhost:3307/sistema_inventarios?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Mexico_City
db.user=inventarios_app
db.password=contraseña
```

### Credenciales iniciales

```text
Usuario: admin
Contraseña: admin123
```

### Iniciar la base de datos

```powershell
docker compose up -d
docker compose ps
```

### Compilar y ejecutar

```powershell
./mvnw.cmd compile
./mvnw.cmd javafx:run
```

Se necesita un JDK compatible con Java 21. El archivo `pom.xml` configura el
compilador, JavaFX, MySQL Connector/J y JSON.

## 9. Verificación actual

La compilación se validó con:

```powershell
./mvnw.cmd -o compile
```

La verificación completa contra MySQL requiere tener Docker Desktop o un
servidor MySQL en ejecución. Después de crear una base nueva se recomienda
probar login, alta, edición, listado, eliminación de productos y el CRUD de
proveedores.

## 10. Más implementaciones que se podrían mejorar

1. Implementar categorías y clientes en interfaz y DAO.
2. Implementar compras, ventas y detalles usando transacciones.
3. Registrar un movimiento de inventario por cada compra, venta o ajuste.
4. Sustituir SHA-1 por BCrypt o Argon2.
5. Usar `BigDecimal` para importes monetarios.
6. Incorporar permisos por rol y pruebas automatizadas para los DAO.

# API usada para el ejemplo
- API disponible en: https://fakestoreapi.comß