
Nombres:

jsuszabaleta : Jesús Zabaleta Benitez
Zahico : Diego Hernandez
MAGJ2334 : Miguel Gil Jaramillo
Jasa23128: Jacob Santamaria


UML:

class Persona {

        <<abstract>>
        -id: String
        -nombre: String
        -documento: String
        +mostrarPerfil()* String
        +compareTo(otra: Persona) int
        +getId() String
        +setId(id: String) void
        +getNombre() String
        +setNombre(nombre: String) void
        +getDocumento() String
        +setDocumento(documento: String) void
    }
    Comparable <|.. Persona

  class Medico {

        -especialidad: Especialidad
        -horaInicio: LocalTime
        -horaFin: LocalTime
        +atiendeEnHora(fechaHora: LocalDateTime) boolean
        +mostrarPerfil() String
        +getEspecialidad() Especialidad
        +setEspecialidad(especialidad: Especialidad) void
        +getHoraInicio() LocalTime
        +setHoraInicio(horaInicio: LocalTime) void
        +getHoraFin() LocalTime
        +setHoraFin(horaFin: LocalTime) void
    }
    Persona <|-- Medico

  class Paciente {
  
        -telefono: String
        -edad: int
        +mostrarPerfil() String
        +getTelefono() String
        +setTelefono(telefono: String) void
        +getEdad() int
        +setEdad(edad: int) void
    }
    Persona <|-- Paciente

  class Especialidad {
  
        <<enumeration>>
        MEDICINA_GENERAL
        CARDIOLOGIA
        PEDIATRIA
        ODONTOLOGIA
    }
    Medico --> Especialidad
    
 class Cita {
    
        -id: String
        -paciente: Paciente
        -medico: Medico
        -fechaHora: LocalDateTime
        -estado: EstadoCita
        -atencionMedica: AtencionMedica
        +confirmar() void
        +atender(atencion: AtencionMedica) void
        +cancelar(momentoActual: LocalDateTime) void
        +cambiarFecha(nuevaFecha: LocalDateTime, momentoActual: LocalDateTime) void
        +marcarNoAsistio() void
        +getId() String
        +getPaciente() Paciente
        +getMedico() Medico
        +getEspecialidad() Especialidad
        +getFechaHora() LocalDateTime
        +setFechaHora(fechaHora: LocalDateTime) void
        +getEstado() EstadoCita
        +setEstado(estado: EstadoCita) void
        +getNombreEstado() String
        +esActiva() boolean
        +getAtencionMedica() AtencionMedica
        +setAtencionMedica(atencionMedica: AtencionMedica) void
        +compareTo(otra: Cita) int
    }
    Comparable <|.. Cita
    Cita "0..*" --> "1" Paciente : paciente
    Cita "0..*" --> "1" Medico : medico*

-------------------PATRON 1: STATE----------------------

    class EstadoCita {
    
        <<interface>>
        +confirmar(cita: Cita) void
        +atender(cita: Cita, atencion: AtencionMedica) void
        +cancelar(cita: Cita, momentoActual: LocalDateTime) void
        +cambiarFecha(cita: Cita, nuevaFecha: LocalDateTime, momentoActual: LocalDateTime) void
        +marcarNoAsistio(cita: Cita) void
        +getNombre() String
        +esActiva() boolean
    }
    Cita "1" *-- "1" EstadoCita : estado actual (Contexto a State)

  
  class EstadoSolicitada {
  
        +confirmar(cita: Cita) void
        +atender(cita: Cita, atencion: AtencionMedica) void
        +cancelar(cita: Cita, momentoActual: LocalDateTime) void
        +cambiarFecha(cita: Cita, nuevaFecha: LocalDateTime, momentoActual: LocalDateTime) void
        +marcarNoAsistio(cita: Cita) void
        +getNombre() String
        +esActiva() boolean
    }


  class EstadoConfirmada {
  
        +confirmar(cita: Cita) void
        +atender(cita: Cita, atencion: AtencionMedica) void
        +cancelar(cita: Cita, momentoActual: LocalDateTime) void
        +cambiarFecha(cita: Cita, nuevaFecha: LocalDateTime, momentoActual: LocalDateTime) void
        +marcarNoAsistio(cita: Cita) void
        +getNombre() String
        +esActiva() boolean
    }

   class EstadoAtendida {
   
        +confirmar(cita: Cita) void
        +atender(cita: Cita, atencion: AtencionMedica) void
        +cancelar(cita: Cita, momentoActual: LocalDateTime) void
        +cambiarFecha(cita: Cita, nuevaFecha: LocalDateTime, momentoActual: LocalDateTime) void
        +marcarNoAsistio(cita: Cita) void
        +getNombre() String
        +esActiva() boolean
    }

  class EstadoCancelada {
  
        +confirmar(cita: Cita) void
        +atender(cita: Cita, atencion: AtencionMedica) void
        +cancelar(cita: Cita, momentoActual: LocalDateTime) void
        +cambiarFecha(cita: Cita, nuevaFecha: LocalDateTime, momentoActual: LocalDateTime) void
        +marcarNoAsistio(cita: Cita) void
        +getNombre() String
        +esActiva() boolean
    }

  class EstadoNoAsistio {
  
        +confirmar(cita: Cita) void
        +atender(cita: Cita, atencion: AtencionMedica) void
        +cancelar(cita: Cita, momentoActual: LocalDateTime) void
        +cambiarFecha(cita: Cita, nuevaFecha: LocalDateTime, momentoActual: LocalDateTime) void
        +marcarNoAsistio(cita: Cita) void
        +getNombre() String
        +esActiva() boolean
    }
 
          EstadoCita <|.. EstadoSolicitada
          EstadoCita <|.. EstadoConfirmada
          EstadoCita <|.. EstadoAtendida
          EstadoCita <|.. EstadoCancelada
          EstadoCita <|.. EstadoNoAsistio
  
  -----PATRON 2------ (Builder)

  
         class AtencionMedica {
  
        -hallazgos: String
        -recomendaciones: String
        -examenes: String
        -medicamentos: String
        -observaciones: String
        +getHallazgos() String
        +getRecomendaciones() String
        +getExamenes() String
        +getMedicamentos() String
        +getObservaciones() String
        +esBreve() boolean
    }
    Cita "1" o-- "0..1" AtencionMedica : registro de atencion

  class AtencionBuilder {
  
        <<interface>>
        +conHallazgos(hallazgos: String) AtencionBuilder
        +conRecomendaciones(recomendaciones: String) AtencionBuilder
        +conExamenes(examenes: String) AtencionBuilder
        +conMedicamentos(medicamentos: String) AtencionBuilder
        +conObservaciones(observaciones: String) AtencionBuilder
        +build() AtencionMedica
    }

   class AtencionMedicaBuilder {
   
        -hallazgos: String
        -recomendaciones: String
        -examenes: String
        -medicamentos: String
        -observaciones: String
        +conHallazgos(hallazgos: String) AtencionBuilder
        +conRecomendaciones(recomendaciones: String) AtencionBuilder
        +conExamenes(examenes: String) AtencionBuilder
        +conMedicamentos(medicamentos: String) AtencionBuilder
        +conObservaciones(observaciones: String) AtencionBuilder
        +build() AtencionMedica
    }
    AtencionBuilder <|.. AtencionMedicaBuilder
    AtencionMedicaBuilder ..> AtencionMedica : construye producto
   ------------------------------------------------- CLASE UTILITARIA Y GENERICOS-------------------------------------:

          class Repositorio~T, ID~ {
    
                <<interface>>
                +guardar(id: ID, entidad: T) void
                +buscarPorId(id: ID) Optional~T~
                +obtenerTodos() List~T~
    }

  class RepositorioMemoria~T, ID~ {
  
        -datos: Map~ID, T~
        +guardar(id: ID, entidad: T) void
        +buscarPorId(id: ID) Optional~T~
        +obtenerTodos() List~T~
    }
    Repositorio <|.. RepositorioMemoria

  class AgendaService {
  
        -repoPacientes: Repositorio~Paciente, String~
        -repoMedicos: Repositorio~Medico, String~
        -repoCitas: Repositorio~Cita, String~
        -contadorCitas: int
        -generadorIdCita: Supplier~String~
        +registrarPaciente(paciente: Paciente) void
        +registrarMedico(medico: Medico) void
        +solicitarCita(idPac: String, idMed: String, fecha: LocalDateTime) Cita
        +confirmarCita(idCita: String) void
        +atenderCita(idCita: String, atencion: AtencionMedica) void
        +cancelarCita(idCita: String, momentoActual: LocalDateTime) void
        +cambiarFechaCita(idCita: String, nuevaFecha: LocalDateTime, momento: LocalDateTime) void
        +marcarNoAsistio(idCita: String) void
        +obtenerCitasOrdenNatural() List~Cita~
    }
    AgendaService --> Repositorio : inyeccion DIP
    AgendaService ..> Cita : orquesta
    AgendaService ..> ValidadorCita : invoca

  class ReporteGerenciaService {
  
        -repoCitas: Repositorio~Cita, String~
        +verAgendaDiariaMedico(idMedico: String, fecha: LocalDate) List~Cita~
        +calcularDemandaPorEspecialidad() Map~Especialidad, Long~
        +calcularPorcentajeInasistencia() double
        +obtenerNombresPacientesAtendidos() String
        +sumarTotalPacientesAtendidosConSum() int
        +calcularEdadPromedioPacientesCitados() double
    }

    
  ReporteGerenciaService --> Repositorio : consulta citas

           class ValidadorCita {
   
                <<interface>>
                +esValida(cita: Cita) boolean
            }
    
------------------------------------------EXCEPCIONES-------------------------------:


        class Exception {
    
            }
            class ClinicaException {
        +ClinicaException(mensaje: String)
            }
            class ReglaClinicaException
            class HorarioOcupadoException
            class CancelacionTardiaException


          Exception <|-- ClinicaException
  
          ClinicaException <|-- ReglaClinicaException
  
        linicaException <|-- HorarioOcupadoException
    
          ClinicaException <|-- CancelacionTardiaException
  


  
          3. Trazabilidad:

Pilares de POO: Persona.java, Medico.java, Paciente.java, Cita.java


Manejo de errores: ClinicaException.java, ReglaClinicaException.java, HorarioOcupadoException.java, CancelacionTardiaException.java, AgendaService.java, Main.java


Comparable: Cita.java, Persona.java


Comparator: ComparadorCitaPorPaciente, AgendaService


Collection.Sort: AgendaService.java


Comparable<? super T>: UtilidadesClinicas.java


Interfaz Generica: Repositorio.java


Clases genericas: RepositorioMemoria.java, resultados.java


Metodos estaticos: UtilidadesClinicas.java


Wildcards: UtilidadesClinicas.java


Prog. Funcional: AgendaService.java, ValidadorCita.java, repositorioMemoria.java


Patrones de diseño: AtencionBuilder.java, AtencionMedicaBuilder.java, EstadoCita.java


Logs: NivelLog.java, AgendaService.java, ReporteGerenciaService.java, Main.java


4. Justificación Solid: 


Single reponsability:
ReporteGerenteService.java es un gran ejemplo ya que se dedica de forma exclusiva a calcular estadisticas y reportes administrativos.
Ejemplo:


        public class ReporteGerenciaService {

          private final Repositorio<Cita, String> repoCitas;

          public ReporteGerenciaService(Repositorio<Cita, String> repoCitas) {
        this.repoCitas = repoCitas;
  }

          public Map<Especialidad, Long> calcularDemandaPorEspecialidad() {
    LOGGER.log(NivelLog.INFO, "Calculando demanda por especialidad.");
      return repoCitas.obtenerTodos().stream()
             .collect(Collectors.groupingBy(Cita::getEspecialidad, Collectors.counting()));
    }
}

Open/Closed 

Si la clínica quiere agregar un nuevo estado en el ciclo de vida (como EstadoReprogramada), no es necesario modificar la clase Cita ni incorporar condicionales switch o if-else en sus métodos. 
Con crear una clase nueva que implemente la interfaz EstadoCita, extendiendo el comportamiento sin alterar el código base ya verificado.

Liskov:

medicos.java y paciente.java ya que heredan de la clase abstracta persona.java y heredan metodos que reciben colecciones genericas
de persona.


        public static List<String> obtenerPerfilesPersonas(List<? extends Persona> personas) {
    List<String> perfiles = new ArrayList<>();
    for (Persona p : personas) {
        perfiles.add(p.mostrarPerfil());
    }
    return perfiles;
}



Interface Segregation


        ValidarCita.java y AtencionBuilder ya que en lugar de crear interfaces generales y acopladas, diseñamos contratos atómicos y de proceso unicos:
        @FunctionalInterface
        public interface ValidadorCita {
    boolean esValida(Cita cita);
        }


Dependency Inversion Principle
La lógica del servicio de negocio AgendaService no instancia ni se acopla a la clase concreta de almacenamiento RepositorioMemoria. 
Depende de la interfaz abstracta Repositorio<T, ID>, la cual es inyectada en su constructor. 
Esto permite reemplazar la memoria volátil por archivos sin modificar el código del servicio.
public class AgendaService {

         private final Repositorio<Paciente, String> repoPacientes;
        private final Repositorio<Medico, String> repoMedicos;
        private final Repositorio<Cita, String> repoCitas;

          public AgendaService(Repositorio<Paciente, String> repoPacientes,
                         Repositorio<Medico, String> repoMedicos,
                         Repositorio<Cita, String> repoCitas) {
        this.repoPacientes = repoPacientes;
        this.repoMedicos = repoMedicos;
        this.repoCitas = repoCitas;
    }
}
