package org.example;

public class Resultado<T> {

    private final T dato;
    private final String mensaje;

    public Resultado(T dato, String mensaje) {
        this.dato = dato;
        this.mensaje = mensaje;
    }

    public T getDato() {
        return dato;
    }

    public String getMensaje() {
        return mensaje;
    }
}
