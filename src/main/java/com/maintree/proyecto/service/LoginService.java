package com.maintree.proyecto.service;

import com.maintree.proyecto.dao.UsuarioRepository;
import com.maintree.proyecto.model.Usuario;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public boolean validarCredenciales(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmail(email);

        if (usuario == null) {
            // El usuario no existe
            return false;
        }

        // Validar que el usuario esté activo
        if (!usuario.isActive()) {
            return false;
        }

        // Comparamos la contraseña del formulario con el hash de la BD
        return BCrypt.checkpw(password, usuario.getPassword());
    }
}