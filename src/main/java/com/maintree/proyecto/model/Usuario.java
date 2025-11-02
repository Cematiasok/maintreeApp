package com.maintree.proyecto.model;

import java.util.Date;
import java.util.Set;
import java.util.HashSet;

/**
 * Clase Modelo (POJO) que representa la tabla 'usuarios' de la base de datos.
 *
 * CORREGIDO:
 * - Se usa 'int id' para coincidir con 'int(11)'.
 * - Se añade 'nombre', 'apellido', 'direccion', 'isActive'.
 * - Se usa 'Date' de java.util.Date para 'reset_token_expiry'.
 * - Se añade el 'Set<Rol> roles' para la relación.
 */
public class Usuario {

    // --- Campos de la tabla 'usuarios' ---
    private int id;
    private String nombre;
    private String apellido;
    private String email;
    private String especialidad; // Nuevo campo
    private String password;
    private boolean isActive; // tinyint(1) se mapea a boolean
    private String resetToken;
    private Date resetTokenExpiry; // datetime se mapea a java.util.Date

    // --- Relación con Roles ---
    // Esto no es una columna en 'usuarios', sino que se carga
    // desde la tabla 'usuariorol'.
    private Set<Rol> roles;

    // Campo temporal para recibir el nombre del rol desde el JSON del formulario.
    // No corresponde a una columna en la tabla 'usuarios'.
    private String rol;

    // Constructor (opcional, pero buena práctica)
    public Usuario() {
        this.roles = new HashSet<>(); // Inicializar el Set para evitar NullPointerException
        this.isActive = false; // Valor por defecto
    }

    // --- Getters y Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public Date getResetTokenExpiry() {
        return resetTokenExpiry;
    }

    public void setResetTokenExpiry(Date resetTokenExpiry) {
        this.resetTokenExpiry = resetTokenExpiry;
    }

    public Set<Rol> getRoles() {
        return roles;
    }

    public void setRoles(Set<Rol> roles) {
        this.roles = roles;
    }

    // Getter y Setter para el campo temporal 'rol'
    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return id != 0 && id == usuario.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}