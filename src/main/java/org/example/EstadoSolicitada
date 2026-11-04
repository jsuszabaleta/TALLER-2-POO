package org.example;

import java.time.LocalDateTime;

public class EstadoSolicitada implements EstadoCita {

    @Override
    public void confirmar(Cita cita) {
        cita.setEstado(new EstadoConfirmada());
    }

    @Override
    public void atender(Cita cita, AtencionMedica atencion) throws ClinicaException {
        throw new ReglaClinicaException("La cita debe confirmarse antes de poder ser atendida.");
    }

    @Override
    public void cancelar(Cita cita, LocalDateTime momentoActual) throws ClinicaException {
        if (momentoActual.plusHours(24).isAfter(cita.getFechaHora())) {
            throw new CancelacionTardiaException("No se aceptan cancelaciones a última hora (menos de 24h).");
        }
        cita.setEstado(new EstadoCancelada());
    }

    @Override
    public void cambiarFecha(Cita cita, LocalDateTime nuevaFecha, LocalDateTime momentoActual) throws ClinicaException {
        if (momentoActual.plusHours(24).isAfter(cita.getFechaHora())) {
            throw new CancelacionTardiaException("No se aceptan cambios de cita a última hora (menos de 24h).");
        }
        cita.setFechaHora(nuevaFecha);
    }

    @Override
    public void marcarNoAsistio(Cita cita) throws ClinicaException {
        throw new ReglaClinicaException("Solo se puede marcar inasistencia si la cita estaba confirmada.");
    }

    @Override
    public String getNombre() {
        return "SOLICITADA";
    }

    @Override
    public boolean esActiva() {
        return true;
    }
}
