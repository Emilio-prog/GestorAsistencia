package com.dam.gestorasistencia.repository;

import com.dam.gestorasistencia.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para consultar y mantener usuarios del sistema.
 * Se usa en login, administración y recuperación de contraseña.
 *
 * @author Equipo de Desarrollo
 */
@Repository
public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    /**
     * Busca un usuario por su correo electrónico.
     *
     * @param email correo electrónico del usuario a localizar.
     * @return {@code Optional} con el usuario si existe; {@code Optional.empty()} si no existe.
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Busca un usuario por su correo ignorando mayúsculas/minúsculas.
     *
     * @param email correo electrónico del usuario a localizar.
     * @return {@code Optional} con el usuario si existe.
     */
    Optional<Usuario> findByEmailIgnoreCase(String email);
}
