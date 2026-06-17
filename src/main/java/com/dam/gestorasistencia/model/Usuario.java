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

    // Nuevos campos para la verificación del email (Nivel Junior)
    private boolean verificado;
    private String codigoVerificacion;

    // Getters y Setters manuales para entender cómo funcionan
    public boolean isVerificado() {
        return verificado;
    }

    public void setVerificado(boolean verificado) {
        this.verificado = verificado;
    }

    public String getCodigoVerificacion() {
        return codigoVerificacion;
    }

    public void setCodigoVerificacion(String codigoVerificacion) {
        this.codigoVerificacion = codigoVerificacion;
    }
}
