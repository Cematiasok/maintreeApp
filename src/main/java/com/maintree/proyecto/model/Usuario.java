package com.maintree.proyecto.model;

import jakarta.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "especialidad")
    private String especialidad;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "isActive")
    private Boolean isActive;

    @Column(name = "reset_token")
    private String resetToken;

    @Column(name = "reset_token_expiry")
    @Temporal(TemporalType.TIMESTAMP)
    private Date resetTokenExpiry;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuariorol",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<Rol> roles = new HashSet<>();

    public Usuario() {
        this.roles = new HashSet<>();
        this.isActive = Boolean.FALSE;  // Explícitamente usar Boolean.FALSE
    }

    public Boolean getIsActive() {
        return isActive != null ? isActive : Boolean.FALSE;  // Nunca retorna null
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive != null ? isActive : Boolean.FALSE;  // Nunca guarda null
    }

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

    // Añadimos un getter con nombre 'getActive' para que Jackson serialice la propiedad
    // como 'active' (el frontend espera 'user.active').
    public Boolean getActive() {
        return getIsActive();
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