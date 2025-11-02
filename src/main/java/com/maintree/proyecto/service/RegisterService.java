package com.maintree.proyecto.service;

import com.maintree.proyecto.dao.RolRepository;
import com.maintree.proyecto.dao.UsuarioRepository;
import com.maintree.proyecto.model.Rol;
import com.maintree.proyecto.model.Usuario;
import com.maintree.proyecto.util.PasswordHasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Set;

@Service
public class RegisterService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    public boolean registerUser(Usuario newUser, String rolNombre) throws SQLException, IllegalStateException {
        if (usuarioRepository.findByEmail(newUser.getEmail()) != null) {
            throw new IllegalStateException("El correo electrónico ya está registrado.");
        }

        String hashedPassword = PasswordHasher.hashPassword(newUser.getPassword());
        newUser.setPassword(hashedPassword);
        newUser.setActive(true);

        Rol selectedRole = rolRepository.findByNombre(rolNombre);
        if (selectedRole == null) {
            throw new IllegalStateException("El rol '" + rolNombre + "' seleccionado no es válido o no existe en la base de datos.");
        }
        Set<Rol> roles = Collections.singleton(selectedRole);
        newUser.setRoles(roles);

        usuarioRepository.save(newUser);

        return true;
    }
}