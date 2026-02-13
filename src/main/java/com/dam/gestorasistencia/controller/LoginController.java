package com.dam.gestorasistencia.controller;

import com.dam.gestorasistencia.model.UserSession;
import com.dam.gestorasistencia.model.Usuario;
import com.dam.gestorasistencia.repository.UsuarioRepository;
import com.dam.gestorasistencia.view.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}