package org.example;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Reportes para la gerencia de la clínica, construidos con la API de Streams.
 */
public class ReporteGerenciaService {

    private final Repositorio<Cita, String> repoCitas;

    public ReporteGerenciaService(Repositorio<Cita, String> repoCitas) {
        this.repoCitas = repoCitas;
    }

    /**
     * Agenda de un médico para un día: citas no canceladas, ordenadas por hora.
     *
     * @throws ReglaClinicaException si faltan datos o el médico no tiene ninguna cita registrada
     */
    public List<Cita> verAgendaDiariaMedico(String idMedico, LocalDate fecha) throws ReglaClinicaException {
        if (idMedico == null || idMedico.isBlank() || fecha == null) {
            throw new ReglaClinicaException("Debe indicar el médico y la fecha para consultar la agenda.");
        }
        boolean medicoConCitas = repoCitas.obtenerTodos().stream()
                .anyMatch(c -> c.getMedico().getId().equals(idMedico));
        if (!medicoConCitas) {
            throw new ReglaClinicaException("El médico con id " + idMedico + " no tiene citas registradas.");
        }
        return repoCitas.obtenerTodos().stream()
                .filter(c -> c.getMedico().getId().equals(idMedico))
                .filter(c -> c.getFechaHora().toLocalDate().equals(fecha))
                .filter(c -> !(c.getEstado() instanceof EstadoCancelada))
                .sorted(Comparator.comparing(Cita::getFechaHora))
                .collect(Collectors.toList());
    }

    /**
     * Cantidad de citas solicitadas por especialidad (groupingBy + counting).
     * Incluye las especialidades sin demanda con valor 0.
     */
    public Map<Especialidad, Long> calcularDemandaPorEspecialidad() {
        Map<Especialidad, Long> conteo = repoCitas.obtenerTodos().stream()
                .collect(Collectors.groupingBy(Cita::getEspecialidad, Collectors.counting()));

        return Arrays.stream(Especialidad.values())
                .collect(Collectors.toMap(
                        Function.identity(),
                        e -> conteo.getOrDefault(e, 0L),
                        (a, b) -> a,
                        () -> new EnumMap<>(Especialidad.class)));
    }

    /**
     * Porcentaje de inasistencia sobre las citas que ya tienen un desenlace de asistencia
     * (ATENDIDA o NO_ASISTIO). Las citas pendientes o canceladas no cuentan.
     */
    public double calcularPorcentajeInasistencia() {
        List<Cita> conDesenlace = repoCitas.obtenerTodos().stream()
                .filter(c -> c.getEstado() instanceof EstadoAtendida || c.getEstado() instanceof EstadoNoAsistio)
                .collect(Collectors.toList());
        if (conDesenlace.isEmpty()) {
            return 0.0;
        }
        long inasistencias = conDesenlace.stream()
                .filter(c -> c.getEstado() instanceof EstadoNoAsistio)
                .count();
        return inasistencias * 100.0 / conDesenlace.size();
    }

    /** Nombres de los pacientes atendidos, unidos con reduce. */
    public String obtenerNombresPacientesAtendidos() {
        return repoCitas.obtenerTodos().stream()
                .filter(c -> c.getEstado() instanceof EstadoAtendida)
                .map(c -> c.getPaciente().getNombre())
                .distinct()
                .sorted()
                .reduce((a, b) -> a + ", " + b)
                .orElse("Ninguno");
    }

    /** Total de citas atendidas usando mapToInt + sum. */
    public int sumarTotalPacientesAtendidosConSum() {
        return repoCitas.obtenerTodos().stream()
                .filter(c -> c.getEstado() instanceof EstadoAtendida)
                .mapToInt(c -> 1)
                .sum();
    }

    /** Edad promedio de los pacientes distintos que tienen al menos una cita (average), con 2 decimales. */
    public double calcularEdadPromedioPacientesCitados() {
        double promedio = repoCitas.obtenerTodos().stream()
                .map(Cita::getPaciente)
                .distinct()
                .mapToInt(Paciente::getEdad)
                .average()
                .orElse(0.0);
        return Math.round(promedio * 100.0) / 100.0;
    }
}
