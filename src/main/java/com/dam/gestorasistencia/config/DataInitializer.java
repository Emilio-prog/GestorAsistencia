package com.dam.gestorasistencia.config;

import com.dam.gestorasistencia.model.Alumno;
import com.dam.gestorasistencia.model.Asignatura;
import com.dam.gestorasistencia.model.Usuario;
import com.dam.gestorasistencia.repository.AlumnoRepository;
import com.dam.gestorasistencia.repository.AsignaturaRepository;
import com.dam.gestorasistencia.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Carga datos de prueba al iniciar la aplicación cuando la base de datos está vacía.
 * Permite tener usuarios, alumnos y asignaturas listas para probar el sistema.
 *
 * @author Equipo de Desarrollo
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired
    private AsignaturaRepository asignaturaRepository;

    /**
     * Ejecuta la carga inicial de datos de prueba para usuarios, alumnos y asignaturas.
     * Solo crea registros cuando no existen datos previos en cada colección.
     *
     * @param args argumentos recibidos al arrancar la aplicación.
     * @throws Exception si ocurre un error inesperado durante la inicialización.
     */
    @Override
    public void run(String... args) throws Exception {
        System.out.println("---- INICIANDO CARGA DE DATOS DE PRUEBA ----");

        // 1. Crear Usuarios si no existen
        if (usuarioRepository.count() == 0) {
            usuarioRepository.save(new Usuario(null, "admin@email.com", "admin123", "Administrador", "ADMIN"));
            usuarioRepository.save(new Usuario(null, "profe@email.com", "profe123", "Profesor Ejemplo", "PROFESOR"));
            System.out.println("-> Usuarios creados: admin@email.com y profe@email.com");
        } else {
            System.out.println("-> Usuarios ya existen. Saltando creación.");
        }

        // 2. Crear Alumnos si no existen
        if (alumnoRepository.count() == 0) {
            alumnoRepository.save(new Alumno(null, "Juan", "Pérez", "juan@email.com", "2DAM", null));
            alumnoRepository.save(new Alumno(null, "Ana", "García", "ana@email.com", "2DAM", null));
            alumnoRepository.save(new Alumno(null, "Luis", "Rodríguez", "luis@email.com", "1DAW", null));
            System.out.println("-> Alumnos de prueba creados (2DAM y 1DAW).");
        } else {
            System.out.println("-> Alumnos ya existen. Saltando creación.");
        }

        // 3. Crear Asignaturas si no existen
        if (asignaturaRepository.count() == 0) {
            asignaturaRepository.save(new Asignatura(null, "Acceso a Datos", "2025-2026"));
            asignaturaRepository.save(new Asignatura(null, "Desarrollo de Interfaces", "2025-2026"));
            System.out.println("-> Asignaturas de prueba creadas.");
        }

        System.out.println("---- CARGA DE DATOS COMPLETADA ----");
    }
}
