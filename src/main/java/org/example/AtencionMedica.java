package com.clinica;

public class AtencionMedica {

    private final String hallazgos;
    private final String recomendaciones;
    private final String examenes;
    private final String medicamentos;
    private final String observaciones;

    public AtencionMedica(String hallazgos, String recomendaciones,
                          String examenes, String medicamentos, String observaciones) {
        this.hallazgos = hallazgos;
        this.recomendaciones = recomendaciones;
        this.examenes = examenes;
        this.medicamentos = medicamentos;
        this.observaciones = observaciones;
    }

    public String getHallazgos() {

        return hallazgos;
    }

    public String getRecomendaciones() {

        return recomendaciones;
    }

    public String getExamenes() {

        return examenes;
    }

    public String getMedicamentos() {

        return medicamentos;
    }

    public String getObservaciones() {

        return observaciones;
    }

    public boolean esBreve() {

        return examenes == null && medicamentos == null && observaciones == null;
    }

    @Override
    public String toString() {
        String info = "Hallazgos: " + hallazgos + " | Recomendaciones: " + recomendaciones;
        if (examenes != null) {
            info += " | Exámenes: " + examenes;
        }
        if (medicamentos != null) {
            info += " | Medicamentos: " + medicamentos;
        }
        if (observaciones != null) {
            info += " | Observaciones: " + observaciones;
        }
        return info;
    }
}
