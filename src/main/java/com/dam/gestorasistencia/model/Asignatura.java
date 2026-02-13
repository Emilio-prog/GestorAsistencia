package com.dam.gestorasistencia.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
