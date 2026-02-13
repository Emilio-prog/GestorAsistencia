package com.dam.gestorasistencia.repository;

import com.dam.gestorasistencia.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    // Método mágico: Spring crea la consulta automáticamente basándose en el nombre
    Optional<Usuario> findByEmail(String email);
}
