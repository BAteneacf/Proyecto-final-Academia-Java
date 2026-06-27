package com.academia.batch.processor;

import com.academia.batch.model.Estudiante;
import com.academia.batch.model.EstudianteReporte;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProcessorTest {

    @Test
    void calculaElPromedioCorrectamente() {
        Estudiante e = new Estudiante();
        e.setNota1(80);
        e.setNota2(90);
        e.setNota3(100);

        Estudiante resultado = new EstudianteProcessor().process(e);

        // (80 + 90 + 100) / 3 = 90
        assertEquals(90.0, resultado.getPromedio(), 0.001);
    }

    @Test
    void marcaAprobadoCuandoElPromedioEsAlMenos70() {
        Estudiante e = new Estudiante();
        e.setPromedio(70);

        EstudianteReporte reporte = new ReporteEstudianteProcessor().process(e);

        assertEquals("APROBADO", reporte.getEstado());
    }

    @Test
    void marcaReprobadoCuandoElPromedioEsMenorA70() {
        Estudiante e = new Estudiante();
        e.setPromedio(69.9);

        EstudianteReporte reporte = new ReporteEstudianteProcessor().process(e);

        assertEquals("REPROBADO", reporte.getEstado());
    }
}