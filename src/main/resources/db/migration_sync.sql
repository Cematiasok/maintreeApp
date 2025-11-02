-- migration_sync.sql
-- Script para sincronizar columnas is_active / isActive y normalizar roles
-- Hacer backup antes de ejecutar.

-- 1) Actualizar isActive desde is_active (si existe)
UPDATE usuarios
SET isActive = 1
WHERE (is_active IS NOT NULL AND is_active <> 0);

-- 2) Asegurar que NULL => 0 en isActive
UPDATE usuarios
SET isActive = 0
WHERE isActive IS NULL;

-- 3) Normalizar nombres de roles (mayúsculas, sin acentos)
UPDATE rol SET nombre = 'TECNICO' WHERE nombre LIKE 'Técnico' OR nombre LIKE 'T%Cnico%';
UPDATE rol SET nombre = 'SUPERVISOR' WHERE UPPER(nombre) LIKE '%SUPERVISOR%';
UPDATE rol SET nombre = 'AUTOMATISTA' WHERE UPPER(nombre) LIKE '%AUTOMATISTA%';
UPDATE rol SET nombre = 'ADMIN' WHERE LOWER(nombre) LIKE '%administrador%' OR LOWER(nombre) LIKE '%admin%';

-- 4) Revisar cambios
SELECT id, nombre FROM rol;
SELECT id, email, isActive FROM usuarios LIMIT 50;

-- NOTA: Si todo está correcto y deseas eliminar la columna redundante `is_active`, ejecutar:
-- ALTER TABLE usuarios DROP COLUMN is_active;
