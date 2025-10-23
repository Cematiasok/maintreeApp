package com.maintree.proyecto.model;

import java.util.HashSet;
import java.util.Set;

public class Rol {
    private int id; // Cambiado de Long a int
    private String nombre; // Ej: "ADMIN", "USER", "SUPERVISOR"
    private String descripcion; // Añadir este campo
    private Set<Permiso> permisos;

    public Rol() {
        this.permisos = new HashSet<>(); // Inicializar para evitar NullPointerException
    }
    // Getters y Setters
    public int getId() { // Cambiado de Long a int
        return id;
    }
    public void setId(int id) { this.id = id; } // Cambiado de Long a int
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; } // Añadir getter
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; } // Añadir setter

    public Set<Permiso> getPermisos() {
        return permisos;
    }

    public void setPermisos(Set<Permiso> permisos) { this.permisos = permisos; }
}
