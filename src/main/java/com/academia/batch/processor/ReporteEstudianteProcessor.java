package com.academia.batch.processor;


import com.academia.batch.model.Estudiante;
import com.academia.batch.model.EstudianteReporte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

// Step 2 - Processor: convierte Estudiante a EstudianteReporte determinando el estado
public class ReporteEstudianteProcessor implements ItemProcessor<Estudiante, EstudianteReporte> {

    private static final Logger log = LoggerFactory.getLogger(ReporteEstudianteProcessor.class);

    @Override
    public EstudianteReporte process(Estudiante estudiante) {
        EstudianteReporte reporte = new EstudianteReporte();
        reporte.setNombre(estudiante.getNombre());
        reporte.setGrupo(estudiante.getGrupo());
        reporte.setPromedio(estudiante.getPromedio());
        reporte.setEstado(estudiante.getPromedio() >= 70 ? "APROBADO" : "REPROBADO");

        log.info("Step 2 - Reporte: {}", reporte);
        return reporte;
    }
}