package com.maintree.proyecto.controller;

import com.google.gson.Gson;
import com.maintree.proyecto.model.Usuario;
import com.maintree.proyecto.service.LoginService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

// Esta anotación mapea esta clase a la URL "/login"
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private Gson gson = new Gson();
    private LoginService loginService = new LoginService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. Leer el cuerpo de la petición (que viene en formato JSON)
        String body = req.getReader().lines().collect(Collectors.joining());

        // 2. Convertir el JSON a un objeto Java (usando la clase Usuario como plantilla)
        Usuario usuario = gson.fromJson(body, Usuario.class);

        // 3. Llamar a nuestra lógica de negocio (el Modelo) para validar
        boolean esValido = loginService.validarCredenciales(usuario.getEmail(), usuario.getPassword());

        // 4. Preparar la respuesta JSON
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        PrintWriter out = resp.getWriter();
        Map<String, Object> responseMap = new HashMap<>();

        if (esValido) {
            // Si es válido, creamos un JSON de éxito
            responseMap.put("success", true);
            responseMap.put("message", "Inicio de sesión correcto");
        } else {
            // Si no es válido, creamos un JSON de error
            responseMap.put("success", false);
            responseMap.put("message", "Correo o contraseña incorrectos");
        }

        // 5. Enviar la respuesta
        out.print(gson.toJson(responseMap));
        out.flush();
    }
}