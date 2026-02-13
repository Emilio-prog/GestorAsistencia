package com.dam.gestorasistencia.service;

import com.dam.gestorasistencia.model.EstadoAsistencia;
import com.dam.gestorasistencia.model.RegistroAsistencia;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class) // Habilita el soporte para Mockito si lo necesitaramos
class AsistenciaServiceTest {

    @InjectMocks
    private AsistenciaService asistenciaService; // La clase que vamos a probar

    @Test
    @DisplayName("Debe devolver 0.0 si la lista de registros está vacía (Caso Límite)")
    void testCalcularPorcentajeListaVacia() {
        List<RegistroAsistencia> listaVacia = new ArrayList<>();
        double resultado = asistenciaService.calcularPorcentajeFaltas(listaVacia);
        assertEquals(0.0, resultado, "El porcentaje debería ser 0 para lista vacía");
    }

    @Test
    @DisplayName("Debe calcular correctamente el 50% de faltas")
    void testCalcularPorcentajeMitadFaltas() {
        // Preparamos datos de prueba (Mock Data)
        List<RegistroAsistencia> registros = new ArrayList<>();
        registros.add(crearRegistro(EstadoAsistencia.FALTA));
        registros.add(crearRegistro(EstadoAsistencia.PRESENTE));

        // Ejecutamos la lógica
        double resultado = asistenciaService.calcularPorcentajeFaltas(registros);

        // Verificamos (Assert)
        assertEquals(0.5, resultado, "Debería ser 0.5 (50%)");
        assertTrue(asistenciaService.esAlumnoEnRiesgo(registros), "Con 50% debería estar en riesgo");
    }

    @Test
    @DisplayName("No debe contar Retrasos o Justificadas como Faltas Injustificadas")
    void testSoloCuentaFaltas() {
        List<RegistroAsistencia> registros = new ArrayList<>();
        registros.add(crearRegistro(EstadoAsistencia.RETRASO));
        registros.add(crearRegistro(EstadoAsistencia.JUSTIFICADA));
        registros.add(crearRegistro(EstadoAsistencia.PRESENTE));

        double resultado = asistenciaService.calcularPorcentajeFaltas(registros);

        assertEquals(0.0, resultado, "Retrasos y Justificadas no deben sumar al porcentaje de faltas");
        assertFalse(asistenciaService.esAlumnoEnRiesgo(registros), "No debería estar en riesgo");
    }

    // Método auxiliar para crear registros rápido
    private RegistroAsistencia crearRegistro(EstadoAsistencia estado) {
        RegistroAsistencia r = new RegistroAsistencia();
        r.setEstado(estado);
        return r;
    }
}