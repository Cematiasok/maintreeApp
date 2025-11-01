package com.maintree.proyecto.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.maintree.proyecto.dao.RolDAO;
import com.maintree.proyecto.dao.UsuarioDAO;
import com.maintree.proyecto.model.Usuario;
import com.maintree.proyecto.service.RegisterService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final RegisterService registerService;

    // Constructor por defecto para que el servidor de servlets (Tomcat) pueda instanciarlo.
    // Aquí se crean las dependencias.
    public RegisterServlet() {
        this.registerService = new RegisterService(new UsuarioDAO(), new RolDAO());
    }

    // Constructor para pruebas, donde podemos "inyectar" versiones falsas (mocks) de los servicios.
    public RegisterServlet(RegisterService registerService) {
        this.registerService = registerService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. Leer el cuerpo JSON de la petición
        String body = req.getReader().lines().collect(Collectors.joining());


        // 2. Preparar la respuesta
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        Map<String, Object> responseMap = new HashMap<>();

        try {
            // 3. Parsear el JSON para extraer el rol y crear el usuario
            JsonObject jsonObject = gson.fromJson(body, JsonObject.class);

            // Extraer el nombre del rol como un String. Si no viene, lanza una excepción.
            String rolNombre = jsonObject.get("rol").getAsString();

            // Convertir el resto del JSON a un objeto Usuario
            Usuario newUser = gson.fromJson(jsonObject, Usuario.class);

            boolean success = registerService.registerUser(newUser, rolNombre);
            if (success) {
                responseMap.put("success", true);
                responseMap.put("message", "¡Registro exitoso!");
                resp.setStatus(HttpServletResponse.SC_CREATED); // 201 Creado
            } else {
                responseMap.put("success", false);
                responseMap.put("message", "No se pudo completar el registro.");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Solicitud incorrecta
            }
        } catch (IllegalStateException e) {
            // Captura el error de "email ya existe" o "rol no válido"
            responseMap.put("success", false);
            responseMap.put("message", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_CONFLICT); // 409 Conflicto
        } catch (SQLException e) {
            responseMap.put("success", false);
            responseMap.put("message", "Error interno del servidor.");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Error del servidor
        }

        out.print(gson.toJson(responseMap));
        out.flush();
    }
}