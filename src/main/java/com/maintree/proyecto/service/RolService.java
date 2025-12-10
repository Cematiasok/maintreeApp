package com.maintree.proyecto.service;

import com.maintree.proyecto.dao.RolRepository;
import com.maintree.proyecto.dao.UsuarioRepository;
import com.maintree.proyecto.model.Rol;
import com.maintree.proyecto.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class RolService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    public List<Rol> findAll() {
        return rolRepository.findAll();
    }

    public boolean asignarRolAUsuario(String email, String rolNombre) {
        try {
            Usuario usuario = usuarioRepository.findByEmail(email);
            if (usuario == null) {
                System.err.println("Error en RolService: No se encontró el usuario con email " + email);
                return false;
            }

            Rol rol = rolRepository.findByNombre(rolNombre);
            if (rol == null) {
                System.err.println("Error en RolService: No se encontró el rol con nombre " + rolNombre);
                return false;
            }

            Set<Rol> rolesDelUsuario = usuario.getRoles();

            for (Rol r : rolesDelUsuario) {
                if (r.getId() == rol.getId()) {
                    System.out.println("Info en RolService: El usuario " + email + " ya tiene el rol " + rolNombre);
                    return true;
                }
            }

            rolesDelUsuario.add(rol);
            usuario.setRoles(rolesDelUsuario);

            usuarioRepository.save(usuario);

            System.out.println("Éxito en RolService: Rol '" + rolNombre + "' asignado a " + email);
            return true;

        } catch (Exception e) {
            System.err.println("Error de base de datos en RolService al asignar rol: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean revocarRolDeUsuario(String email, String rolNombre) {
        try {
            Usuario usuario = usuarioRepository.findByEmail(email);
            if (usuario == null) {
                System.err.println("Error en RolService: No se encontró el usuario con email " + email);
                return false;
            }

            Set<Rol> rolesDelUsuario = usuario.getRoles();
            Rol rolARevocar = null;
            for (Rol rol : rolesDelUsuario) {
                if (rol.getNombre().equals(rolNombre)) {
                    rolARevocar = rol;
                    break;
                }
            }

            if (rolARevocar == null) {
                System.out.println("Info en RolService: El usuario " + email + " no tenía el rol " + rolNombre);
                return true;
            }

            rolesDelUsuario.remove(rolARevocar);
            usuario.setRoles(rolesDelUsuario);

            usuarioRepository.save(usuario);

            System.out.println("Éxito en RolService: Rol '" + rolNombre + "' revocado de " + email);
            return true;

        } catch (Exception e) {
            System.err.println("Error de base de datos en RolService al revocar rol: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
