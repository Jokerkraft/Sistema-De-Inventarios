# Base de datos con Docker

La aplicacion se conecta a MySQL en localhost:3307. Docker ejecuta la base sin
necesitar instalar MySQL como servicio de Windows.

## Preparacion

1. Instala e inicia Docker Desktop.
2. Deten el servicio local MySQL80: ambos servicios usan el puerto 3307.
3. Copia `.env.example` como `.env` y cambia las contrasenas de ejemplo.

## Inicio

Desde la raiz del proyecto ejecuta:

```powershell
docker compose up -d
docker compose ps
```

La primera inicializacion ejecuta `database_script.sql`, que crea la base,
tablas y datos de demostracion.

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
