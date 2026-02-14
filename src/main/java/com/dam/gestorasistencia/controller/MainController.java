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
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.GridPane; // Nuevo
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
    @FXML private HBox navClases;
    @FXML private HBox navAlumnos;

    @FXML private VBox vistaAsistencia;
    @FXML private VBox vistaClases;
    @FXML private VBox vistaAlumnos;
    @FXML private Label lblPageTitle;
    @FXML private Label lblBreadcrumb;

    // Tabla
    @FXML private TableView<AlumnoAsistenciaRow> tblAlumnos;
    @FXML private TableColumn<AlumnoAsistenciaRow, String> colNombre;
    @FXML private TableColumn<AlumnoAsistenciaRow, String> colApellidos;
    @FXML private TableColumn<AlumnoAsistenciaRow, EstadoAsistencia> colEstado;
    @FXML private Label lblInfo;

    private ObservableList<AlumnoAsistenciaRow> listaAlumnosUI = FXCollections.observableArrayList();

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
    }

    @FXML
    public void mostrarPanelControl() {
        activarVista("panel");
        lblPageTitle.setText("Panel de Control");
        lblBreadcrumb.setText("Gestión de asistencia diaria");
    }

    @FXML
    public void mostrarSeccionClases() {
        activarVista("clases");
        lblPageTitle.setText("Clases");
        lblBreadcrumb.setText("Gestión de clases");
    }

    @FXML
    public void mostrarSeccionAlumnos() {
        activarVista("alumnos");
        lblPageTitle.setText("Alumnos");
        lblBreadcrumb.setText("Gestión de alumnos");
    }

    private void activarVista(String vistaActiva) {
        boolean panelControlActivo = "panel".equals(vistaActiva);
        boolean clasesActivo = "clases".equals(vistaActiva);
        boolean alumnosActivo = "alumnos".equals(vistaActiva);

        vistaAsistencia.setVisible(panelControlActivo);
        vistaAsistencia.setManaged(panelControlActivo);

        vistaClases.setVisible(clasesActivo);
        vistaClases.setManaged(clasesActivo);

        vistaAlumnos.setVisible(alumnosActivo);
        vistaAlumnos.setManaged(alumnosActivo);

        navPanelControl.getStyleClass().remove("sidebar-item-active");
        navClases.getStyleClass().remove("sidebar-item-active");
        navAlumnos.getStyleClass().remove("sidebar-item-active");

        if (panelControlActivo) {
            navPanelControl.getStyleClass().add("sidebar-item-active");
        } else if (clasesActivo) {
            navClases.getStyleClass().add("sidebar-item-active");
        } else if (alumnosActivo) {
            navAlumnos.getStyleClass().add("sidebar-item-active");
        }
    }

    private void configurarColoresTabla() {
        tblAlumnos.setRowFactory(tv -> {
            TableRow<AlumnoAsistenciaRow> row = new TableRow<>();

            // Repintar al cambiar el item (scroll/carga)
            row.itemProperty().addListener((obs, oldVal, newVal) -> actualizarEstiloFila(row));

            // Repintar al cambiar el estado (combobox)
            row.itemProperty().addListener((obs, oldRow, newRow) -> {
                if (newRow != null) {
                    newRow.estadoProperty().addListener((o, oldEstado, newEstado) -> actualizarEstiloFila(row));
                }
            });

            return row;
        });
    }

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
    }

    // --- NUEVO: AÑADIR ALUMNO (SOLO ADMIN) ---
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
            if (nuevoAlumno.getNombre().isEmpty() || nuevoAlumno.getEmail().isEmpty() || nuevoAlumno.getIdAsignatura() == null) {
                mostrarAlerta("Error", "Nombre, Email y Asignatura son obligatorios");
                return;
            }
            alumnoRepository.save(nuevoAlumno);
            cargarAlumnos(); // Refrescar tabla
            mostrarAlerta("Éxito", "Alumno añadido correctamente.");
        });
    }

    // --- NUEVO: ELIMINAR ALUMNO (SOLO ADMIN) ---
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

    @FXML
    public void onMarcarTodosPresentes() {
        for (AlumnoAsistenciaRow row : listaAlumnosUI) {
            row.setEstado(EstadoAsistencia.PRESENTE);
        }
        tblAlumnos.refresh();
    }

    @FXML
    public void verEstadisticas() {
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

        PieChart chart = new PieChart(pieChartData);
        chart.setTitle("Resumen de Asistencia");

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Estadísticas");
        Scene scene = new Scene(chart, 500, 400);
        popup.setScene(scene);
        popup.show();
    }

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

    @FXML
    public void irAdmin() {
        SceneManager.switchScene("register_view");
    }

    @FXML
    public void onCerrarSesion() {
        UserSession.getInstance().logOut();
        SceneManager.switchScene("login");
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
