# MaintreeApp - Sistema de Autenticación

MaintreeApp es una aplicación web de ejemplo que implementa un sistema de autenticación de usuarios seguro, incluyendo registro, inicio de sesión y un flujo completo de recuperación de contraseña a través de correo electrónico.

## ✨ Funcionalidades Implementadas

*   **Inicio de Sesión de Usuario:** Autenticación de usuarios mediante correo electrónico y contraseña.
*   **Hashing de Contraseñas:** Almacenamiento seguro de contraseñas utilizando el algoritmo jBCrypt.
*   **Recuperación de Contraseña:**
    *   Los usuarios pueden solicitar un restablecimiento de contraseña si la olvidan.
    *   Se genera un token de seguridad único con una validez de 1 hora.
    *   Se envía un correo electrónico con un enlace para restablecer la contraseña.
*   **Servidor de Correo Local:** Integración con **Mailhog** para capturar y visualizar los correos electrónicos enviados en un entorno de desarrollo local.

## 🚀 Tecnologías Utilizadas

*   **Backend:** Java 22
*   **APIs de Servidor:** Jakarta Servlet 5.0
*   **Base de Datos:** MySQL 8.0
*   **Procesamiento JSON:** Google GSON 2.10.1
*   **Hashing:** jBCrypt 0.4
*   **Servidor de Correo:** Jakarta Mail 2.0.1
*   **Gestión de Proyecto y Dependencias:** Apache Maven
*   **Servidor de Aplicaciones:** Apache Tomcat 10.x (gestionado por `cargo-maven3-plugin`)

## 🏁 Guía de Instalación y Puesta en Marcha

Sigue estos pasos para configurar y ejecutar el proyecto en tu entorno local.

### Prerrequisitos

*   **JDK 22** o superior.
*   **Apache Maven**.
*   **MySQL Server 8.0** o superior.
*   El ejecutable **`mailhog.exe`** (incluido en la raíz del proyecto).

### 1. Configuración de la Base de Datos

El sistema requiere una base de datos MySQL llamada `maintreebd`.

1.  Abre una terminal de MySQL y crea la base de datos:
    ```sql
    CREATE DATABASE maintreebd;
    ```
2.  Selecciona la base de datos:
    ```sql
    USE maintreebd;
    ```

3**Importante:** La aplicación está configurada para conectarse con el usuario `root` sin contraseña. Asegúrate de que tu instancia de MySQL permita esta conexión o ajusta las credenciales en `src/main/java/com/maintree/proyecto/util/DatabaseConnection.java`.

### 2. Iniciar el Servidor de Correo (Mailhog)

Para poder probar el flujo de recuperación de contraseña, necesitas ejecutar el servidor de correo local.

1.  Abre una nueva terminal en la raíz del proyecto.
2.  Ejecuta el archivo `mailhog.exe`:
    ```sh
    ./mailhog.exe
    ```
3.  Mantén esta terminal abierta. Mailhog ahora está capturando todos los correos enviados por la aplicación.
4.  Puedes ver los correos capturados abriendo la siguiente URL en tu navegador: **`http://localhost:8025/`**

### 3. Ejecutar la Aplicación Web

1.  **Clona el repositorio** (si aún no lo has hecho).
2.  Abre una nueva terminal en la raíz del proyecto.
3.  **Empaqueta la aplicación** usando Maven:
    ```sh
    mvn package
    ```
4.  **Despliega y ejecuta la aplicación** en el servidor Tomcat embebido con el plugin de Cargo:
    ```sh
    mvn cargo:run
    ```
5.  Una vez que el servidor se inicie, puedes acceder a la aplicación en: **`http://localhost:8080/mywebapp/main.html`**

## 🔀 Cómo Probar la Recuperación de Contraseña

1.  **Inserta un usuario de prueba:** Para poder probar la recuperación, primero necesitas un usuario en la base de datos. Puedes añadir uno manualmente con una contraseña hasheada o implementar una función de registro.
2.  Ve a la página de recuperación: `http://localhost:8080/mywebapp/recuperar.html`.
3.  Ingresa el correo electrónico del usuario de prueba y haz clic en "Enviar".
4.  **Revisa Mailhog:** Abre `http://localhost:8025/`. Verás un nuevo correo electrónico.
5.  **Haz clic en el enlace** dentro del correo. Te redirigirá a la página `reset-password.html` con el token en la URL.
6.  Ingresa una nueva contraseña y confírmala.
7.  ¡Listo! La contraseña del usuario ha sido actualizada en la base de datos.

## 📄 Licencia
Maintree © 2025 de Herrada Carlos Edgar Matias está bajo la licencia Creative Commons Atribución-NoComercial-CompartirIgual 4.0 Internacional. Para ver una copia de esta licencia, visite https://creativecommons.org/licenses/by-nc-sa/4.0/