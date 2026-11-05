package com.clinica;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Servicio principal de gestión de la agenda de la clínica.
 * <p>
 * Une los repositorios genéricos, el Patrón State de {@link Cita} y las reglas de negocio:
 * <ul>
 *     <li>El médico solo atiende dentro de su horario.</li>
 *     <li>Un médico no puede tener dos citas activas a la misma hora.</li>
 *     <li>Un paciente no puede tener dos citas activas a la misma hora.</li>
 *     <li>Un paciente solo puede tener una cita activa por especialidad.</li>
 *     <li>Cancelaciones y cambios de fecha con menos de 24h se rechazan (lo valida cada estado).</li>
 * </ul>
 */
public class AgendaService {

    private static final Logger LOGGER = Logger.getLogger(AgendaService.class.getName());
    private static final String PREFIJO_CITA = "CITA-";

    private final Repositorio<Paciente, String> repoPacientes;
    private final Repositorio<Medico, String> repoMedicos;
    private final Repositorio<Cita, String> repoCitas;
    private int contadorCitas;

    public AgendaService(Repositorio<Paciente, String> repoPacientes,
                         Repositorio<Medico, String> repoMedicos,
                         Repositorio<Cita, String> repoCitas) {
        this.repoPacientes = repoPacientes;
        this.repoMedicos = repoMedicos;
        this.repoCitas = repoCitas;
        this.contadorCitas = repoCitas.obtenerTodos().size();
    }

    // ------------------------------------------------------------------
    // Registro de personas
    // ------------------------------------------------------------------

    public void registrarMedico(Medico medico) {
        if (medico == null || medico.getId() == null) {
            throw new IllegalArgumentException("El médico y su id son obligatorios.");
        }
        repoMedicos.guardar(medico.getId(), medico);
        LOGGER.fine("Médico registrado: " + medico.mostrarPerfil());
    }

    public void registrarPaciente(Paciente paciente) {
        if (paciente == null || paciente.getId() == null) {
            throw new IllegalArgumentException("El paciente y su id son obligatorios.");
        }
        repoPacientes.guardar(paciente.getId(), paciente);
        LOGGER.fine("Paciente registrado: " + paciente.mostrarPerfil());
    }

    // ------------------------------------------------------------------
    // Flujo de citas (delegado al Patrón State)
    // ------------------------------------------------------------------

    /**
     * Crea una cita en estado SOLICITADA después de validar todas las reglas de negocio.
     */
    public Cita solicitarCita(String idPaciente, String idMedico, LocalDateTime fechaHora) throws ClinicaException {
        if (fechaHora == null) {
            throw new ReglaClinicaException("La fecha y hora de la cita son obligatorias.");
        }
        Paciente paciente = buscarPaciente(idPaciente);
        Medico medico = buscarMedico(idMedico);

        Cita nueva = new Cita(PREFIJO_CITA + (contadorCitas + 1), paciente, medico, fechaHora);
        validarDisponibilidad(nueva, null);

        contadorCitas++;
        repoCitas.guardar(nueva.getId(), nueva);
        LOGGER.fine("Cita solicitada: " + nueva);
        return nueva;
    }

    public void confirmarCita(String idCita) throws ClinicaException {
        Cita cita = buscarCita(idCita);
        cita.confirmar();
        LOGGER.fine("Cita confirmada: " + cita.getId());
    }

    public void atenderCita(String idCita, AtencionMedica atencion) throws ClinicaException {
        Cita cita = buscarCita(idCita);
        cita.atender(atencion);
        LOGGER.fine("Cita atendida: " + cita.getId());
    }

    public void marcarNoAsistio(String idCita) throws ClinicaException {
        Cita cita = buscarCita(idCita);
        cita.marcarNoAsistio();
        LOGGER.fine("Inasistencia registrada: " + cita.getId());
    }

    public void cancelarCita(String idCita, LocalDateTime momentoActual) throws ClinicaException {
        Cita cita = buscarCita(idCita);
        cita.cancelar(momentoActual);
        LOGGER.fine("Cita cancelada: " + cita.getId());
    }

    /**
     * Cambia la fecha de una cita. Si la cita sigue activa se verifica que el nuevo horario esté libre;
     * la regla de las 24h y las restricciones por estado las aplica el Patrón State.
     */
    public void cambiarFechaCita(String idCita, LocalDateTime nuevaFecha, LocalDateTime momentoActual)
            throws ClinicaException {
        if (nuevaFecha == null) {
            throw new ReglaClinicaException("La nueva fecha de la cita es obligatoria.");
        }
        Cita cita = buscarCita(idCita);
        if (cita.esActiva()) {
            Cita propuesta = new Cita(cita.getId(), cita.getPaciente(), cita.getMedico(), nuevaFecha);
            validarDisponibilidad(propuesta, cita.getId());
        }
        cita.cambiarFecha(nuevaFecha, momentoActual);
        LOGGER.fine("Cita reprogramada: " + cita);
    }

    // ------------------------------------------------------------------
    // Reglas de negocio (interfaz funcional ValidadorCita + lambdas)
    // ------------------------------------------------------------------

    /**
     * Verifica la cita candidata contra las citas activas existentes.
     *
     * @param candidata  cita a validar
     * @param idExcluido id de una cita que no debe compararse consigo misma (al reprogramar), o null
     */
    private void validarDisponibilidad(Cita candidata, String idExcluido) throws ClinicaException {
        List<Cita> otrasActivas = repoCitas.obtenerTodos().stream()
                .filter(Cita::esActiva)
                .filter(c -> !c.getId().equals(idExcluido))
                .toList();

        ValidadorCita dentroDeHorario = c -> c.getMedico().atiendeEnHora(c.getFechaHora());

        ValidadorCita medicoLibre = c -> otrasActivas.stream()
                .noneMatch(o -> o.getMedico().getId().equals(c.getMedico().getId())
                        && o.getFechaHora().equals(c.getFechaHora()));

        ValidadorCita pacienteLibre = c -> otrasActivas.stream()
                .noneMatch(o -> o.getPaciente().getId().equals(c.getPaciente().getId())
                        && o.getFechaHora().equals(c.getFechaHora()));

        ValidadorCita unaPorEspecialidad = c -> otrasActivas.stream()
                .noneMatch(o -> o.getPaciente().getId().equals(c.getPaciente().getId())
                        && o.getEspecialidad() == c.getEspecialidad());

        Medico medico = candidata.getMedico();

        if (!dentroDeHorario.esValida(candidata)) {
            throw new ReglaClinicaException("El Dr(a). " + medico.getNombre() + " solo atiende de "
                    + medico.getHoraInicio() + " a " + medico.getHoraFin() + ".");
        }
        if (!medicoLibre.esValida(candidata)) {
            throw new HorarioOcupadoException("El Dr(a). " + medico.getNombre()
                    + " ya tiene una cita asignada el " + candidata.getFechaHora() + ".");
        }
        if (!pacienteLibre.esValida(candidata)) {
            throw new HorarioOcupadoException("El paciente " + candidata.getPaciente().getNombre()
                    + " ya tiene otra cita el " + candidata.getFechaHora() + ".");
        }
        if (!unaPorEspecialidad.esValida(candidata)) {
            throw new ReglaClinicaException("El paciente " + candidata.getPaciente().getNombre()
                    + " ya tiene una cita activa en " + candidata.getEspecialidad()
                    + ". Solo se permite una por especialidad.");
        }
    }

    // ------------------------------------------------------------------
    // Búsquedas
    // ------------------------------------------------------------------

    public Cita buscarCita(String idCita) throws ReglaClinicaException {
        return repoCitas.buscarPorId(idCita)
                .orElseThrow(() -> new ReglaClinicaException("No existe la cita con id " + idCita + "."));
    }

    public Paciente buscarPaciente(String idPaciente) throws ReglaClinicaException {
        return repoPacientes.buscarPorId(idPaciente)
                .orElseThrow(() -> new ReglaClinicaException("No existe el paciente con id " + idPaciente + "."));
    }

    public Medico buscarMedico(String idMedico) throws ReglaClinicaException {
        return repoMedicos.buscarPorId(idMedico)
                .orElseThrow(() -> new ReglaClinicaException("No existe el médico con id " + idMedico + "."));
    }

    public List<Paciente> obtenerPacientes() {
        List<Paciente> pacientes = new ArrayList<>(repoPacientes.obtenerTodos());
        Collections.sort(pacientes);
        return pacientes;
    }

    public List<Medico> obtenerMedicos() {
        List<Medico> medicos = new ArrayList<>(repoMedicos.obtenerTodos());
        Collections.sort(medicos);
        return medicos;
    }

    // ------------------------------------------------------------------
    // Ordenamientos (Comparable + 3 tipos de Comparator)
    // ------------------------------------------------------------------

    /** Orden natural definido por {@link Cita#compareTo(Cita)}: fecha y hora. */
    public List<Cita> obtenerCitasOrdenNatural() {
        List<Cita> citas = new ArrayList<>(repoCitas.obtenerTodos());
        Collections.sort(citas);
        return citas;
    }

    /** Comparator implementado como clase: nombre del paciente. */
    public List<Cita> obtenerCitasPorPaciente() {
        List<Cita> citas = new ArrayList<>(repoCitas.obtenerTodos());
        citas.sort(new ComparadorCitaPorPaciente());
        return citas;
    }

    /** Comparator escrito como lambda: nombre del médico. */
    public List<Cita> obtenerCitasPorMedicoLambda() {
        List<Cita> citas = new ArrayList<>(repoCitas.obtenerTodos());
        citas.sort((c1, c2) -> c1.getMedico().getNombre().compareToIgnoreCase(c2.getMedico().getNombre()));
        return citas;
    }

    /** Comparator compuesto: especialidad ascendente y, dentro de ella, fecha y hora de la más reciente a la más antigua. */
    public List<Cita> obtenerCitasCompuestoReverso() {
        List<Cita> citas = new ArrayList<>(repoCitas.obtenerTodos());
        citas.sort(Comparator.comparing(Cita::getEspecialidad)
                .thenComparing(Cita::getFechaHora, Comparator.reverseOrder()));
        return citas;
    }
}
