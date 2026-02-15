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

/**
 * Controla el registro de nuevos usuarios desde la interfaz de administración.
 * Valida datos básicos, evita correos duplicados y guarda el nuevo usuario.
 *
 * @author Equipo de Desarrollo
 */
@Component
@Scope("prototype")
public class RegisterController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @FXML private TextField txtNombre;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private ComboBox<String> cbRol;

    /**
     * Carga los roles disponibles y define un rol por defecto al abrir la pantalla.
     */
    @FXML
    public void initialize() {
        cbRol.getItems().addAll("PROFESOR", "ADMIN");
        cbRol.setValue("PROFESOR");
    }

    /**
     * Valida el formulario, crea un usuario nuevo y lo guarda en la base de datos.
     * Si falta información o el correo ya existe, muestra un mensaje de error.
     */
    @FXML
    public void onRegistrar() {
        if (txtNombre.getText().isEmpty() || txtEmail.getText().isEmpty() || txtPassword.getText().isEmpty() || txtConfirmPassword.getText().isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        if (!txtPassword.getText().equals(txtConfirmPassword.getText())) {
            mostrarAlerta("Error", "Las contraseñas no coinciden.");
            return;
        }

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

    /**
     * Regresa al menú principal después del proceso de registro.
     */
    @FXML
    public void onVolver() {
        SceneManager.switchScene("main_menu");
    }

    /**
     * Muestra una alerta informativa para feedback de validación o éxito.
     *
     * @param titulo título principal de la alerta.
     * @param mensaje descripción del resultado a mostrar al usuario.
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
