package com.maintree.proyecto.dao;

import com.maintree.proyecto.model.Usuario;
import com.maintree.proyecto.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public Usuario findByEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setEmail(rs.getString("email"));
                    usuario.setPassword(rs.getString("password")); // Importante: obtener el hash de la BD
                    return usuario;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
