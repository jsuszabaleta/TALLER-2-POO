package org.example;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Medico extends Persona {

    private Especialidad especialidad;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    public Medico(String id, String nombre, String documento, Especialidad especialidad,
                  LocalTime horaInicio, LocalTime horaFin) {
        super(id, nombre, documento);
        this.especialidad = especialidad;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public boolean atiendeEnHora(LocalDateTime fechaHora) {
        LocalTime horaCita = fechaHora.toLocalTime();
        return !horaCita.isBefore(horaInicio) && horaCita.isBefore(horaFin);
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    @Override
    public String mostrarPerfil() {
        return "Dr(a). " + getNombre() + " - " + especialidad + " (Horario: " + horaInicio + " a " + horaFin + ")";
    }
}