package com.dam.gestorasistencia.repository;

import com.dam.gestorasistencia.model.Alumno;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlumnoRepository extends MongoRepository<Alumno, String> {

    List<Alumno> findByGrupo(String grupo);

    List<Alumno> findByGrupoAndIdAsignatura(String grupo, String idAsignatura);

    long countByIdAsignatura(String idAsignatura);

    List<Alumno> findByIdAsignatura(String idAsignatura);
}
