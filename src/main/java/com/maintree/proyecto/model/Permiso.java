package com.maintree.proyecto.model;

public class Permiso {

    private int id;
    private String nombre; // Ej: "MANAGE_USERS", "READ_REPORTS", "POST_COMMENTS"
    private String descripcion;

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}