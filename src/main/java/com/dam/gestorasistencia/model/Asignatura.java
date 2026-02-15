package com.dam.gestorasistencia.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Representa una asignatura que se imparte en un curso concreto.
 * Se usa para filtrar asistencia y organizar alumnos por materia.
 *
 * @author Equipo de Desarrollo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "asignaturas")
public class Asignatura {
    @Id
    private String id;

    private String nombre;
    private String curso; // Ejemplo: "2025-2026"
}
