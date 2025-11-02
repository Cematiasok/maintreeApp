package com.maintree.proyecto.controller;

import com.maintree.proyecto.model.Rol;
import com.maintree.proyecto.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RolController {

    @Autowired
    private RolService rolService;

    @GetMapping("/roles")
    public List<Rol> getRoles() {
        return rolService.findAll();
    }
}
