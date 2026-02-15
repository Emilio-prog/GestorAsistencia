package com.dam.gestorasistencia.repository;

import com.dam.gestorasistencia.model.Alumno;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para acceder y consultar alumnos en MongoDB.
 * Permite operaciones CRUD y búsquedas por grupo académico.
 *
 * @author Equipo de Desarrollo
 */
@Repository
public interface AlumnoRepository extends MongoRepository<Alumno, String> {

    /**
     * Busca todos los alumnos que pertenecen a un grupo concreto.
     * Se usa para cargar la tabla de asistencia por clase.
     *
     * @param grupo código del grupo que se quiere consultar, por ejemplo "2DAM".
     * @return lista de alumnos del grupo indicado; devuelve una lista vacía si no hay resultados.
     */
    List<Alumno> findByGrupo(String grupo);

    /**
     * Busca un alumno por su correo electrónico.
     *
     * @param email correo del alumno.
     * @return alumno encontrado si existe.
     */
    Optional<Alumno> findByEmail(String email);

    /**
     * Busca un alumno por su correo ignorando mayúsculas/minúsculas.
     *
     * @param email correo del alumno.
     * @return alumno encontrado si existe.
     */
    Optional<Alumno> findByEmailIgnoreCase(String email);
}

