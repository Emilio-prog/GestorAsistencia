package com.dam.gestorasistencia.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "alumnos")
public class Alumno {
    @Id
    private String id;

    private String nombre;
    private String apellidos;
    private String email;
    private String grupo; // Ejemplo: "2DAM", "1DAW" - Crucial para el filtro
    private String idAsignatura; // Asignatura principal del alumno
}
