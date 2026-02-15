package com.dam.gestorasistencia.service;

import com.dam.gestorasistencia.model.EstadoAsistencia;
import com.dam.gestorasistencia.model.RegistroAsistencia;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Ofrece lógica de negocio para analizar el estado de asistencia del alumnado.
 *
 * @author Equipo de Desarrollo
 */
@Service
public class AsistenciaService {

    private static final double LIMITE_FALTAS = 0.20; // 20% de faltas permite anular matrícula

    /**
     * Calcula el porcentaje de faltas injustificadas sobre el total de clases registradas.
     *
     * @param registros lista de registros de asistencia del alumno.
     * @return porcentaje de faltas entre 0.0 y 1.0; devuelve 0.0 si la lista es nula o vacía.
     */
    public double calcularPorcentajeFaltas(List<RegistroAsistencia> registros) {
        if (registros == null || registros.isEmpty()) {
            return 0.0;
        }

        long totalClases = registros.size();
        long faltas = registros.stream()
                .filter(r -> r.getEstado() == EstadoAsistencia.FALTA)
                .count();

        return (double) faltas / totalClases;
    }

    /**
     * Indica si un alumno está en riesgo según su porcentaje de faltas injustificadas.
     *
     * @param registros lista de registros de asistencia del alumno.
     * @return {@code true} si el porcentaje de faltas es igual o mayor al límite permitido; {@code false} en caso contrario.
     */
    public boolean esAlumnoEnRiesgo(List<RegistroAsistencia> registros) {
        return calcularPorcentajeFaltas(registros) >= LIMITE_FALTAS;
    }
}
