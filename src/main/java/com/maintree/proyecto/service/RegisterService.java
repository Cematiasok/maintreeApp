package com.maintree.proyecto.service;

import com.maintree.proyecto.dao.RolDAO;
import com.maintree.proyecto.dao.UsuarioDAO;
import com.maintree.proyecto.model.Rol;
import com.maintree.proyecto.model.Usuario;
import com.maintree.proyecto.util.PasswordHasher;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Set;

public class RegisterService {

    private final UsuarioDAO usuarioDAO;
    private final RolDAO rolDAO;
    private static final String DEFAULT_ROLE = "CLIENTE"; // Rol por defecto para nuevos usuarios

    public RegisterService() {
        this.usuarioDAO = new UsuarioDAO();
        this.rolDAO = new RolDAO();
    }

    // Constructor para inyección de dependencias (útil para pruebas)
    public RegisterService(UsuarioDAO usuarioDAO, RolDAO rolDAO) {
        this.usuarioDAO = usuarioDAO;
        this.rolDAO = rolDAO;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * @param newUser El objeto Usuario con los datos del formulario.
     * @param rolNombre El nombre del rol seleccionado en el formulario.
     * @return true si el registro fue exitoso.
     * @throws SQLException si hay un error con la base de datos.
     * @throws IllegalStateException si el email ya está en uso o el rol por defecto no existe.
     */
    public boolean registerUser(Usuario newUser, String rolNombre) throws SQLException, IllegalStateException {
        // 1. Verificar si el email ya existe
        if (usuarioDAO.findByEmail(newUser.getEmail()) != null) {
            throw new IllegalStateException("El correo electrónico ya está registrado.");
        }

        // 2. Hashear la contraseña antes de guardarla
        String hashedPassword = PasswordHasher.hashPassword(newUser.getPassword());
        newUser.setPassword(hashedPassword);
        newUser.setActive(true); // Asegurarse de que el usuario esté activo

        // 3. Crear el usuario en la base de datos
        int newUserId = usuarioDAO.create(newUser);

        if (newUserId > 0) {
            // 4. Asignar el rol por defecto
            Rol defaultRole = rolDAO.findByNombre(DEFAULT_ROLE);
            Rol selectedRole = rolDAO.findByNombre(rolNombre);
            if (selectedRole == null) {
                throw new IllegalStateException("El rol '" + rolNombre + "' seleccionado no es válido o no existe en la base de datos.");
            }
            Set<Rol> roles = Collections.singleton(selectedRole);
            usuarioDAO.actualizarRolesUsuario(newUserId, roles); // Usamos el método que ya tienes
            return true;
        }

        return false;
    }
}