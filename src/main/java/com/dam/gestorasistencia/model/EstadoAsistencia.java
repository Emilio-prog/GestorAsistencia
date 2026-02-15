package com.dam.gestorasistencia.model;

/**
 * Define los posibles estados de asistencia que puede tener un alumno en una clase.
 *
 * @author Equipo de Desarrollo
 */
public enum EstadoAsistencia {
    /** Alumno presente en clase. */
    PRESENTE,
    /** Alumno ausente sin justificación. */
    FALTA,
    /** Alumno que llegó tarde. */
    RETRASO,
    /** Alumno ausente con justificación registrada. */
    JUSTIFICADA
}
