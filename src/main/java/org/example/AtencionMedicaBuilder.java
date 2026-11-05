package org.example;

public class AtencionMedicaBuilder implements AtencionBuilder {

    private String hallazgos;
    private String recomendaciones;
    private String examenes;
    private String medicamentos;
    private String observaciones;

    @Override
    public AtencionBuilder conHallazgos(String hallazgos) {
        this.hallazgos = hallazgos;
        return this;
    }

    @Override
    public AtencionBuilder conRecomendaciones(String recomendaciones) {
        this.recomendaciones = recomendaciones;
        return this;
    }

    @Override
    public AtencionBuilder conExamenes(String examenes) {
        this.examenes = examenes;
        return this;
    }

    @Override
    public AtencionBuilder conMedicamentos(String medicamentos) {
        this.medicamentos = medicamentos;
        return this;
    }

    @Override
    public AtencionBuilder conObservaciones(String observaciones) {
        this.observaciones = observaciones;
        return this;
    }

    @Override
    public AtencionMedica build() throws ReglaClinicaException {
        if (hallazgos == null || hallazgos.isBlank() || recomendaciones == null || recomendaciones.isBlank()) {
            throw new ReglaClinicaException("El médico debe registrar obligatoriamente lo que encontró y lo que recomienda.");
        }
        return new AtencionMedica(hallazgos, recomendaciones, examenes, medicamentos, observaciones);
    }
}
