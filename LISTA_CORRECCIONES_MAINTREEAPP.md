# LISTA DE CORRECCIONES PARA MAINTREEAPP

**Fecha:** $(date)  
**Proyecto:** MaintreeApp - Sistema de Autenticación  
**Prioridad:** ALTA = Crítico, MEDIA = Importante, BAJA = Mejora

---

## 🔴 ERRORES CRÍTICOS QUE ROMPEN LA APLICACIÓN

### 1. **CRÍTICO - Error de NullPointerException potencial**
**Archivo:** `src/main/java/com/maintree/proyecto/service/PasswordRecoveryService.java`  
**Línea:** 58  
**Problema:** Si `usuario.getResetTokenExpiry()` es `null`, lanzará NullPointerException  
**Solución:**
```java
if (usuario == null || usuario.getResetTokenExpiry() == null || usuario.getResetTokenExpiry().before(new Date())) {
    return false;
}
```

### 2. **CRÍTICO - Inconsistencia en clases CSS**
**Archivo:** `src/main/webApp/recuperar.html`  
**Línea:** 17  
**Problema:** Usa clase `info-childs` pero CSS define `Imfo-childs` (con I mayúscula)  
**Solución:** Cambiar `info-childs` por `Imfo-childs` en recuperar.html línea 17

### 3. **CRÍTICO - Falta validación de usuario activo en login**
**Archivo:** `src/main/java/com/maintree/proyecto/service/LoginService.java`  
**Línea:** 11-20  
**Problema:** Permite login de usuarios inactivos  
**Solución:**
```java
public boolean validarCredenciales(String email, String password) {
    Usuario usuario = usuarioDAO.findByEmail(email);
    
    if (usuario == null) {
        return false;
    }
    
    // Validar que el usuario esté activo
    if (!usuario.isActive()) {
        return false;
    }
    
    return BCrypt.checkpw(password, usuario.getPassword());
}
```

---

## 🟠 ERRORES DE CÓDIGO DUPLICADO E INCONSISTENCIAS

### 4. **ALTA - Constructor comentado en RegisterService**
**Archivo:** `src/main/java/com/maintree/proyecto/service/RegisterService.java`  
**Línea:** 19-22  
**Problema:** Constructor por defecto está comentado, pero puede ser necesario  
**Solución:** Descomentar el constructor o eliminar completamente el comentario

### 5. **ALTA - Línea de código sin efecto**
**Archivo:** `src/main/java/com/maintree/proyecto/service/RegisterService.java`  
**Línea:** 54  
**Problema:** `rolDAO.findByNombre(DEFAULT_ROLE);` no hace nada con el resultado  
**Solución:** Eliminar esa línea, es código muerto

### 6. **ALTA - Inconsistencia en inyección de dependencias**
**Archivo:** `src/main/java/com/maintree/proyecto/service/LoginService.java`  
**Línea:** 9  
**Problema:** Usa `new UsuarioDAO()` en lugar de inyección por constructor como otros servicios  
**Solución:**
```java
private final UsuarioDAO usuarioDAO;

public LoginService() {
    this.usuarioDAO = new UsuarioDAO();
}

// Constructor para pruebas
public LoginService(UsuarioDAO usuarioDAO) {
    this.usuarioDAO = usuarioDAO;
}
```

### 7. **ALTA - Archivo PasswordHashingManual en src/main**
**Archivo:** `src/main/java/com/maintree/proyecto/util/PasswordHashingManual.java`  
**Línea:** Todo el archivo  
**Problema:** Clase con método main() en carpeta de producción, debería estar en test/ o eliminarse  
**Solución:** Mover a `src/test/java/com/maintree/proyecto/util/` o eliminar

---

## 🟡 PROBLEMAS DE LOGGING Y MANEJO DE ERRORES

### 8. **MEDIA - Uso excesivo de printStackTrace()**
**Archivos:** Múltiples archivos Java  
**Problema:** 12 ocurrencias de `printStackTrace()` sin logging apropiado  
**Solución:** Agregar dependencia de logging en pom.xml:
```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>2.0.7</version>
</dependency>
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.4.8</version>
</dependency>
```

### 9. **MEDIA - System.out.println en producción**
**Archivos:** `PasswordRecoveryService.java`, `RolService.java`, `PasswordHashingManual.java`  
**Problema:** 8 ocurrencias de `System.out.println()`  
**Solución:** Reemplazar con logger:
```java
private static final Logger logger = LoggerFactory.getLogger(PasswordRecoveryService.class);
// ...
logger.info("Email de recuperación enviado a {}", usuario.getEmail());
```

---

## 🔵 MEJORAS DE SEGURIDAD

### 10. **ALTA - Credenciales hardcodeadas en DatabaseConnection**
**Archivo:** `src/main/java/com/maintree/proyecto/util/DatabaseConnection.java`  
**Línea:** 8-10  
**Problema:** Credenciales en código fuente  
**Solución:** Crear archivo `src/main/resources/application.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/maintreebd
db.user=root
db.password=
```

Y modificar `DatabaseConnection.java` para leer las propiedades.

### 11. **MEDIA - Configuración hardcodeada de email**
**Archivo:** `src/main/java/com/maintree/proyecto/service/PasswordRecoveryService.java`  
**Línea:** 79-84  
**Problema:** Configuración SMTP en código  
**Solución:** Mover a `application.properties`:
```properties
mail.host=localhost
mail.port=1025
mail.username=noreply@maintree.app
mail.password=
mail.auth=false
```

### 12. **MEDIA - Falta validación de fortaleza de contraseña**
**Archivo:** `src/main/java/com/maintree/proyecto/service/RegisterService.java`  
**Línea:** 38  
**Problema:** Acepta cualquier contraseña sin validar  
**Solución:** Agregar método de validación:
```java
private boolean isValidPassword(String password) {
    // Al menos 8 caracteres
    if (password.length() < 8) {
        return false;
    }
    // Verificar que tenga al menos una mayúscula, una minúscula y un número
    return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$");
}
```

### 13. **BAJA - Falta validación CSRF**
**Problema:** No hay protección contra ataques CSRF  
**Solución:** Implementar tokens CSRF en formularios

---

## 🟢 MEJORAS DE CÓDIGO Y MANTENIBILIDAD

### 14. **MEDIA - Código duplicado en mapeo de Usuario**
**Archivo:** `src/main/java/com/maintree/proyecto/dao/UsuarioDAO.java`  
**Línea:** 41-57 y 80-96  
**Problema:** Lógica de mapeo de ResultSet duplicada  
**Solución:** Crear método privado `mapUsuarioFromResultSet(ResultSet rs, Connection conn)`

### 15. **BAJA - Falta manejo de excepciones específicas en RegisterServlet**
**Archivo:** `src/main/java/com/maintree/proyecto/controller/RegisterServlet.java`  
**Línea:** 76-82  
**Problema:** Captura genérica de SQLException  
**Solución:** Ser más específico con los tipos de error SQL

### 16. **MEDIA - Falta clase de constantes para strings mágicos**
**Problema:** URLs y mensajes hardcodeados en múltiples lugares  
**Solución:** Crear `Constants.java`:
```java
public class Constants {
    public static final String DEFAULT_ROLE = "CLIENTE";
    public static final String SERVLET_LOGIN = "/login";
    public static final String SERVLET_REGISTER = "/register";
    public static final long TOKEN_EXPIRATION_MS = 3600000;
}
```

---

## 🟣 MEJORAS DE FRONTEND

### 17. **MEDIA - Falta validación de contraseñas coincidentes en reset**
**Archivo:** `src/main/webApp/reset-password.html`  
**Problema:** No valida en cliente que las contraseñas coincidan  
**Solución:** Agregar validación JavaScript antes de submit:
```javascript
document.getElementById('reset-form').addEventListener('submit', function(e) {
    const newPass = document.getElementById('newPassword').value;
    const confirmPass = document.getElementById('confirmPassword').value;
    
    if (newPass !== confirmPass) {
        e.preventDefault();
        document.getElementById('error-message').textContent = 'Las contraseñas no coinciden';
        return false;
    }
});
```

### 18. **BAJA - Mejorar feedback visual en formularios**
**Problema:** No hay indicadores de carga durante requests  
**Solución:** Agregar spinner o mensaje "Procesando..." más visible

### 19. **BAJA - Accesibilidad HTML**
**Problema:** Falta `aria-labels` y mejor estructura semántica  
**Solución:** Agregar atributos de accesibilidad

### 20. **BAJA - Estructura HTML de reset-password.html**
**Archivo:** `src/main/webApp/reset-password.html`  
**Línea:** 26-60  
**Problema:** Div anidado innecesario `<div class="form-information">` dentro de otro contenedor  
**Solución:** Limpiar estructura HTML

---

## 📋 ARCHIVOS DUPLICADOS A ELIMINAR (si existen)

### 21. **Verificar y eliminar si son duplicados:**
- `RegisterService.java` (raíz)
- `RegisterServlet.java` (raíz)
- `register.html` (raíz)
- `correo.txt` (raíz)
- `mailhog.exe` (considerar mover a carpeta tools/ o documentar)

---

## 🎯 RESUMEN POR PRIORIDAD

### **CRÍTICO - Corregir INMEDIATAMENTE:**
1. NullPointerException en PasswordRecoveryService (línea 58)
2. Inconsistencia CSS en recuperar.html (info-childs → Imfo-childs)
3. Falta validación de usuario activo en LoginService

### **ALTA - Corregir PRONTO:**
4. Constructor comentado en RegisterService
5. Línea de código sin efecto en RegisterService
6. Inconsistencia en inyección de dependencias en LoginService
7. PasswordHashingManual en src/main
10. Credenciales hardcodeadas

### **MEDIA - Mejorar:**
8. Implementar logging apropiado
9. Reemplazar System.out.println
12. Validación de contraseña fuerte
14. Refactorizar código duplicado
15. Mejorar manejo de excepciones
16. Crear clase de constantes
17. Validación cliente en reset password

### **BAJA - Opcional:**
13. Protección CSRF
18. Mejorar feedback visual
19. Accesibilidad HTML
20. Limpiar estructura HTML

---

## 📝 NOTAS ADICIONALES

- **Testing:** No se encontraron archivos de prueba en `src/test/java/`. Considerar agregar tests unitarios.
- **Documentación:** Faltan Javadocs en varios métodos públicos.
- **Configuración:** Considerar usar Spring Framework o similar para DI y configuración en futuras versiones.
- **Base de datos:** Verificar que existen índices en columnas `email` y `reset_token` en la tabla usuarios.
- **Pool de conexiones:** Considerar implementar HikariCP para gestión de conexiones en producción.

---

## 🚀 ORDEN DE IMPLEMENTACIÓN SUGERIDO

1. Corregir errores CRÍTICOS (1, 2, 3)
2. Corregir errores ALTA (4, 5, 6, 7, 10)
3. Implementar mejoras MEDIA (8, 9, 12, 14, 15, 16, 17)
4. Eliminar archivos duplicados si existen
5. Aplicar mejoras BAJA según tiempo disponible

---

**Total de correcciones identificadas:** 21  
**Tiempo estimado:** 8-12 horas de desarrollo
