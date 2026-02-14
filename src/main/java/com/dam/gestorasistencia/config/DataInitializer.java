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

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired
    private AsignaturaRepository asignaturaRepository;

    @Override
    public void run(String... args) {
        System.out.println("---- INICIANDO CARGA DE DATOS DE PRUEBA ----");

        if (usuarioRepository.count() == 0) {
            usuarioRepository.save(new Usuario(null, "admin@email.com", "admin123", "Administrador", "ADMIN"));
            usuarioRepository.save(new Usuario(null, "profe@email.com", "profe123", "Profesor Ejemplo", "PROFESOR"));
            System.out.println("-> Usuarios creados: admin@email.com y profe@email.com");
        } else {
            System.out.println("-> Usuarios ya existen. Saltando creación.");
        }

        Asignatura ad = null;
        Asignatura di = null;

        if (asignaturaRepository.count() == 0) {
            ad = asignaturaRepository.save(new Asignatura(null, "Acceso a Datos", "2025-2026", "DAM"));
            di = asignaturaRepository.save(new Asignatura(null, "Desarrollo de Interfaces", "2025-2026", "DAW"));
            System.out.println("-> Asignaturas de prueba creadas.");
        } else {
            System.out.println("-> Asignaturas ya existen. Saltando creación.");
        }

        if (alumnoRepository.count() == 0) {
            if (ad == null || di == null) {
                ad = asignaturaRepository.findAll().stream()
                        .filter(a -> "Acceso a Datos".equalsIgnoreCase(a.getNombre()))
                        .findFirst().orElse(null);
                di = asignaturaRepository.findAll().stream()
                        .filter(a -> "Desarrollo de Interfaces".equalsIgnoreCase(a.getNombre()))
                        .findFirst().orElse(null);
            }

            alumnoRepository.save(new Alumno(null, "Juan", "Pérez", "juan@email.com", "2DAM", ad != null ? ad.getId() : null));
            alumnoRepository.save(new Alumno(null, "Ana", "García", "ana@email.com", "2DAM", ad != null ? ad.getId() : null));
            alumnoRepository.save(new Alumno(null, "Luis", "Rodríguez", "luis@email.com", "1DAW", di != null ? di.getId() : null));
            System.out.println("-> Alumnos de prueba creados y vinculados a asignaturas.");
        } else {
            System.out.println("-> Alumnos ya existen. Saltando creación.");
        }

        System.out.println("---- CARGA DE DATOS COMPLETADA ----");
    }
}
