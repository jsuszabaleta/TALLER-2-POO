package org.example;

public abstract class Persona implements Comparable<Persona> {

    private String id;
    private String nombre;
    private String documento;

    public Persona(String id, String nombre, String documento) {
        this.id = id;
        this.nombre = nombre;
        this.documento = documento;
    }

    public abstract String mostrarPerfil();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    @Override
    public int compareTo(Persona otra) {
        return this.nombre.compareToIgnoreCase(otra.nombre);
    }

    @Override
    public String toString() {
        return mostrarPerfil();
    }
}