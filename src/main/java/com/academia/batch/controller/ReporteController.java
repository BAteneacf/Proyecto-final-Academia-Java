package com.academia.batch.controller;

import com.academia.batch.model.EstudianteReporte;
import com.academia.batch.repository.ReporteRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteRepository repository;

    public ReporteController(ReporteRepository repository) {
        this.repository = repository;
    }

    // GET /api/reportes -> todos los reportes (MongoDB)
    @GetMapping
    public List<EstudianteReporte> listar() {
        return repository.findAll();
    }

    // GET /api/reportes/estado/APROBADO  o  /REPROBADO
    @GetMapping("/estado/{estado}")
    public List<EstudianteReporte> porEstado(@PathVariable String estado) {
        return repository.findByEstado(estado.toUpperCase());
    }
}