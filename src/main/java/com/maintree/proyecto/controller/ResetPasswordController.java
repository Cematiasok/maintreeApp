package com.maintree.proyecto.controller;

import com.maintree.proyecto.service.PasswordRecoveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ResetPasswordController {

    @Autowired
    private PasswordRecoveryService recoveryService;

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestBody Map<String, String> payload) {
        String newPassword = payload.get("newPassword");
        String confirmPassword = payload.get("confirmPassword");

        if (newPassword == null || newPassword.isEmpty() || !newPassword.equals(confirmPassword)) {
            return new ResponseEntity<>("Las contraseñas no coinciden o están vacías.", HttpStatus.BAD_REQUEST);
        }

        if (newPassword.length() < 8) {
            return new ResponseEntity<>("La contraseña debe tener al menos 8 caracteres.", HttpStatus.BAD_REQUEST);
        }

        boolean success = recoveryService.finalizePasswordReset(token, newPassword);

        if (success) {
            return ResponseEntity.ok("Contraseña actualizada correctamente.");
        } else {
            return new ResponseEntity<>("El enlace de recuperación es inválido o ha expirado. Por favor, solicita uno nuevo.", HttpStatus.BAD_REQUEST);
        }
    }
}
