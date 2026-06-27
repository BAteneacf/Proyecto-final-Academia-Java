package com.academia.batch.repository;

import com.academia.batch.model.EstudianteReporte;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ReporteRepository extends MongoRepository<EstudianteReporte, String> {

    List<EstudianteReporte> findByEstado(String estado);
}