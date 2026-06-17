package com.dam.gestorasistencia.controller;

import com.dam.gestorasistencia.model.Usuario;
import com.dam.gestorasistencia.repository.UsuarioRepository;
import com.dam.gestorasistencia.view.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Controlador para la pantalla de registro de profesores.
 * Se encarga de recoger los datos, validarlos y guardar el nuevo profesor en la base de datos.
 */
@Component
public class RegistroProfesorController {

    // Constantes y objetos compartidos de la clase
    private final String CARACTERES_PERMITIDOS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    private final java.util.Random rnd = new java.util.Random();
    
    // Estado del icono del ojo de la contraseña
    private boolean isPasswordVisible = false;

    // Necesitamos el repositorio para guardar datos en la base de datos
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Estos son los campos de la interfaz visual
    @FXML private TextField txtNombre;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtPasswordVisible; // Campo de texto normal para ver la contraseña
    @FXML private org.kordamp.ikonli.javafx.FontIcon iconTogglePassword; // Icono del ojo
    @FXML private PasswordField txtConfirmPassword;

    /**
     * Este método se llama al cargar la pantalla.
     * Sirve para que lo que escribamos en el campo oculto se copie al visible y viceversa.
     */
    @FXML
    public void initialize() {
        if (txtPassword != null && txtPasswordVisible != null) {
            txtPasswordVisible.textProperty().bindBidirectional(txtPassword.textProperty());
        }
    }

    /**
     * Método que se ejecuta al darle al botón de Registrarse
     */
    @FXML
    public void onRegistrar() {
        // 1. Obtener los valores que ha escrito el usuario
        String nombre = txtNombre.getText();
        String email = txtEmail.getText();
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        // 2. Comprobar que no hay campos vacíos
        if (nombre == null || nombre.isEmpty()) {
            mostrarAlerta("Error", "Falta el nombre.");
            return;
        }
        if (email == null || email.isEmpty()) {
            mostrarAlerta("Error", "Falta el correo electrónico.");
            return;
        }
        if (password == null || password.isEmpty()) {
            mostrarAlerta("Error", "Falta la contraseña.");
            return;
        }
        if (confirmPassword == null || confirmPassword.isEmpty()) {
            mostrarAlerta("Error", "Falta confirmar la contraseña.");
            return;
        }

        // 2.5 Comprobar que el correo sea válido (que contenga una @)
        if (!email.contains("@")) {
            mostrarAlerta("Error", "Por favor, introduce un correo electrónico válido que contenga '@'.");
            return;
        }

        // 2.6 Comprobar que la contraseña sea segura (al menos 12 caracteres)
        if (password.length() < 12) {
            mostrarAlerta("Error", "La contraseña es muy corta. Debe tener al menos 12 caracteres para ser segura.");
            return;
        }

        // 3. Comprobar que las dos contraseñas son exactamente iguales
        if (!password.equals(confirmPassword)) {
            mostrarAlerta("Error", "Las contraseñas no coinciden. Inténtalo de nuevo.");
            return;
        }

        // 4. Asegurarnos de que el correo no esté siendo usado por otra persona
        // Convertimos a minúsculas por seguridad
        String emailLimpio = email.trim().toLowerCase();
        
        // Usamos isPresent() para ver si ya existe alguien con ese correo
        if (usuarioRepository.findByEmailIgnoreCase(emailLimpio).isPresent()) {
            mostrarAlerta("Error", "Este correo ya está registrado.");
            return;
        }

        // 5. Creamos un nuevo usuario asignándole los valores usando setters manuales
        Usuario nuevoProfesor = new Usuario();
        nuevoProfesor.setNombre(nombre.trim());
        nuevoProfesor.setEmail(emailLimpio);
        nuevoProfesor.setPassword(password);
        nuevoProfesor.setRol("PROFESOR"); // Este registro es exclusivo para profesores
        
        // 5.1 Nuevos campos para la verificación del email (Nivel Junior)
        nuevoProfesor.setVerificado(false); // Empieza sin estar verificado
        
        // Generamos un código de 6 números al azar (ej. "123456")
        String codigoGenerado = "";
        for (int i = 0; i < 6; i++) {
            codigoGenerado += rnd.nextInt(10); // número del 0 al 9
        }
        nuevoProfesor.setCodigoVerificacion(codigoGenerado);

        // 6. Guardamos el usuario en la base de datos
        usuarioRepository.save(nuevoProfesor);

        // 7. Enviamos el correo con el código de verificación
        enviarCorreo(emailLimpio, codigoGenerado);

        // 8. Pasamos a la nueva pantalla de verificación
        VerificarCorreoController.emailParaVerificar = emailLimpio; // Pasamos el correo a la siguiente pantalla
        SceneManager.switchScene("verificar_correo");
    }

    /**
     * Método que genera una contraseña aleatoria y segura de 12 caracteres.
     */
    @FXML
    public void onGenerarPassword() {
        StringBuilder pass = new StringBuilder();
        
        // Bucle sencillo para coger 12 caracteres al azar
        for (int i = 0; i < 12; i++) {
            int indiceAleatorio = rnd.nextInt(CARACTERES_PERMITIDOS.length());
            pass.append(CARACTERES_PERMITIDOS.charAt(indiceAleatorio));
        }
        
        String nuevaPassword = pass.toString();
        
        // Rellenamos los campos automáticamente
        txtPassword.setText(nuevaPassword);
        txtConfirmPassword.setText(nuevaPassword);
        
        // Mostramos una ventanita para que el usuario pueda verla y copiarla
        mostrarAlerta("Contraseña generada", "Tu nueva contraseña es:\n\n" + nuevaPassword + "\n\nCópiala o guárdala en un lugar seguro.");
    }

    /**
     * Alterna la visibilidad de la contraseña ocultando el PasswordField y mostrando el TextField.
     */
    @FXML
    public void onTogglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;

        txtPassword.setVisible(!isPasswordVisible);
        txtPassword.setManaged(!isPasswordVisible);

        txtPasswordVisible.setVisible(isPasswordVisible);
        txtPasswordVisible.setManaged(isPasswordVisible);

        // Cambiamos el icono según el estado
        if (isPasswordVisible) {
            iconTogglePassword.setIconLiteral("mdmz-visibility");
        } else {
            iconTogglePassword.setIconLiteral("mdmz-visibility_off");
        }
    }

    /**
     * Método que se ejecuta al darle al botón de volver
     */
    @FXML
    public void onVolver() {
        SceneManager.switchScene("inicio_sesion");
    }

    /**
     * Método para mostrar pequeñas ventanas de alerta
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Método básico (Nivel Junior) para enviar un correo de verificación usando tu propio Gmail.
     * Es más fácil y gratis.
     */
    private void enviarCorreo(String emailDestino, String codigo) {
        try {
            // 1. Configuramos el servicio con tu cuenta de Gmail
            org.springframework.mail.javamail.JavaMailSenderImpl mailSender = new org.springframework.mail.javamail.JavaMailSenderImpl();
            mailSender.setHost("smtp.gmail.com");
            mailSender.setPort(587); // Puerto estándar de Gmail
            
            // 2. Ponemos tus credenciales reales
            mailSender.setUsername("noreply.asistencia@gmail.com");
            mailSender.setPassword("twez lzmr daaj ludk");
            
            // 3. Configuración de seguridad obligatoria para que Google nos deje entrar
            java.util.Properties props = mailSender.getJavaMailProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            
            // 4. Creamos un mensaje HTML avanzado
            jakarta.mail.internet.MimeMessage mimeMessage = mailSender.createMimeMessage();
            org.springframework.mail.javamail.MimeMessageHelper helper = new org.springframework.mail.javamail.MimeMessageHelper(mimeMessage, "utf-8");
            
            helper.setFrom("noreply.asistencia@gmail.com");
            helper.setTo(emailDestino);
            helper.setSubject("Verifica tu cuenta en Gestor Asistencia");
            
            // Plantilla HTML con mezcla de colores de la app (#2563EB) y toque Windows XP (#0058E6, Tahoma)
            // Leemos el archivo HTML que hemos creado aparte (Nivel Junior)
            java.io.InputStream inputStream = getClass().getResourceAsStream("/templates/email_verificacion.html");
            String contenidoHtml = new String(inputStream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            
            // Cambiamos el texto {{codigo}} por el número aleatorio de verdad
            contenidoHtml = contenidoHtml.replace("{{codigo}}", codigo);
            
            helper.setText(contenidoHtml, true); // El 'true' indica que es HTML
            
            // 5. ¡Enviamos!
            mailSender.send(mimeMessage);
            System.out.println("Correo enviado con éxito por Gmail a: " + emailDestino);
            
        } catch (Exception e) {
            System.out.println("Hubo un error al intentar enviar el correo por Gmail: " + e.getMessage());
        }
    }
}
