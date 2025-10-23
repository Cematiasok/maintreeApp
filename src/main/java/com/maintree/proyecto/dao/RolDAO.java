package com.maintree.proyecto.dao;

import com.maintree.proyecto.model.Permiso;
import com.maintree.proyecto.model.Rol;
import com.maintree.proyecto.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * RolDAO - Versión Corregida
 * - Consulta la tabla 'rol' (singular).
 * - CORREGIDO: Usa rs.getInt("id") en lugar de rs.getLong("id")
 * para coincidir con los setters que esperan un 'int'.
 */
public class RolDAO {

    /**
     * Busca un Rol por su nombre.
     * CORREGIDO: Consulta la tabla 'rol'
     */
    public Rol findByNombre(String nombre) {
        String sql = "SELECT * FROM rol WHERE nombre = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Rol rol = new Rol();
                    // --- CORRECCIÓN AQUÍ ---
                    rol.setId(rs.getInt("id")); // Usar getInt() en lugar de getLong()
                    rol.setNombre(rs.getString("nombre"));
                    rol.setDescripcion(rs.getString("descripcion"));

                    // Cargar los permisos asociados a este rol
                    rol.setPermisos(findPermissionsByRolId(rol.getId(), conn));

                    return rol;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Método privado para cargar los permisos de un rol desde 'permiso' y 'rolpermiso'
     */
    private Set<Permiso> findPermissionsByRolId(int rolId, Connection conn) throws SQLException {
        Set<Permiso> permisos = new HashSet<>();

        // CORREGIDO: Consulta las tablas 'permiso' y 'rolpermiso'
        String sql = "SELECT p.* FROM permiso p " +
                "JOIN rolpermiso rp ON p.id = rp.permiso_id " +
                "WHERE rp.rol_id = ?";

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, rolId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Permiso permiso = new Permiso();
                // --- CORRECCIÓN AQUÍ ---
                permiso.setId(rs.getInt("id"));
                permiso.setNombre(rs.getString("nombre"));
                permiso.setDescripcion(rs.getString("descripcion"));
                permisos.add(permiso);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // Relanzar para que el método principal la maneje
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
        return permisos;
    }

    // (Aquí irían otros métodos como create, update, delete para roles
    //  si tuvieras un ABM de Roles)
}

/*
package com.maintree.proyecto.dao;


import com.maintree.proyecto.model.Rol;
import com.maintree.proyecto.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
// NOTA: Asumimos que la tabla de roles se llama 'roles'
// y tiene columnas 'id', 'nombre' y 'descripcion'.

public class RolDAO {

    /**
     * Busca un Rol por su nombre (ej. "ROL_ADMIN").
     * Este método es el que utiliza RolService.

    public Rol findByNombre(String nombre) {
        // Asumimos que también queremos cargar los permisos de este rol
        // (Aunque el RolService actual no los usa, es buena práctica)
        String sql = "SELECT * FROM roles WHERE nombre = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Rol rol = new Rol();
                    rol.setId(rs.getLong("id"));
                    rol.setNombre(rs.getString("nombre"));
                    rol.setDescripcion(rs.getString("descripcion"));

                    // Aquí es donde también cargarías los permisos de este rol
                    // rol.setPermisos(findPermisosByRolId(rol.getId(), conn));

                    return rol;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Aquí podrías añadir otros métodos que necesites, como:
    // public Set<Rol> findAll() { ... }
    // public Rol findById(Long id) { ... }
    // public Set<Permiso> findPermisosByRolId(Long rolId, Connection conn) { ... }

}
*/