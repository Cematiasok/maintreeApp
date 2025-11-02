# MaintreeApp - Sistema de Autenticación

MaintreeApp es una aplicación web de ejemplo que implementa un sistema de autenticación de usuarios, incluyendo registro, inicio de sesión, administración básica de usuarios (UI de administración) y un flujo de recuperación de contraseña por correo electrónico.

Este README se actualizó para incluir las últimas mejoras y los pasos necesarios para ejecutar la aplicación en un entorno local o preparar un despliegue en producción.

## ✨ Características principales

- Autenticación de usuarios (login / logout).
- Registro de usuarios.
- Hashing seguro de contraseñas.
- Recuperación y reset de contraseña vía email (token con expiración).
- UI de administración de usuarios (listar/editar/activar/desactivar/eliminar).
- Seeder opcional que crea un rol `ADMIN` y un usuario admin para entornos de desarrollo.
- Integración con Mailhog para pruebas de correo en local.

## 🚀 Tecnologías

- Java 22 / Spring Boot
- Jakarta Persistence (JPA) / Hibernate
- MySQL 8
- Maven
- Bootstrap 5 (UI estática bajo `src/main/resources/static`)
- Mail (Jakarta Mail) + Mailhog para pruebas locales

## 🧰 Prerrequisitos

- JDK 22 o superior
- Apache Maven
- MySQL Server 8.x
- En entorno Windows: `mailhog.exe` (incluido en la raíz del proyecto en este repositorio)

## 🏁 Configuración y puesta en marcha (local)

Sigue estos pasos para dejar la aplicación corriendo en tu máquina local (Windows / PowerShell ejemplos).

### 1) Base de datos

1. Crea la base de datos MySQL (ejemplo):

```sql
CREATE DATABASE maintreebd;
```

2. Asegúrate de que las credenciales en `src/main/resources/application.properties` son correctas para tu entorno (por defecto el proyecto usaba `root` sin contraseña). Recomendado: crear un usuario específico para la app.

### 2) Mailhog (correo local)

1. Abre PowerShell en la raíz del proyecto y ejecuta:

```powershell
# En Windows
./mailhog.exe
```

2. Accede a la interfaz de Mailhog en http://localhost:8025 para ver los correos enviados desde la app.

### 3) Construir y ejecutar la aplicación

1. Empaqueta con Maven (desde la raíz del proyecto):

```powershell
mvn -DskipTests package
```

2. Ejecuta la app con Spring Boot (opciones):

- Ejecutar con Maven (modo rápido):

```powershell
mvn spring-boot:run
```

- Ejecutar el JAR generado:

```powershell
java -jar target\\maintreeApp-*.jar
```

Nota: el puerto por defecto en `application.properties` puede ser `8081` (revisar `server.port`). Ajusta las URLs en el navegador en consecuencia.

### 4) Admin Seeder (rol/usuario ADMIN)

El proyecto incluye una clase `AdminSeeder` (`src/main/java/com/maintree/proyecto/config/AdminSeeder.java`) que crea un rol `ADMIN` y un usuario administrador al arrancar si la propiedad `admin.create` está activada.

Nota importante: en la configuración local por defecto de esta rama `admin.create` está habilitado (`admin.create=true`) y el `server.port` por defecto está configurado a `8081` en `src/main/resources/application.properties`. Esto facilita pruebas locales — recuerda desactivarlo antes de publicar a producción.

Habilitar / deshabilitar (alternativas)

- Usando `application.properties` (local):

```properties
admin.create=true            # crea/asegura rol ADMIN y usuario admin al arrancar
admin.email=admin@local
admin.password=AdminPass123!
```

- Usando variable de entorno (recomendado para despliegue):

PowerShell (Windows):
```powershell
$env:ADMIN_CREATE = 'false'
mvn spring-boot:run
```

Linux / Docker (ejemplo):
```bash
# export variable para el proceso
export ADMIN_CREATE=false
java -jar target/maintreeApp-*.jar
```

- Usando JVM arg al iniciar el JAR:

```powershell
mvn -DskipTests package
java -Dadmin.create=false -jar target\\maintreeApp-*.jar
```

- Usando perfiles de Spring (hacer el seeder dependiente de perfil `dev`):

Puedes modificar `AdminSeeder` añadiendo `@Profile("dev")` para que solo se ejecute con `spring.profiles.active=dev`. Esto es una medida segura para garantizar que el seeder no se ejecute en entornos no intencionados.

> Riesgos: si el seeder está activo en producción puede crear un usuario/admin con credenciales por defecto. No dejes credenciales por defecto en producción ni subidas al repositorio; usa gestores de secretos o variables de entorno para credenciales reales.

### 5) Páginas y UI relevantes

- Archivos estáticos: `src/main/resources/static/` (ej: `user-admin.html`, `RoleAssign.html`, `js/user-admin.js`, `js/script.js`).
- Interfaz de administración: `user-admin.html` — permite listar, editar y cambiar roles/estado de los usuarios.

## 🔧 Despliegue: buenas prácticas

- No dejar `admin.create=true` en producción. Usar variables de entorno y gestores de secretos para credenciales.
- Configura `spring.profiles.active` y separa `application.properties` para `dev` y `prod` (p. ej. `application-dev.properties`, `application-prod.properties`).
- Haz backup de la base de datos antes de aplicar cambios destructivos (borrados, migraciones).

## 🧩 SQL útil: eliminar rol ADMIN (si es necesario)

Ejecuta con precaución y haz backup antes:

```sql
-- Ver id del rol ADMIN
SELECT id FROM rol WHERE nombre = 'ADMIN';

-- Reemplaza <ID> por el id real antes de ejecutar
DELETE FROM rolpermiso WHERE rol_id = <ID>;
DELETE FROM usuariorol WHERE rol_id = <ID>;
DELETE FROM rol WHERE id = <ID>;
```

Si prefieres que limpie el registro mediante JPA, puedo añadir un endpoint de administración temporal para hacerlo de forma controlada.

## 🧪 Pruebas y validación rápida

- Inicia Mailhog y la aplicación.
- Crea un usuario (vía registro o por script) y prueba la recuperación de contraseña.
- Abre `user-admin.html` para verificar la UI administrativa (necesitas un usuario con rol ADMIN para ver todo el panel).

## 🛠️ Solución de problemas comunes

- Si no ves correos en Mailhog: verifica que `spring.mail.host` y `spring.mail.port` concuerdan con Mailhog (por defecto host=localhost y port=1025).
- Si la app no arranca por la BD: revisa `spring.datasource.url`, usuario/contraseña y que MySQL esté escuchando en el puerto correcto.
- Si el `user-admin.html` muestra un comportamiento extraño en dropdowns o selects, revisa `static/js/user-admin.js` donde se implementó la lógica del formulario y la carga de roles.

## 📄 Licencia
Maintree © 2025 de Herrada Carlos Edgar Matias — licencia Creative Commons Atribución-NoComercial-CompartirIgual 4.0 Internacional.

---

Si quieres, puedo:

- Añadir ejemplos de `docker-compose.yml` para levantar MySQL + Mailhog + la app.
- Añadir scripts SQL de inicialización más completos (roles, permisos).
- Crear el endpoint seguro para limpiar el rol `ADMIN` automáticamente (solo accesible desde `localhost` o con autenticación).

Dime qué prefieres y lo preparo.