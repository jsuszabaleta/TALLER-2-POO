package org.example;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        System.out.println("=== DEMOSTRACIÓN DEL SISTEMA DE CLÍNICA Y AGENDA DE CITAS ===\n");

        // 1. Inicializar repositorios genéricos y servicios
        Repositorio<Paciente, String> repoPacientes = new RepositorioMemoria<>();
        Repositorio<Medico, String> repoMedicos = new RepositorioMemoria<>();
        Repositorio<Cita, String> repoCitas = new RepositorioMemoria<>();

        AgendaService agendaService = new AgendaService(repoPacientes, repoMedicos, repoCitas);
        ReporteGerenciaService reporteService = new ReporteGerenciaService(repoCitas);

        // 2. Crear y registrar médicos (cada uno con su especialidad y horario propio) y pacientes
        Medico medCardio = new Medico("M1", "Carlos Ramírez", "1010",
                Especialidad.CARDIOLOGIA, LocalTime.of(8, 0), LocalTime.of(14, 0));
        Medico medGeneral = new Medico("M2", "Laura Gómez", "2020",
                Especialidad.MEDICINA_GENERAL, LocalTime.of(7, 0), LocalTime.of(17, 0));
        Medico medPediatria = new Medico("M3", "Andrés Pineda", "3030",
                Especialidad.PEDIATRIA, LocalTime.of(8, 0), LocalTime.of(16, 0));

        agendaService.registrarMedico(medCardio);
        agendaService.registrarMedico(medGeneral);
        agendaService.registrarMedico(medPediatria);

        Paciente pacAna = new Paciente("P1", "Ana Torres", "1111", "300123", 45);
        Paciente pacJuan = new Paciente("P2", "Juan Vega", "2222", "300456", 30);
        Paciente pacSofia = new Paciente("P3", "Sofía Castro", "3333", "300789", 8);

        agendaService.registrarPaciente(pacAna);
        agendaService.registrarPaciente(pacJuan);
        agendaService.registrarPaciente(pacSofia);

        // 3. Probar solicitud de citas, Patrón State y Patrón Builder
        try {
            System.out.println("--- 1. Flujo de citas con Patrón State y Patrón Builder ---");

            // Cita 1: Se solicita -> Se confirma -> Se atiende con registro completo (Builder)
            Cita cita1 = agendaService.solicitarCita("P1", "M1", LocalDateTime.of(2026, 11, 16, 9, 0));
            agendaService.confirmarCita(cita1.getId());

            AtencionMedica atencionCompleta = new AtencionMedicaBuilder()
                    .conHallazgos("Presión arterial elevada (145/90)")
                    .conRecomendaciones("Dieta baja en sal y caminata diaria")
                    .conExamenes("Electrocardiograma y perfil lipídico")
                    .conMedicamentos("Losartán 50mg cada 12 horas")
                    .conObservaciones("Control en 30 días")
                    .build();

            agendaService.atenderCita(cita1.getId(), atencionCompleta);
            System.out.println("Cita 1 atendida: " + cita1);
            System.out.println("Detalle atención (Builder completo): " + cita1.getAtencionMedica());

            // Cita 2: Se solicita -> Se confirma -> El paciente nunca aparece (NO_ASISTIO)
            Cita cita2 = agendaService.solicitarCita("P2", "M1", LocalDateTime.of(2026, 11, 16, 10, 0));
            agendaService.confirmarCita(cita2.getId());
            agendaService.marcarNoAsistio(cita2.getId());
            System.out.println("Cita 2 marcada como inasistencia: " + cita2);

            // Cita 3: Se solicita -> Se confirma -> Se atiende con registro breve (Builder)
            Cita cita3 = agendaService.solicitarCita("P2", "M2", LocalDateTime.of(2026, 11, 16, 8, 0));
            agendaService.confirmarCita(cita3.getId());

            AtencionMedica atencionBreve = new AtencionMedicaBuilder()
                    .conHallazgos("Signos vitales normales")
                    .conRecomendaciones("Apto para certificado médico")
                    .build();

            agendaService.atenderCita(cita3.getId(), atencionBreve);
            System.out.println("Cita 3 atendida (Builder breve): " + cita3.getAtencionMedica());

            // Cita 4: Queda en estado SOLICITADA y luego se cambia de hora con anticipación
            Cita cita4 = agendaService.solicitarCita("P3", "M3", LocalDateTime.of(2026, 11, 18, 9, 0));
            agendaService.cambiarFechaCita(
                    cita4.getId(),
                    LocalDateTime.of(2026, 11, 18, 11, 0),
                    LocalDateTime.of(2026, 11, 15, 9, 0));
            System.out.println("Cita 4 reprogramada: " + cita4);

        } catch (ReglaClinicaException | HorarioOcupadoException | CancelacionTardiaException e) {
            LOGGER.log(NivelLog.ERROR, "Regla de negocio incumplida en el flujo principal: " + e.getMessage());
        } catch (ClinicaException e) {
            LOGGER.log(NivelLog.ERROR, "Error en el flujo principal: " + e.getMessage());
        } finally {
            LOGGER.fine("Finalizó bloque de prueba de flujo normal.");
        }

        // 4. Probar validaciones y excepciones personalizadas (try / catch / finally)
        System.out.println("\n--- 2. Prueba de Reglas de Negocio y Excepciones ---");
        try {
            // Intentar asignar dos pacientes al mismo médico a la misma hora (M3 el 2026-11-18 a las 11:00)
            agendaService.solicitarCita("P1", "M3", LocalDateTime.of(2026, 11, 18, 11, 0));
            LOGGER.log(NivelLog.ERROR, "La prueba de choque de horario debía lanzar una excepción.");
        } catch (HorarioOcupadoException e) {
            LOGGER.log(NivelLog.WARNING, "Prueba exitosa de choque de horario: " + e.getMessage());
        } catch (ReglaClinicaException e) {
            LOGGER.log(NivelLog.ERROR, "Choque de horario rechazado por otra regla: " + e.getMessage());
        } catch (ClinicaException e) {
            LOGGER.log(NivelLog.ERROR, "Error inesperado en la prueba de choque de horario: " + e.getMessage());
        } finally {
            LOGGER.fine("Validación de choque de horario verificada.");
        }

        try {
            // Intentar que el mismo paciente (P3) acapare otro turno activo en PEDIATRIA
            agendaService.solicitarCita("P3", "M3", LocalDateTime.of(2026, 11, 19, 10, 0));
            LOGGER.log(NivelLog.ERROR, "La prueba de límite por especialidad debía lanzar una excepción.");
        } catch (ReglaClinicaException e) {
            LOGGER.log(NivelLog.WARNING, "Prueba exitosa de límite por especialidad: " + e.getMessage());
        } catch (HorarioOcupadoException e) {
            LOGGER.log(NivelLog.ERROR, "Límite por especialidad rechazado por choque de horario: " + e.getMessage());
        } catch (ClinicaException e) {
            LOGGER.log(NivelLog.ERROR, "Error inesperado en la prueba de límite por especialidad: " + e.getMessage());
        }

        try {
            // Intentar cancelar a última hora (faltando solo 2 horas para la cita4)
            agendaService.cancelarCita("CITA-4", LocalDateTime.of(2026, 11, 18, 9, 0));
            LOGGER.log(NivelLog.ERROR, "La prueba de cancelación tardía debía lanzar una excepción.");
        } catch (CancelacionTardiaException e) {
            LOGGER.log(NivelLog.WARNING, "Prueba exitosa de cancelación tardía: " + e.getMessage());
        } catch (ReglaClinicaException e) {
            LOGGER.log(NivelLog.ERROR, "Cancelación rechazada por otra regla: " + e.getMessage());
        } catch (ClinicaException e) {
            LOGGER.log(NivelLog.ERROR, "Error inesperado en la prueba de cancelación tardía: " + e.getMessage());
        }

        // 5. Probar Ordenamientos (Comparable, los 3 Comparators y algoritmo burbuja genérico)
        System.out.println("\n--- 3. Pruebas de Ordenamiento ---");
        System.out.println("Orden natural (Comparable por fechaHora):");
        agendaService.obtenerCitasOrdenNatural().forEach(c -> System.out.println("  " + c));

        System.out.println("Orden por Comparator Clase (nombre del paciente):");
        agendaService.obtenerCitasPorPaciente().forEach(c -> System.out.println("  " + c));

        System.out.println("Orden por Comparator Lambda (nombre del médico):");
        agendaService.obtenerCitasPorMedicoLambda().forEach(c -> System.out.println("  " + c));

        System.out.println("Orden por Comparator Compuesto (especialidad y fechaHora reverso):");
        agendaService.obtenerCitasCompuestoReverso().forEach(c -> System.out.println("  " + c));

        List<Paciente> listaPacientes = new ArrayList<>(List.of(pacSofia, pacJuan, pacAna));
        UtilidadesClinica.ordenar(listaPacientes);
        System.out.println("Pacientes ordenados con algoritmo genérico a mano: " + listaPacientes);

        // 6. Probar Genéricos y Wildcards (<?>, ? extends T, ? super T)
        System.out.println("\n--- 4. Pruebas de Genéricos y Wildcards ---");
        Resultado<Integer> totalPacientes = new Resultado<>(
                UtilidadesClinica.contarElementos(listaPacientes), "Total pacientes contados con wildcard <?>");
        System.out.println(totalPacientes.getMensaje() + ": " + totalPacientes.getDato());
        System.out.println("Perfiles con ? extends Persona: " + UtilidadesClinica.obtenerPerfilesPersonas(listaPacientes));

        List<Persona> listaGeneralPersonas = new ArrayList<>();
        UtilidadesClinica.agregarPacientes(listaPacientes, listaGeneralPersonas);
        System.out.println("Total en lista de Persona tras usar ? super Paciente: " + listaGeneralPersonas.size());

        // 7. Reportes de Gerencia y Operaciones con Streams
        System.out.println("\n--- 5. Reportes de la Gerencia y Streams ---");
        try {
            System.out.println("Agenda diaria del Dr. Carlos Ramírez (M1) para el 2026-11-16:");
            reporteService.verAgendaDiariaMedico("M1", LocalDate.of(2026, 11, 16))
                    .forEach(c -> System.out.println("  " + c));
        } catch (ReglaClinicaException e) {
            LOGGER.log(NivelLog.ERROR, e.getMessage());
        }

        System.out.println("Demanda por especialidad: " + reporteService.calcularDemandaPorEspecialidad());
        System.out.printf("Porcentaje de pacientes que no asisten: %.2f%%%n",
                reporteService.calcularPorcentajeInasistencia());
        System.out.println("Pacientes atendidos (Stream reduce): " + reporteService.obtenerNombresPacientesAtendidos());
        System.out.println("Total citas atendidas (Stream sum): " + reporteService.sumarTotalPacientesAtendidosConSum());
        System.out.println("Edad promedio en citas (Stream average): " + reporteService.calcularEdadPromedioPacientesCitados());
    }
}
