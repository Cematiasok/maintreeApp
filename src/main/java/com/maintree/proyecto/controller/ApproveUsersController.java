package com.maintree.proyecto.controller;

import com.maintree.proyecto.service.ApproveUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class ApproveUsersController {

    @Autowired
    private ApproveUsersService approveUsersService;

    @PostMapping("/confirmar-roles-lote")
    public ResponseEntity<?> approveUsers(@RequestBody List<Integer> userIds) {
        try {
            approveUsersService.approveUsers(userIds);
            return ResponseEntity.ok().body("{\"success\": true}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"success\": false, \"message\": \"Error al aprobar usuarios.\"}");
        }
    }

    @GetMapping("/usuarios-pendientes")
    public List<com.maintree.proyecto.model.Usuario> getPendingUsers() {
        return approveUsersService.getPendingUsers();
    }
}
