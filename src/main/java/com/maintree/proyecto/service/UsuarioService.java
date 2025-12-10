package com.maintree.proyecto.service;

import com.maintree.proyecto.dao.UsuarioRepository;
import com.maintree.proyecto.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public java.util.Optional<Usuario> findById(int id) {
        return usuarioRepository.findById(id);
    }

    public Usuario save(Usuario u) {
        if (u != null) {
            return usuarioRepository.save(u);
        }
        return null;
    }

    public void deleteById(int id) {
        usuarioRepository.deleteById(id);
    }
}
