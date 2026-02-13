package com.dam.gestorasistencia.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import java.io.IOException;
import java.util.Objects;

public class SceneManager {

    private static Stage stage;
    private static ApplicationContext springContext;

    public static void setInitialStage(Stage stage, ApplicationContext context) {
        SceneManager.stage = stage;
        SceneManager.springContext = context;
    }

    public static void switchScene(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/views/" + fxml + ".fxml"));
            loader.setControllerFactory(springContext::getBean);

            Parent root = loader.load();

            if (stage.getScene() == null) {
                Scene scene = new Scene(root);
                // --- CAMBIO: CARGAR CSS ---
                cargarEstilos(scene);
                stage.setScene(scene);
            } else {
                // Si cambiamos el root, nos aseguramos de que la escena mantenga los estilos
                stage.getScene().setRoot(root);
                cargarEstilos(stage.getScene());
            }

            stage.sizeToScene();
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al cargar la vista: " + fxml, e);
        }
    }

    // Método auxiliar para evitar errores si el CSS no existe aún
    private static void cargarEstilos(Scene scene) {
        String cssPath = "/styles/main.css";
        if (SceneManager.class.getResource(cssPath) != null) {
            scene.getStylesheets().clear();
            scene.getStylesheets().add(
                    Objects.requireNonNull(SceneManager.class.getResource(cssPath)).toExternalForm()
            );
        } else {
            System.err.println("ADVERTENCIA: No se encontró el archivo CSS en " + cssPath);
        }
    }
}