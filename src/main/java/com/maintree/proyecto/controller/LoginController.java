package com.maintree.proyecto.controller;

import com.maintree.proyecto.model.Usuario;
import com.maintree.proyecto.service.LoginService;
import com.maintree.proyecto.dao.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;  // Incluye CrossOrigin

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")  // Permite todas las origenes por ahora
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario usuario) {
        try {
            System.out.println("Recibida petición de login para email: " + usuario.getEmail());
            
            if (usuario.getEmail() == null || usuario.getPassword() == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Email y password son requeridos");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            try {
                boolean esValido = loginService.validarCredenciales(usuario.getEmail(), usuario.getPassword());
                Map<String, Object> responseMap = new HashMap<>();
                if (esValido) {
                    // Recuperar usuario para incluir roles en la respuesta
                    Usuario u = usuarioRepository.findByEmail(usuario.getEmail());
                    responseMap.put("success", true);
                    responseMap.put("message", "Inicio de sesión correcto");
                    if (u != null && u.getRoles() != null) {
                        java.util.List<String> roles = u.getRoles().stream().map(r -> r.getNombre()).toList();
                        responseMap.put("roles", roles);
                        boolean isAdmin = roles.stream().anyMatch(name -> name != null && name.toUpperCase().contains("ADMIN"));
                        responseMap.put("isAdmin", isAdmin);
                    } else {
                        responseMap.put("roles", java.util.Collections.emptyList());
                        responseMap.put("isAdmin", false);
                    }
                    return ResponseEntity.ok(responseMap);
                } else {
                    responseMap.put("success", false);
                    responseMap.put("message", "Correo o contraseña incorrectos");
                    return ResponseEntity.status(401).body(responseMap);
                }
            } catch (Exception e) {
                System.err.println("Error en validación de credenciales: " + e.getMessage());
                e.printStackTrace();
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Error al validar credenciales: " + e.getMessage());
                return ResponseEntity.internalServerError().body(errorResponse);
            }
        } catch (Exception e) {
            System.err.println("Error general en login: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error interno del servidor: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}
