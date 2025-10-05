package com.maintree.proyecto.controller;

import com.google.gson.Gson;
import com.maintree.proyecto.model.Usuario;
import com.maintree.proyecto.service.PasswordRecoveryService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {

    private PasswordRecoveryService recoveryService = new PasswordRecoveryService();
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. Leer el cuerpo JSON de la petición
        String body = req.getReader().lines().collect(Collectors.joining());
        Usuario usuario = gson.fromJson(body, Usuario.class);
        String email = usuario.getEmail();

        // 2. Obtenemos la URL de la solicitud para construir el enlace de reseteo
        String requestUrl = req.getRequestURL().toString();

        // 3. Preparar la respuesta JSON
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        String jsonResponse;

        try {
            recoveryService.initiatePasswordReset(email, requestUrl);
            // Proceso iniciado. Por seguridad, siempre devolvemos un mensaje genérico de éxito.
            jsonResponse = "{\"success\": true, \"message\": \"Si tu correo está registrado, recibirás un enlace de recuperación.\"}";
        } catch (Exception e) {
            // Si algo falla (ej. el servidor de correo), devolvemos un error.
            e.printStackTrace();
            jsonResponse = "{\"success\": false, \"message\": \"Hubo un error al procesar tu solicitud. Inténtalo de nuevo más tarde.\"}";
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        
        // 4. Enviar la respuesta
        out.print(jsonResponse);
        out.flush();
    }
}
