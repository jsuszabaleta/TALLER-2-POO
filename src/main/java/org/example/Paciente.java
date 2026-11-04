package org.example;

public class Paciente extends Persona {

    private String telefono;
    private int edad;

    public Paciente(String id, String nombre, String documento, String telefono, int edad) {
        super(id, nombre, documento);
        this.telefono = telefono;
        this.edad = edad;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    @Override
    public String mostrarPerfil() {
        return "Paciente: " + getNombre() + " (Doc: " + getDocumento() + ", Edad: " + edad + ")";
    }
}
