package com.dam.gestorasistencia.controller;

import com.dam.gestorasistencia.model.Usuario;
import com.dam.gestorasistencia.repository.UsuarioRepository;
import com.dam.gestorasistencia.view.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Controlador para la pantalla de verificación de correo electrónico.
 * Se encarga de comprobar si el código introducido por el usuario
 * coincide con el que le hemos enviado al registrarse.
 */
@Component
public class VerificarCorreoController {

    // Variable global sencilla para saber qué correo estamos verificando
    public static String emailParaVerificar = "";

    @Autowired
    private UsuarioRepository usuarioRepository;

    @FXML
    private TextField txtCodigo;

    /**
     * Este método se llama cuando el usuario pulsa el botón "Verificar".
     */
    @FXML
    public void onVerificar() {
        String codigoIntroducido = txtCodigo.getText();

        // 1. Comprobamos que el usuario haya escrito algo
        if (codigoIntroducido == null || codigoIntroducido.isEmpty()) {
            mostrarAlerta("Error", "Por favor, introduce el código de verificación.");
            return;
        }

        // 2. Buscamos al usuario en la base de datos usando el correo guardado en la variable
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(emailParaVerificar).orElse(null);

        if (usuario == null) {
            mostrarAlerta("Error", "No hemos podido encontrar al usuario en la base de datos.");
            return;
        }

        // 3. Comprobamos si el código coincide
        if (codigoIntroducido.equals(usuario.getCodigoVerificacion())) {
            // ¡Correcto! Marcamos al usuario como verificado
            usuario.setVerificado(true);
            usuario.setCodigoVerificacion(""); // Borramos el código por seguridad
            
            // Guardamos los cambios
            usuarioRepository.save(usuario);

            // Avisamos al usuario y le mandamos a la pantalla de login
            mostrarAlerta("¡Éxito!", "Tu correo ha sido verificado correctamente. Ya puedes iniciar sesión.");
            SceneManager.switchScene("inicio_sesion");
            
        } else {
            // El código no es correcto
            mostrarAlerta("Error", "El código introducido no es correcto. Por favor, revisa tu correo e inténtalo de nuevo.");
        }
    }

    /**
     * Si el usuario se equivoca o quiere cancelar, le devolvemos al login.
     */
    @FXML
    public void onCancelar() {
        SceneManager.switchScene("inicio_sesion");
    }

    /**
     * Método básico para mostrar alertas
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
