# Base de datos con Docker

La aplicacion se conecta a MySQL en localhost:3307. Docker ayuda a ejecutar la base sin
necesitar instalar MySQL como servicio de manera local.

## Preparación

1. Instala e inicia Docker Desktop.
2. Deten el servicio local MySQL80: ambos servicios usan el puerto 3307.
3. Copia `.env.example` como `.env` y cambia las contraseñas de ejemplo.

## Inicio

Desde la raiz del proyecto ejecuta:

```powershell
docker compose up -d
docker compose ps
```

La primera inicializacion ejecuta `database_script.sql`, que crea la base,
tablas y datos de demostracion.

## Actualizar una base existente

`database_script.sql` crea el diseño normalizado solamente en una base nueva.
Si ya tienes el volumen con el esquema anterior, no ejecutes este archivo como
si fuera una actualización: MySQL conserva las tablas existentes.

1. Haz una copia de seguridad de `sistema_inventarios`.
2. Ejecuta `database_migration_normalizacion.sql` con un usuario que pueda usar
   `ALTER TABLE` y `DROP TABLE`.
3. Revisa que las consultas de verificación iniciales no devuelvan filas antes
   de permitir que el script elimine `detalleproductos`.
4. Reinicia la aplicación y verifica el alta, edición y eliminación de un
   producto en una base de desarrollo.

La migración elimina `detalleproductos` porque duplicaba el stock y los precios
actuales de `productos`. No la ejecutes sobre datos reales sin revisar ese
contenido y sin respaldo.

La aplicacion debe usar `src/main/resources/database.properties`:

```properties
db.url=jdbc:mysql://localhost:3307/sistema_inventarios?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Mexico_City
db.user=inventarios_app
db.password=admin
```

El usuario inicial de la aplicacion es `admin` y la contrasena es `admin123`.

## Datos persistentes

`docker compose down` detiene y elimina el contenedor, pero conserva los datos
en el volumen `mysql_data`. Para reiniciar la base desde cero usa:

```powershell
docker compose down -v
docker compose up -d
```

El comando con `-v` elimina permanentemente todos los datos de la base.
