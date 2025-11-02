package com.maintree.proyecto.dao;

import com.maintree.proyecto.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Usuario findByEmail(String email);

    Usuario findByResetToken(String resetToken);

    // Devuelve los usuarios que aún no están activos (pendientes de aprobación)
    java.util.List<Usuario> findByIsActiveFalse();

    // Devuelve usuarios pendientes pero incluyendo aquellos con isActive == NULL
    java.util.List<Usuario> findByIsActiveFalseOrIsActiveIsNull();
}
