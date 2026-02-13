package com.dam.gestorasistencia.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;

public class AlumnoAsistenciaRow {
    // Usamos el objeto Alumno real
    @Getter
    private final Alumno alumno;

    // Propiedad observable para el ComboBox de la tabla
    private final ObjectProperty<EstadoAsistencia> estado;

    public AlumnoAsistenciaRow(Alumno alumno, EstadoAsistencia estadoInicial) {
        this.alumno = alumno;
        this.estado = new SimpleObjectProperty<>(estadoInicial);
    }

    public ObjectProperty<EstadoAsistencia> estadoProperty() {
        return estado;
    }

    public EstadoAsistencia getEstado() {
        return estado.get();
    }

    public void setEstado(EstadoAsistencia estado) {
        this.estado.set(estado);
    }
}