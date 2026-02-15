package com.dam.gestorasistencia.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;

/**
 * Representa una fila de la tabla de asistencia en la interfaz.
 * Une un alumno con un estado de asistencia observable para edición en pantalla.
 *
 * @author Equipo de Desarrollo
 */
public class AlumnoAsistenciaRow {
    // Usamos el objeto Alumno real
    @Getter
    private final Alumno alumno;

    // Propiedad observable para el ComboBox de la tabla
    private final ObjectProperty<EstadoAsistencia> estado;

    /**
     * Crea una fila de asistencia con el alumno y su estado inicial.
     *
     * @param alumno alumno que se mostrará en la fila.
     * @param estadoInicial estado inicial de asistencia para ese alumno.
     */
    public AlumnoAsistenciaRow(Alumno alumno, EstadoAsistencia estadoInicial) {
        this.alumno = alumno;
        this.estado = new SimpleObjectProperty<>(estadoInicial);
    }

    /**
     * Expone la propiedad observable del estado para enlazarla con componentes JavaFX.
     *
     * @return propiedad observable del estado de asistencia.
     */
    public ObjectProperty<EstadoAsistencia> estadoProperty() {
        return estado;
    }

    /**
     * Devuelve el estado de asistencia actual de la fila.
     *
     * @return estado actual de asistencia.
     */
    public EstadoAsistencia getEstado() {
        return estado.get();
    }

    /**
     * Actualiza el estado de asistencia de la fila.
     *
     * @param estado nuevo estado de asistencia que se aplicará.
     */
    public void setEstado(EstadoAsistencia estado) {
        this.estado.set(estado);
    }
}
