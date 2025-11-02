package com.maintree.proyecto.controller;

import com.maintree.proyecto.model.Usuario;
import com.maintree.proyecto.service.UsuarioService;
import com.maintree.proyecto.dao.RolRepository;
import com.maintree.proyecto.model.Rol;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RolRepository rolRepository;

    @GetMapping("/usuarios")
    public List<Usuario> getUsuarios(@RequestParam(required = false) String rol,
                                     @RequestParam(required = false) String especialidad,
                                     @RequestParam(required = false) Boolean active) {
        List<Usuario> users = usuarioService.findAll();
        // Filtrar si se proporcionan parámetros
        if (rol != null && !rol.isEmpty()) {
            users = users.stream().filter(u -> u.getRoles().stream().anyMatch(r -> r.getNombre().equalsIgnoreCase(rol))).toList();
        }
        if (especialidad != null && !especialidad.isEmpty()) {
            users = users.stream().filter(u -> u.getEspecialidad() != null && u.getEspecialidad().equalsIgnoreCase(especialidad)).toList();
        }
        if (active != null) {
            users = users.stream().filter(u -> u.getIsActive().equals(active)).toList();
        }
        return users;
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable int id) {
        return usuarioService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<?> updateUsuario(@PathVariable int id, @RequestBody Usuario update) {
        return usuarioService.findById(id).map(existing -> {
            // actualizar campos permitidos
            existing.setNombre(update.getNombre() != null ? update.getNombre() : existing.getNombre());
            existing.setApellido(update.getApellido() != null ? update.getApellido() : existing.getApellido());
            existing.setEspecialidad(update.getEspecialidad() != null ? update.getEspecialidad() : existing.getEspecialidad());
            // actualizar password si viene
            if (update.getPassword() != null && !update.getPassword().isEmpty()) {
                existing.setPassword(update.getPassword());
            }
            // actualizar isActive si viene
            if (update.getIsActive() != null) existing.setIsActive(update.getIsActive());
            // actualizar roles por nombre si vienen
            if (update.getRoles() != null && !update.getRoles().isEmpty()) {
                java.util.Set<Rol> newRoles = new java.util.HashSet<>();
                for (Rol r : update.getRoles()) {
                    Rol found = rolRepository.findByNombre(r.getNombre());
                    if (found != null) newRoles.add(found);
                }
                if (!newRoles.isEmpty()) existing.setRoles(newRoles);
            }
            usuarioService.save(existing);
            return ResponseEntity.ok().body(existing);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<?> deleteUsuario(@PathVariable int id) {
        if (usuarioService.findById(id).isPresent()) {
            // Baja lógica: marcar isActive = false
            Usuario u = usuarioService.findById(id).get();
            u.setIsActive(Boolean.FALSE);
            usuarioService.save(u);
            return ResponseEntity.ok().body("{\"success\":true}");
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/especialidades")
    public java.util.List<String> getEspecialidades() {
        return usuarioService.findAll().stream()
                .map(Usuario::getEspecialidad)
                .filter(java.util.Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList();
    }
}
