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

/**
 * Pruebas unitarias del servicio de asistencia.
 * Verifica cálculos de porcentaje de faltas y detección de riesgo académico.
 *
 * @author Equipo de Desarrollo
 */
class AsistenciaServiceTest {

    private AsistenciaService asistenciaService;

    /**
     * Prepara una instancia limpia del servicio antes de cada prueba.
     */
    @BeforeEach
    void setUp() {
        asistenciaService = new AsistenciaService();
    }

    /**
     * Comprueba que el porcentaje de faltas sea cero cuando la entrada es nula.
     */
    @Test
    @DisplayName("calcularPorcentajeFaltas devuelve 0.0 cuando la lista es null")
    void calcularPorcentajeFaltasConListaNullDevuelveCero() {
        double porcentaje = asistenciaService.calcularPorcentajeFaltas(null);

        assertEquals(0.0, porcentaje);
    }

    /**
     * Comprueba que el porcentaje de faltas sea cero cuando no hay registros.
     */
    @Test
    @DisplayName("calcularPorcentajeFaltas devuelve 0.0 cuando la lista está vacía")
    void calcularPorcentajeFaltasConListaVaciaDevuelveCero() {
        double porcentaje = asistenciaService.calcularPorcentajeFaltas(List.of());

        assertEquals(0.0, porcentaje);
    }

    /**
     * Comprueba que el porcentaje de faltas sea cero cuando no existe ninguna falta injustificada.
     */
    @Test
    @DisplayName("calcularPorcentajeFaltas devuelve 0.0 cuando no hay faltas")
    void calcularPorcentajeFaltasSinFaltasDevuelveCero() {
        List<RegistroAsistencia> registros = List.of(
                registro(EstadoAsistencia.PRESENTE),
                registro(EstadoAsistencia.JUSTIFICADA),
                registro(EstadoAsistencia.PRESENTE)
        );

        double porcentaje = asistenciaService.calcularPorcentajeFaltas(registros);

        assertEquals(0.0, porcentaje);
    }

    /**
     * Comprueba que el porcentaje de faltas sea uno cuando todos los registros son falta.
     */
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

    /**
     * Comprueba el cálculo de un porcentaje intermedio con mezcla de estados.
     */
    @Test
    @DisplayName("calcularPorcentajeFaltas calcula correctamente un valor intermedio")
    void calcularPorcentajeFaltasConValorIntermedio() {
        List<RegistroAsistencia> registros = List.of(
                registro(EstadoAsistencia.FALTA),
                registro(EstadoAsistencia.FALTA),
                registro(EstadoAsistencia.PRESENTE),
                registro(EstadoAsistencia.JUSTIFICADA),
                registro(EstadoAsistencia.PRESENTE)
        );

        double porcentaje = asistenciaService.calcularPorcentajeFaltas(registros);

        assertEquals(0.4, porcentaje, 1e-9);
    }

    /**
     * Comprueba que no se marque riesgo cuando el porcentaje está por debajo del límite.
     */
    @Test
    @DisplayName("esAlumnoEnRiesgo devuelve false por debajo del límite del 20%")
    void esAlumnoEnRiesgoPorDebajoDelLimite() {
        List<RegistroAsistencia> registros = List.of(
                registro(EstadoAsistencia.FALTA),
                registro(EstadoAsistencia.PRESENTE),
                registro(EstadoAsistencia.PRESENTE),
                registro(EstadoAsistencia.PRESENTE),
                registro(EstadoAsistencia.PRESENTE),
                registro(EstadoAsistencia.PRESENTE)
        );

        assertFalse(asistenciaService.esAlumnoEnRiesgo(registros));
    }

    /**
     * Comprueba que se marque riesgo cuando el porcentaje coincide exactamente con el límite.
     */
    @Test
    @DisplayName("esAlumnoEnRiesgo devuelve true cuando el porcentaje es exactamente 20%")
    void esAlumnoEnRiesgoEnElLimite() {
        List<RegistroAsistencia> registros = List.of(
                registro(EstadoAsistencia.FALTA),
                registro(EstadoAsistencia.PRESENTE),
                registro(EstadoAsistencia.PRESENTE),
                registro(EstadoAsistencia.PRESENTE),
                registro(EstadoAsistencia.PRESENTE)
        );

        assertTrue(asistenciaService.esAlumnoEnRiesgo(registros));
    }

    /**
     * Comprueba que se marque riesgo cuando el porcentaje supera el límite.
     */
    @Test
    @DisplayName("esAlumnoEnRiesgo devuelve true por encima del límite del 20%")
    void esAlumnoEnRiesgoPorEncimaDelLimite() {
        List<RegistroAsistencia> registros = List.of(
                registro(EstadoAsistencia.FALTA),
                registro(EstadoAsistencia.FALTA),
                registro(EstadoAsistencia.PRESENTE),
                registro(EstadoAsistencia.PRESENTE),
                registro(EstadoAsistencia.PRESENTE)
        );

        assertTrue(asistenciaService.esAlumnoEnRiesgo(registros));
    }

    /**
     * Crea un registro base para simplificar la creación de datos de prueba.
     *
     * @param estado estado de asistencia que tendrá el registro de prueba.
     * @return registro de asistencia construido con datos de ejemplo para test.
     */
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
