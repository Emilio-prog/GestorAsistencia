package com.dam.gestorasistencia;

import com.dam.gestorasistencia.view.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class GestorAsistenciaApplication extends Application {

    private ConfigurableApplicationContext springContext;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        // ESTA LÍNEA ES LA CLAVE: Arranca Spring Boot y la conexión a BD antes de mostrar la ventana
        springContext = SpringApplication.run(GestorAsistenciaApplication.class);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Configuramos el SceneManager con el Stage y el contexto de Spring
        SceneManager.setInitialStage(stage, springContext);

        stage.setTitle("Gestor de Asistencia - IES");

        // Intentamos cargar la pantalla de Login (que crearemos en el siguiente paso)
        // Por ahora esto dará error si ejecutamos, pero es correcto dejarlo listo.
        SceneManager.switchScene("login");
    }

    @Override
    public void stop() {
        // Cierra la conexión con Spring y la base de datos al cerrar la ventana
        springContext.close();
    }
}