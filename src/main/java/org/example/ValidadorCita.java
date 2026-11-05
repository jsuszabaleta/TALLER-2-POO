package com.clinica;

@FunctionalInterface
public interface ValidadorCita {

    boolean esValida(Cita cita);
}
