package com.maintree.proyecto.controller;

import com.maintree.proyecto.model.Usuario;
import com.maintree.proyecto.service.LoginService;
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
                    responseMap.put("success", true);
                    responseMap.put("message", "Inicio de sesión correcto");
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
