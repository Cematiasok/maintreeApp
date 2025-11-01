package com.maintree.proyecto.dao;

import com.maintree.proyecto.model.Rol;
import com.maintree.proyecto.model.Usuario;
import com.maintree.proyecto.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * UsuarioDAO - Versión Completa y Corregida
 *
 * CORREGIDO:
 * - Se alinea con la tabla 'usuarios' (con 'id', 'nombre', 'apellido', etc.).
 * - 'findByEmail' y 'findByResetToken' ahora cargan los roles del usuario (método findRolesByUsuarioId).
 * - Se añade 'actualizarRolesUsuario(int id, ...)' que funciona con el ID (int) y maneja la transacción.
 * - Se corrige el 'if (rol.getId() == null)' a 'if (rol.getId() == 0)'.
 */
public class UsuarioDAO {

    /**
     * Busca un usuario por su email.
     * Carga también los roles asociados a ese usuario.
     */
    public Usuario findByEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        Usuario usuario = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setId(rs.getInt("id")); // Cargar el ID
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setApellido(rs.getString("apellido"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setEspecialidad(rs.getString("especialidad"));
                    usuario.setPassword(rs.getString("password"));
                    usuario.setActive(rs.getBoolean("isActive"));
                    usuario.setResetToken(rs.getString("reset_token"));

                    java.sql.Timestamp timestamp = rs.getTimestamp("reset_token_expiry");
                    if (timestamp != null) {
                        usuario.setResetTokenExpiry(new Date(timestamp.getTime()));
                    }

                    // Una vez que tenemos el usuario (y su ID), cargamos sus roles
                    usuario.setRoles(findRolesByUsuarioId(usuario.getId(), conn));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuario;
    }

    /**
     * Busca un usuario por su token de reseteo.
     * Carga también los roles asociados.
     */
    public Usuario findByResetToken(String token) {
        String sql = "SELECT * FROM usuarios WHERE reset_token = ?";
        Usuario usuario = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, token);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setId(rs.getInt("id")); // Cargar el ID
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setApellido(rs.getString("apellido"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setEspecialidad(rs.getString("especialidad"));
                    usuario.setPassword(rs.getString("password"));
                    usuario.setActive(rs.getBoolean("isActive"));
                    usuario.setResetToken(rs.getString("reset_token"));

                    java.sql.Timestamp timestamp = rs.getTimestamp("reset_token_expiry");
                    if (timestamp != null) {
                        usuario.setResetTokenExpiry(new Date(timestamp.getTime()));
                    }

                    // Cargamos sus roles usando la misma conexión
                    usuario.setRoles(findRolesByUsuarioId(usuario.getId(), conn));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuario;
    }

    /**
     * Actualiza la información básica del usuario (password, token).
     */
    public void update(Usuario usuario) {
        String sql = "UPDATE usuarios SET password = ?, reset_token = ?, reset_token_expiry = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getPassword());
            pstmt.setString(2, usuario.getResetToken());

            if (usuario.getResetTokenExpiry() != null) {
                pstmt.setTimestamp(3, new java.sql.Timestamp(usuario.getResetTokenExpiry().getTime()));
            } else {
                pstmt.setNull(3, java.sql.Types.TIMESTAMP);
            }

            pstmt.setInt(4, usuario.getId()); // Usamos el ID (int)

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Crea un nuevo usuario en la base de datos.
     * La contraseña ya debe venir hasheada desde el servicio.
     * @param usuario El objeto Usuario con los datos a insertar.
     * @return El ID del usuario recién creado.
     * @throws SQLException Si ocurre un error en la base de datos.
     */
    public int create(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre, apellido, email, password, isActive) VALUES (?, ?, ?, ?, ?)";
        String sql = "INSERT INTO usuarios (nombre, apellido, email, especialidad, password, isActive) VALUES (?, ?, ?, ?, ?, ?)";
        int generatedId = 0;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getApellido());
            pstmt.setString(3, usuario.getEmail());
            pstmt.setString(4, usuario.getPassword()); // La contraseña ya debe estar hasheada
            pstmt.setBoolean(5, usuario.isActive());
            pstmt.setString(4, usuario.getEspecialidad());
            pstmt.setString(5, usuario.getPassword()); // La contraseña ya debe estar hasheada
            pstmt.setBoolean(6, usuario.isActive());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedId = generatedKeys.getInt(1);
                        usuario.setId(generatedId); // Asignamos el nuevo ID al objeto
                    }
                }
            }
        }
        // La excepción se propaga para que el servicio la maneje
        return generatedId;
    }

    // --- Métodos de Roles ---

    /**
     * Método auxiliar para cargar los roles de un usuario.
     * Reutiliza una conexión existente para ser eficiente.
     * Se une con la tabla 'rol' para obtener los nombres.
     */
    private Set<Rol> findRolesByUsuarioId(int usuarioId, Connection conn) throws SQLException {
        Set<Rol> roles = new HashSet<>();
        // Consulta que une usuariorol con rol
        String sql = "SELECT r.id, r.nombre, r.descripcion " +
                "FROM usuariorol ur " +
                "JOIN rol r ON ur.rol_id = r.id " +
                "WHERE ur.usuario_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, usuarioId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Rol rol = new Rol();
                    rol.setId(rs.getInt("id"));
                    rol.setNombre(rs.getString("nombre"));
                    rol.setDescripcion(rs.getString("descripcion"));
                    roles.add(rol);
                }
            }
        }
        // NO cerramos la conexión aquí, se maneja en el método que llama
        return roles;
    }

    /**
     * Actualiza la lista de roles de un usuario en la tabla 'usuariorol'.
     * CORREGIDO: Usa 'usuario_id' (int) en lugar de 'email'.
     * CORREGIDO: Usa 'if (rol.getId() == 0)' para la validación.
     *
     * @param usuarioId El ID (int) del usuario a modificar.
     * @param roles     El conjunto completo de roles que el usuario DEBERÍA tener.
     */
    public void actualizarRolesUsuario(int usuarioId, Set<Rol> roles) throws SQLException {
        String deleteSQL = "DELETE FROM usuariorol WHERE usuario_id = ?";
        String insertSQL = "INSERT INTO usuariorol (usuario_id, rol_id) VALUES (?, ?)";

        Connection conn = null;
        PreparedStatement deletePstmt = null;
        PreparedStatement insertPstmt = null;

        try {
            // 1. Obtenemos una nueva conexión para manejar la transacción
            conn = DatabaseConnection.getConnection();
            // 2. Iniciar transacción (desactivamos el auto-commit)
            conn.setAutoCommit(false);

            // 3. Borrar todos los roles antiguos del usuario
            deletePstmt = conn.prepareStatement(deleteSQL);
            deletePstmt.setInt(1, usuarioId);
            deletePstmt.executeUpdate();

            // 4. Insertar todos los roles nuevos de la lista
            insertPstmt = conn.prepareStatement(insertSQL);

            for (Rol rol : roles) {
                // ¡CORRECCIÓN! Un int primitivo no puede ser null, su valor por defecto es 0.
                if (rol.getId() == 0) {
                    System.err.println("Advertencia: Se intentó guardar un rol sin ID para el usuario " + usuarioId + ". Omitiendo...");
                    continue; // Saltar este rol
                }

                insertPstmt.setInt(1, usuarioId);
                insertPstmt.setInt(2, rol.getId());
                insertPstmt.addBatch(); // Agrupamos las inserciones
            }
            insertPstmt.executeBatch(); // Ejecutamos todas las inserciones juntas

            // 5. Si todo salió bien, confirmamos la transacción
            conn.commit();

        } catch (SQLException e) {
            // 6. Si algo falla, revertimos la transacción
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            throw e; // Relanzamos la excepción para que RolService se entere
        } finally {
            // 7. Cerramos todos los recursos y restauramos el auto-commit
            if (deletePstmt != null) deletePstmt.close();
            if (insertPstmt != null) insertPstmt.close();
            if (conn != null) {
                conn.setAutoCommit(true); // Restaurar modo auto-commit
                conn.close();
            }
        }
    }
}
