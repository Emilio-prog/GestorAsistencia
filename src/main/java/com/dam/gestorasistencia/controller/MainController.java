package com.dam.gestorasistencia.controller;

import com.dam.gestorasistencia.model.*;
import com.dam.gestorasistencia.repository.AlumnoRepository;
import com.dam.gestorasistencia.repository.AsignaturaRepository;
import com.dam.gestorasistencia.repository.RegistroAsistenciaRepository;
import com.dam.gestorasistencia.view.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets; // Nuevo
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.GridPane; // Nuevo
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Gestiona la pantalla principal de asistencia diaria, informes y configuración.
 * Coordina la carga de alumnos, el guardado de estados y la exportación de datos.
 *
 * @author Equipo de Desarrollo
 */
@Component
public class MainController {

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired
    private RegistroAsistenciaRepository registroRepository;

    @Autowired
    private AsignaturaRepository asignaturaRepository;

    // Filtros
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<String> cbGrupo;
    @FXML private ComboBox<Asignatura> cbAsignatura;

    // Botones de Administración
    @FXML private Button btnAdmin;
    @FXML private Button btnAddAlumno; // Nuevo
    @FXML private Button btnDelAlumno; // Nuevo
    @FXML private HBox navPanelControl;
    @FXML private HBox navInformes;
    @FXML private HBox navConfiguracion;

    @FXML private VBox vistaAsistencia;
    @FXML private VBox vistaConfiguracion;
    @FXML private VBox vistaInformes;
    @FXML private Label lblPageTitle;
    @FXML private Label lblBreadcrumb;

    @FXML private PieChart chartEstadisticas;
    @FXML private Label lblTotalPresentes;
    @FXML private Label lblTotalFaltas;
    @FXML private Label lblTotalRetrasos;
    @FXML private Label lblTotalJustificadas;

    // Tabla
    @FXML private TableView<AlumnoAsistenciaRow> tblAlumnos;
    @FXML private TableColumn<AlumnoAsistenciaRow, String> colNombre;
    @FXML private TableColumn<AlumnoAsistenciaRow, String> colApellidos;
    @FXML private TableColumn<AlumnoAsistenciaRow, EstadoAsistencia> colEstado;
    @FXML private Label lblInfo;

    @FXML private ToggleButton btnTemaLight;
    @FXML private ToggleButton btnTemaDark;
    @FXML private ToggleButton btnTemaSystem;

    private ObservableList<AlumnoAsistenciaRow> listaAlumnosUI = FXCollections.observableArrayList();

    /**
     * Configura filtros, tabla, permisos según rol y estado inicial de la vista principal.
     */
    @FXML
    public void initialize() {
        // 1. Configurar filtros
        dpFecha.setValue(LocalDate.now());
        cbGrupo.setItems(FXCollections.observableArrayList("2DAM", "1DAW"));
        cbGrupo.getSelectionModel().selectFirst();

        // Configurar Combo Asignaturas
        List<Asignatura> asignaturas = asignaturaRepository.findAll();
        cbAsignatura.setItems(FXCollections.observableArrayList(asignaturas));

        cbAsignatura.setConverter(new StringConverter<Asignatura>() {
            @Override
            public String toString(Asignatura a) { return a != null ? a.getNombre() : ""; }
            @Override
            public Asignatura fromString(String s) { return null; }
        });
        cbAsignatura.getSelectionModel().selectFirst();

        // 2. Configurar columnas tabla
        colNombre.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAlumno().getNombre()));
        colApellidos.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAlumno().getApellidos()));

        colEstado.setCellValueFactory(cellData -> cellData.getValue().estadoProperty());
        colEstado.setCellFactory(ComboBoxTableCell.forTableColumn(EstadoAsistencia.values()));
        colEstado.setEditable(true);

        tblAlumnos.setItems(listaAlumnosUI);
        tblAlumnos.setEditable(true);

        // Configurar colores visuales (RowFactory)
        configurarColoresTabla();

        // 3. SEGURIDAD ADMIN (Gestión de visibilidad de botones)
        Usuario usuarioActual = UserSession.getInstance().getUsuarioLogueado();
        boolean esAdmin = usuarioActual != null && "ADMIN".equals(usuarioActual.getRol());

        btnAdmin.setVisible(esAdmin);
        btnAddAlumno.setVisible(esAdmin);     // Solo admin puede añadir
        btnDelAlumno.setVisible(esAdmin);     // Solo admin puede borrar

        mostrarPanelControl();

        // 4. Cargar datos iniciales
        cargarAlumnos();
        aplicarTema("system");
    }

    /**
     * Muestra la vista de panel de control para registrar asistencia diaria.
     */
    @FXML
    public void mostrarPanelControl() {
        activarVista("panel");
        lblPageTitle.setText("Panel de Control");
        lblBreadcrumb.setText("Gestión de asistencia diaria");
    }


    /**
     * Muestra la sección de informes y actualiza los datos del gráfico.
     */
    @FXML
    public void mostrarSeccionInformes() {
        activarVista("informes");
        lblPageTitle.setText("Informes");
        lblBreadcrumb.setText("Estadísticas de asistencia");
        actualizarEstadisticas();
    }

    /**
     * Muestra la sección de configuración con opciones de tema y exportación.
     */
    @FXML
    public void mostrarSeccionConfiguracion() {
        activarVista("configuracion");
        lblPageTitle.setText("Configuración");
        lblBreadcrumb.setText("Preferencias y exportación de datos");
    }

    /**
     * Activa una sección de la interfaz y desactiva las demás para mostrar solo una vista.
     *
     * @param seccionActiva nombre interno de la sección a mostrar.
     */
    private void activarVista(String seccionActiva) {
        boolean panelControlActivo = "panel".equals(seccionActiva);
        boolean informesActiva = "informes".equals(seccionActiva);
        boolean configuracionActiva = "configuracion".equals(seccionActiva);

        vistaAsistencia.setVisible(panelControlActivo);
        vistaAsistencia.setManaged(panelControlActivo);

        vistaInformes.setVisible(informesActiva);
        vistaInformes.setManaged(informesActiva);

        vistaConfiguracion.setVisible(configuracionActiva);
        vistaConfiguracion.setManaged(configuracionActiva);

        navPanelControl.getStyleClass().remove("sidebar-item-active");
        navInformes.getStyleClass().remove("sidebar-item-active");
        navConfiguracion.getStyleClass().remove("sidebar-item-active");

        if (panelControlActivo) {
            navPanelControl.getStyleClass().add("sidebar-item-active");
        } else if (informesActiva) {
            navInformes.getStyleClass().add("sidebar-item-active");
        } else if (configuracionActiva) {
            navConfiguracion.getStyleClass().add("sidebar-item-active");
        }
    }

    /**
     * Configura el color de cada fila según el estado de asistencia del alumno.
     */
    private void configurarColoresTabla() {
        tblAlumnos.setRowFactory(tv -> {
            TableRow<AlumnoAsistenciaRow> row = new TableRow<>();

            // Repintar al cambiar el item (scroll/carga)
            row.itemProperty().addListener((obs, oldVal, newVal) -> actualizarEstiloFila(row));

            // Repintar al cambiar el estado (combobox)
            row.itemProperty().addListener((obs, oldRow, newRow) -> {
                if (newRow != null) {
                    newRow.estadoProperty().addListener((o, oldEstado, newEstado) -> {
                        actualizarEstiloFila(row);
                        actualizarEstadisticas();
                    });
                }
            });

            return row;
        });
    }

    /**
     * Aplica el estilo visual de una fila en función del estado seleccionado.
     *
     * @param row fila de la tabla que se va a pintar.
     */
    private void actualizarEstiloFila(TableRow<AlumnoAsistenciaRow> row) {
        row.getStyleClass().removeAll("fila-falta", "fila-retraso", "fila-presente", "fila-justificada");

        if (row.getItem() != null) {
            switch (row.getItem().getEstado()) {
                case FALTA -> row.getStyleClass().add("fila-falta");
                case RETRASO -> row.getStyleClass().add("fila-retraso");
                case PRESENTE -> row.getStyleClass().add("fila-presente");
                case JUSTIFICADA -> row.getStyleClass().add("fila-justificada");
            }
        }
    }

    /**
     * Carga en la tabla los alumnos del grupo y asignatura seleccionados para la fecha elegida.
     */
    @FXML
    public void cargarAlumnos() {
        String grupo = cbGrupo.getValue();
        LocalDate fecha = dpFecha.getValue();
        Asignatura asignatura = cbAsignatura.getValue();

        if (grupo == null || fecha == null || asignatura == null) return;

        listaAlumnosUI.clear();

        List<Alumno> alumnos = alumnoRepository.findByGrupo(grupo);

        for (Alumno a : alumnos) {
            RegistroAsistencia registro = registroRepository
                    .findByFechaAndIdAlumnoAndIdAsignatura(fecha, a.getId(), asignatura.getId())
                    .orElse(null);

            EstadoAsistencia estadoInicial = (registro != null)
                    ? registro.getEstado()
                    : EstadoAsistencia.PRESENTE;

            listaAlumnosUI.add(new AlumnoAsistenciaRow(a, estadoInicial));
        }

        lblInfo.setText("Alumnos: " + alumnos.size() + " | Asignatura: " + asignatura.getNombre());
        actualizarEstadisticas();
    }

    // --- NUEVO: AÑADIR ALUMNO (SOLO ADMIN) ---
    /**
     * Abre un formulario para crear un alumno nuevo y lo guarda si los datos son válidos.
     */
    @FXML
    public void onAddAlumno() {
        // Crear diálogo
        Dialog<Alumno> dialog = new Dialog<>();
        dialog.setTitle("Nuevo Alumno");
        dialog.setHeaderText("Introduce los datos del alumno");

        // Botones
        ButtonType loginButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        // Campos
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nombre = new TextField();
        nombre.setPromptText("Nombre");
        TextField apellidos = new TextField();
        apellidos.setPromptText("Apellidos");
        TextField email = new TextField();
        email.setPromptText("Email");
        ComboBox<String> grupo = new ComboBox<>();
        grupo.getItems().addAll("2DAM", "1DAW");
        grupo.setValue(cbGrupo.getValue()); // Preseleccionar el grupo actual

        ComboBox<Asignatura> asignaturaAlumno = new ComboBox<>();
        asignaturaAlumno.setItems(FXCollections.observableArrayList(asignaturaRepository.findAll()));
        asignaturaAlumno.setConverter(new StringConverter<Asignatura>() {
            @Override
            public String toString(Asignatura a) { return a != null ? a.getNombre() : ""; }

            @Override
            public Asignatura fromString(String s) { return null; }
        });
        asignaturaAlumno.setValue(cbAsignatura.getValue()); // Preseleccionar asignatura actual

        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(nombre, 1, 0);
        grid.add(new Label("Apellidos:"), 0, 1);
        grid.add(apellidos, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(email, 1, 2);
        grid.add(new Label("Grupo:"), 0, 3);
        grid.add(grupo, 1, 3);
        grid.add(new Label("Asignatura:"), 0, 4);
        grid.add(asignaturaAlumno, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Convertir resultado
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                String idAsignatura = asignaturaAlumno.getValue() != null ? asignaturaAlumno.getValue().getId() : null;
                return new Alumno(null, nombre.getText(), apellidos.getText(), email.getText(), grupo.getValue(), idAsignatura);
            }
            return null;
        });

        // Procesar respuesta
        Optional<Alumno> result = dialog.showAndWait();
        result.ifPresent(nuevoAlumno -> {
            String nombreAlumno = nuevoAlumno.getNombre() != null ? nuevoAlumno.getNombre().trim() : "";
            String apellidosAlumno = nuevoAlumno.getApellidos() != null ? nuevoAlumno.getApellidos().trim() : "";
            String emailAlumno = nuevoAlumno.getEmail() != null ? nuevoAlumno.getEmail().trim().toLowerCase() : "";

            if (nombreAlumno.isEmpty() || emailAlumno.isEmpty() || nuevoAlumno.getIdAsignatura() == null) {
                mostrarAlerta("Error", "Nombre, Email y Asignatura son obligatorios");
                return;
            }

            if (alumnoRepository.findByEmail(emailAlumno).isPresent()) {
                mostrarAlerta("Error", "El alumno ya existe con ese email.");
                return;
            }

            nuevoAlumno.setNombre(nombreAlumno);
            nuevoAlumno.setApellidos(apellidosAlumno);
            nuevoAlumno.setEmail(emailAlumno);

            alumnoRepository.save(nuevoAlumno);
            cargarAlumnos(); // Refrescar tabla
            mostrarAlerta("Éxito", "Alumno añadido correctamente.");
        });
    }

    // --- NUEVO: ELIMINAR ALUMNO (SOLO ADMIN) ---
    /**
     * Elimina el alumno seleccionado en la tabla tras confirmación del usuario.
     */
    @FXML
    public void onDeleteAlumno() {
        AlumnoAsistenciaRow seleccion = tblAlumnos.getSelectionModel().getSelectedItem();

        if (seleccion == null) {
            mostrarAlerta("Aviso", "Selecciona un alumno de la tabla para eliminarlo.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar Alumno");
        alert.setHeaderText("¿Estás seguro?");
        alert.setContentText("Vas a eliminar a " + seleccion.getAlumno().getNombre() + ". Esto no se puede deshacer.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            alumnoRepository.delete(seleccion.getAlumno());
            listaAlumnosUI.remove(seleccion);
            mostrarAlerta("Eliminado", "Alumno eliminado correctamente.");
        }
    }

    /**
     * Marca como presente a todos los alumnos mostrados en la tabla actual.
     */
    @FXML
    public void onMarcarTodosPresentes() {
        for (AlumnoAsistenciaRow row : listaAlumnosUI) {
            row.setEstado(EstadoAsistencia.PRESENTE);
        }
        tblAlumnos.refresh();
        actualizarEstadisticas();
    }

    /**
     * Recalcula y muestra los totales de asistencia en etiquetas y gráfico circular.
     */
    private void actualizarEstadisticas() {
        int presentes = 0, faltas = 0, retrasos = 0, justificados = 0;
        for (AlumnoAsistenciaRow row : listaAlumnosUI) {
            switch (row.getEstado()) {
                case PRESENTE -> presentes++;
                case FALTA -> faltas++;
                case RETRASO -> retrasos++;
                case JUSTIFICADA -> justificados++;
            }
        }

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Presentes", presentes),
                new PieChart.Data("Faltas", faltas),
                new PieChart.Data("Retrasos", retrasos),
                new PieChart.Data("Justificadas", justificados));

        chartEstadisticas.setData(pieChartData);
        lblTotalPresentes.setText("Presentes: " + presentes);
        lblTotalFaltas.setText("Faltas: " + faltas);
        lblTotalRetrasos.setText("Retrasos: " + retrasos);
        lblTotalJustificadas.setText("Justificadas: " + justificados);
    }

    /**
     * Guarda en la base de datos el estado de asistencia de cada alumno para la fecha y asignatura activas.
     */
    @FXML
    public void onGuardar() {
        LocalDate fecha = dpFecha.getValue();
        Asignatura asignatura = cbAsignatura.getValue();

        if (asignatura == null) {
            mostrarAlerta("Error", "Debes seleccionar una asignatura");
            return;
        }

        int guardados = 0;

        for (AlumnoAsistenciaRow row : listaAlumnosUI) {
            Alumno alumno = row.getAlumno();
            RegistroAsistencia registro = registroRepository
                    .findByFechaAndIdAlumnoAndIdAsignatura(fecha, alumno.getId(), asignatura.getId())
                    .orElse(new RegistroAsistencia());

            registro.setFecha(fecha);
            registro.setIdAlumno(alumno.getId());
            registro.setNombreAlumno(alumno.getNombre() + " " + alumno.getApellidos());
            registro.setEstado(row.getEstado());
            registro.setIdAsignatura(asignatura.getId());

            registroRepository.save(registro);
            guardados++;
        }

        mostrarAlerta("Guardado", "Datos guardados correctamente.");
    }

    /**
     * Navega a la vista de registro de usuarios para tareas de administración.
     */
    @FXML
    public void irAdmin() {
        SceneManager.switchScene("register_view");
    }

    /**
     * Cierra la sesión actual y regresa a la pantalla de inicio de sesión.
     */
    @FXML
    public void onCerrarSesion() {
        UserSession.getInstance().logOut();
        SceneManager.switchScene("login");
    }

    /**
     * Cambia el tema visual de la aplicación al modo claro.
     */
    @FXML
    public void onTemaLight() {
        aplicarTema("light");
    }

    /**
     * Cambia el tema visual de la aplicación al modo oscuro.
     */
    @FXML
    public void onTemaDark() {
        aplicarTema("dark");
    }

    /**
     * Aplica el tema visual configurado por el sistema operativo.
     */
    @FXML
    public void onTemaSystem() {
        aplicarTema("system");
    }

    /**
     * Aplica clases de estilo al contenedor raíz según el tema solicitado.
     *
     * @param tema nombre del tema a aplicar: light, dark o system.
     */
    private void aplicarTema(String tema) {
        if (vistaAsistencia == null || vistaAsistencia.getScene() == null) {
            return;
        }

        Scene scene = vistaAsistencia.getScene();
        Parent root = scene.getRoot();
        root.getStyleClass().removeAll("theme-light", "theme-dark", "theme-system");

        switch (tema) {
            case "dark" -> root.getStyleClass().add("theme-dark");
            case "light" -> root.getStyleClass().add("theme-light");
            default -> root.getStyleClass().add("theme-system");
        }

        btnTemaLight.setSelected("light".equals(tema));
        btnTemaDark.setSelected("dark".equals(tema));
        btnTemaSystem.setSelected("system".equals(tema));
    }

    /**
     * Exporta la configuración y datos principales a un archivo JSON elegido por el usuario.
     */
    @FXML
    public void exportarDatosJson() {
        Map<String, Object> data = construirDatosExportacion();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar exportación JSON");
        fileChooser.setInitialFileName("configuracion_export.json");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo JSON", "*.json"));

        File file = fileChooser.showSaveDialog(vistaAsistencia.getScene().getWindow());
        if (file == null) {
            return;
        }

        try {
            Files.writeString(file.toPath(), toJson(data), StandardCharsets.UTF_8);
            mostrarAlerta("Exportación completada", "Se guardó el archivo JSON correctamente.");
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo exportar JSON: " + e.getMessage());
        }
    }

    /**
     * Exporta la configuración y datos principales a un documento PDF con tablas.
     */
    @FXML
    public void exportarDatosPdf() {
        Map<String, Object> data = construirDatosExportacion();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar exportación PDF");
        fileChooser.setInitialFileName("configuracion_export.pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Documento PDF", "*.pdf"));

        File file = fileChooser.showSaveDialog(vistaAsistencia.getScene().getWindow());
        if (file == null) {
            return;
        }

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            float[] cursorY = new float[]{780f};

            cursorY[0] = escribirTitulo(document, page, "Exportación de Configuración", cursorY[0]);
            cursorY[0] = escribirTexto(document, page, "Fecha de exportación: " + data.get("exportadoEn"), cursorY[0] - 10);

            @SuppressWarnings("unchecked")
            Map<String, Object> usuario = (Map<String, Object>) data.get("usuarioSesion");
            List<List<String>> filasUsuario = List.of(
                    List.of("Campo", "Valor"),
                    List.of("Nombre", String.valueOf(usuario.get("nombre"))),
                    List.of("Email", String.valueOf(usuario.get("email"))),
                    List.of("Password", String.valueOf(usuario.get("password"))),
                    List.of("Rol", String.valueOf(usuario.get("rol")))
            );
            cursorY[0] = dibujarTabla(document, page, cursorY[0] - 18, "Usuario en sesión", new float[]{180f, 330f}, filasUsuario);

            @SuppressWarnings("unchecked")
            Map<String, List<Map<String, String>>> alumnos = (Map<String, List<Map<String, String>>>) data.get("alumnosPorClase");
            for (Map.Entry<String, List<Map<String, String>>> entry : alumnos.entrySet()) {
                List<List<String>> filasAlumnos = new ArrayList<>();
                filasAlumnos.add(List.of("Nombre", "Apellidos", "Email", "Asignatura"));
                for (Map<String, String> alumno : entry.getValue()) {
                    filasAlumnos.add(List.of(
                            valor(alumno.get("nombre")),
                            valor(alumno.get("apellidos")),
                            valor(alumno.get("email")),
                            valor(alumno.get("idAsignatura"))
                    ));
                }
                cursorY[0] = dibujarTabla(document, page, cursorY[0] - 18, "Alumnos - Clase " + entry.getKey(), new float[]{100f, 120f, 190f, 100f}, filasAlumnos);
            }

            @SuppressWarnings("unchecked")
            List<Map<String, String>> asignaturas = (List<Map<String, String>>) data.get("asignaturas");
            List<List<String>> filasAsignaturas = new ArrayList<>();
            filasAsignaturas.add(List.of("Nombre", "Curso"));
            for (Map<String, String> asignatura : asignaturas) {
                filasAsignaturas.add(List.of(valor(asignatura.get("nombre")), valor(asignatura.get("curso"))));
            }
            dibujarTabla(document, page, cursorY[0] - 18, "Asignaturas", new float[]{350f, 160f}, filasAsignaturas);

            document.save(file);
            mostrarAlerta("Exportación completada", "Se guardó el archivo PDF correctamente.");
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo exportar PDF: " + e.getMessage());
        }
    }

    /**
     * Construye la estructura de datos que se usa para exportar en JSON y PDF.
     *
     * @return mapa con datos de sesión, alumnos por clase, asignaturas y fecha de exportación.
     */
    private Map<String, Object> construirDatosExportacion() {
        Usuario usuario = UserSession.getInstance().getUsuarioLogueado();
        Map<String, Object> root = new LinkedHashMap<>();

        Map<String, Object> usuarioMap = new LinkedHashMap<>();
        usuarioMap.put("id", usuario != null ? usuario.getId() : "N/A");
        usuarioMap.put("nombre", usuario != null ? usuario.getNombre() : "N/A");
        usuarioMap.put("email", usuario != null ? usuario.getEmail() : "N/A");
        usuarioMap.put("password", usuario != null ? usuario.getPassword() : "N/A");
        usuarioMap.put("rol", usuario != null ? usuario.getRol() : "N/A");
        root.put("usuarioSesion", usuarioMap);

        Map<String, List<Map<String, String>>> alumnosPorClase = alumnoRepository.findAll().stream()
                .sorted(Comparator.comparing(Alumno::getGrupo, Comparator.nullsLast(String::compareToIgnoreCase))
                        .thenComparing(Alumno::getApellidos, Comparator.nullsLast(String::compareToIgnoreCase))
                        .thenComparing(Alumno::getNombre, Comparator.nullsLast(String::compareToIgnoreCase)))
                .collect(Collectors.groupingBy(
                        alumno -> Optional.ofNullable(alumno.getGrupo()).orElse("Sin clase"),
                        LinkedHashMap::new,
                        Collectors.mapping(a -> {
                            Map<String, String> alumno = new LinkedHashMap<>();
                            alumno.put("id", a.getId());
                            alumno.put("nombre", a.getNombre());
                            alumno.put("apellidos", a.getApellidos());
                            alumno.put("email", a.getEmail());
                            alumno.put("idAsignatura", a.getIdAsignatura());
                            return alumno;
                        }, Collectors.toList())
                ));
        root.put("alumnosPorClase", alumnosPorClase);

        List<Map<String, String>> asignaturas = asignaturaRepository.findAll().stream()
                .sorted(Comparator.comparing(Asignatura::getNombre, Comparator.nullsLast(String::compareToIgnoreCase)))
                .map(asignatura -> {
                    Map<String, String> asignaturaMap = new LinkedHashMap<>();
                    asignaturaMap.put("id", asignatura.getId());
                    asignaturaMap.put("nombre", asignatura.getNombre());
                    asignaturaMap.put("curso", asignatura.getCurso());
                    return asignaturaMap;
                })
                .collect(Collectors.toList());

        root.put("asignaturas", asignaturas);
        root.put("exportadoEn", LocalDate.now().toString());

        return root;
    }

    /**
     * Escribe un título en el PDF en la posición vertical indicada.
     *
     * @param document documento PDF destino.
     * @param page página donde se escribirá el texto.
     * @param text contenido del título.
     * @param y coordenada vertical inicial.
     * @return nueva coordenada vertical para continuar escribiendo contenido.
     * @throws IOException si ocurre un error al escribir en el PDF.
     */
    private float escribirTitulo(PDDocument document, PDPage page, String text, float y) throws IOException {
        try (PDPageContentStream cs = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true)) {
            cs.beginText();
            cs.setNonStrokingColor(0, 0, 0);
            cs.setFont(PDType1Font.HELVETICA_BOLD, 16);
            cs.newLineAtOffset(40, y);
            cs.showText(text);
            cs.endText();
        }
        return y - 18;
    }

    /**
     * Escribe una línea de texto normal en el PDF en la posición indicada.
     *
     * @param document documento PDF destino.
     * @param page página donde se dibujará el texto.
     * @param text contenido de la línea.
     * @param y coordenada vertical inicial.
     * @return nueva coordenada vertical para el siguiente bloque de texto.
     * @throws IOException si falla la escritura en el PDF.
     */
    private float escribirTexto(PDDocument document, PDPage page, String text, float y) throws IOException {
        try (PDPageContentStream cs = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true)) {
            cs.beginText();
            cs.setNonStrokingColor(0, 0, 0);
            cs.setFont(PDType1Font.HELVETICA, 11);
            cs.newLineAtOffset(40, y);
            cs.showText(text);
            cs.endText();
        }
        return y - 12;
    }

    /**
     * Dibuja una tabla en el PDF con cabecera, bordes y filas de datos.
     *
     * @param document documento PDF donde se pintará la tabla.
     * @param page página actual del documento.
     * @param yStart coordenada vertical inicial para empezar la tabla.
     * @param titulo texto que se mostrará encima de la tabla.
     * @param colWidths anchos de cada columna en puntos.
     * @param rows filas y columnas con los datos a mostrar.
     * @return coordenada vertical final para continuar dibujando más bloques.
     * @throws IOException si ocurre un error durante el dibujo en el PDF.
     */
    private float dibujarTabla(PDDocument document, PDPage page, float yStart, String titulo, float[] colWidths, List<List<String>> rows) throws IOException {
        float marginX = 40f;
        float rowHeight = 20f;
        float y = yStart;
        float tableWidth = 0f;
        for (float width : colWidths) {
            tableWidth += width;
        }

        y = escribirTexto(document, page, titulo, y);

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            List<String> row = rows.get(rowIndex);
            float x = marginX;

            try (PDPageContentStream cs = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true)) {
                if (rowIndex == 0) {
                    cs.setNonStrokingColor(210, 224, 244);
                    cs.addRect(marginX, y - rowHeight + 4, tableWidth, rowHeight);
                    cs.fill();
                }

                cs.setStrokingColor(120, 120, 120);
                cs.addRect(marginX, y - rowHeight + 4, tableWidth, rowHeight);
                cs.stroke();

                for (float colWidth : colWidths) {
                    x += colWidth;
                    cs.moveTo(x, y - rowHeight + 4);
                    cs.lineTo(x, y + 4);
                    cs.stroke();
                }
            }

            x = marginX;
            for (int col = 0; col < colWidths.length; col++) {
                String text = col < row.size() ? valor(row.get(col)) : "";
                String safe = text.length() > 34 ? text.substring(0, 34) + "..." : text;

                try (PDPageContentStream cs = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true)) {
                    cs.beginText();
                    cs.setNonStrokingColor(15, 23, 42);
                    cs.setFont(rowIndex == 0 ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA, 11);
                    cs.newLineAtOffset(x + 4, y - 13);
                    cs.showText(safe);
                    cs.endText();
                }
                x += colWidths[col];
            }

            y -= rowHeight;
        }

        return y - 8;
    }

    /**
     * Devuelve un valor seguro para mostrar cuando el dato original viene vacío.
     *
     * @param value texto original a validar.
     * @return el texto recibido o "N/A" si es nulo o está en blanco.
     */
    private String valor(String value) {
        return value == null || value.isBlank() ? "N/A" : value;
    }

    /**
     * Convierte un objeto Java simple en su representación JSON en texto.
     *
     * @param value valor a convertir, como mapa, lista, número, booleano o texto.
     * @return cadena JSON generada a partir del valor recibido.
     */
    private String toJson(Object value) {
        if (value == null) {
            return "null";
        }

        if (value instanceof Map<?, ?> map) {
            return "{" + map.entrySet().stream()
                    .map(entry -> "\"" + escapeJson(String.valueOf(entry.getKey())) + "\":" + toJson(entry.getValue()))
                    .collect(Collectors.joining(",")) + "}";
        }

        if (value instanceof List<?> list) {
            return "[" + list.stream().map(this::toJson).collect(Collectors.joining(",")) + "]";
        }

        if (value instanceof Number || value instanceof Boolean) {
            return String.valueOf(value);
        }

        return "\"" + escapeJson(String.valueOf(value)) + "\"";
    }

    /**
     * Escapa caracteres especiales para que un texto pueda incluirse de forma segura en JSON.
     *
     * @param input texto original que puede contener caracteres especiales.
     * @return texto escapado listo para usarse en una cadena JSON.
     */
    private String escapeJson(String input) {
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Muestra una alerta informativa reutilizable para mensajes de la interfaz.
     *
     * @param titulo título principal de la alerta.
     * @param mensaje detalle del mensaje que se quiere mostrar.
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
