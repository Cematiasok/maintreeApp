package com.tuempresa.proyecto.model;

public class LoginService {

    /**
     * Valida las credenciales del usuario.
     * TODO: Reemplazar esto con una consulta a la base de datos.
     */
    public boolean validarCredenciales(String email, String password) {
        // Simulación: Aceptamos un único usuario válido
        if ("admin@correo.com".equals(email) && "12345".equals(password)) {
            return true;
        }
        return false;
    }
}