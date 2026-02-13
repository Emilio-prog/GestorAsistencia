package com.dam.gestorasistencia.repository;

import com.dam.gestorasistencia.model.Alumno;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlumnoRepository extends MongoRepository<Alumno, String> {

    // Devuelve todos los alumnos que pertenezcan a un grupo (ej: "2DAM")
    List<Alumno> findByGrupo(String grupo);
}
