# 🛑 CÓMO DETENER TOMCAT

## Si usas Cargo Maven Plugin (tu caso)

### Opción 1: Comando de Cargo (recomendado)
```bash
mvn cargo:stop
```

### Opción 2: Puerto 8080 ocupado
Si el proceso sigue activo en el puerto 8080:

**Windows (PowerShell):**
```powershell
# Ver qué proceso usa el puerto 8080
netstat -ano | findstr :8080

# Matar el proceso (reemplaza PID con el número que aparezca)
taskkill /F /PID <PID>
```

**Linux/Mac:**
```bash
# Ver qué proceso usa el puerto 8080
lsof -i :8080

# Matar el proceso
kill -9 <PID>
```

### Opción 3: Matar todos los procesos Java
```bash
# Windows
taskkill /F /IM java.exe

# Linux/Mac
pkill -9 java
```

### Opción 4: Matar procesos de Tomcat específicamente
```bash
# Windows
Get-Process | Where-Object {$_.ProcessName -like "*tomcat*" -or $_.CommandLine -like "*tomcat*"} | Stop-Process -Force

# Linux/Mac
ps aux | grep tomcat | grep -v grep | awk '{print $2}' | xargs kill -9
```

## Comandos útiles de Cargo

```bash
# Iniciar servidor
mvn cargo:run

# Detener servidor
mvn cargo:stop

# Reiniciar (detener + iniciar)
mvn cargo:stop cargo:run

# Solo limpiar carpeta target
mvn clean
```

## Verificar si está corriendo

```bash
# Windows
netstat -ano | findstr :8080

# Linux/Mac
lsof -i :8080

# O abrir en navegador
http://localhost:8080/mywebapp/main.html
```

## Consejos

1. Si usas Cargo desde Maven, siempre usa `mvn cargo:stop` primero
2. Si no responde, usa Ctrl+C en la terminal donde está corriendo
3. Como último recurso, cierra la terminal completamente
4. Si reinicias, recuerda ejecutar `mvn cargo:run` de nuevo
