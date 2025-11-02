package com.maintree.proyecto.model;

import jakarta.persistence.*;

@Entity
@Table(name = "permiso")
public class Permiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nombre", unique = true, nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permiso permiso = (Permiso) o;
        // Si el id es 0, el objeto no ha sido persistido, por lo que no puede ser igual a otro.
        return id != 0 && id == permiso.id;
    }

    @Override
    public int hashCode() {
        // Usamos una implementación simple basada en el ID.
        // Los IDEs pueden generar una más compleja si hay más campos inmutables.
        return Integer.hashCode(id);
    }
}