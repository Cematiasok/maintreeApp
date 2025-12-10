package com.maintree.proyecto.service;

import com.maintree.proyecto.dao.UsuarioRepository;
import com.maintree.proyecto.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApproveUsersService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private com.maintree.proyecto.dao.RolRepository rolRepository;

    public void approveUsers(List<Integer> userIds) {
        if (userIds == null) {
            return;
        }
        for (Integer userId : userIds) {
            if (userId != null) {
                Usuario usuario = usuarioRepository.findById(userId).orElse(null);
                if (usuario != null) {
                    usuario.setActive(true);
                    usuarioRepository.save(usuario);
                }
            }
        }
    }

    // Devuelve la lista de usuarios pendientes de aprobación (isActive == false)
    public List<Usuario> getPendingUsers() {
        // Intentamos incluir también aquellos registros donde isActive es NULL
        try {
            return usuarioRepository.findByIsActiveFalseOrIsActiveIsNull();
        } catch (Exception ex) {
            // Fallback: si por alguna razón no existe el método, usamos la versión simple
            return usuarioRepository.findByIsActiveFalse();
        }
    }

    // Aprobar un usuario y asignarle un rol por nombre
    public boolean approveUserWithRole(int userId, String rolNombre) {
        if (rolNombre == null || rolNombre.isEmpty()) {
            return false;
        }
        Usuario usuario = usuarioRepository.findById(userId).orElse(null);
        if (usuario == null) return false;
        com.maintree.proyecto.model.Rol rol = rolRepository.findByNombre(rolNombre);
        if (rol == null) return false;
        java.util.Set<com.maintree.proyecto.model.Rol> roles = usuario.getRoles();
        if (roles != null) {
            roles.add(rol);
            usuario.setRoles(roles);
        }
        usuario.setActive(true);
        usuarioRepository.save(usuario);
        return true;
    }

    // Rechazar: eliminar usuario (se puede cambiar a otro comportamiento como marcar "rejected")
    public boolean rejectUser(int userId) {
        if (usuarioRepository.existsById(userId)) {
            usuarioRepository.deleteById(userId);
            return true;
        }
        return false;
    }
}
