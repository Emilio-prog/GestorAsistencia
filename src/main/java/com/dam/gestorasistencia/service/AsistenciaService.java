package com.dam.gestorasistencia.service;

import com.dam.gestorasistencia.model.EstadoAsistencia;
import com.dam.gestorasistencia.model.RegistroAsistencia;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AsistenciaService {

    private static final double LIMITE_FALTAS = 0.20; // 20% de faltas permite anular matrícula

    /**
     * Calcula el porcentaje de faltas injustificadas de un alumno.
     * Casos límite a probar: Lista vacía, 0 faltas, todas faltas.
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
     * Determina si un alumno está en riesgo de perder la evaluación continua.
     */
    public boolean esAlumnoEnRiesgo(List<RegistroAsistencia> registros) {
        return calcularPorcentajeFaltas(registros) >= LIMITE_FALTAS;
    }
}