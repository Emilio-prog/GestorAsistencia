package com.dam.gestorasistencia.controller;

import com.dam.gestorasistencia.model.*;
import com.dam.gestorasistencia.repository.AlumnoRepository;
import com.dam.gestorasistencia.repository.AsignaturaRepository;
import com.dam.gestorasistencia.repository.RegistroAsistenciaRepository;
import com.dam.gestorasistencia.view.SceneManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
    @FXML private Button btnAddAlumno;
    @FXML private Button btnDelAlumno;
    @FXML private Button btnAddClase;

    // Sección clases
    @FXML private ComboBox<String> cbCursoClases;
    @FXML private ComboBox<String> cbCicloClases;
    @FXML private TextField txtBuscarClase;
    @FXML private FlowPane classesGrid;

    @FXML private HBox navPanelControl;
    @FXML private HBox navClases;
    @FXML private HBox navInformes;

    @FXML private VBox vistaAsistencia;
    @FXML private VBox vistaClases;
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

    private ObservableList<AlumnoAsistenciaRow> listaAlumnosUI = FXCollections.observableArrayList();
    private boolean esAdmin;

    @FXML
    public void initialize() {
        dpFecha.setValue(LocalDate.now());
        cbGrupo.setItems(FXCollections.observableArrayList("2DAM", "1DAW"));
        cbGrupo.getSelectionModel().selectFirst();

        cargarAsignaturasEnSelector();

        cbAsignatura.setConverter(new StringConverter<>() {
            @Override
            public String toString(Asignatura a) { return a != null ? a.getNombre() : ""; }
            @Override
            public Asignatura fromString(String s) { return null; }
        });

        colNombre.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAlumno().getNombre()));
        colApellidos.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAlumno().getApellidos()));

        colEstado.setCellValueFactory(cellData -> cellData.getValue().estadoProperty());
        colEstado.setCellFactory(ComboBoxTableCell.forTableColumn(EstadoAsistencia.values()));
        colEstado.setEditable(true);

        tblAlumnos.setItems(listaAlumnosUI);
        tblAlumnos.setEditable(true);
        configurarColoresTabla();

        Usuario usuarioActual = UserSession.getInstance().getUsuarioLogueado();
        esAdmin = usuarioActual != null && "ADMIN".equals(usuarioActual.getRol());

        btnAdmin.setVisible(esAdmin);
        btnAddAlumno.setVisible(esAdmin);
        btnDelAlumno.setVisible(esAdmin);
        btnAddClase.setVisible(esAdmin);

        mostrarPanelControl();
        cargarAlumnos();
        inicializarSeccionClases();
    }

    private void cargarAsignaturasEnSelector() {
        List<Asignatura> asignaturas = asignaturaRepository.findAll();
        cbAsignatura.setItems(FXCollections.observableArrayList(asignaturas));
        if (!asignaturas.isEmpty()) {
            cbAsignatura.getSelectionModel().selectFirst();
        }
    }

    private void inicializarSeccionClases() {
        List<Asignatura> asignaturas = asignaturaRepository.findAll();
        List<String> cursos = asignaturas.stream()
                .map(Asignatura::getCurso)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        cbCursoClases.setItems(FXCollections.observableArrayList());
        cbCursoClases.getItems().add("Todos");
        cbCursoClases.getItems().addAll(cursos);
        cbCursoClases.getSelectionModel().selectFirst();

        cbCicloClases.setItems(FXCollections.observableArrayList("Todos", "DAM", "DAW", "MIXTO", "Sin ciclo"));
        cbCicloClases.getSelectionModel().selectFirst();

        renderizarClases();
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
        renderizarClases();
    }

    @FXML
    public void mostrarSeccionInformes() {
        activarVista("informes");
        lblPageTitle.setText("Informes");
        lblBreadcrumb.setText("Estadísticas de asistencia");
        actualizarEstadisticas();
    }

    private void activarVista(String seccionActiva) {
        boolean panelControlActivo = "panel".equals(seccionActiva);
        boolean clasesActiva = "clases".equals(seccionActiva);
        boolean informesActiva = "informes".equals(seccionActiva);

        vistaAsistencia.setVisible(panelControlActivo);
        vistaAsistencia.setManaged(panelControlActivo);

        vistaClases.setVisible(clasesActiva);
        vistaClases.setManaged(clasesActiva);

        vistaInformes.setVisible(informesActiva);
        vistaInformes.setManaged(informesActiva);

        navPanelControl.getStyleClass().remove("sidebar-item-active");
        navClases.getStyleClass().remove("sidebar-item-active");
        navInformes.getStyleClass().remove("sidebar-item-active");

        switch (seccionActiva) {
            case "clases" -> navClases.getStyleClass().add("sidebar-item-active");
            case "informes" -> navInformes.getStyleClass().add("sidebar-item-active");
            default -> navPanelControl.getStyleClass().add("sidebar-item-active");
        }
    }

    private void configurarColoresTabla() {
        tblAlumnos.setRowFactory(tv -> {
            TableRow<AlumnoAsistenciaRow> row = new TableRow<>();

            row.itemProperty().addListener((obs, oldVal, newVal) -> actualizarEstiloFila(row));
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
        actualizarEstadisticas();
    }

    @FXML
    public void onAddAlumno() {
        Dialog<Alumno> dialog = new Dialog<>();
        dialog.setTitle("Nuevo Alumno");
        dialog.setHeaderText("Introduce los datos del alumno");

        ButtonType saveButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nombre = new TextField();
        TextField apellidos = new TextField();
        TextField email = new TextField();
        ComboBox<String> grupo = new ComboBox<>();
        grupo.getItems().addAll("2DAM", "1DAW");
        grupo.setValue(cbGrupo.getValue());

        ComboBox<Asignatura> asignaturaAlumno = new ComboBox<>();
        asignaturaAlumno.setItems(FXCollections.observableArrayList(asignaturaRepository.findAll()));
        asignaturaAlumno.setConverter(new StringConverter<>() {
            @Override
            public String toString(Asignatura a) { return a != null ? a.getNombre() : ""; }
            @Override
            public Asignatura fromString(String s) { return null; }
        });
        asignaturaAlumno.setValue(cbAsignatura.getValue());

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

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String idAsignatura = asignaturaAlumno.getValue() != null ? asignaturaAlumno.getValue().getId() : null;
                return new Alumno(null, nombre.getText(), apellidos.getText(), email.getText(), grupo.getValue(), idAsignatura);
            }
            return null;
        });

        Optional<Alumno> result = dialog.showAndWait();
        result.ifPresent(nuevoAlumno -> {
            if (nuevoAlumno.getNombre().isBlank() || nuevoAlumno.getEmail().isBlank() || nuevoAlumno.getIdAsignatura() == null) {
                mostrarAlerta("Error", "Nombre, Email y Asignatura son obligatorios");
                return;
            }
            alumnoRepository.save(nuevoAlumno);
            cargarAlumnos();
            renderizarClases();
            mostrarAlerta("Éxito", "Alumno añadido correctamente.");
        });
    }

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

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            alumnoRepository.delete(seleccion.getAlumno());
            listaAlumnosUI.remove(seleccion);
            renderizarClases();
            mostrarAlerta("Eliminado", "Alumno eliminado correctamente.");
        }
    }

    @FXML
    public void onMarcarTodosPresentes() {
        for (AlumnoAsistenciaRow row : listaAlumnosUI) {
            row.setEstado(EstadoAsistencia.PRESENTE);
        }
        tblAlumnos.refresh();
        actualizarEstadisticas();
    }

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

    @FXML
    public void onGuardar() {
        LocalDate fecha = dpFecha.getValue();
        Asignatura asignatura = cbAsignatura.getValue();

        if (asignatura == null) {
            mostrarAlerta("Error", "Debes seleccionar una asignatura");
            return;
        }

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
        }

        mostrarAlerta("Guardado", "Datos guardados correctamente.");
    }

    @FXML
    public void onFiltroClasesChanged() {
        renderizarClases();
    }

    @FXML
    public void onAddClase() {
        if (!esAdmin) {
            mostrarAlerta("Permisos", "Solo los administradores pueden añadir clases.");
            return;
        }

        Dialog<Asignatura> dialog = new Dialog<>();
        dialog.setTitle("Nueva Clase");
        dialog.setHeaderText("Introduce los datos de la clase");

        ButtonType saveButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nombre = new TextField();
        nombre.setPromptText("Nombre de asignatura");
        TextField curso = new TextField();
        curso.setPromptText("2025-2026");

        grid.add(new Label("Asignatura:"), 0, 0);
        grid.add(nombre, 1, 0);
        grid.add(new Label("Curso:"), 0, 1);
        grid.add(curso, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Asignatura(null, nombre.getText(), curso.getText());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(nuevaClase -> {
            if (nuevaClase.getNombre() == null || nuevaClase.getNombre().isBlank()) {
                mostrarAlerta("Error", "El nombre de la clase es obligatorio.");
                return;
            }
            if (nuevaClase.getCurso() == null || nuevaClase.getCurso().isBlank()) {
                nuevaClase.setCurso("Sin curso");
            }

            asignaturaRepository.save(nuevaClase);
            cargarAsignaturasEnSelector();
            inicializarSeccionClases();
            mostrarAlerta("Éxito", "Clase añadida correctamente.");
        });
    }

    private void renderizarClases() {
        classesGrid.getChildren().clear();

        String cursoFiltro = cbCursoClases.getValue();
        String cicloFiltro = cbCicloClases.getValue();
        String textoBusqueda = txtBuscarClase.getText() != null ? txtBuscarClase.getText().trim().toLowerCase() : "";

        List<Asignatura> asignaturasFiltradas = asignaturaRepository.findAll().stream()
                .filter(a -> cursoFiltro == null || "Todos".equals(cursoFiltro) || cursoFiltro.equals(a.getCurso()))
                .filter(a -> textoBusqueda.isBlank() || a.getNombre().toLowerCase().contains(textoBusqueda))
                .filter(a -> {
                    if (cicloFiltro == null || "Todos".equals(cicloFiltro)) return true;
                    return cicloAsignatura(a.getId()).equalsIgnoreCase(cicloFiltro);
                })
                .sorted(Comparator.comparing(Asignatura::getNombre))
                .toList();

        for (Asignatura asignatura : asignaturasFiltradas) {
            classesGrid.getChildren().add(crearCardClase(asignatura));
        }
    }

    private VBox crearCardClase(Asignatura asignatura) {
        VBox card = new VBox(6);
        card.getStyleClass().add("class-card");

        long totalAlumnos = alumnoRepository.countByIdAsignatura(asignatura.getId());
        String ciclo = cicloAsignatura(asignatura.getId());

        Label titulo = new Label(asignatura.getNombre());
        titulo.setWrapText(true);
        titulo.getStyleClass().add("class-card-title");

        Label tutor = new Label("Tutor: Profesor asignado");
        tutor.getStyleClass().add("class-card-text");

        Label aula = new Label("Ciclo: " + ciclo);
        aula.getStyleClass().add("class-card-text");

        Label alumnos = new Label("Alumnos: " + totalAlumnos);
        alumnos.getStyleClass().add("class-card-text");

        card.getChildren().addAll(titulo, tutor, aula, alumnos);

        if (esAdmin) {
            HBox acciones = new HBox(6);

            Button btnEditar = new Button("Editar");
            btnEditar.getStyleClass().addAll("btn-ghost", "class-card-btn");
            btnEditar.setOnAction(e -> onEditarClase(asignatura));

            Button btnEliminar = new Button("Eliminar");
            btnEliminar.getStyleClass().addAll("btn-ghost", "class-card-btn-danger");
            btnEliminar.setOnAction(e -> onEliminarClase(asignatura));

            acciones.getChildren().addAll(btnEditar, btnEliminar);
            card.getChildren().add(acciones);
        }

        return card;
    }

    private String cicloAsignatura(String idAsignatura) {
        List<Alumno> alumnos = alumnoRepository.findByIdAsignatura(idAsignatura);
        if (alumnos.isEmpty()) {
            return "Sin ciclo";
        }

        Set<String> ciclos = alumnos.stream()
                .map(Alumno::getGrupo)
                .filter(Objects::nonNull)
                .map(grupo -> grupo.replaceAll("\\d", ""))
                .collect(Collectors.toSet());

        if (ciclos.isEmpty()) return "Sin ciclo";
        if (ciclos.size() > 1) return "MIXTO";
        return ciclos.iterator().next();
    }

    private void onEditarClase(Asignatura asignatura) {
        Dialog<Asignatura> dialog = new Dialog<>();
        dialog.setTitle("Editar Clase");
        dialog.setHeaderText("Modifica los datos de la clase");

        ButtonType saveButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nombre = new TextField(asignatura.getNombre());
        TextField curso = new TextField(asignatura.getCurso());

        grid.add(new Label("Asignatura:"), 0, 0);
        grid.add(nombre, 1, 0);
        grid.add(new Label("Curso:"), 0, 1);
        grid.add(curso, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> dialogButton == saveButtonType
                ? new Asignatura(asignatura.getId(), nombre.getText(), curso.getText())
                : null);

        dialog.showAndWait().ifPresent(actualizada -> {
            if (actualizada.getNombre() == null || actualizada.getNombre().isBlank()) {
                mostrarAlerta("Error", "El nombre de la clase es obligatorio.");
                return;
            }
            asignaturaRepository.save(actualizada);
            cargarAsignaturasEnSelector();
            inicializarSeccionClases();
            mostrarAlerta("Éxito", "Clase actualizada correctamente.");
        });
    }

    private void onEliminarClase(Asignatura asignatura) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar clase");
        alert.setHeaderText("¿Eliminar " + asignatura.getNombre() + "?");
        alert.setContentText("La clase se eliminará y los alumnos quedarán sin asignatura asignada.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            List<Alumno> alumnos = alumnoRepository.findByIdAsignatura(asignatura.getId());
            for (Alumno alumno : alumnos) {
                alumno.setIdAsignatura(null);
            }
            alumnoRepository.saveAll(alumnos);
            asignaturaRepository.delete(asignatura);

            cargarAsignaturasEnSelector();
            inicializarSeccionClases();
            mostrarAlerta("Éxito", "Clase eliminada correctamente.");
        }
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
