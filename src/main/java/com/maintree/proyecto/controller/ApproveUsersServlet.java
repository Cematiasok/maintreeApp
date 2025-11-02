package com.maintree.proyecto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maintree.proyecto.dao.UsuarioDAO;
import com.maintree.proyecto.model.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/confirmar-roles-lote")
public class ApproveUsersServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        usuarioDAO = new UsuarioDAO();
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Integer> userIds = objectMapper.readValue(req.getReader(), List.class);

        try {
            for (Integer userId : userIds) {
                Usuario usuario = usuarioDAO.findById(userId);
                if (usuario != null) {
                    usuario.setActive(true);
                    usuarioDAO.update(usuario);
                }
            }
            resp.setContentType("application/json");
            resp.getWriter().write("{\"success\": true}");
        } catch (Exception e) {
            resp.setContentType("application/json");
            resp.getWriter().write("{\"success\": false, \"message\": \"Error al aprobar usuarios.\"}");
        }
    }
}
