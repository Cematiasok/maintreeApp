package com.maintree.proyecto.dao;

import com.maintree.proyecto.model.Usuario;
import com.maintree.proyecto.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

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
                    usuario.setResetToken(rs.getString("reset_token"));
                    java.sql.Timestamp timestamp = rs.getTimestamp("reset_token_expiry");
                    if (timestamp != null) {
                        usuario.setResetTokenExpiry(new Date(timestamp.getTime()));
                    }
                    return usuario;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Usuario findByResetToken(String token) {
        String sql = "SELECT * FROM usuarios WHERE reset_token = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, token);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setEmail(rs.getString("email"));
                    usuario.setPassword(rs.getString("password"));
                    usuario.setResetToken(rs.getString("reset_token"));
                    java.sql.Timestamp timestamp = rs.getTimestamp("reset_token_expiry");
                    if (timestamp != null) {
                        usuario.setResetTokenExpiry(new Date(timestamp.getTime()));
                    }
                    return usuario;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void update(Usuario usuario) {
        String sql = "UPDATE usuarios SET password = ?, reset_token = ?, reset_token_expiry = ? WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getPassword());
            pstmt.setString(2, usuario.getResetToken());

            if (usuario.getResetTokenExpiry() != null) {
                pstmt.setTimestamp(3, new java.sql.Timestamp(usuario.getResetTokenExpiry().getTime()));
            } else {
                pstmt.setNull(3, java.sql.Types.TIMESTAMP);
            }

            pstmt.setString(4, usuario.getEmail());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
