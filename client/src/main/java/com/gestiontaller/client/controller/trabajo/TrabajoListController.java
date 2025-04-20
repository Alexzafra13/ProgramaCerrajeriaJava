package com.gestiontaller.client.controller.trabajo;

import com.gestiontaller.client.api.TrabajoApiClient;
import com.gestiontaller.client.util.FXMLLoaderUtil;
import com.gestiontaller.common.dto.trabajo.TrabajoDTO;
import com.gestiontaller.common.model.trabajo.PrioridadTrabajo;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component
public class TrabajoListController {

    private static final Logger logger = LoggerFactory.getLogger(TrabajoListController.class);

    @FXML private TextField txtCliente;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private DatePicker dpDesde;
    @FXML private DatePicker dpHasta;
    @FXML private TableView<TrabajoDTO> tablaTrabajos;
    @FXML private TableColumn<TrabajoDTO, String> colCodigo;
    @FXML private TableColumn<TrabajoDTO, String> colFecha;
    @FXML private TableColumn<TrabajoDTO, String> colCliente;
    @FXML private TableColumn<TrabajoDTO, String> colEstado;
    @FXML private TableColumn<TrabajoDTO, PrioridadTrabajo> colPrioridad;
    @FXML private TableColumn<TrabajoDTO, String> colAsignado;
    @FXML private Button btnNuevoTrabajo;
    @FXML private Button btnEditar;
    @FXML private Button btnCambiarEstado;
    @FXML private Button btnVerDetalles;

    private final TrabajoApiClient trabajoApiClient;
    private final FXMLLoaderUtil fxmlLoaderUtil;

    @Autowired
    public TrabajoListController(TrabajoApiClient trabajoApiClient,
                                 FXMLLoaderUtil fxmlLoaderUtil) {
        this.trabajoApiClient = trabajoApiClient;
        this.fxmlLoaderUtil = fxmlLoaderUtil;
    }

    @FXML
    public void initialize() {
        // Inicializar fechas
        dpDesde.setValue(LocalDate.now().minusMonths(1));
        dpHasta.setValue(LocalDate.now());

        // Cargar estados disponibles
        cargarEstados();

        // Configurar columnas de la tabla
        configurarColumnas();

        // Configurar acción de selección
        tablaTrabajos.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    boolean haySeleccion = newSelection != null;
                    btnEditar.setDisable(!haySeleccion);
                    btnCambiarEstado.setDisable(!haySeleccion);
                    btnVerDetalles.setDisable(!haySeleccion);
                });

        // Configurar doble clic para ver detalles
        tablaTrabajos.setRowFactory(tv -> {
            TableRow<TrabajoDTO> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    handleVerDetalles();
                }
            });
            return row;
        });

        // Cargar datos iniciales
        cargarTrabajos();
    }

    private void cargarEstados() {
        try {
            // Aquí podrías cargar los estados desde el servidor
            // Por ahora usaremos algunos estados predefinidos
            cmbEstado.setItems(FXCollections.observableArrayList(
                    "",  // Opción para todos los estados
                    "PENDIENTE",
                    "EN_PROGRESO",
                    "TERMINADO",
                    "ENTREGADO",
                    "CANCELADO"
            ));
            cmbEstado.setPromptText("Todos los estados");
        } catch (Exception e) {
            logger.error("Error al cargar estados", e);
        }
    }

    private void configurarColumnas() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        colCodigo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCodigo()));

        colFecha.setCellValueFactory(data -> {
            TrabajoDTO trabajo = data.getValue();
            String fecha = trabajo.getFechaCreacion() != null ?
                    trabajo.getFechaCreacion().toLocalDate().format(formatter) : "";
            return new SimpleStringProperty(fecha);
        });

        colCliente.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombreCliente()));

        colEstado.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombreEstado()));

        // Colorear celda de estado según su valor
        colEstado.setCellFactory(column -> new TableCell<TrabajoDTO, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);

                    // Color de fondo según estado
                    String color = data.getValue().getColorEstado();
                    if (color != null && !color.isEmpty()) {
                        setStyle("-fx-background-color: " + color + ";");
                    } else {
                        // Colores por defecto si no viene el color del servidor
                        switch (item) {
                            case "PENDIENTE":
                                setStyle("-fx-background-color: #fff3cd;"); // Amarillo claro
                                break;
                            case "EN_PROGRESO":
                                setStyle("-fx-background-color: #cce5ff;"); // Azul claro
                                break;
                            case "TERMINADO":
                                setStyle("-fx-background-color: #d4edda;"); // Verde claro
                                break;
                            case "ENTREGADO":
                                setStyle("-fx-background-color: #d1ecf1;"); // Azul-verde claro
                                break;
                            case "CANCELADO":
                                setStyle("-fx-background-color: #f8d7da;"); // Rojo claro
                                break;
                            default:
                                setStyle("");
                                break;
                        }
                    }
                }
            }
        });

        colPrioridad.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getPrioridad()));

        // Colorear celda de prioridad según su valor
        colPrioridad.setCellFactory(column -> new TableCell<TrabajoDTO, PrioridadTrabajo>() {
            @Override
            protected void updateItem(PrioridadTrabajo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());

                    // Color de fondo según prioridad
                    switch (item) {
                        case BAJA:
                            setStyle("-fx-background-color: #d1ecf1;"); // Azul claro
                            break;
                        case NORMAL:
                            setStyle("-fx-background-color: #fff3cd;"); // Amarillo claro
                            break;
                        case ALTA:
                            setStyle("-fx-background-color: #ffcc80;"); // Naranja claro
                            break;
                        case URGENTE:
                            setStyle("-fx-background-color: #f8d7da;"); // Rojo claro
                            break;
                        default:
                            setStyle("");
                            break;
                    }
                }
            }
        });

        colAsignado.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombreUsuarioAsignado() != null ?
                        data.getValue().getNombreUsuarioAsignado() : "Sin asignar"));
    }

    private void cargarTrabajos() {
        try {
            List<TrabajoDTO> trabajos;
            String textoBusqueda = txtCliente.getText().trim();
            String estadoSeleccionado = cmbEstado.getValue();

            if (!textoBusqueda.isEmpty() && estadoSeleccionado != null && !estadoSeleccionado.isEmpty()) {
                // Buscar por texto y estado
                // Aquí tendríamos que obtener el ID del estado a partir de su nombre
                // Por simplicidad, asumimos que tenemos una función para ello
                Long estadoId = obtenerIdEstado(estadoSeleccionado);
                trabajos = trabajoApiClient.buscarPorEstado(textoBusqueda, estadoId);
            } else if (!textoBusqueda.isEmpty()) {
                // Buscar solo por texto
                trabajos = trabajoApiClient.buscar(textoBusqueda);
            } else if (estadoSeleccionado != null && !estadoSeleccionado.isEmpty()) {
                // Buscar solo por estado
                Long estadoId = obtenerIdEstado(estadoSeleccionado);
                trabajos = trabajoApiClient.obtenerPorEstado(estadoId);
            } else if (dpDesde.getValue() != null && dpHasta.getValue() != null) {
                // Buscar por rango de fechas
                trabajos = trabajoApiClient.obtenerPorFechasProgramadas(dpDesde.getValue(), dpHasta.getValue());
            } else {
                // Obtener todos los trabajos
                trabajos = trabajoApiClient.obtenerTodos();
            }

            tablaTrabajos.setItems(FXCollections.observableArrayList(trabajos));
        } catch (Exception e) {
            logger.error("Error al cargar trabajos", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al cargar trabajos",
                    "No se pudieron cargar los trabajos: " + e.getMessage());
        }
    }

    // Método simulado para obtener ID de estado a partir de su nombre
    private Long obtenerIdEstado(String nombreEstado) {
        // En una implementación real, esto podría ser una llamada al servidor
        // o una búsqueda en un mapa previamente cargado
        switch (nombreEstado) {
            case "PENDIENTE": return 1L;
            case "EN_PROGRESO": return 2L;
            case "TERMINADO": return 3L;
            case "ENTREGADO": return 4L;
            case "CANCELADO": return 5L;
            default: return null;
        }
    }

    @FXML
    private void handleBuscar() {
        cargarTrabajos();
    }

    @FXML
    private void handleNuevoTrabajo() {
        try {
            // Cargar vista del formulario
            Parent root = fxmlLoaderUtil.loadFXML("/fxml/trabajo/trabajo-form.fxml");

            // Obtener controlador y configurarlo para nuevo trabajo
            TrabajoFormController controller = (TrabajoFormController) root.getUserData();
            controller.setTrabajo(new TrabajoDTO(), false);

            // Configurar y mostrar ventana
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Nuevo Trabajo");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Recargar lista después de cerrar
            cargarTrabajos();
        } catch (Exception e) {
            logger.error("Error al abrir formulario de trabajo", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al abrir formulario",
                    "No se pudo abrir el formulario de trabajo: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditar() {
        TrabajoDTO trabajoSeleccionado = tablaTrabajos.getSelectionModel().getSelectedItem();
        if (trabajoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Selección requerida",
                    "Por favor, seleccione un trabajo para editar.");
            return;
        }

        try {
            // Cargar trabajo completo
            TrabajoDTO trabajoCompleto = trabajoApiClient.obtenerPorId(trabajoSeleccionado.getId());

            // Cargar vista del formulario
            Parent root = fxmlLoaderUtil.loadFXML("/fxml/trabajo/trabajo-form.fxml");

            // Obtener controlador y configurarlo para editar
            TrabajoFormController controller = (TrabajoFormController) root.getUserData();
            controller.setTrabajo(trabajoCompleto, true);

            // Configurar y mostrar ventana
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Editar Trabajo");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Recargar lista después de cerrar
            cargarTrabajos();
        } catch (Exception e) {
            logger.error("Error al abrir formulario de edición", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al abrir formulario",
                    "No se pudo abrir el formulario de edición: " + e.getMessage());
        }
    }

    @FXML
    private void handleCambiarEstado() {
        TrabajoDTO trabajoSeleccionado = tablaTrabajos.getSelectionModel().getSelectedItem();
        if (trabajoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Selección requerida",
                    "Por favor, seleccione un trabajo para cambiar su estado.");
            return;
        }

        try {
            // Crear diálogo para cambiar estado
            Dialog<Long> dialog = new Dialog<>();
            dialog.setTitle("Cambiar Estado");
            dialog.setHeaderText("Cambiar estado del trabajo " + trabajoSeleccionado.getCodigo());

            // Botones
            ButtonType cambiarButtonType = new ButtonType("Cambiar", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(cambiarButtonType, ButtonType.CANCEL);

            // Crear contenido
            javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(10);
            content.setStyle("-fx-padding: 20px;");

            // Aquí deberíamos cargar los estados disponibles desde el servidor
            // Por simplicidad, usamos algunos estados predefinidos
            ComboBox<String> cmbNuevoEstado = new ComboBox<>();
            cmbNuevoEstado.setItems(FXCollections.observableArrayList(
                    "PENDIENTE",
                    "EN_PROGRESO",
                    "TERMINADO",
                    "ENTREGADO",
                    "CANCELADO"
            ));

            Label lblEstadoActual = new Label("Estado actual: " + trabajoSeleccionado.getNombreEstado());
            Label lblNuevoEstado = new Label("Nuevo estado:");

            TextArea txtObservaciones = new TextArea();
            txtObservaciones.setPromptText("Observaciones del cambio");
            txtObservaciones.setPrefRowCount(3);

            TextField txtMotivoCambio = new TextField();
            txtMotivoCambio.setPromptText("Motivo del cambio");

            content.getChildren().addAll(
                    lblEstadoActual,
                    lblNuevoEstado, cmbNuevoEstado,
                    new Label("Motivo:"), txtMotivoCambio,
                    new Label("Observaciones:"), txtObservaciones
            );

            dialog.getDialogPane().setContent(content);

            // Convertir resultado
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == cambiarButtonType) {
                    String estado = cmbNuevoEstado.getValue();
                    if (estado == null || estado.isEmpty()) {
                        mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Estado requerido",
                                "Por favor, seleccione un nuevo estado.");
                        return null;
                    }

                    // Obtener ID del estado
                    return obtenerIdEstado(estado);
                }
                return null;
            });

            // Mostrar diálogo y procesar resultado
            Optional<Long> resultado = dialog.showAndWait();

            if (resultado.isPresent() && resultado.get() != null) {
                Long estadoId = resultado.get();
                String observaciones = txtObservaciones.getText();
                String motivoCambio = txtMotivoCambio.getText();

                // Cambiar estado en el servidor
                TrabajoDTO trabajoActualizado = trabajoApiClient.cambiarEstado(
                        trabajoSeleccionado.getId(), estadoId, observaciones, motivoCambio, null);

                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Estado actualizado",
                        "El estado del trabajo ha sido actualizado correctamente.");

                // Recargar lista
                cargarTrabajos();
            }
        } catch (Exception e) {
            logger.error("Error al cambiar estado", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al cambiar estado",
                    "No se pudo cambiar el estado del trabajo: " + e.getMessage());
        }
    }

    @FXML
    private void handleVerDetalles() {
        TrabajoDTO trabajoSeleccionado = tablaTrabajos.getSelectionModel().getSelectedItem();
        if (trabajoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Selección requerida",
                    "Por favor, seleccione un trabajo para ver sus detalles.");
            return;
        }

        try {
            // Cargar trabajo completo
            TrabajoDTO trabajoCompleto = trabajoApiClient.obtenerPorId(trabajoSeleccionado.getId());

            // Cargar vista de detalles
            Parent root = fxmlLoaderUtil.loadFXML("/fxml/trabajo/trabajo-detalle.fxml");

            // Obtener controlador y configurarlo
            TrabajoDetalleController controller = (TrabajoDetalleController) root.getUserData();
            controller.setTrabajo(trabajoCompleto);

            // Configurar y mostrar ventana
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Detalles del Trabajo");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Recargar lista por si hubo cambios
            cargarTrabajos();
        } catch (Exception e) {
            logger.error("Error al abrir vista de detalles", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al abrir detalles",
                    "No se pudo abrir la vista de detalles: " + e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String cabecera, String contenido) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(cabecera);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
}