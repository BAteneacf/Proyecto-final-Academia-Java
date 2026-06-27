package com.academia.batch.service;

import com.academia.batch.repository.EstudianteEntity;
import com.academia.batch.repository.EstudianteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudianteServiceTest {

    @Mock
    private EstudianteRepository repository;     // repositorio falso

    @InjectMocks
    private EstudianteService service;           // recibe el mock por constructor

    @Test
    void cuentaSoloLosAprobados() {
        // Preparamos datos de prueba: 2 aprobados (>=70) y 1 reprobado
        EstudianteEntity aprobado1 = nuevo(85);
        EstudianteEntity aprobado2 = nuevo(70);
        EstudianteEntity reprobado = nuevo(50);

        // Cuando alguien llame repository.findAll(), devuelve esta lista
        when(repository.findAll()).thenReturn(List.of(aprobado1, aprobado2, reprobado));

        long total = service.contarAprobados();

        assertEquals(2, total);   // solo 2 estan aprobados
    }

    private EstudianteEntity nuevo(double promedio) {
        EstudianteEntity e = new EstudianteEntity();
        e.setPromedio(promedio);
        return e;
    }
}