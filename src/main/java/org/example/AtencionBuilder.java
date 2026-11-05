package org.example;

public interface AtencionBuilder {

    AtencionBuilder conHallazgos(String hallazgos);

    AtencionBuilder conRecomendaciones(String recomendaciones);

    AtencionBuilder conExamenes(String examenes);

    AtencionBuilder conMedicamentos(String medicamentos);

    AtencionBuilder conObservaciones(String observaciones);

    AtencionMedica build() throws ReglaClinicaException;
}
