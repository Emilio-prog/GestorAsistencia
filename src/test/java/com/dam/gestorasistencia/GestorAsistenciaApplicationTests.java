package com.dam.gestorasistencia;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Prueba básica de la clase principal de la aplicación.
 * No carga el contexto de Spring para evitar la dependencia de MongoDB en tiempo de compilación.
 *
 * @author Equipo de Desarrollo
 */
class GestorAsistenciaApplicationTests {

    /**
     * Verifica que la clase AppLauncher se puede instanciar sin errores.
     */
    @Test
    void appLauncherSeInstanciaCorrectamente() {
        assertDoesNotThrow(() -> {
            AppLauncher launcher = new AppLauncher();
        });
    }
}
