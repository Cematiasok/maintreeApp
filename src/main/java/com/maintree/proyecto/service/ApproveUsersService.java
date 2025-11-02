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

    public void approveUsers(List<Integer> userIds) {
        for (Integer userId : userIds) {
            Usuario usuario = usuarioRepository.findById(userId).orElse(null);
            if (usuario != null) {
                usuario.setActive(true);
                usuarioRepository.save(usuario);
            }
        }
    }
}
