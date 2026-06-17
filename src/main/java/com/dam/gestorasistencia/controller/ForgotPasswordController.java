package com.dam.gestorasistencia.controller;

import com.dam.gestorasistencia.service.PasswordRecoveryService;
import com.dam.gestorasistencia.view.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Controla el flujo de recuperación de contraseña desde la interfaz.
 * Valida el correo, solicita una contraseña temporal y guía al usuario para volver al login.
 *
 * @author Equipo de Desarrollo
 */
@Component
public class ForgotPasswordController {

    @Autowired
    private PasswordRecoveryService passwordRecoveryService;

    @FXML
    private TextField txtRecoveryEmail;

    /**
     * Configura los efectos visuales de foco al cargar la pantalla de recuperación.
     */
    @FXML
    public void initialize() {
        configurarFocusGroup(txtRecoveryEmail);
    }

    /**
     * Añade o quita una clase CSS cuando el campo recibe o pierde foco.
     *
     * @param campo control de entrada sobre el que se aplicará el efecto visual.
     */
    private void configurarFocusGroup(javafx.scene.control.Control campo) {
        campo.focusedProperty().addListener((obs, oldVal, focused) -> {
            if (campo.getParent() instanceof HBox grupo) {
                if (focused) {
                    grupo.getStyleClass().add("input-group-focused");
                } else {
                    grupo.getStyleClass().remove("input-group-focused");
                }
            }
        });
    }

    /**
     * Procesa la recuperación de contraseña para el correo indicado por el usuario.
     * Muestra mensajes de error o éxito según el resultado del servicio.
     */
    @FXML
    public void onSendInstructions() {
        String email = txtRecoveryEmail.getText() == null ? "" : txtRecoveryEmail.getText().trim();

        if (email.isEmpty()) {
            mostrarAlerta("Error", "Introduce un correo electrónico para recuperar la contraseña.");
            return;
        }

        String tempPassword = passwordRecoveryService.recoverPassword(email);
        if (tempPassword == null) {
            mostrarAlerta("No encontrado", "No existe ningún usuario registrado con ese correo.");
            return;
        }

        mostrarAlerta(
                "Instrucciones enviadas",
                "Se ha generado una contraseña temporal para " + email + ":\n\n" + tempPassword +
                        "\n\nInicia sesión con esa contraseña y cámbiala cuanto antes."
        );

        onBackToLogin();
    }

    /**
     * Vuelve a la pantalla de inicio de sesión.
     */
    @FXML
    public void onBackToLogin() {
        SceneManager.switchScene("inicio_sesion");
    }

    /**
     * Muestra una alerta informativa al usuario.
     *
     * @param titulo título principal de la alerta.
     * @param mensaje texto que explica el resultado de la operación.
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
