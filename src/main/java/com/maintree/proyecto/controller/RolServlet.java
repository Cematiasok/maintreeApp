package com.maintree.proyecto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maintree.proyecto.dao.RolDAO;
import com.maintree.proyecto.model.Rol;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/roles")
public class RolServlet extends HttpServlet {

    private RolDAO rolDAO;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        rolDAO = new RolDAO();
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Rol> roles = rolDAO.findAll();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(resp.getWriter(), roles);
    }
}
