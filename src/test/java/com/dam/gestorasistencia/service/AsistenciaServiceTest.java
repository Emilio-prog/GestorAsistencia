package com.dam.gestorasistencia.service;

import com.dam.gestorasistencia.model.EstadoAsistencia;
import com.dam.gestorasistencia.model.RegistroAsistencia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AsistenciaServiceTest {

    private AsistenciaService asistenciaService;

    @BeforeEach
    void setUp() {
        asistenciaService = new AsistenciaService();
    }

    @Test
    @DisplayName("calcularPorcentajeFaltas devuelve 0.0 cuando la lista es null")
    void calcularPorcentajeFaltasConListaNullDevuelveCero() {
        double porcentaje = asistenciaService.calcularPorcentajeFaltas(null);

        assertEquals(0.0, porcentaje);
    }

    @Test
    @DisplayName("calcularPorcentajeFaltas devuelve 0.0 cuando la lista está vacía")
    void calcularPorcentajeFaltasConListaVaciaDevuelveCero() {
        double porcentaje = asistenciaService.calcularPorcentajeFaltas(List.of());

        assertEquals(0.0, porcentaje);
    }

    @Test
    @DisplayName("calcularPorcentajeFaltas devuelve 0.0 cuando no hay faltas")
    void calcularPorcentajeFaltasSinFaltasDevuelveCero() {
        List<RegistroAsistencia> registros = List.of(
                registro(EstadoAsistencia.ASISTENCIA),
                registro(EstadoAsistencia.JUSTIFICADA),
                registro(EstadoAsistencia.ASISTENCIA)
        );

        double porcentaje = asistenciaService.calcularPorcentajeFaltas(registros);

        assertEquals(0.0, porcentaje);
    }

    @Test
    @DisplayName("calcularPorcentajeFaltas devuelve 1.0 cuando todas son faltas")
    void calcularPorcentajeFaltasConTodasFaltasDevuelveUno() {
        List<RegistroAsistencia> registros = List.of(
                registro(EstadoAsistencia.FALTA),
                registro(EstadoAsistencia.FALTA),
                registro(EstadoAsistencia.FALTA)
        );

        double porcentaje = asistenciaService.calcularPorcentajeFaltas(registros);

        assertEquals(1.0, porcentaje);
    }

    @Test
    @DisplayName("calcularPorcentajeFaltas calcula correctamente un valor intermedio")
    void calcularPorcentajeFaltasConValorIntermedio() {
        List<RegistroAsistencia> registros = List.of(
                registro(EstadoAsistencia.FALTA),
                registro(EstadoAsistencia.FALTA),
                registro(EstadoAsistencia.ASISTENCIA),
                registro(EstadoAsistencia.JUSTIFICADA),
                registro(EstadoAsistencia.ASISTENCIA)
        );

        double porcentaje = asistenciaService.calcularPorcentajeFaltas(registros);

        assertEquals(0.4, porcentaje, 1e-9);
    }

    @Test
    @DisplayName("esAlumnoEnRiesgo devuelve false por debajo del límite del 20%")
    void esAlumnoEnRiesgoPorDebajoDelLimite() {
        List<RegistroAsistencia> registros = List.of(
                registro(EstadoAsistencia.FALTA),
                registro(EstadoAsistencia.ASISTENCIA),
                registro(EstadoAsistencia.ASISTENCIA),
                registro(EstadoAsistencia.ASISTENCIA),
                registro(EstadoAsistencia.ASISTENCIA),
                registro(EstadoAsistencia.ASISTENCIA)
        );

        assertFalse(asistenciaService.esAlumnoEnRiesgo(registros));
    }

    @Test
    @DisplayName("esAlumnoEnRiesgo devuelve true cuando el porcentaje es exactamente 20%")
    void esAlumnoEnRiesgoEnElLimite() {
        List<RegistroAsistencia> registros = List.of(
                registro(EstadoAsistencia.FALTA),
                registro(EstadoAsistencia.ASISTENCIA),
                registro(EstadoAsistencia.ASISTENCIA),
                registro(EstadoAsistencia.ASISTENCIA),
                registro(EstadoAsistencia.ASISTENCIA)
        );

        assertTrue(asistenciaService.esAlumnoEnRiesgo(registros));
    }

    @Test
    @DisplayName("esAlumnoEnRiesgo devuelve true por encima del límite del 20%")
    void esAlumnoEnRiesgoPorEncimaDelLimite() {
        List<RegistroAsistencia> registros = List.of(
                registro(EstadoAsistencia.FALTA),
                registro(EstadoAsistencia.FALTA),
                registro(EstadoAsistencia.ASISTENCIA),
                registro(EstadoAsistencia.ASISTENCIA),
                registro(EstadoAsistencia.ASISTENCIA)
        );

        assertTrue(asistenciaService.esAlumnoEnRiesgo(registros));
    }

    private RegistroAsistencia registro(EstadoAsistencia estado) {
        return new RegistroAsistencia(
                null,
                LocalDate.now(),
                "alumno-1",
                "Alumno Test",
                "asignatura-1",
                estado,
                null
        );
    }
}
