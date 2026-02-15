package com.dam.gestorasistencia.controller;

import com.dam.gestorasistencia.model.UserSession;
import com.dam.gestorasistencia.model.Usuario;
import com.dam.gestorasistencia.repository.UsuarioRepository;
import com.dam.gestorasistencia.view.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtPasswordVisible;

    @FXML
    private FontIcon iconTogglePassword;

    private boolean isPasswordVisible = false;

    @FXML
    public void initialize() {
        // Efecto focus-within: resaltar el input-group cuando el campo tiene foco
        configurarFocusGroup(txtEmail);
        configurarFocusGroup(txtPassword);
        configurarFocusGroup(txtPasswordVisible);

        txtPasswordVisible.textProperty().bindBidirectional(txtPassword.textProperty());
    }

    private void configurarFocusGroup(javafx.scene.control.Control campo) {
        campo.focusedProperty().addListener((obs, oldVal, focused) -> {
            HBox grupo = buscarInputGroup(campo);
            if (grupo != null) {
                if (focused) {
                    grupo.getStyleClass().add("input-group-focused");
                } else {
                    grupo.getStyleClass().remove("input-group-focused");
                }
            }
        });
    }

    private HBox buscarInputGroup(Node nodo) {
        Node actual = nodo;
        while (actual != null && !(actual instanceof HBox)) {
            actual = actual.getParent();
        }
        return (HBox) actual;
    }

    @FXML
    public void onTogglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;

        txtPassword.setVisible(!isPasswordVisible);
        txtPassword.setManaged(!isPasswordVisible);

        txtPasswordVisible.setVisible(isPasswordVisible);
        txtPasswordVisible.setManaged(isPasswordVisible);

        iconTogglePassword.setIconLiteral(isPasswordVisible ? "mdmz-visibility" : "mdmz-visibility_off");

        if (isPasswordVisible) {
            txtPasswordVisible.requestFocus();
            txtPasswordVisible.positionCaret(txtPasswordVisible.getText().length());
        } else {
            txtPassword.requestFocus();
            txtPassword.positionCaret(txtPassword.getText().length());
        }
    }

    @FXML
    public void onLogin() {
        String email = txtEmail.getText();
        String pass = txtPassword.getText();

        // 1. Validación de campos vacíos
        if (email.isEmpty() || pass.isEmpty()) {
            mostrarAlerta("Error", "Por favor, rellena todos los campos.");
            return;
        }

        // 2. Consulta a MongoDB
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        // 3. Verificación de contraseña
        if (usuario != null && usuario.getPassword().equals(pass)) {
            // --- PASO 8: GUARDAR SESIÓN ---
            UserSession.getInstance().logIn(usuario);

            System.out.println("Login exitoso: " + usuario.getNombre());

            // Navegar al menú principal
            SceneManager.switchScene("main_menu");

        } else {
            mostrarAlerta("Error", "Credenciales incorrectas.");
        }
    }

    @FXML
    public void onForgotPassword() {
        SceneManager.switchScene("forgot_password");
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
