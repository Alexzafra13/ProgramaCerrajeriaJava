package com.gestiontaller.client.controller.presupuesto;

import com.gestiontaller.client.api.PresupuestoApiClient;
import com.gestiontaller.client.api.TrabajoApiClient;
import com.gestiontaller.client.util.FXMLLoaderUtil;
import com.gestiontaller.common.dto.presupuesto.PresupuestoDTO;
import com.gestiontaller.common.model.presupuesto.EstadoPresupuesto;

import javafx.beans.property.SimpleDoubleProperty;
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
public class PresupuestoListController {

    private static final Logger logger = LoggerFactory.getLogger(PresupuestoListController.class);

    @FXML private TextField txtCliente;
    @FXML private ComboBox<EstadoPresupuesto> cmbEstado;
    @FXML private DatePicker dpDesde;
    @FXML private DatePicker dpHasta;
    @FXML private TableView<PresupuestoDTO> tablaPresupuestos;
    @FXML private TableColumn<PresupuestoDTO, String> colNumero;
    @FXML private TableColumn<PresupuestoDTO, String> colFecha;
    @FXML private TableColumn<PresupuestoDTO, String> colCliente;
    @FXML private TableColumn<PresupuestoDTO, Double> colTotal;
    @FXML private TableColumn<PresupuestoDTO, EstadoPresupuesto> colEstado;
    @FXML private Button btnNuevoPresupuesto;
    @FXML private Button btnEditar;
    @FXML private Button btnVerDetalles;
    @FXML private Button btnGenerarTrabajo;

    private final PresupuestoApiClient presupuestoApiClient;
    private final TrabajoApiClient trabajoApiClient;
    private final FXMLLoaderUtil fxmlLoaderUtil;

    @Autowired
    public PresupuestoListController(PresupuestoApiClient presupuestoApiClient,
                                     TrabajoApiClient trabajoApiClient,
                                     FXMLLoaderUtil fxmlLoaderUtil) {
        this.presupuestoApiClient = presupuestoApiClient;
        this.trabajoApiClient = trabajoApiClient;
        this.fxmlLoaderUtil = fxmlLoaderUtil;
    }

    @FXML
    public void initialize() {
        // Configurar DatePickers
        dpDesde.setValue(LocalDate.now().minusMonths(1));
        dpHasta.setValue(LocalDate.now());

        // Configurar ComboBox de estados
        cmbEstado.setItems(FXCollections.observableArrayList(EstadoPresupuesto.values()));
        cmbEstado.getItems().add(0, null); // Opción para todos los estados
        cmbEstado.setPromptText("Todos");

        // Configurar columnas de la tabla
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        colNumero.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNumero()));

        colFecha.setCellValueFactory(data -> {
            if (data.getValue().getFechaCreacion() != null) {
                return new SimpleStringProperty(data.getValue().getFechaCreacion().toLocalDate().format(formatter));
            }
            return new SimpleStringProperty("");
        });

        colCliente.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombreCliente()));

        colTotal.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getTotalPresupuesto()).asObject());
        colTotal.setCellFactory(column -> new TableCell<PresupuestoDTO, Double>() {
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

        colEstado.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getEstado()));
        colEstado.setCellFactory(column -> new TableCell<PresupuestoDTO, EstadoPresupuesto>() {
            @Override
            protected void updateItem(EstadoPresupuesto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    setText(item.toString());

                    // Establecer color de fondo según estado
                    String colorFondo;
                    switch (item) {
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

                    setStyle("-fx-background-color: " + colorFondo + ";");
                }
            }
        });

        // Configurar evento de selección para habilitar/deshabilitar botones
        tablaPresupuestos.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    boolean haySeleccion = newSelection != null;
                    btnEditar.setDisable(!haySeleccion);
                    btnVerDetalles.setDisable(!haySeleccion);

                    // Solo permitir generar trabajo desde presupuestos aceptados
                    boolean puedeGenerarTrabajo = haySeleccion &&
                            newSelection.getEstado() == EstadoPresupuesto.ACEPTADO;
                    btnGenerarTrabajo.setDisable(!puedeGenerarTrabajo);
                });

        // Cargar datos iniciales
        cargarPresupuestos();
    }

    @FXML
    private void handleBuscar() {
        cargarPresupuestos();
    }

    private void cargarPresupuestos() {
        try {
            List<PresupuestoDTO> presupuestos;
            String textoBusqueda = txtCliente.getText().trim();
            EstadoPresupuesto estado = cmbEstado.getValue();

            if (!textoBusqueda.isEmpty() && estado != null) {
                // Buscar por texto y estado
                presupuestos = presupuestoApiClient.buscarPorEstado(textoBusqueda, estado);
            } else if (!textoBusqueda.isEmpty()) {
                // Buscar solo por texto
                presupuestos = presupuestoApiClient.buscar(textoBusqueda);
            } else if (estado != null) {
                // Buscar solo por estado
                presupuestos = presupuestoApiClient.obtenerPorEstado(estado);
            } else if (dpDesde.getValue() != null && dpHasta.getValue() != null) {
                // Buscar por fechas
                presupuestos = presupuestoApiClient.obtenerPorFechas(dpDesde.getValue(), dpHasta.getValue());
            } else {
                // Cargar todos
                presupuestos = presupuestoApiClient.obtenerTodos();
            }

            tablaPresupuestos.setItems(FXCollections.observableArrayList(presupuestos));
        } catch (Exception e) {
            logger.error("Error al cargar presupuestos", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al cargar presupuestos",
                    "No se pudieron cargar los presupuestos: " + e.getMessage());
        }
    }

    @FXML
    private void handleNuevoPresupuesto() {
        try {
            // Cargar la vista del formulario
            Parent root = fxmlLoaderUtil.loadFXML("/fxml/presupuesto/presupuesto-form.fxml");

            // Obtener el controlador y configurarlo para un nuevo presupuesto
            PresupuestoFormController controller = (PresupuestoFormController) root.getUserData();
            controller.setPresupuesto(new PresupuestoDTO(), false);

            // Mostrar en una nueva ventana
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Nuevo Presupuesto");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Recargar la lista después de cerrar el formulario
            cargarPresupuestos();
        } catch (Exception e) {
            logger.error("Error al abrir formulario de presupuesto", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al abrir formulario",
                    "No se pudo abrir el formulario de presupuesto: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditar() {
        PresupuestoDTO presupuestoSeleccionado = tablaPresupuestos.getSelectionModel().getSelectedItem();
        if (presupuestoSeleccionado == null) {
            return;
        }

        // No permitir editar presupuestos facturados o cancelados
        if (presupuestoSeleccionado.getEstado() == EstadoPresupuesto.FACTURADO ||
                presupuestoSeleccionado.getEstado() == EstadoPresupuesto.CANCELADO) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "No se puede editar",
                    "No se pueden editar presupuestos facturados o cancelados.");
            return;
        }

        try {
            // Cargar los datos completos del presupuesto
            PresupuestoDTO presupuestoCompleto = presupuestoApiClient.obtenerPorId(presupuestoSeleccionado.getId());

            // Cargar la vista del formulario
            Parent root = fxmlLoaderUtil.loadFXML("/fxml/presupuesto/presupuesto-form.fxml");

            // Obtener el controlador y configurarlo con el presupuesto seleccionado
            PresupuestoFormController controller = (PresupuestoFormController) root.getUserData();
            controller.setPresupuesto(presupuestoCompleto, true);

            // Mostrar en una nueva ventana
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Editar Presupuesto");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Recargar la lista después de cerrar el formulario
            cargarPresupuestos();
        } catch (Exception e) {
            logger.error("Error al abrir formulario de edición", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al abrir formulario",
                    "No se pudo abrir el formulario de edición: " + e.getMessage());
        }
    }

    @FXML
    private void handleVerDetalles() {
        PresupuestoDTO presupuestoSeleccionado = tablaPresupuestos.getSelectionModel().getSelectedItem();
        if (presupuestoSeleccionado == null) {
            return;
        }

        try {
            // Cargar los datos completos del presupuesto
            PresupuestoDTO presupuestoCompleto = presupuestoApiClient.obtenerPorId(presupuestoSeleccionado.getId());

            // Cargar la vista de detalles
            Parent root = fxmlLoaderUtil.loadFXML("/fxml/presupuesto/presupuesto-detalle.fxml");

            // Obtener el controlador y configurarlo con el presupuesto seleccionado
            PresupuestoDetalleController controller = (PresupuestoDetalleController) root.getUserData();
            controller.setPresupuesto(presupuestoCompleto);

            // Mostrar en una nueva ventana
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Detalles de Presupuesto");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            logger.error("Error al abrir vista de detalles", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al abrir detalles",
                    "No se pudo abrir la vista de detalles: " + e.getMessage());
        }
    }

    @FXML
    private void handleGenerarTrabajo() {
        PresupuestoDTO presupuestoSeleccionado = tablaPresupuestos.getSelectionModel().getSelectedItem();
        if (presupuestoSeleccionado == null) {
            return;
        }

        // Verificar que el presupuesto esté aceptado
        if (presupuestoSeleccionado.getEstado() != EstadoPresupuesto.ACEPTADO) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "No se puede generar trabajo",
                    "Solo se pueden generar trabajos a partir de presupuestos aceptados.");
            return;
        }

        // Confirmar generación de trabajo
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar generación de trabajo");
        confirmacion.setHeaderText("¿Está seguro que desea generar un trabajo a partir de este presupuesto?");
        confirmacion.setContentText("Se creará un nuevo trabajo basado en el presupuesto seleccionado.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                // Generar trabajo
                trabajoApiClient.crearDesdePrespuesto(presupuestoSeleccionado.getId());

                // Mostrar mensaje de éxito
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Trabajo generado",
                        "El trabajo ha sido generado correctamente a partir del presupuesto.");

                // Recargar la lista
                cargarPresupuestos();
            } catch (Exception e) {
                logger.error("Error al generar trabajo", e);
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al generar trabajo",
                        "No se pudo generar el trabajo: " + e.getMessage());
            }
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