package com.maintree.proyecto.service;

import com.maintree.proyecto.dao.RolDAO;
import com.maintree.proyecto.dao.UsuarioDAO;
import com.maintree.proyecto.model.Rol;
import com.maintree.proyecto.model.Usuario;

import java.sql.SQLException;
import java.util.Set;

/**
 * RolService - Versión Completa y Corregida.
 *
 * Esta clase contiene la lógica de negocio para asignar y revocar roles.
 * Se comunica con la capa DAO (UsuarioDAO, RolDAO) para persistir los cambios.
 *
 * CORREGIDO: Llama a actualizarRolesUsuario usando el ID (int) del usuario,
 * no el email (String).
 */
public class RolService {

    private UsuarioDAO usuarioDAO;
    private RolDAO rolDAO;

    // Constructor para Inyección de Dependencias manual
    public RolService(UsuarioDAO usuarioDAO, RolDAO rolDAO) {
        this.usuarioDAO = usuarioDAO;
        this.rolDAO = rolDAO;
    }

    /**
     * Asigna un rol a un usuario.
     * Esta es la lógica de negocio principal.
     *
     * @param email El email del usuario al que se le asignará el rol.
     * @param rolNombre El nombre del rol a asignar (Ej: "ADMIN", "TECNICO").
     * @return true si la asignación fue exitosa, false si no.
     */
    public boolean asignarRolAUsuario(String email, String rolNombre) {

        try {
            // 1. Encontrar el usuario por email.
            // (findByEmail ya está modificado para cargar el usuario, su ID y sus roles)
            Usuario usuario = usuarioDAO.findByEmail(email);
            if (usuario == null) {
                System.err.println("Error en RolService: No se encontró el usuario con email " + email);
                return false;
            }

            // 2. Encontrar el rol por nombre.
            // (findByNombre cargará el rol y su ID)
            Rol rol = rolDAO.findByNombre(rolNombre);
            if (rol == null) {
                System.err.println("Error en RolService: No se encontró el rol con nombre " + rolNombre);
                return false;
            }

            // 3. Modificar el objeto Usuario en memoria.
            // 'rolesDelUsuario' ya contiene los roles que el usuario tenía.
            Set<Rol> rolesDelUsuario = usuario.getRoles();

            // 4. Verificar si el usuario ya tiene este rol para no duplicar
            // (Usamos el ID para una comparación segura)
            for (Rol r : rolesDelUsuario) {
                if (r.getId() == rol.getId()) {
                    System.out.println("Info en RolService: El usuario " + email + " ya tiene el rol " + rolNombre);
                    return true; // Ya lo tiene, la operación es exitosa.
                }
            }

            // Si no lo tiene, lo añadimos
            rolesDelUsuario.add(rol);
            usuario.setRoles(rolesDelUsuario); // Actualizamos el Set en el objeto usuario

            // 5. Persistir todos los cambios en la base de datos (Transacción)
            // ¡ESTA ES LA CORRECCIÓN CLAVE!
            // Usamos el ID (int) del usuario, no el email.
            usuarioDAO.actualizarRolesUsuario(usuario.getId(), usuario.getRoles());

            System.out.println("Éxito en RolService: Rol '" + rolNombre + "' asignado a " + email);
            return true;

        } catch (SQLException e) {
            System.err.println("Error de base de datos en RolService al asignar rol: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Revoca un rol de un usuario.
     *
     * @param email El email del usuario al que se le revocará el rol.
     * @param rolNombre El nombre del rol a revocar (Ej: "ADMIN", "TECNICO").
     * @return true si la revocación fue exitosa, false si no.
     */
    public boolean revocarRolDeUsuario(String email, String rolNombre) {
        try {
            // 1. Encontrar el usuario por email
            Usuario usuario = usuarioDAO.findByEmail(email);
            if (usuario == null) {
                System.err.println("Error en RolService: No se encontró el usuario con email " + email);
                return false;
            }

            // 2. Verificar si el usuario tiene ese rol y encontrarlo
            Set<Rol> rolesDelUsuario = usuario.getRoles();
            Rol rolARevocar = null;
            for (Rol rol : rolesDelUsuario) {
                if (rol.getNombre().equals(rolNombre)) {
                    rolARevocar = rol;
                    break;
                }
            }

            // 3. Si no tiene el rol, no hay nada que hacer
            if (rolARevocar == null) {
                System.out.println("Info en RolService: El usuario " + email + " no tenía el rol " + rolNombre);
                return true; // La operación es exitosa (el rol ya no está)
            }

            // 4. Quitar el rol de la colección en memoria
            rolesDelUsuario.remove(rolARevocar);
            usuario.setRoles(rolesDelUsuario); // Actualizamos el Set

            // 5. Persistir los cambios en la BD (Transacción)
            // ¡ESTA ES LA CORRECCIÓN CLAVE!
            // Usamos el ID (int) del usuario, no el email.
            usuarioDAO.actualizarRolesUsuario(usuario.getId(), usuario.getRoles());

            System.out.println("Éxito en RolService: Rol '" + rolNombre + "' revocado de " + email);
            return true;

        } catch (SQLException e) {
            System.err.println("Error de base de datos en RolService al revocar rol: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

