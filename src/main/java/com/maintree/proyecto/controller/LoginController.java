package com.maintree.proyecto.controller;

import com.maintree.proyecto.model.Usuario;
import com.maintree.proyecto.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody Usuario usuario) {
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
    }
}
