package com.dam.gestorasistencia.repository;

import com.dam.gestorasistencia.model.Asignatura;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para gestionar asignaturas en MongoDB.
 * Hereda operaciones básicas de alta, consulta, edición y borrado.
 *
 * @author Equipo de Desarrollo
 */
@Repository
public interface AsignaturaRepository extends MongoRepository<Asignatura, String> {
    // No necesitamos consultas especiales por ahora
}
