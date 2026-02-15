package com.dam.gestorasistencia.repository;

import com.dam.gestorasistencia.model.RegistroAsistencia;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para acceder a los registros de asistencia diarios.
 * Permite consultar asistencia por fecha, alumno y asignatura.
 *
 * @author Equipo de Desarrollo
 */
@Repository
public interface RegistroAsistenciaRepository extends MongoRepository<RegistroAsistencia, String> {

    /**
     * Busca el registro de asistencia de un alumno para una fecha y asignatura concretas.
     * Se usa para editar asistencia sin mezclar materias distintas.
     *
     * @param fecha día del registro que se quiere consultar.
     * @param idAlumno identificador del alumno.
     * @param idAsignatura identificador de la asignatura.
     * @return {@code Optional} con el registro si existe; {@code Optional.empty()} si no existe.
     */
    Optional<RegistroAsistencia> findByFechaAndIdAlumnoAndIdAsignatura(LocalDate fecha, String idAlumno, String idAsignatura);

    /**
     * Busca los registros de una fecha para un conjunto de alumnos.
     * Es útil para cargar una clase completa con una sola consulta.
     *
     * @param fecha día del que se desean obtener registros.
     * @param idsAlumnos lista de identificadores de alumnos a consultar.
     * @return lista de registros encontrados; devuelve lista vacía si no hay coincidencias.
     */
    List<RegistroAsistencia> findByFechaAndIdAlumnoIn(LocalDate fecha, List<String> idsAlumnos);

    /**
     * Obtiene el historial de asistencia de un alumno concreto.
     *
     * @param idAlumno identificador del alumno del que se quiere el historial.
     * @return lista de registros del alumno; devuelve lista vacía si no tiene registros.
     */
    List<RegistroAsistencia> findByIdAlumno(String idAlumno);
}
