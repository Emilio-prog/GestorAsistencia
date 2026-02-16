package com.dam.gestorasistencia.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import java.io.IOException;
import java.util.Objects;

/**
 * Gestiona la navegación entre pantallas JavaFX y la carga de estilos globales.
 * Integra el ciclo de vistas con el contenedor de Spring para inyectar controladores.
 *
 * @author Equipo de Desarrollo
 */
public class SceneManager {

    private static Stage stage;
    private static ApplicationContext springContext;

    /**
     * Guarda el escenario principal y el contexto de Spring para futuras navegaciones.
     *
     * @param stage escenario principal de la aplicación.
     * @param context contexto de Spring usado para crear controladores.
     */
    public static void setInitialStage(Stage stage, ApplicationContext context) {
        SceneManager.stage = stage;
        SceneManager.springContext = context;
    }

    /**
     * Cambia la vista actual cargando un archivo FXML desde la carpeta de vistas.
     *
     * @param fxml nombre del archivo FXML sin extensión.
     * @throws RuntimeException si ocurre un error al cargar la vista solicitada.
     */
    public static void switchScene(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/views/" + fxml + ".fxml"));
            loader.setControllerFactory(springContext::getBean);

            Parent root = loader.load();

            if (stage.getScene() == null) {
                Scene scene = new Scene(root);
                cargarEstilos(scene);
                stage.setScene(scene);
            } else {
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

    /**
     * Carga la hoja de estilos principal si está disponible en recursos.
     *
     * @param scene escena a la que se le aplicarán los estilos.
     */
    private static void cargarEstilos(Scene scene) {
        String cssPath = "/styles/main.css";
        var resource = SceneManager.class.getResource(cssPath);
        if (resource != null) {
            String cssUri = resource.toExternalForm();
            // Fix para Spring Boot Fat JAR: convertir rutas jar:nested a rutas legibles
            if (cssUri.contains("!") && !cssUri.startsWith("jar:file:")) {
                try (var is = SceneManager.class.getResourceAsStream(cssPath)) {
                    if (is != null) {
                        var tempFile = java.io.File.createTempFile("main", ".css");
                        tempFile.deleteOnExit();
                        java.nio.file.Files.copy(is, tempFile.toPath(),
                                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        cssUri = tempFile.toURI().toString();
                    }
                } catch (Exception e) {
                    System.err.println("ADVERTENCIA: Error al extraer CSS temporal: " + e.getMessage());
                }
            }
            scene.getStylesheets().clear();
            scene.getStylesheets().add(cssUri);
        } else {
            System.err.println("ADVERTENCIA: No se encontró el archivo CSS en " + cssPath);
        }
    }
}
