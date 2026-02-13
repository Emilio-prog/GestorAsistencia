package com.dam.gestorasistencia.controller;

import com.dam.gestorasistencia.model.Usuario;
import com.dam.gestorasistencia.repository.UsuarioRepository;
import com.dam.gestorasistencia.view.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype") // Importante: Crea una instancia nueva cada vez que entramos
public class RegisterController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @FXML private TextField txtNombre;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cbRol;

    @FXML
    public void initialize() {
        cbRol.getItems().addAll("PROFESOR", "ADMIN");
        cbRol.setValue("PROFESOR");
    }

    @FXML
    public void onRegistrar() {
        if (txtNombre.getText().isEmpty() || txtEmail.getText().isEmpty() || txtPassword.getText().isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        // Comprobar si ya existe el email
        if (usuarioRepository.findByEmail(txtEmail.getText()).isPresent()) {
            mostrarAlerta("Error", "El email ya está registrado.");
            return;
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(txtNombre.getText());
        nuevoUsuario.setEmail(txtEmail.getText());
        nuevoUsuario.setPassword(txtPassword.getText());
        nuevoUsuario.setRol(cbRol.getValue());

        usuarioRepository.save(nuevoUsuario);

        mostrarAlerta("Éxito", "Usuario registrado correctamente.");
        onVolver();
    }

    @FXML
    public void onVolver() {
        SceneManager.switchScene("main_menu");
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}