package com.academia.batch.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "estudiantes_procesados")   // mapea a la tabla existente
public class EstudianteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // id autoincremental de MySQL
    private Long id;

    private String nombre;
    private String grupo;
    private double nota1;
    private double nota2;
    private double nota3;
    private double promedio;

    public EstudianteEntity() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getGrupo() { return grupo; }
    public void setGrupo(String grupo) { this.grupo = grupo; }

    public double getNota1() { return nota1; }
    public void setNota1(double nota1) { this.nota1 = nota1; }

    public double getNota2() { return nota2; }
    public void setNota2(double nota2) { this.nota2 = nota2; }

    public double getNota3() { return nota3; }
    public void setNota3(double nota3) { this.nota3 = nota3; }

    public double getPromedio() { return promedio; }
    public void setPromedio(double promedio) { this.promedio = promedio; }
}