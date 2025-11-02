package com.maintree.proyecto.model;

import java.util.HashSet;


import jakarta.persistence.*;

//import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "rol")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nombre", unique = true, nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "rolpermiso",
        joinColumns = @JoinColumn(name = "rol_id"),
        inverseJoinColumns = @JoinColumn(name = "permiso_id")
    )
    private Set<Permiso> permisos = new HashSet<>();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rol rol = (Rol) o;
        return id != 0 && id == rol.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
