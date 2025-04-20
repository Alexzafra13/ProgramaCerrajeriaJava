package com.gestiontaller.client.controller.trabajo;

import com.gestiontaller.client.api.TrabajoApiClient;
import com.gestiontaller.common.dto.trabajo.CambioEstadoDTO;
import com.gestiontaller.common.dto.trabajo.MaterialAsignadoDTO;
import com.gestiontaller.common.dto.trabajo.TrabajoDTO;
import com.gestiontaller.common.model.trabajo.PrioridadTrabajo;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component
public class TrabajoDetalleController {

    private static final Logger logger = LoggerFactory.getLogger(TrabajoDetalleController.class);

    @FXML private Label lblCodigo;
    @FXML private Label lblPresupuesto;
    @FXML private Label lblCliente;
    @FXML private Label lblFechaCreacion;
    @FXML private Label lblFechaProgramada;
    @FXML private Label lblFechaInicio;
    @FXML private Label lblFechaFinalizacion;
    @FXML private Label lblDireccion;
    @FXML private Label lblEstado;
    @FXML private Label lblPrioridad;
    @FXML private Label lblAsignado;
    @FXML private Label lblHorasReales;
    @FXML private TextArea txtObservaciones;
    @FXML private TableView<MaterialAsignadoDTO> tablaMateriales;
    @FXML private TableColumn<MaterialAsignadoDTO, String> colProducto;
    @FXML private TableColumn<MaterialAsignadoDTO, Integer> colCantidadAsignada;
    @FXML private TableColumn<MaterialAsignadoDTO, Integer> colCantidadUsada;
    @FXML private TableColumn<MaterialAsignadoDTO, String> colObservacionesMaterial;
    @FXML private TableView<CambioEstadoDTO> tablaHistorial;
    @FXML private TableColumn<CambioEstadoDTO, String> colFechaCambio;
    @FXML private TableColumn<CambioEstadoDTO, String> colEstadoAnterior;
    @FXML private TableColumn<CambioEstadoDTO, String> colEstadoNuevo;
    @FXML private TableColumn<CambioEstadoDTO, String> colUsuario;
    @FXML private TableColumn<CambioEstadoDTO, String> colMotivoCambio;
    @FXML private Button btnCambiarEstado;
    @FXML private Button btnCambiarPrioridad;
    @FXML private Button btnAsignarUsuario;
    @FXML private Button btnIniciarTrabajo;
    @FXML private Button btnFinalizarTrabajo;
    @FXML private Button btnCerrar;

    private final TrabajoApiClient trabajoApiClient;

    private TrabajoDTO trabajo;

    @Autowired
    public TrabajoDetalleController(TrabajoApiClient trabajoApiClient) {
        this.trabajoApiClient = trabajoApiClient;
    }

    @FXML
    public void initialize() {
        // Configurar columnas de la tabla de materiales
        colProducto.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombreProducto() != null ?
                        data.getValue().getNombreProducto() : ""));

        colCantidadAsignada.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getCantidadAsignada() != null ?
                        data.getValue().getCantidadAsignada() : 0).asObject());

        colCantidadUsada.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getCantidadUsada() != null ?
                        data.getValue().getCantidadUsada() : 0).asObject());

        colObservacionesMaterial.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getObservaciones() != null ?
                        data.getValue().getObservaciones() : ""));

        // Configurar columnas de la tabla de historial
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        colFechaCambio.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFechaCambio() != null ?
                        data.getValue().getFechaCambio().format(formatter) : ""));

        colEstadoAnterior.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombreEstadoAnterior() != null ?
                        data.getValue().getNombreEstadoAnterior() : ""));

        colEstadoNuevo.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombreEstadoNuevo() != null ?
                        data.getValue().getNombreEstadoNuevo() : ""));

        colUsuario.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombreUsuario() != null ?
                        data.getValue().getNombreUsuario() : ""));

        colMotivoCambio.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getMotivoCambio() != null ?
                        data.getValue().getMotivoCambio() : ""));

        // Deshabilitar edición
        txtObservaciones.setEditable(false);

        // Guardar referencia al controlador
        if (btnCerrar.getScene() != null) {
            btnCerrar.getScene().setUserData(this);
        }
    }

    public void setTrabajo(TrabajoDTO trabajo) {
        this.trabajo = trabajo;

        if (btnCerrar.getScene() == null) {
            // La vista aún no está completamente inicializada
            return;
        }

        // Cargar datos en los componentes
        cargarDatosTrabajo();

        // Cargar historial de cambios
        cargarHistorialCambios();
    }

    private void cargarDatosTrabajo() {
        // Formateo de fechas
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Datos básicos
        lblCodigo.setText(trabajo.getCodigo());

        // Presupuesto
        if (trabajo.getNumeroPresupuesto() != null) {
            lblPresupuesto.setText(trabajo.getNumeroPresupuesto());
        } else {
            lblPresupuesto.setText("N/A");
        }

        // Cliente
        lblCliente.setText(trabajo.getNombreCliente());

        // Fechas
        if (trabajo.getFechaCreacion() != null) {
            lblFechaCreacion.setText(trabajo.getFechaCreacion().format(dateTimeFormatter));
        } else {
            lblFechaCreacion.setText("N/A");
        }

        if (trabajo.getFechaProgramada() != null) {
            lblFechaProgramada.setText(trabajo.getFechaProgramada().format(dateFormatter));
        } else {
            lblFechaProgramada.setText("N/A");
        }

        if (trabajo.getFechaInicio() != null) {
            lblFechaInicio.setText(trabajo.getFechaInicio().format(dateTimeFormatter));
        } else {
            lblFechaInicio.setText("Pendiente de inicio");
        }

        if (trabajo.getFechaFinalizacion() != null) {
            lblFechaFinalizacion.setText(trabajo.getFechaFinalizacion().format(dateTimeFormatter));
        } else {
            lblFechaFinalizacion.setText("Pendiente de finalización");
        }

        // Dirección
        lblDireccion.setText(trabajo.getDireccionInstalacion() != null ?
                trabajo.getDireccionInstalacion() : "No especificada");

        // Estado con color
        lblEstado.setText(trabajo.getNombreEstado() != null ? trabajo.getNombreEstado() : "PENDIENTE");

        // Aplicar color al estado
        String colorEstado = trabajo.getColorEstado();
        if (colorEstado != null && !colorEstado.isEmpty()) {
            lblEstado.setStyle("-fx-background-color: " + colorEstado + "; -fx-padding: 5px; -fx-background-radius: 3px;");
        } else {
            // Colores por defecto si no viene el color del servidor
            switch (lblEstado.getText()) {
                case "PENDIENTE":
                    lblEstado.setStyle("-fx-background-color: #fff3cd; -fx-padding: 5px; -fx-background-radius: 3px;"); // Amarillo claro
                    break;
                case "EN_PROGRESO":
                    lblEstado.setStyle("-fx-background-color: #cce5ff; -fx-padding: 5px; -fx-background-radius: 3px;"); // Azul claro
                    break;
                case "TERMINADO":
                    lblEstado.setStyle("-fx-background-color: #d4edda; -fx-padding: 5px; -fx-background-radius: 3px;"); // Verde claro
                    break;
                case "ENTREGADO":
                    lblEstado.setStyle("-fx-background-color: #d1ecf1; -fx-padding: 5px; -fx-background-radius: 3px;"); // Azul-verde claro
                    break;
                case "CANCELADO":
                    lblEstado.setStyle("-fx-background-color: #f8d7da; -fx-padding: 5px; -fx-background-radius: 3px;"); // Rojo claro
                    break;
                default:
                    lblEstado.setStyle("-fx-padding: 5px; -fx-background-radius: 3px;");
                    break;
            }
        }

        // Prioridad con color
        lblPrioridad.setText(trabajo.getPrioridad() != null ? trabajo.getPrioridad().toString() : "NORMAL");

        // Aplicar color a la prioridad
        switch (trabajo.getPrioridad()) {
            case BAJA:
                lblPrioridad.setStyle("-fx-background-color: #d1ecf1; -fx-padding: 5px; -fx-background-radius: 3px;"); // Azul claro
                break;
            case NORMAL:
                lblPrioridad.setStyle("-fx-background-color: #fff3cd; -fx-padding: 5px; -fx-background-radius: 3px;"); // Amarillo claro
                break;
            case ALTA:
                lblPrioridad.setStyle("-fx-background-color: #ffcc80; -fx-padding: 5px; -fx-background-radius: 3px;"); // Naranja claro
                break;
            case URGENTE:
                lblPrioridad.setStyle("-fx-background-color: #f8d7da; -fx-padding: 5px; -fx-background-radius: 3px;"); // Rojo claro
                break;
            default:
                lblPrioridad.setStyle("-fx-padding: 5px; -fx-background-radius: 3px;");
                break;
        }

        // Usuario asignado
        lblAsignado.setText(trabajo.getNombreUsuarioAsignado() != null ?
                trabajo.getNombreUsuarioAsignado() : "Sin asignar");

        // Horas reales
        lblHorasReales.setText(trabajo.getHorasReales() != null ?
                trabajo.getHorasReales() + " horas" : "No registradas");

        // Observaciones
        txtObservaciones.setText(trabajo.getObservaciones() != null ?
                trabajo.getObservaciones() : "");

        // Materiales
        tablaMateriales.setItems(FXCollections.observableArrayList(trabajo.getMaterialesAsignados()));

        // Configurar botones según estado
        configurarBotones();
    }

    private void cargarHistorialCambios() {
        try {
            // Obtener historial de cambios del servidor
            List<CambioEstadoDTO> historial = trabajoApiClient.obtenerHistorialCambios(trabajo.getId());

            // Mostrar en la tabla
            tablaHistorial.setItems(FXCollections.observableArrayList(historial));
        } catch (Exception e) {
            logger.error("Error al cargar historial de cambios", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al cargar historial",
                    "No se pudo cargar el historial de cambios: " + e.getMessage());
        }
    }

    private void configurarBotones() {
        // Habilitar/deshabilitar botones según el estado actual
        boolean trabajoActivo = true;

        if (trabajo.getNombreEstado() != null) {
            if (trabajo.getNombreEstado().equals("ENTREGADO") ||
                    trabajo.getNombreEstado().equals("CANCELADO")) {
                trabajoActivo = false;
            }
        }

        // Cambio de estado siempre disponible para trabajos activos
        btnCambiarEstado.setDisable(!trabajoActivo);

        // Cambio de prioridad solo para trabajos activos
        btnCambiarPrioridad.setDisable(!trabajoActivo);

        // Asignación de usuario solo para trabajos activos
        btnAsignarUsuario.setDisable(!trabajoActivo);

        // Iniciar trabajo solo para PENDIENTE
        btnIniciarTrabajo.setDisable(!trabajoActivo ||
                !("PENDIENTE".equals(trabajo.getNombreEstado())));

        // Finalizar trabajo solo para EN_PROGRESO
        btnFinalizarTrabajo.setDisable(!trabajoActivo ||
                !("EN_PROGRESO".equals(trabajo.getNombreEstado())));
    }

    @FXML
    private void handleCambiarEstado() {
        try {
            // Verificar si el trabajo está activo
            if ("ENTREGADO".equals(trabajo.getNombreEstado()) ||
                    "CANCELADO".equals(trabajo.getNombreEstado())) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "No se puede cambiar el estado",
                        "Los trabajos entregados o cancelados no pueden cambiar de estado.");
                return;
            }

            // Crear diálogo para cambiar estado
            Dialog<Long> dialog = new Dialog<>();
            dialog.setTitle("Cambiar Estado");
            dialog.setHeaderText("Cambiar estado del trabajo " + trabajo.getCodigo());

            // Botones
            ButtonType cambiarButtonType = new ButtonType("Cambiar", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(cambiarButtonType, ButtonType.CANCEL);

            // Crear contenido
            VBox content = new VBox(10);
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

            Label lblEstadoActual = new Label("Estado actual: " + trabajo.getNombreEstado());
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
                trabajo = trabajoApiClient.cambiarEstado(
                        trabajo.getId(), estadoId, observaciones, motivoCambio, null);

                // Recargar datos
                cargarDatosTrabajo();
                cargarHistorialCambios();

                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Estado actualizado",
                        "El estado del trabajo ha sido actualizado correctamente.");
            }
        } catch (Exception e) {
            logger.error("Error al cambiar estado", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al cambiar estado",
                    "No se pudo cambiar el estado del trabajo: " + e.getMessage());
        }
    }

    @FXML
    private void handleCambiarPrioridad() {
        try {
            // Verificar si el trabajo está activo
            if ("ENTREGADO".equals(trabajo.getNombreEstado()) ||
                    "CANCELADO".equals(trabajo.getNombreEstado())) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "No se puede cambiar la prioridad",
                        "Los trabajos entregados o cancelados no pueden cambiar de prioridad.");
                return;
            }

            // Crear diálogo para cambiar prioridad
            Dialog<PrioridadTrabajo> dialog = new Dialog<>();
            dialog.setTitle("Cambiar Prioridad");
            dialog.setHeaderText("Cambiar prioridad del trabajo " + trabajo.getCodigo());

            // Botones
            ButtonType cambiarButtonType = new ButtonType("Cambiar", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(cambiarButtonType, ButtonType.CANCEL);

            // Crear contenido
            VBox content = new VBox(10);
            content.setStyle("-fx-padding: 20px;");

            ComboBox<PrioridadTrabajo> cmbNuevaPrioridad = new ComboBox<>();
            cmbNuevaPrioridad.setItems(FXCollections.observableArrayList(PrioridadTrabajo.values()));

            Label lblPrioridadActual = new Label("Prioridad actual: " + trabajo.getPrioridad());
            Label lblNuevaPrioridad = new Label("Nueva prioridad:");

            content.getChildren().addAll(
                    lblPrioridadActual,
                    lblNuevaPrioridad, cmbNuevaPrioridad
            );

            dialog.getDialogPane().setContent(content);

            // Convertir resultado
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == cambiarButtonType) {
                    return cmbNuevaPrioridad.getValue();
                }
                return null;
            });

            // Mostrar diálogo y procesar resultado
            Optional<PrioridadTrabajo> resultado = dialog.showAndWait();

            if (resultado.isPresent() && resultado.get() != null) {
                PrioridadTrabajo nuevaPrioridad = resultado.get();

                // Actualizar prioridad en el servidor
                trabajo = trabajoApiClient.actualizarPrioridad(trabajo.getId(), nuevaPrioridad);

                // Recargar datos
                cargarDatosTrabajo();

                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Prioridad actualizada",
                        "La prioridad del trabajo ha sido actualizada correctamente.");
            }
        } catch (Exception e) {
            logger.error("Error al cambiar prioridad", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al cambiar prioridad",
                    "No se pudo cambiar la prioridad del trabajo: " + e.getMessage());
        }
    }

    @FXML
    private void handleAsignarUsuario() {
        try {
            // Verificar si el trabajo está activo
            if ("ENTREGADO".equals(trabajo.getNombreEstado()) ||
                    "CANCELADO".equals(trabajo.getNombreEstado())) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "No se puede asignar usuario",
                        "Los trabajos entregados o cancelados no pueden ser asignados.");
                return;
            }

            // Crear diálogo para asignar usuario
            Dialog<Long> dialog = new Dialog<>();
            dialog.setTitle("Asignar Usuario");
            dialog.setHeaderText("Asignar usuario al trabajo " + trabajo.getCodigo());

            // Botones
            ButtonType asignarButtonType = new ButtonType("Asignar", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(asignarButtonType, ButtonType.CANCEL);

            // Crear contenido
            VBox content = new VBox(10);
            content.setStyle("-fx-padding: 20px;");

            // Aquí deberíamos cargar los usuarios desde el servidor
            // Por simplicidad, usamos algunos usuarios predefinidos
            ComboBox<String> cmbUsuario = new ComboBox<>();
            cmbUsuario.setItems(FXCollections.observableArrayList(
                    "Sin asignar",
                    "Técnico 1",
                    "Técnico 2",
                    "Técnico 3"
            ));

            Label lblUsuarioActual = new Label("Usuario actual: " +
                    (trabajo.getNombreUsuarioAsignado() != null ? trabajo.getNombreUsuarioAsignado() : "Sin asignar"));
            Label lblNuevoUsuario = new Label("Nuevo usuario:");

            content.getChildren().addAll(
                    lblUsuarioActual,
                    lblNuevoUsuario, cmbUsuario
            );

            dialog.getDialogPane().setContent(content);

            // Convertir resultado
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == asignarButtonType) {
                    String usuario = cmbUsuario.getValue();
                    if (usuario == null || usuario.isEmpty() || "Sin asignar".equals(usuario)) {
                        return null; // Usuario no asignado
                    }

                    // Obtener ID del usuario
                    return obtenerIdUsuario(usuario);
                }
                return null;
            });

            // Mostrar diálogo y procesar resultado
            Optional<Long> resultado = dialog.showAndWait();

            if (resultado.isPresent()) {
                Long usuarioId = resultado.get();

                // Asignar usuario en el servidor
                trabajo = trabajoApiClient.asignarUsuario(trabajo.getId(), usuarioId);

                // Recargar datos
                cargarDatosTrabajo();

                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Usuario asignado",
                        "El usuario ha sido asignado correctamente al trabajo.");
            }
        } catch (Exception e) {
            logger.error("Error al asignar usuario", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al asignar usuario",
                    "No se pudo asignar el usuario al trabajo: " + e.getMessage());
        }
    }

    @FXML
    private void handleIniciarTrabajo() {
        try {
            // Verificar si el trabajo está en estado PENDIENTE
            if (!"PENDIENTE".equals(trabajo.getNombreEstado())) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "No se puede iniciar el trabajo",
                        "Solo se pueden iniciar trabajos en estado PENDIENTE.");
                return;
            }

            // Confirmar inicio
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar inicio de trabajo");
            confirmacion.setHeaderText("¿Está seguro que desea iniciar este trabajo?");
            confirmacion.setContentText("El trabajo pasará a estado EN_PROGRESO y se registrará la fecha de inicio.");

            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                // El ID de usuario se obtendría de la sesión
                Long usuarioId = null; // Por simplicidad, usamos null

                // Iniciar trabajo
                trabajo = trabajoApiClient.cambiarEstado(
                        trabajo.getId(), obtenerIdEstado("EN_PROGRESO"),
                        "Trabajo iniciado", "Inicio de trabajo", usuarioId);

                // Recargar datos
                cargarDatosTrabajo();
                cargarHistorialCambios();

                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Trabajo iniciado",
                        "El trabajo ha sido iniciado correctamente.");
            }
        } catch (Exception e) {
            logger.error("Error al iniciar trabajo", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al iniciar trabajo",
                    "No se pudo iniciar el trabajo: " + e.getMessage());
        }
    }

    @FXML
    private void handleFinalizarTrabajo() {
        try {
            // Verificar si el trabajo está en estado EN_PROGRESO
            if (!"EN_PROGRESO".equals(trabajo.getNombreEstado())) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "No se puede finalizar el trabajo",
                        "Solo se pueden finalizar trabajos en estado EN_PROGRESO.");
                return;
            }

            // Crear diálogo para finalizar trabajo
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Finalizar Trabajo");
            dialog.setHeaderText("Finalizar trabajo " + trabajo.getCodigo());

            // Botones
            ButtonType finalizarButtonType = new ButtonType("Finalizar", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(finalizarButtonType, ButtonType.CANCEL);

            // Crear contenido
            VBox content = new VBox(10);
            content.setStyle("-fx-padding: 20px;");

            TextField txtHoras = new TextField();
            txtHoras.setPromptText("Horas realizadas");
            txtHoras.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.matches("\\d*")) {
                    txtHoras.setText(oldVal);
                }
            });

            TextArea txtObservacionesFinalizacion = new TextArea();
            txtObservacionesFinalizacion.setPromptText("Observaciones de finalización");
            txtObservacionesFinalizacion.setPrefRowCount(5);

            content.getChildren().addAll(
                    new Label("Horas realizadas:"), txtHoras,
                    new Label("Observaciones:"), txtObservacionesFinalizacion
            );

            dialog.getDialogPane().setContent(content);

            // Convertir resultado
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == finalizarButtonType) {
                    return txtObservacionesFinalizacion.getText();
                }
                return null;
            });

            // Mostrar diálogo y procesar resultado
            Optional<String> resultado = dialog.showAndWait();

            if (resultado.isPresent()) {
                String observaciones = resultado.get();

                // El ID de usuario se obtendría de la sesión
                Long usuarioId = null; // Por simplicidad, usamos null

                // Finalizar trabajo
                trabajo = trabajoApiClient.cambiarEstado(
                        trabajo.getId(), obtenerIdEstado("TERMINADO"),
                        observaciones, "Finalización de trabajo", usuarioId);

                // Actualizar horas realizadas si se especificaron
                if (!txtHoras.getText().isEmpty()) {
                    // Aquí habría que hacer una llamada adicional para actualizar las horas
                    // Por simplicidad, asumimos que se actualiza correctamente
                    trabajo.setHorasReales(Integer.parseInt(txtHoras.getText()));
                }

                // Recargar datos
                cargarDatosTrabajo();
                cargarHistorialCambios();

                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Trabajo finalizado",
                        "El trabajo ha sido finalizado correctamente.");
            }
        } catch (Exception e) {
            logger.error("Error al finalizar trabajo", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al finalizar trabajo",
                    "No se pudo finalizar el trabajo: " + e.getMessage());
        }
    }

    @FXML
    private void handleCerrar() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
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

    // Método simulado para obtener ID de usuario a partir de su nombre
    private Long obtenerIdUsuario(String nombreUsuario) {
        // En una implementación real, esto podría ser una llamada al servidor
        // o una búsqueda en un mapa previamente cargado
        switch (nombreUsuario) {
            case "Técnico 1": return 2L;
            case "Técnico 2": return 3L;
            case "Técnico 3": return 4L;
            default: return null;
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