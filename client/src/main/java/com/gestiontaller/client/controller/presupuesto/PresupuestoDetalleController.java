package com.gestiontaller.client.controller.presupuesto;

import com.gestiontaller.client.api.PresupuestoApiClient;
import com.gestiontaller.client.api.TrabajoApiClient;
import com.gestiontaller.common.dto.presupuesto.LineaPresupuestoDTO;
import com.gestiontaller.common.dto.presupuesto.PresupuestoDTO;
import com.gestiontaller.common.model.presupuesto.EstadoPresupuesto;

import javafx.beans.property.SimpleDoubleProperty;
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
import java.util.Optional;

@Component
public class PresupuestoDetalleController {

    private static final Logger logger = LoggerFactory.getLogger(PresupuestoDetalleController.class);

    @FXML private Label lblNumero;
    @FXML private Label lblFechaCreacion;
    @FXML private Label lblFechaValidez;
    @FXML private Label lblCliente;
    @FXML private Label lblUsuario;
    @FXML private Label lblEstado;
    @FXML private Label lblDireccionObra;
    @FXML private Label lblReferencia;
    @FXML private Label lblTiempoEstimado;
    @FXML private Label lblDescuento;
    @FXML private TextArea txtObservaciones;
    @FXML private Label lblMotivoRechazo;
    @FXML private TableView<LineaPresupuestoDTO> tablaLineas;
    @FXML private TableColumn<LineaPresupuestoDTO, Integer> colOrden;
    @FXML private TableColumn<LineaPresupuestoDTO, String> colDescripcion;
    @FXML private TableColumn<LineaPresupuestoDTO, Integer> colCantidad;
    @FXML private TableColumn<LineaPresupuestoDTO, String> colMedidas;
    @FXML private TableColumn<LineaPresupuestoDTO, Double> colPrecioUnitario;
    @FXML private TableColumn<LineaPresupuestoDTO, Double> colImporte;
    @FXML private Label lblBaseImponible;
    @FXML private Label lblIVA;
    @FXML private Label lblTotalPresupuesto;
    @FXML private Button btnCambiarEstado;
    @FXML private Button btnGenerarTrabajo;
    @FXML private Button btnImprimir;
    @FXML private Button btnCerrar;

    private final PresupuestoApiClient presupuestoApiClient;
    private final TrabajoApiClient trabajoApiClient;

    private PresupuestoDTO presupuesto;

    @Autowired
    public PresupuestoDetalleController(
            PresupuestoApiClient presupuestoApiClient,
            TrabajoApiClient trabajoApiClient) {
        this.presupuestoApiClient = presupuestoApiClient;
        this.trabajoApiClient = trabajoApiClient;
    }

    @FXML
    public void initialize() {
        // Configurar columnas de la tabla
        colOrden.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getOrden()).asObject());

        colDescripcion.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescripcion()));

        colCantidad.setCellValueFactory(data -> new SimpleIntegerProperty(
                data.getValue().getCantidad() != null ? data.getValue().getCantidad() : 1).asObject());

        colMedidas.setCellValueFactory(data -> {
            LineaPresupuestoDTO linea = data.getValue();
            if (linea.getMedidas() != null && !linea.getMedidas().isEmpty()) {
                return new SimpleStringProperty(linea.getMedidas());
            } else if (linea.getAncho() != null && linea.getAlto() != null) {
                return new SimpleStringProperty(linea.getAncho() + "x" + linea.getAlto() + " mm");
            } else {
                return new SimpleStringProperty("");
            }
        });

        colPrecioUnitario.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getPrecioUnitario()).asObject());

        colPrecioUnitario.setCellFactory(column -> new TableCell<LineaPresupuestoDTO, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f €", item));
                }
            }
        });

        colImporte.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getImporte()).asObject());

        colImporte.setCellFactory(column -> new TableCell<LineaPresupuestoDTO, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f €", item));
                }
            }
        });

        // Deshabilitar edición
        txtObservaciones.setEditable(false);

        // Guardar una referencia al controlador para acceso desde otras clases
        if (btnCerrar.getScene() != null) {
            btnCerrar.getScene().setUserData(this);
        }
    }

    public void setPresupuesto(PresupuestoDTO presupuesto) {
        this.presupuesto = presupuesto;

        if (btnCerrar.getScene() == null) {
            // Aún no se ha inicializado la vista completamente
            return;
        }

        // Cargar datos en los componentes
        cargarDatosPresupuesto();
    }

    private void cargarDatosPresupuesto() {
        // Formateo de fechas
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Datos básicos
        lblNumero.setText(presupuesto.getNumero());

        if (presupuesto.getFechaCreacion() != null) {
            lblFechaCreacion.setText(presupuesto.getFechaCreacion().format(dateTimeFormatter));
        } else {
            lblFechaCreacion.setText("N/A");
        }

        if (presupuesto.getFechaValidez() != null) {
            lblFechaValidez.setText(presupuesto.getFechaValidez().format(dateFormatter));
        } else {
            lblFechaValidez.setText("N/A");
        }

        lblCliente.setText(presupuesto.getNombreCliente());
        lblUsuario.setText(presupuesto.getNombreUsuario());

        // Estado con color
        lblEstado.setText(presupuesto.getEstado().toString());

        // Aplicar estilo según estado
        String colorFondo;
        switch (presupuesto.getEstado()) {
            case PENDIENTE:
                colorFondo = "#fff3cd"; // Amarillo claro
                break;
            case ACEPTADO:
                colorFondo = "#d4edda"; // Verde claro
                break;
            case RECHAZADO:
                colorFondo = "#f8d7da"; // Rojo claro
                break;
            case FACTURADO:
                colorFondo = "#cce5ff"; // Azul claro
                break;
            case CANCELADO:
                colorFondo = "#e2e3e5"; // Gris claro
                break;
            default:
                colorFondo = "";
                break;
        }

        lblEstado.setStyle("-fx-background-color: " + colorFondo + "; -fx-padding: 5px; -fx-background-radius: 3px;");

        // Datos adicionales
        lblDireccionObra.setText(presupuesto.getDireccionObra() != null ? presupuesto.getDireccionObra() : "");
        lblReferencia.setText(presupuesto.getReferencia() != null ? presupuesto.getReferencia() : "");

        if (presupuesto.getTiempoEstimado() != null) {
            lblTiempoEstimado.setText(presupuesto.getTiempoEstimado() + " horas");
        } else {
            lblTiempoEstimado.setText("No especificado");
        }

        lblDescuento.setText(String.format("%.2f%%", presupuesto.getDescuento()));

        txtObservaciones.setText(presupuesto.getObservaciones() != null ? presupuesto.getObservaciones() : "");

        // Motivo de rechazo
        if (presupuesto.getEstado() == EstadoPresupuesto.RECHAZADO && presupuesto.getMotivoRechazo() != null) {
            lblMotivoRechazo.setText(presupuesto.getMotivoRechazo());
            lblMotivoRechazo.setVisible(true);
        } else {
            lblMotivoRechazo.setVisible(false);
        }

        // Líneas de presupuesto
        tablaLineas.setItems(FXCollections.observableArrayList(presupuesto.getLineas()));

        // Mostrar totales
        lblBaseImponible.setText(String.format("%.2f €", presupuesto.getBaseImponible()));
        lblIVA.setText(String.format("%.2f €", presupuesto.getImporteIva()));
        lblTotalPresupuesto.setText(String.format("%.2f €", presupuesto.getTotalPresupuesto()));

        // Configurar botones según estado
        actualizarBotones();
    }

    private void actualizarBotones() {
        // Cambiar estado siempre disponible excepto para FACTURADO o CANCELADO
        btnCambiarEstado.setDisable(
                presupuesto.getEstado() == EstadoPresupuesto.FACTURADO ||
                        presupuesto.getEstado() == EstadoPresupuesto.CANCELADO
        );

        // Generar trabajo solo disponible para ACEPTADO
        btnGenerarTrabajo.setDisable(presupuesto.getEstado() != EstadoPresupuesto.ACEPTADO);
    }

    @FXML
    private void handleCambiarEstado() {
        try {
            // No permitir cambiar estado para facturados o cancelados
            if (presupuesto.getEstado() == EstadoPresupuesto.FACTURADO ||
                    presupuesto.getEstado() == EstadoPresupuesto.CANCELADO) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "No se puede cambiar el estado",
                        "No se pueden cambiar presupuestos facturados o cancelados.");
                return;
            }

            // Crear diálogo para seleccionar nuevo estado
            Dialog<EstadoPresupuesto> dialog = new Dialog<>();
            dialog.setTitle("Cambiar Estado");
            dialog.setHeaderText("Seleccione el nuevo estado para el presupuesto");

            // Botones
            ButtonType cambiarButtonType = new ButtonType("Cambiar", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(cambiarButtonType, ButtonType.CANCEL);

            // Crear contenido del diálogo
            VBox content = new VBox(10);
            content.setStyle("-fx-padding: 20px;");

            // ComboBox de estados
            ComboBox<EstadoPresupuesto> cmbNuevoEstado = new ComboBox<>();
            cmbNuevoEstado.setPromptText("Seleccione nuevo estado");

            // Filtrar estados según el estado actual (evitar transiciones no permitidas)
            EstadoPresupuesto[] estadosDisponibles;
            switch (presupuesto.getEstado()) {
                case PENDIENTE:
                    estadosDisponibles = new EstadoPresupuesto[]{
                            EstadoPresupuesto.ACEPTADO,
                            EstadoPresupuesto.RECHAZADO,
                            EstadoPresupuesto.CANCELADO
                    };
                    break;
                case ENVIADO:
                    estadosDisponibles = new EstadoPresupuesto[]{
                            EstadoPresupuesto.ACEPTADO,
                            EstadoPresupuesto.RECHAZADO,
                            EstadoPresupuesto.CANCELADO
                    };
                    break;
                case ACEPTADO:
                    estadosDisponibles = new EstadoPresupuesto[]{
                            EstadoPresupuesto.FACTURADO,
                            EstadoPresupuesto.CANCELADO
                    };
                    break;
                case RECHAZADO:
                    estadosDisponibles = new EstadoPresupuesto[]{
                            EstadoPresupuesto.CANCELADO
                    };
                    break;
                default:
                    estadosDisponibles = new EstadoPresupuesto[]{};
                    break;
            }

            cmbNuevoEstado.setItems(FXCollections.observableArrayList(estadosDisponibles));
            Label lblEstadoActual = new Label("Estado actual: " + presupuesto.getEstado());
            Label lblNuevoEstado = new Label("Nuevo estado:");

            content.getChildren().addAll(lblEstadoActual, lblNuevoEstado, cmbNuevoEstado);

            // Campo para motivo de rechazo (solo visible si se selecciona RECHAZADO)
            TextArea txtMotivoRechazo = new TextArea();
            txtMotivoRechazo.setPromptText("Indique el motivo del rechazo");
            txtMotivoRechazo.setPrefRowCount(3);

            Label lblMotivoRechazo = new Label("Motivo de rechazo:");
            VBox motivoRechazoBox = new VBox(5, lblMotivoRechazo, txtMotivoRechazo);
            motivoRechazoBox.setVisible(false);

            content.getChildren().add(motivoRechazoBox);

            // Mostrar/ocultar campo de motivo según el estado seleccionado
            cmbNuevoEstado.valueProperty().addListener((obs, oldVal, newVal) -> {
                motivoRechazoBox.setVisible(newVal == EstadoPresupuesto.RECHAZADO);
            });

            dialog.getDialogPane().setContent(content);

            // Convertir el resultado
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == cambiarButtonType) {
                    return cmbNuevoEstado.getValue();
                }
                return null;
            });

            // Mostrar diálogo y procesar resultado
            Optional<EstadoPresupuesto> resultado = dialog.showAndWait();

            if (resultado.isPresent() && resultado.get() != null) {
                EstadoPresupuesto nuevoEstado = resultado.get();
                String motivoRechazo = null;

                // Capturar motivo de rechazo si corresponde
                if (nuevoEstado == EstadoPresupuesto.RECHAZADO) {
                    motivoRechazo = txtMotivoRechazo.getText();
                    if (motivoRechazo.trim().isEmpty()) {
                        mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Motivo requerido",
                                "Debe indicar un motivo para rechazar el presupuesto.");
                        return;
                    }
                }

                // Actualizar estado en el servidor
                presupuesto = presupuestoApiClient.actualizarEstado(
                        presupuesto.getId(), nuevoEstado, motivoRechazo);

                // Recargar datos
                cargarDatosPresupuesto();

                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Estado actualizado",
                        "El estado del presupuesto ha sido actualizado correctamente.");
            }
        } catch (Exception e) {
            logger.error("Error al cambiar estado", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al cambiar estado",
                    "No se pudo cambiar el estado del presupuesto: " + e.getMessage());
        }
    }

    @FXML
    private void handleGenerarTrabajo() {
        // Verificar que el presupuesto esté aceptado
        if (presupuesto.getEstado() != EstadoPresupuesto.ACEPTADO) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "No se puede generar trabajo",
                    "Solo se pueden generar trabajos a partir de presupuestos aceptados.");
            return;
        }

        // Confirmar generación de trabajo
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar generación de trabajo");
        confirmacion.setHeaderText("¿Está seguro que desea generar un trabajo a partir de este presupuesto?");
        confirmacion.setContentText("Se creará un nuevo trabajo basado en el presupuesto " + presupuesto.getNumero());

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                // Generar trabajo
                trabajoApiClient.crearDesdePrespuesto(presupuesto.getId());

                // Recargar presupuesto para ver posibles cambios de estado
                presupuesto = presupuestoApiClient.obtenerPorId(presupuesto.getId());
                cargarDatosPresupuesto();

                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Trabajo generado",
                        "El trabajo ha sido generado correctamente a partir del presupuesto.");
            } catch (Exception e) {
                logger.error("Error al generar trabajo", e);
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al generar trabajo",
                        "No se pudo generar el trabajo: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleImprimir() {
        mostrarAlerta(Alert.AlertType.INFORMATION, "Información", "Función no disponible",
                "La función de impresión aún no está implementada.");
    }

    @FXML
    private void handleCerrar() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String cabecera, String contenido) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(cabecera);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
}