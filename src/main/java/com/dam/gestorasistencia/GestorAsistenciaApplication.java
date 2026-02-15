package com.dam.gestorasistencia;

import com.dam.gestorasistencia.view.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Clase principal de la aplicación.
 * Arranca Spring Boot y JavaFX para mostrar la interfaz de gestión de asistencia.
 *
 * @author Equipo de Desarrollo
 */
@SpringBootApplication
public class GestorAsistenciaApplication extends Application {

    private ConfigurableApplicationContext springContext;

    /**
     * Punto de entrada estándar para iniciar la aplicación JavaFX.
     *
     * @param args argumentos de arranque de la aplicación.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Inicializa el contexto de Spring antes de abrir la ventana principal.
     */
    @Override
    public void init() {
        springContext = SpringApplication.run(GestorAsistenciaApplication.class);
    }

    /**
     * Configura la ventana principal y carga la vista de inicio de sesión.
     *
     * @param stage escenario principal de JavaFX donde se mostrarán las vistas.
     * @throws Exception si ocurre un error al cargar la primera escena.
     */
    @Override
    public void start(Stage stage) throws Exception {
        SceneManager.setInitialStage(stage, springContext);

        stage.setTitle("Gestor de Asistencia - IES");
        SceneManager.switchScene("login");
    }

    /**
     * Cierra el contexto de Spring al finalizar la aplicación.
     */
    @Override
    public void stop() {
        springContext.close();
    }
}
