package com.dam.gestorasistencia.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Representa a un usuario que puede acceder a la aplicación.
 * Guarda datos de autenticación y el rol para controlar permisos.
 *
 * @author Equipo de Desarrollo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "usuarios") // Nombre de la colección en MongoDB Atlas
public class Usuario {
    @Id
    private String id;

    private String email;      // Usaremos el email como username
    private String password;   // En un caso real iría encriptada
    private String nombre;
    private String rol;        // "ADMIN" o "PROFESOR"
}
