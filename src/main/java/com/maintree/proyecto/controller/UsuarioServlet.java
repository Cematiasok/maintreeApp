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

@WebServlet("/usuarios")
public class UsuarioServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        usuarioDAO = new UsuarioDAO();
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Usuario> usuarios = usuarioDAO.findAll();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(resp.getWriter(), usuarios);
    }
}
