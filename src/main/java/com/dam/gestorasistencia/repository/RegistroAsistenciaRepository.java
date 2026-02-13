package com.dam.gestorasistencia.repository;

import com.dam.gestorasistencia.model.RegistroAsistencia;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegistroAsistenciaRepository extends MongoRepository<RegistroAsistencia, String> {

    // Buscar si existe un registro específico para un alumno en una fecha, también por idAsignatura para no mezclar clases
    Optional<RegistroAsistencia> findByFechaAndIdAlumnoAndIdAsignatura(LocalDate fecha, String idAlumno, String idAsignatura);

    // Buscar todos los registros de una fecha para una lista de IDs de alumnos
    // (Útil para cargar la tabla de una clase entera de golpe y no hacer 20 consultas)
    List<RegistroAsistencia> findByFechaAndIdAlumnoIn(LocalDate fecha, List<String> idsAlumnos);

    // Buscar todas las faltas de un alumno (para historial/estadísticas)
    List<RegistroAsistencia> findByIdAlumno(String idAlumno);
}
