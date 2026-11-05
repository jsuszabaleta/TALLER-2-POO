package com.clinica;

import java.util.Comparator;

public class ComparadorCitaPorPaciente implements Comparator<Cita> {

    @Override
    public int compare(Cita c1, Cita c2) {
        return c1.getPaciente().getNombre().compareToIgnoreCase(c2.getPaciente().getNombre());
    }
}
