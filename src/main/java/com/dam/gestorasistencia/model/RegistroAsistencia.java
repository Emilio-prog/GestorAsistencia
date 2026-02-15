package com.dam.gestorasistencia.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

/**
 * Guarda el registro diario de asistencia de un alumno en una asignatura.
 * Incluye fecha, estado y datos básicos para consultar informes rápidamente.
 *
 * @author Equipo de Desarrollo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "registros_asistencia")
public class RegistroAsistencia {
    @Id
    private String id;

    private LocalDate fecha;

    // Guardamos el ID del alumno.
    // Podríamos usar @DBRef, pero guardar el ID manualmente es más performante en NoSQL simple.
    private String idAlumno;

    // Opcional: Nombre del alumno duplicado para evitar muchas consultas (Desnormalización típica NoSQL)
    private String nombreAlumno;

    private String idAsignatura;

    private EstadoAsistencia estado; // Usamos el Enum creado antes

    private String observaciones;
}
