package com.academia.batch.processor;

import com.academia.batch.model.Estudiante;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

// Step 1 - Processor: calcula el promedio del estudiante leido del CSV
public class EstudianteProcessor implements ItemProcessor<Estudiante, Estudiante> {

    private static final Logger log = LoggerFactory.getLogger(EstudianteProcessor.class);

    @Override
    public Estudiante process(Estudiante estudiante) {
        double promedio = (estudiante.getNota1() + estudiante.getNota2() + estudiante.getNota3()) / 3;
        estudiante.setPromedio(promedio);

        log.info("Step 1 - Procesando: {}", estudiante);
        return estudiante;
    }
}