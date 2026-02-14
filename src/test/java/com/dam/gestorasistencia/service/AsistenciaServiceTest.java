package com.dam.gestorasistencia.service;

import com.dam.gestorasistencia.model.EstadoAsistencia;
import com.dam.gestorasistencia.model.RegistroAsistencia;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Suite unitaria del algoritmo de riesgo de asistencia")
class AsistenciaServiceTest {

    private final AsistenciaService asistenciaService = new AsistenciaService();

    @Test
    @DisplayName("Devuelve 0.0 cuando la lista es null")
    void calcularPorcentaje_listaNull_devuelveCero() {
        double resultado = asistenciaService.calcularPorcentajeFaltas(null);

        assertEquals(0.0, resultado, 1e-9);
        assertFalse(asistenciaService.esAlumnoEnRiesgo(null));
    }

    @Test
    @DisplayName("Devuelve 0.0 cuando la lista está vacía")
    void calcularPorcentaje_listaVacia_devuelveCero() {
        List<RegistroAsistencia> registros = new ArrayList<>();

        double resultado = asistenciaService.calcularPorcentajeFaltas(registros);

        assertEquals(0.0, resultado, 1e-9);
        assertFalse(asistenciaService.esAlumnoEnRiesgo(registros));
    }

    @Test
    @DisplayName("Cuenta solo faltas injustificadas en el porcentaje")
    void calcularPorcentaje_soloCuentaEstadoFalta() {
        List<RegistroAsistencia> registros = List.of(
                crearRegistro(EstadoAsistencia.FALTA),
                crearRegistro(EstadoAsistencia.PRESENTE),
                crearRegistro(EstadoAsistencia.RETRASO),
                crearRegistro(EstadoAsistencia.JUSTIFICADA),
                crearRegistro(EstadoAsistencia.FALTA)
        );

        double resultado = asistenciaService.calcularPorcentajeFaltas(registros);

        assertEquals(0.4, resultado, 1e-9);
        assertTrue(asistenciaService.esAlumnoEnRiesgo(registros));
    }

    @ParameterizedTest(name = "{0} faltas de {1} clases => {2} y riesgo {3}")
    @CsvSource({
            "0, 5, 0.0, false",
            "1, 5, 0.2, true",
            "2, 10, 0.2, true",
            "1, 10, 0.1, false",
            "5, 5, 1.0, true"
    })
    @DisplayName("Evalúa correctamente umbrales y casos representativos")
    void evaluarUmbralRiesgo_variosEscenarios(int faltas, int totalClases, double esperado, boolean riesgoEsperado) {
        List<RegistroAsistencia> registros = crearRegistros(faltas, totalClases);

        double resultado = asistenciaService.calcularPorcentajeFaltas(registros);

        assertEquals(esperado, resultado, 1e-9);
        assertEquals(riesgoEsperado, asistenciaService.esAlumnoEnRiesgo(registros));
    }

    private List<RegistroAsistencia> crearRegistros(int faltas, int totalClases) {
        List<RegistroAsistencia> registros = new ArrayList<>();
        for (int i = 0; i < totalClases; i++) {
            EstadoAsistencia estado = i < faltas ? EstadoAsistencia.FALTA : EstadoAsistencia.PRESENTE;
            registros.add(crearRegistro(estado));
        }
        return registros;
    }

    private RegistroAsistencia crearRegistro(EstadoAsistencia estado) {
        RegistroAsistencia registro = new RegistroAsistencia();
        registro.setEstado(estado);
        return registro;
    }
}
