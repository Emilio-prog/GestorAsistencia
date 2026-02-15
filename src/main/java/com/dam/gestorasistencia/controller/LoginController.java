package com.dam.gestorasistencia.controller;

import com.dam.gestorasistencia.model.UserSession;
import com.dam.gestorasistencia.model.Usuario;
import com.dam.gestorasistencia.repository.UsuarioRepository;
import com.dam.gestorasistencia.view.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Controla el acceso de usuarios y la navegación inicial de la aplicación.
 * También gestiona el comportamiento visual del formulario de inicio de sesión.
 *
 * @author Equipo de Desarrollo
 */
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

    /**
     * Prepara el formulario al cargar la vista.
     * Configura los efectos de foco y sincroniza los campos de contraseña visible y oculta.
     */
    @FXML
    public void initialize() {
        configurarFocusGroup(txtEmail);
        configurarFocusGroup(txtPassword);
        configurarFocusGroup(txtPasswordVisible);

        txtPasswordVisible.textProperty().bindBidirectional(txtPassword.textProperty());
    }

    /**
     * Activa o quita la clase de estilo del grupo visual cuando un campo gana o pierde foco.
     *
     * @param campo control visual al que se le aplicará el seguimiento de foco.
     */
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

    /**
     * Busca el contenedor de tipo HBox que envuelve al control recibido.
     *
     * @param nodo nodo desde el que empieza la búsqueda en la jerarquía de la interfaz.
     * @return el contenedor HBox encontrado, o {@code null} si no existe uno en sus padres.
     */
    private HBox buscarInputGroup(Node nodo) {
        Node actual = nodo;
        while (actual != null && !(actual instanceof HBox)) {
            actual = actual.getParent();
        }
        return (HBox) actual;
    }

    /**
     * Alterna entre mostrar y ocultar la contraseña escrita por el usuario.
     * Mantiene el cursor en el campo activo para no interrumpir la escritura.
     */
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

    /**
     * Valida las credenciales del formulario y crea la sesión si son correctas.
     * Si hay error, muestra un mensaje para informar al usuario.
     */
    @FXML
    public void onLogin() {
        String email = txtEmail.getText();
        String pass = txtPassword.getText();

        if (email.isEmpty() || pass.isEmpty()) {
            mostrarAlerta("Error", "Por favor, rellena todos los campos.");
            return;
        }

        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario != null && usuario.getPassword().equals(pass)) {
            UserSession.getInstance().logIn(usuario);

            System.out.println("Login exitoso: " + usuario.getNombre());
            SceneManager.switchScene("main_menu");

        } else {
            mostrarAlerta("Error", "Credenciales incorrectas.");
        }
    }

    /**
     * Redirige al formulario para recuperar contraseña.
     */
    @FXML
    public void onForgotPassword() {
        SceneManager.switchScene("forgot_password");
    }

    /**
     * Muestra una alerta informativa en pantalla.
     *
     * @param titulo texto principal que aparecerá como título de la ventana.
     * @param mensaje detalle que se mostrará al usuario.
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
