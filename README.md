# Proyecto-final-Academia-Java
Este repositorio contiene el proyecto final de la Academia Full Stack Java
Spring Batch Final — Calificaciones de Estudiantes

Proyecto final que implementa un proceso batch con Spring Batch para procesar calificaciones de estudiantes, expone los datos mediante una API REST y cuenta con pruebas unitarias.


Tecnologías utilizadas


Java 17
Spring Boot 3.2.2
Spring Batch
Spring MVC (API REST)
Spring Data JPA
Spring Data MongoDB
MySQL 8
MongoDB 7
JUnit 5
Mockito
Maven
Docker



Requisitos previos


Java 17
Maven
Docker con los contenedores mysql-academia y mongo-academia corriendo


bashdocker start mysql-academia
docker start mongo-academia


Estructura del proyecto

src/main/java/com/academia/batch/
├── SpringBatchApplication.java         # Clase principal
├── config/
│   └── BatchConfig.java                # Configuración de Steps y Job
├── model/
│   ├── Estudiante.java                 # Modelo CSV / MySQL
│   └── EstudianteReporte.java          # Modelo MongoDB
├── processor/
│   ├── EstudianteProcessor.java        # Calcula el promedio (Step 1)
│   └── ReporteEstudianteProcessor.java # Determina el estado (Step 2)
├── repository/
│   ├── EstudianteEntity.java           # Entidad JPA
│   ├── EstudianteRepository.java       # Repositorio MySQL (JPA)
│   └── ReporteRepository.java          # Repositorio MongoDB
├── service/
│   └── EstudianteService.java          # Lógica de negocio
└── controller/
    ├── EstudianteController.java        # API REST estudiantes
    └── ReporteController.java           # API REST reportes

src/main/resources/
├── application.properties
└── estudiantes.csv

src/test/java/com/academia/batch/
├── processor/
│   └── ProcessorTest.java              # Pruebas del processor
└── service/
    └── EstudianteServiceTest.java       # Pruebas del servicio con Mockito


Arquitectura del proceso Batch

El Job tiene dos Steps:

estudiantes.csv
      │
   [Step 1]
      │  Leer CSV → Calcular promedio → Guardar en MySQL
      ▼
tabla: estudiantes_procesados (MySQL)
      │
   [Step 2]
      │  Leer MySQL → Determinar estado → Guardar en MongoDB
      ▼
colección: reportes_estudiantes (MongoDB)

Regla de negocio:


Promedio ≥ 70 → APROBADO
Promedio < 70 → REPROBADO



Configuración

application.properties

properties# MySQL
spring.datasource.url=jdbc:mysql://localhost:3307/academia
spring.datasource.username=alumno
spring.datasource.password=alumno123
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Spring Batch
spring.batch.jdbc.initialize-schema=always
spring.batch.job.enabled=false  # true para ejecutar el batch al iniciar

# MongoDB
spring.data.mongodb.uri=mongodb://root:root123@localhost:27017/academia?authSource=admin

# JPA
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true

Tabla MySQL (crear manualmente)

sqlCREATE TABLE IF NOT EXISTS estudiantes_procesados (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    grupo VARCHAR(10) NOT NULL,
    nota1 DECIMAL(5,2) NOT NULL,
    nota2 DECIMAL(5,2) NOT NULL,
    nota3 DECIMAL(5,2) NOT NULL,
    promedio DECIMAL(5,2) NOT NULL
);


Ejecución

Ejecutar el batch (primera vez)


Asegúrate de que spring.batch.job.enabled=true en application.properties
Ejecutar:


bashmvn spring-boot:run

Ejecutar solo la API (después del batch)


Pon spring.batch.job.enabled=false
Ejecutar:


bashmvn spring-boot:run

El servidor queda escuchando en http://localhost:8080.

Limpiar datos antes de re-ejecutar el batch

bash# MySQL
docker exec -i mysql-academia mysql -u root -proot123 academia -e "DELETE FROM estudiantes_procesados;"

# MongoDB
docker exec -i mongo-academia mongosh -u root -p root123 --quiet --eval "db.getSiblingDB('academia').reportes_estudiantes.drop()"


API REST

Estudiantes (MySQL)

MétodoURIDescripciónCódigoGET/api/estudiantesLista todos200GET/api/estudiantes/{id}Obtiene uno por id200 / 404GET/api/estudiantes/aprobados/totalTotal de aprobados200POST/api/estudiantesCrea un estudiante201PUT/api/estudiantes/{id}Reemplaza un estudiante200 / 404PATCH/api/estudiantes/{id}Cambia el grupo200 / 404DELETE/api/estudiantes/{id}Elimina un estudiante204 / 404

Reportes (MongoDB)

MétodoURIDescripciónCódigoGET/api/reportesLista todos los reportes200GET/api/reportes/estado/{estado}Filtra por APROBADO o REPROBADO200

Ejemplos con curl

bash# Listar todos
curl http://localhost:8080/api/estudiantes

# Obtener uno por id
curl -i http://localhost:8080/api/estudiantes/1

# Crear
curl -i -X POST http://localhost:8080/api/estudiantes \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Nuevo Alumno","grupo":"A","nota1":80,"nota2":85,"nota3":90,"promedio":85}'

# Actualizar completo
curl -i -X PUT http://localhost:8080/api/estudiantes/1 \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Juan Perez","grupo":"B","nota1":80,"nota2":75,"nota3":90,"promedio":81.67}'

# Actualizar parcial (solo grupo)
curl -i -X PATCH http://localhost:8080/api/estudiantes/1 \
  -H "Content-Type: application/json" \
  -d '{"grupo":"Z"}'

# Eliminar
curl -i -X DELETE http://localhost:8080/api/estudiantes/1

# Total aprobados
curl http://localhost:8080/api/estudiantes/aprobados/total

# Reportes MongoDB
curl http://localhost:8080/api/reportes
curl http://localhost:8080/api/reportes/estado/REPROBADO


Pruebas unitarias

bashmvn test

Resultado esperado: Tests run: 4, Failures: 0, Errors: 0

ClasePruebasProcessorTestCalcula promedio correctamente, marca APROBADO (≥70), marca REPROBADO (<70)EstudianteServiceTestCuenta solo los estudiantes aprobados (con Mockito)


Datos de ejemplo

El archivo estudiantes.csv contiene 10 estudiantes. Resultado esperado tras el batch:

EstudiantePromedioEstadoJuan Perez81.67APROBADOMaria Lopez51.67REPROBADOCarlos Garcia91.67APROBADOAna Martinez70.0APROBADOPedro Sanchez51.67REPROBADOLaura Diaz85.0APROBADORoberto Flores48.33REPROBADOSofia Ramirez97.67APROBADODiego Torres70.0APROBADOFernanda Rios71.33APROBADO
