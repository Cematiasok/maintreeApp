package com.maintree.proyecto.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHashingManual {

    public static void main(String[] args) {
        // Escribe aquí la contraseña que quieres hashear
        String passwordToHash = "1234"; // Cambia "12345" por la contraseña que necesites

        // Generar el hash
        String hashedPassword = BCrypt.hashpw(passwordToHash, BCrypt.gensalt());

        // Imprimir el hash para que puedas copiarlo
        System.out.println("Contraseña en texto plano: " + passwordToHash);
        System.out.println("Hash generado (cópialo y pégalo en la base de datos):");
        System.out.println(hashedPassword);
    }
}