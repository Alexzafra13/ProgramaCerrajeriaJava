package com.gestiontaller.client.controller.presupuesto;

import com.gestiontaller.client.api.ClienteApiClient;
import com.gestiontaller.client.api.PresupuestoApiClient;
import com.gestiontaller.client.api.SerieApiClient;
import com.gestiontaller.client.util.FXMLLoaderUtil;
import com.gestiontaller.client.util.SessionManager;
import com.gestiontaller.common.dto.cliente.ClienteDTO;
import com.gestiontaller.common.dto.presupuesto.LineaPresupuestoDTO;
import com.gestiontaller.common.dto.presupuesto.PresupuestoDTO;
import com.gestiontaller.common.dto.serie.SerieBaseDTO;
import com.gestiontaller.common.model.presupuesto.EstadoPresupuesto;
import com.gestiontaller.common.model.presupuesto.TipoPresupuesto;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class PresupuestoFormController {

    private static final Logger logger = LoggerFactory.getLogger(PresupuestoFormController.class);

    @FXML private Label lblTitulo;
    @FXML private TextField txtNumero;
    @FXML private DatePicker dpFechaValidez;
    @FXML private ComboBox<ClienteDTO> cmbCliente;
    @FXML private TextField txtDescuento;
    @FXML private TextField txtDireccionObra;
    @FXML private TextField txtReferencia;
    @FXML private TextField txtTiempoEstimado;
    @FXML private TextArea txtObservaciones;
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
    @FXML private ComboBox<EstadoPresupuesto> cmbEstado;
    @FXML private Button btnAgregarLinea;
    @FXML private Button btnEditarLinea;
    @FXML private Button btnEliminarLinea;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private final PresupuestoApiClient presupuestoApiClient;
    private final ClienteApiClient clienteApiClient;
    private final SerieApiClient serieApiClient;
    private final FXMLLoaderUtil fxmlLoaderUtil;

    private PresupuestoDTO presupuesto;
    private boolean esEdicion = false;

    @Autowired
    public PresupuestoFormController(
            PresupuestoApiClient presupuestoApiClient,
            ClienteApiClient clienteApiClient,
            SerieApiClient serieApiClient,
            FXMLLoaderUtil fxmlLoaderUtil) {
        this.presupuestoApiClient = presupuestoApiClient;
        this.clienteApiClient = clienteApiClient;
        this.serieApiClient = serieApiClient;
        this.fxmlLoaderUtil = fxmlLoaderUtil;
    }

    @FXML
    public void initialize() {
        // Configurar un valor por defecto para la fecha de validez (+1 mes)
        dpFechaValidez.setValue(LocalDate.now().plusMonths(1));

        // Configurar el comboboz de estados
        cmbEstado.setItems(FXCollections.observableArrayList(EstadoPresupuesto.values()));
        cmbEstado.setValue(EstadoPresupuesto.PENDIENTE);

        // Configurar la validación para el campo de descuento (solo números y decimales)
        txtDescuento.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                txtDescuento.setText(oldValue);
            }
        });

        // Configurar la validación para el campo de tiempo estimado (solo números)
        txtTiempoEstimado.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtTiempoEstimado.setText(oldValue);
            }
        });

        // Configurar las columnas de la tabla
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

        // Configurar acciones al seleccionar fila
        tablaLineas.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    boolean haySeleccion = newSelection != null;
                    btnEditarLinea.setDisable(!haySeleccion);
                    btnEliminarLinea.setDisable(!haySeleccion);
                });

        // Cargar clientes para el combo
        cargarClientes();

        // Guardar una referencia al controlador para acceso desde otras clases
        if (btnGuardar.getScene() != null) {
            btnGuardar.getScene().setUserData(this);
        }
    }

    public void setPresupuesto(PresupuestoDTO presupuesto, boolean esEdicion) {
        this.presupuesto = presupuesto;
        this.esEdicion = esEdicion;

        if (btnGuardar.getScene() == null) {
            // Aún no se ha inicializado la vista completamente
            return;
        }

        if (esEdicion) {
            lblTitulo.setText("Editar Presupuesto");
            cargarDatosPresupuesto();
        } else {
            lblTitulo.setText("Nuevo Presupuesto");
            inicializarNuevoPresupuesto();
        }
    }

    private void cargarClientes() {
        try {
            cmbCliente.setItems(FXCollections.observableArrayList(clienteApiClient.obtenerTodos()));

            // Configurar la celda para mostrar el nombre completo del cliente
            cmbCliente.setCellFactory(lv -> new ListCell<ClienteDTO>() {
                @Override
                protected void updateItem(ClienteDTO item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        String nombreCliente;
                        switch (item.getTipoCliente()) {
                            case EMPRESA:
                            case ADMINISTRACION:
                                nombreCliente = item.getRazonSocial();
                                break;
                            default:
                                nombreCliente = item.getNombre() + " " + item.getApellidos();
                                break;
                        }
                        setText(nombreCliente.trim() + " (" + item.getCodigo() + ")");
                    }
                }
            });

            // Configurar el toString para mostrar en la parte seleccionada del combo
            cmbCliente.setConverter(new javafx.util.StringConverter<ClienteDTO>() {
                @Override
                public String toString(ClienteDTO cliente) {
                    if (cliente == null) {
                        return null;
                    }
                    String nombreCliente;
                    switch (cliente.getTipoCliente()) {
                        case EMPRESA:
                        case ADMINISTRACION:
                            nombreCliente = cliente.getRazonSocial();
                            break;
                        default:
                            nombreCliente = cliente.getNombre() + " " + cliente.getApellidos();
                            break;
                    }
                    return nombreCliente.trim() + " (" + cliente.getCodigo() + ")";
                }

                @Override
                public ClienteDTO fromString(String string) {
                    // Este método no se usa realmente para la conversión inversa
                    return null;
                }
            });

        } catch (Exception e) {
            logger.error("Error al cargar clientes", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al cargar clientes",
                    "No se pudieron cargar los clientes: " + e.getMessage());
        }
    }

    private void inicializarNuevoPresupuesto() {
        // Generar número de presupuesto
        try {
            String nuevoNumero = presupuestoApiClient.generarNumeroPresupuesto();
            txtNumero.setText(nuevoNumero);
        } catch (Exception e) {
            logger.error("Error al generar número de presupuesto", e);
            txtNumero.setText("ERROR-GENERACION");
        }

        // Configurar valores por defecto
        dpFechaValidez.setValue(LocalDate.now().plusMonths(1));
        cmbEstado.setValue(EstadoPresupuesto.PENDIENTE);
        txtDescuento.setText("0");

        // Inicializar objeto presupuesto
        if (presupuesto == null) {
            presupuesto = new PresupuestoDTO();
        }

        presupuesto.setNumero(txtNumero.getText());
        presupuesto.setFechaValidez(dpFechaValidez.getValue());
        presupuesto.setEstado(EstadoPresupuesto.PENDIENTE);
        presupuesto.setUsuarioId(SessionManager.getInstance().getUserId());

        // Actualizar la tabla de líneas
        tablaLineas.setItems(FXCollections.observableArrayList(presupuesto.getLineas()));

        // Actualizar totales
        actualizarTotales();
    }

    private void cargarDatosPresupuesto() {
        // Llenar los campos con los datos del presupuesto
        txtNumero.setText(presupuesto.getNumero());

        if (presupuesto.getFechaValidez() != null) {
            dpFechaValidez.setValue(presupuesto.getFechaValidez());
        } else {
            dpFechaValidez.setValue(LocalDate.now().plusMonths(1));
        }

        // Seleccionar el cliente
        if (presupuesto.getClienteId() != null) {
            for (ClienteDTO cliente : cmbCliente.getItems()) {
                if (cliente.getId().equals(presupuesto.getClienteId())) {
                    cmbCliente.setValue(cliente);
                    break;
                }
            }
        }

        // Campos adicionales
        txtDescuento.setText(String.valueOf(presupuesto.getDescuento()));
        txtDireccionObra.setText(presupuesto.getDireccionObra() != null ? presupuesto.getDireccionObra() : "");
        txtReferencia.setText(presupuesto.getReferencia() != null ? presupuesto.getReferencia() : "");

        if (presupuesto.getTiempoEstimado() != null) {
            txtTiempoEstimado.setText(presupuesto.getTiempoEstimado().toString());
        } else {
            txtTiempoEstimado.setText("");
        }

        txtObservaciones.setText(presupuesto.getObservaciones() != null ? presupuesto.getObservaciones() : "");

        // Estado
        if (presupuesto.getEstado() != null) {
            cmbEstado.setValue(presupuesto.getEstado());
        } else {
            cmbEstado.setValue(EstadoPresupuesto.PENDIENTE);
        }

        // Líneas de presupuesto
        tablaLineas.setItems(FXCollections.observableArrayList(presupuesto.getLineas()));

        // Actualizar totales
        actualizarTotales();
    }

    private void actualizarTotales() {
        double baseImponible = 0.0;
        double iva = 0.0;
        double total = 0.0;

        if (presupuesto != null) {
            // Calcular la base imponible sumando los importes de las líneas
            for (LineaPresupuestoDTO linea : presupuesto.getLineas()) {
                baseImponible += linea.getImporte();
            }

            // Aplicar descuento general
            double descuento = 0.0;
            try {
                descuento = Double.parseDouble(txtDescuento.getText().trim());
            } catch (NumberFormatException e) {
                descuento = 0.0;
            }

            if (descuento > 0) {
                baseImponible = baseImponible * (1 - (descuento / 100.0));
            }

            // Calcular IVA (21%)
            iva = baseImponible * 0.21;

            // Calcular total
            total = baseImponible + iva;

            // Actualizar en el modelo
            presupuesto.setBaseImponible(baseImponible);
            presupuesto.setImporteIva(iva);
            presupuesto.setTotalPresupuesto(total);
        }

        // Actualizar etiquetas
        lblBaseImponible.setText(String.format("%.2f €", baseImponible));
        lblIVA.setText(String.format("%.2f €", iva));
        lblTotalPresupuesto.setText(String.format("%.2f €", total));
    }

    @FXML
    private void handleAgregarLinea() {
        if (cmbCliente.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Cliente requerido",
                    "Por favor, seleccione un cliente antes de agregar líneas al presupuesto.");
            return;
        }

        try {
            // Crear una nueva línea
            LineaPresupuestoDTO nuevaLinea = new LineaPresupuestoDTO();
            nuevaLinea.setPresupuestoId(presupuesto.getId());
            nuevaLinea.setOrden(presupuesto.getLineas().size() + 1);
            nuevaLinea.setCantidad(1);

            // Abrir diálogo para configurar la línea
            abrirDialogoEditarLinea(nuevaLinea, false);
        } catch (Exception e) {
            logger.error("Error al agregar línea", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al agregar línea",
                    "No se pudo agregar la línea: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditarLinea() {
        LineaPresupuestoDTO lineaSeleccionada = tablaLineas.getSelectionModel().getSelectedItem();
        if (lineaSeleccionada == null) {
            return;
        }

        try {
            abrirDialogoEditarLinea(lineaSeleccionada, true);
        } catch (Exception e) {
            logger.error("Error al editar línea", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al editar línea",
                    "No se pudo editar la línea: " + e.getMessage());
        }
    }

    private void abrirDialogoEditarLinea(LineaPresupuestoDTO linea, boolean esEdicion) {
        try {
            // Crear un diálogo personalizado
            Dialog<LineaPresupuestoDTO> dialog = new Dialog<>();
            dialog.setTitle(esEdicion ? "Editar Línea" : "Nueva Línea");
            dialog.setHeaderText(esEdicion ? "Editar línea de presupuesto" : "Agregar línea de presupuesto");

            // Botones
            ButtonType guardarButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(guardarButtonType, ButtonType.CANCEL);

            // Crear el contenido del diálogo
            VBox content = new VBox(10);
            content.setStyle("-fx-padding: 20px;");

            // Campos del formulario
            // Tipo de presupuesto
            ComboBox<TipoPresupuesto> cmbTipoPresupuesto = new ComboBox<>();
            cmbTipoPresupuesto.setPromptText("Seleccione tipo");
            cmbTipoPresupuesto.setItems(FXCollections.observableArrayList(TipoPresupuesto.values()));

            Label lblTipo = new Label("Tipo de producto/servicio:");
            content.getChildren().addAll(lblTipo, cmbTipoPresupuesto);

            // Descripción
            TextField txtDescripcionLinea = new TextField();
            txtDescripcionLinea.setPromptText("Descripción del producto/servicio");
            Label lblDescripcion = new Label("Descripción:");
            content.getChildren().addAll(lblDescripcion, txtDescripcionLinea);

            // Cantidad
            TextField txtCantidadLinea = new TextField();
            txtCantidadLinea.setPromptText("Cantidad");
            txtCantidadLinea.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.matches("\\d*")) {
                    txtCantidadLinea.setText(oldVal);
                }
            });
            Label lblCantidad = new Label("Cantidad:");
            content.getChildren().addAll(lblCantidad, txtCantidadLinea);

            // Medidas
            TextField txtAnchoLinea = new TextField();
            txtAnchoLinea.setPromptText("Ancho (mm)");
            txtAnchoLinea.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.matches("\\d*")) {
                    txtAnchoLinea.setText(oldVal);
                }
            });

            TextField txtAltoLinea = new TextField();
            txtAltoLinea.setPromptText("Alto (mm)");
            txtAltoLinea.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.matches("\\d*")) {
                    txtAltoLinea.setText(oldVal);
                }
            });

            Label lblMedidas = new Label("Medidas (mm):");
            content.getChildren().addAll(lblMedidas);

            // Añadir campos de ancho y alto en una fila
            javafx.scene.layout.HBox hboxMedidas = new javafx.scene.layout.HBox(10);
            hboxMedidas.getChildren().addAll(
                    new Label("Ancho:"), txtAnchoLinea,
                    new Label("Alto:"), txtAltoLinea
            );
            content.getChildren().add(hboxMedidas);

            // Serie (solo para ventanas/puertas)
            ComboBox<SerieBaseDTO> cmbSerie = new ComboBox<>();
            cmbSerie.setPromptText("Seleccione serie");

            Label lblSerie = new Label("Serie:");
            content.getChildren().addAll(lblSerie, cmbSerie);

            // Cargar series
            try {
                cmbSerie.setItems(FXCollections.observableArrayList(serieApiClient.obtenerTodasLasSeries()));

                // Configurar la celda para mostrar el nombre de la serie
                cmbSerie.setCellFactory(lv -> new ListCell<SerieBaseDTO>() {
                    @Override
                    protected void updateItem(SerieBaseDTO item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.getCodigo() + " - " + item.getNombre());
                        }
                    }
                });

                // Configurar el toString para mostrar en la parte seleccionada del combo
                cmbSerie.setConverter(new javafx.util.StringConverter<SerieBaseDTO>() {
                    @Override
                    public String toString(SerieBaseDTO serie) {
                        if (serie == null) {
                            return null;
                        }
                        return serie.getCodigo() + " - " + serie.getNombre();
                    }

                    @Override
                    public SerieBaseDTO fromString(String string) {
                        return null;
                    }
                });
            } catch (Exception e) {
                logger.error("Error al cargar series", e);
            }

            // Precio unitario
            TextField txtPrecioUnitarioLinea = new TextField();
            txtPrecioUnitarioLinea.setPromptText("Precio unitario");
            txtPrecioUnitarioLinea.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.matches("\\d*(\\.\\d*)?")) {
                    txtPrecioUnitarioLinea.setText(oldVal);
                }
            });
            Label lblPrecioUnitario = new Label("Precio unitario (€):");
            content.getChildren().addAll(lblPrecioUnitario, txtPrecioUnitarioLinea);

            // Descuento
            TextField txtDescuentoLinea = new TextField();
            txtDescuentoLinea.setPromptText("Descuento (%)");
            txtDescuentoLinea.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.matches("\\d*(\\.\\d*)?")) {
                    txtDescuentoLinea.setText(oldVal);
                }
            });
            Label lblDescuentoLinea = new Label("Descuento (%):");
            content.getChildren().addAll(lblDescuentoLinea, txtDescuentoLinea);

            // Opciones adicionales para ventanas
            CheckBox chkIncluyePersiana = new CheckBox("Incluye persiana");
            TextField txtTipoVidrio = new TextField();
            txtTipoVidrio.setPromptText("Tipo de vidrio (ej: Climalit 4+6+4)");
            TextField txtColorPerfil = new TextField();
            txtColorPerfil.setPromptText("Color perfil (ej: Blanco, Plata...)");

            content.getChildren().addAll(
                    new Label("Opciones adicionales:"),
                    chkIncluyePersiana,
                    new Label("Tipo de vidrio:"), txtTipoVidrio,
                    new Label("Color de perfil:"), txtColorPerfil
            );

            // Cargar datos de la línea existente si es edición
            if (esEdicion) {
                if (linea.getTipoPresupuesto() != null) {
                    cmbTipoPresupuesto.setValue(linea.getTipoPresupuesto());
                }

                txtDescripcionLinea.setText(linea.getDescripcion() != null ? linea.getDescripcion() : "");
                txtCantidadLinea.setText(linea.getCantidad() != null ? linea.getCantidad().toString() : "1");

                if (linea.getAncho() != null) {
                    txtAnchoLinea.setText(linea.getAncho().toString());
                }

                if (linea.getAlto() != null) {
                    txtAltoLinea.setText(linea.getAlto().toString());
                }

                // Seleccionar serie si existe
                if (linea.getSerieId() != null) {
                    for (SerieBaseDTO serie : cmbSerie.getItems()) {
                        if (serie.getId().equals(linea.getSerieId())) {
                            cmbSerie.setValue(serie);
                            break;
                        }
                    }
                }

                txtPrecioUnitarioLinea.setText(String.valueOf(linea.getPrecioUnitario()));
                txtDescuentoLinea.setText(String.valueOf(linea.getDescuento()));

                // Opciones adicionales
                chkIncluyePersiana.setSelected(linea.getIncluyePersiana() != null && linea.getIncluyePersiana());

                if (linea.getTipoVidrio() != null) {
                    txtTipoVidrio.setText(linea.getTipoVidrio());
                }

                if (linea.getColorPerfil() != null) {
                    txtColorPerfil.setText(linea.getColorPerfil());
                }
            } else {
                // Valores por defecto para nueva línea
                txtCantidadLinea.setText("1");
                txtDescuentoLinea.setText("0");
                txtPrecioUnitarioLinea.setText("0");
            }

            dialog.getDialogPane().setContent(content);

            // Convertir el resultado
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == guardarButtonType) {
                    try {
                        // Actualizar objeto línea
                        linea.setTipoPresupuesto(cmbTipoPresupuesto.getValue());
                        linea.setDescripcion(txtDescripcionLinea.getText());

                        // Cantidad
                        try {
                            linea.setCantidad(Integer.parseInt(txtCantidadLinea.getText()));
                        } catch (NumberFormatException e) {
                            linea.setCantidad(1);
                        }

                        // Medidas
                        try {
                            if (!txtAnchoLinea.getText().isEmpty()) {
                                linea.setAncho(Integer.parseInt(txtAnchoLinea.getText()));
                            }

                            if (!txtAltoLinea.getText().isEmpty()) {
                                linea.setAlto(Integer.parseInt(txtAltoLinea.getText()));
                            }

                            // Actualizar campo medidas
                            if (linea.getAncho() != null && linea.getAlto() != null) {
                                linea.setMedidas(linea.getAncho() + "x" + linea.getAlto() + " mm");
                            }
                        } catch (NumberFormatException e) {
                            // Ignorar errores de conversión
                        }

                        // Serie
                        if (cmbSerie.getValue() != null) {
                            linea.setSerieId(cmbSerie.getValue().getId());
                            linea.setNombreSerie(cmbSerie.getValue().getNombre());
                        }

                        // Precio unitario y descuento
                        try {
                            linea.setPrecioUnitario(Double.parseDouble(txtPrecioUnitarioLinea.getText()));
                        } catch (NumberFormatException e) {
                            linea.setPrecioUnitario(0.0);
                        }

                        try {
                            linea.setDescuento(Double.parseDouble(txtDescuentoLinea.getText()));
                        } catch (NumberFormatException e) {
                            linea.setDescuento(0.0);
                        }

                        // Calcular importe
                        double importe = linea.getPrecioUnitario() * linea.getCantidad();
                        if (linea.getDescuento() > 0) {
                            importe = importe * (1 - (linea.getDescuento() / 100.0));
                        }
                        linea.setImporte(importe);

                        // Opciones adicionales
                        linea.setIncluyePersiana(chkIncluyePersiana.isSelected());
                        linea.setTipoVidrio(txtTipoVidrio.getText());
                        linea.setColorPerfil(txtColorPerfil.getText());

                        return linea;
                    } catch (Exception e) {
                        logger.error("Error al procesar datos de línea", e);
                        mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error en datos",
                                "Por favor, verifique los datos ingresados: " + e.getMessage());
                        return null;
                    }
                }
                return null;
            });

            // Mostrar diálogo y procesar resultado
            Optional<LineaPresupuestoDTO> resultado = dialog.showAndWait();

            if (resultado.isPresent()) {
                if (!esEdicion) {
                    // Agregar la nueva línea a la lista
                    presupuesto.getLineas().add(resultado.get());
                }

                // Actualizar tabla y totales
                tablaLineas.setItems(FXCollections.observableArrayList(presupuesto.getLineas()));
                actualizarTotales();
            }

        } catch (Exception e) {
            logger.error("Error al abrir diálogo de línea", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al procesar línea",
                    "No se pudo procesar la línea: " + e.getMessage());
        }
    }

    @FXML
    private void handleEliminarLinea() {
        LineaPresupuestoDTO lineaSeleccionada = tablaLineas.getSelectionModel().getSelectedItem();
        if (lineaSeleccionada == null) {
            return;
        }

        // Confirmar eliminación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro que desea eliminar esta línea?");
        confirmacion.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            // Eliminar línea
            presupuesto.getLineas().remove(lineaSeleccionada);

            // Reordenar líneas restantes
            int orden = 1;
            for (LineaPresupuestoDTO linea : presupuesto.getLineas()) {
                linea.setOrden(orden++);
            }

            // Actualizar tabla y totales
            tablaLineas.setItems(FXCollections.observableArrayList(presupuesto.getLineas()));
            actualizarTotales();
        }
    }

    @FXML
    private void handleGuardar() {
        if (!validarFormulario()) {
            return;
        }

        try {
            // Actualizar objeto presupuesto con los datos del formulario
            actualizarPresupuestoDesdeFormulario();

            // Guardar en la base de datos
            PresupuestoDTO presupuestoGuardado = presupuestoApiClient.guardar(presupuesto);

            // Mostrar mensaje de éxito
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Presupuesto guardado",
                    "El presupuesto ha sido guardado correctamente.");

            // Cerrar ventana
            Stage stage = (Stage) btnGuardar.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            logger.error("Error al guardar presupuesto", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al guardar",
                    "No se pudo guardar el presupuesto: " + e.getMessage());
        }
    }

    private boolean validarFormulario() {
        StringBuilder errores = new StringBuilder();

        if (txtNumero.getText().trim().isEmpty()) {
            errores.append("- El número de presupuesto es obligatorio\n");
        }

        if (cmbCliente.getValue() == null) {
            errores.append("- Debe seleccionar un cliente\n");
        }

        if (dpFechaValidez.getValue() == null) {
            errores.append("- La fecha de validez es obligatoria\n");
        }

        if (presupuesto.getLineas().isEmpty()) {
            errores.append("- El presupuesto debe tener al menos una línea\n");
        }

        if (errores.length() > 0) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Corrija los siguientes errores:",
                    errores.toString());
            return false;
        }

        return true;
    }

    private void actualizarPresupuestoDesdeFormulario() {
        presupuesto.setNumero(txtNumero.getText().trim());
        presupuesto.setFechaValidez(dpFechaValidez.getValue());

        if (cmbCliente.getValue() != null) {
            presupuesto.setClienteId(cmbCliente.getValue().getId());
            presupuesto.setNombreCliente(cmbCliente.getValue().getNombre() + " " + cmbCliente.getValue().getApellidos());
        }

        // El usuario nunca debería cambiar si estamos editando
        if (!esEdicion) {
            presupuesto.setUsuarioId(SessionManager.getInstance().getUserId());
            presupuesto.setNombreUsuario(SessionManager.getInstance().getNombreUsuario());
        }

        // Campos adicionales
        try {
            presupuesto.setDescuento(Double.parseDouble(txtDescuento.getText().trim()));
        } catch (NumberFormatException e) {
            presupuesto.setDescuento(0.0);
        }

        presupuesto.setDireccionObra(txtDireccionObra.getText().trim());
        presupuesto.setReferencia(txtReferencia.getText().trim());

        try {
            presupuesto.setTiempoEstimado(Integer.parseInt(txtTiempoEstimado.getText().trim()));
        } catch (NumberFormatException e) {
            presupuesto.setTiempoEstimado(null);
        }

        presupuesto.setObservaciones(txtObservaciones.getText().trim());
        presupuesto.setEstado(cmbEstado.getValue());
    }

    @FXML
    private void handleCancelar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
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