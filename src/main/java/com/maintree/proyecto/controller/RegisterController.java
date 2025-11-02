package com.maintree.proyecto.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.maintree.proyecto.model.Usuario;
import com.maintree.proyecto.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")  // Permitir CORS
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    private final Gson gson = new Gson();

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody String body) {
        Map<String, Object> responseMap = new HashMap<>();
        try {
            JsonObject jsonObject = gson.fromJson(body, JsonObject.class);

            if (jsonObject.get("rol") == null || jsonObject.get("rol").getAsString().isEmpty()) {
                responseMap.put("success", false);
                responseMap.put("message", "El campo 'rol' es obligatorio.");
                return new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
            }
            String rolNombre = jsonObject.get("rol").getAsString();

            Usuario newUser = gson.fromJson(jsonObject, Usuario.class);

            boolean success = registerService.registerUser(newUser, rolNombre);
            if (success) {
                responseMap.put("success", true);
                responseMap.put("message", "¡Registro exitoso!");
                return new ResponseEntity<>(responseMap, HttpStatus.CREATED);
            } else {
                responseMap.put("success", false);
                responseMap.put("message", "No se pudo completar el registro.");
                return new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
            }
        } catch (IllegalStateException e) {
            responseMap.put("success", false);
            responseMap.put("message", e.getMessage());
            return new ResponseEntity<>(responseMap, HttpStatus.CONFLICT);
        } catch (SQLException e) {
            responseMap.put("success", false);
            responseMap.put("message", "Error interno del servidor.");
            return new ResponseEntity<>(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
