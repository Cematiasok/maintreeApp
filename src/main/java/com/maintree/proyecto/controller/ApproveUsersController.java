package com.maintree.proyecto.controller;

import com.maintree.proyecto.service.ApproveUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @PostMapping("/usuarios/{id}/aprobar")
    public ResponseEntity<?> approveUser(@PathVariable int id, @RequestBody java.util.Map<String,String> body) {
        String rol = body != null ? body.get("rol") : null;
        if (rol == null || rol.isEmpty()) {
            return ResponseEntity.badRequest().body("{\"success\":false,\"message\":\"Falta rol\"}");
        }
        boolean ok = approveUsersService.approveUserWithRole(id, rol);
    if (ok) return ResponseEntity.ok().body("{\"success\":true}");
    return ResponseEntity.badRequest().body("{\"success\":false}");
    }

    @DeleteMapping("/usuarios/{id}/rechazar")
    public ResponseEntity<?> rejectUser(@PathVariable int id) {
        boolean ok = approveUsersService.rejectUser(id);
        if (ok) return ResponseEntity.ok().body("{\"success\":true}");
        return ResponseEntity.notFound().build();
    }
}
