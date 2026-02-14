package com.dam.gestorasistencia.service;

import com.dam.gestorasistencia.model.EstadoAsistencia;
import com.dam.gestorasistencia.model.RegistroAsistencia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    void calcularPorcentajeFaltas_devuelveCeroCuandoListaEsNull() {
        double resultado = asistenciaService.calcularPorcentajeFaltas(null);

        assertEquals(0.0, resultado, 1e-9);
    }

    @Test
    void calcularPorcentajeFaltas_devuelveCeroCuandoListaEstaVacia() {
        double resultado = asistenciaService.calcularPorcentajeFaltas(List.of());

        assertEquals(0.0, resultado, 1e-9);
    }

    @Test
    void calcularPorcentajeFaltas_devuelveCeroCuandoNoHayFaltas() {
        List<RegistroAsistencia> registros = List.of(
                registroConEstado(EstadoAsistencia.PRESENTE),
                registroConEstado(EstadoAsistencia.RETRASO),
                registroConEstado(EstadoAsistencia.JUSTIFICADA)
        );

        double resultado = asistenciaService.calcularPorcentajeFaltas(registros);

        assertEquals(0.0, resultado, 1e-9);
    }

    @Test
    void calcularPorcentajeFaltas_devuelveUnoCuandoTodasSonFaltas() {
        List<RegistroAsistencia> registros = List.of(
                registroConEstado(EstadoAsistencia.FALTA),
                registroConEstado(EstadoAsistencia.FALTA),
                registroConEstado(EstadoAsistencia.FALTA)
        );

        double resultado = asistenciaService.calcularPorcentajeFaltas(registros);

        assertEquals(1.0, resultado, 1e-9);
    }

    @Test
    void calcularPorcentajeFaltas_calculaProporcionCorrectaEnCasoMixto() {
        List<RegistroAsistencia> registros = List.of(
                registroConEstado(EstadoAsistencia.FALTA),
                registroConEstado(EstadoAsistencia.PRESENTE),
                registroConEstado(EstadoAsistencia.FALTA),
                registroConEstado(EstadoAsistencia.RETRASO),
                registroConEstado(EstadoAsistencia.JUSTIFICADA)
        );

        double resultado = asistenciaService.calcularPorcentajeFaltas(registros);

        assertEquals(0.4, resultado, 1e-9);
    }

    @Test
    void esAlumnoEnRiesgo_devuelveTrueCuandoAlcanzaElLimiteDelVeintePorCiento() {
        List<RegistroAsistencia> registros = List.of(
                registroConEstado(EstadoAsistencia.FALTA),
                registroConEstado(EstadoAsistencia.PRESENTE),
                registroConEstado(EstadoAsistencia.PRESENTE),
                registroConEstado(EstadoAsistencia.PRESENTE),
                registroConEstado(EstadoAsistencia.PRESENTE)
        );

        boolean resultado = asistenciaService.esAlumnoEnRiesgo(registros);

        assertTrue(resultado);
    }

    @Test
    void esAlumnoEnRiesgo_devuelveFalseCuandoEstaPorDebajoDelLimite() {
        List<RegistroAsistencia> registros = List.of(
                registroConEstado(EstadoAsistencia.FALTA),
                registroConEstado(EstadoAsistencia.PRESENTE),
                registroConEstado(EstadoAsistencia.PRESENTE),
                registroConEstado(EstadoAsistencia.PRESENTE),
                registroConEstado(EstadoAsistencia.PRESENTE),
                registroConEstado(EstadoAsistencia.PRESENTE)
        );

        boolean resultado = asistenciaService.esAlumnoEnRiesgo(registros);

        assertFalse(resultado);
    }

    private RegistroAsistencia registroConEstado(EstadoAsistencia estado) {
        RegistroAsistencia registro = new RegistroAsistencia();
        registro.setEstado(estado);
        return registro;
    }
}
