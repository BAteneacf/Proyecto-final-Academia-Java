package com.academia.batch.config;

import com.academia.batch.model.Estudiante;
import com.academia.batch.model.EstudianteReporte;
import com.academia.batch.processor.EstudianteProcessor;
import com.academia.batch.processor.ReporteEstudianteProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfig {

    // =====================================================================
    //  STEP 1: Lee CSV -> calcula promedio -> escribe en MySQL
    // =====================================================================

    @Bean
    public FlatFileItemReader<Estudiante> leerCsv() {
        return new FlatFileItemReaderBuilder<Estudiante>()
                .name("estudianteReader")
                .resource(new ClassPathResource("estudiantes.csv"))
                .delimited()
                .names("nombre", "grupo", "nota1", "nota2", "nota3")
                .targetType(Estudiante.class)
                .linesToSkip(1)
                .build();
    }

    @Bean
    public EstudianteProcessor procesarEstudiante() {
        return new EstudianteProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Estudiante> escribirEnBd(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Estudiante>()
                .sql("INSERT INTO estudiantes_procesados (nombre, grupo, nota1, nota2, nota3, promedio) "
                   + "VALUES (:nombre, :grupo, :nota1, :nota2, :nota3, :promedio)")
                .dataSource(dataSource)
                .beanMapped()
                .build();
    }

    @Bean
    public Step paso1(JobRepository jobRepository,
                      PlatformTransactionManager transactionManager,
                      FlatFileItemReader<Estudiante> leerCsv,
                      EstudianteProcessor procesarEstudiante,
                      JdbcBatchItemWriter<Estudiante> escribirEnBd) {

        return new StepBuilder("paso1", jobRepository)
                .<Estudiante, Estudiante>chunk(3, transactionManager)
                .reader(leerCsv)
                .processor(procesarEstudiante)
                .writer(escribirEnBd)
                .build();
    }

    // =====================================================================
    //  STEP 2: Lee MySQL -> determina estado -> escribe en MongoDB
    // =====================================================================

    @Bean
    public JdbcCursorItemReader<Estudiante> leerDeBd(DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<Estudiante>()
                .name("estudianteDbReader")
                .dataSource(dataSource)
                .sql("SELECT nombre, grupo, promedio FROM estudiantes_procesados")
                .rowMapper((rs, rowNum) -> {
                    Estudiante estudiante = new Estudiante();
                    estudiante.setNombre(rs.getString("nombre"));
                    estudiante.setGrupo(rs.getString("grupo"));
                    estudiante.setPromedio(rs.getDouble("promedio"));
                    return estudiante;
                })
                .build();
    }

    @Bean
    public ReporteEstudianteProcessor procesarReporte() {
        return new ReporteEstudianteProcessor();
    }

    @Bean
    public MongoItemWriter<EstudianteReporte> escribirEnMongo(MongoTemplate mongoTemplate) {
        return new MongoItemWriterBuilder<EstudianteReporte>()
                .template(mongoTemplate)
                .collection("reportes_estudiantes")
                .build();
    }

    @Bean
    public Step paso2(JobRepository jobRepository,
                      PlatformTransactionManager transactionManager,
                      JdbcCursorItemReader<Estudiante> leerDeBd,
                      ReporteEstudianteProcessor procesarReporte,
                      MongoItemWriter<EstudianteReporte> escribirEnMongo) {

        return new StepBuilder("paso2", jobRepository)
                .<Estudiante, EstudianteReporte>chunk(3, transactionManager)
                .reader(leerDeBd)
                .processor(procesarReporte)
                .writer(escribirEnMongo)
                .build();
    }

    // =====================================================================
    //  JOB: ejecuta paso1 y luego paso2
    // =====================================================================

    @Bean
    public Job procesarCalificacionesJob(JobRepository jobRepository, Step paso1, Step paso2) {
        return new JobBuilder("procesarCalificacionesJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(paso1)
                .next(paso2)
                .build();
    }
}