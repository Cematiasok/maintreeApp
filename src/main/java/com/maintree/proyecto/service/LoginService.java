package com.maintree.proyecto.service;

import com.maintree.proyecto.dao.UsuarioDAO;
import com.maintree.proyecto.model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

public class LoginService {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public boolean validarCredenciales(String email, String password) {
        Usuario usuario = usuarioDAO.findByEmail(email);

        if (usuario == null) {
            // El usuario no existe
            return false;
        }

        // Comparamos la contraseña del formulario con el hash de la BD
        return BCrypt.checkpw(password, usuario.getPassword());
    }
}