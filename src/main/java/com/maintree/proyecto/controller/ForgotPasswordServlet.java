package com.maintree.proyecto.controller;

import com.maintree.proyecto.service.PasswordRecoveryService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {

    private PasswordRecoveryService recoveryService = new PasswordRecoveryService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Simplemente muestra la página para ingresar el email
        req.getRequestDispatcher("/recuperar.html").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        
        // Obtenemos la URL completa de la solicitud para construir el enlace de reseteo
        String requestUrl = req.getRequestURL().toString();

        try {
            recoveryService.initiatePasswordReset(email, requestUrl);
            // Proceso iniciado. Por seguridad, no confirmamos si el email existía o no.
            // Redirigimos a la misma página con un mensaje de éxito.
            resp.sendRedirect("recuperar.html?status=sent");
        } catch (Exception e) {
            // Si algo falla (ej. el servidor de correo), redirigimos con un error.
            e.printStackTrace();
            resp.sendRedirect("recuperar.html?status=error");
        }
    }
}
