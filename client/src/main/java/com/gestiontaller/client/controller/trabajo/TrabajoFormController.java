package com.gestiontaller.client.controller.trabajo;

import com.gestiontaller.client.api.ClienteApiClient;
import com.gestiontaller.client.api.PresupuestoApiClient;
import com.gestiontaller.client.api.TrabajoApiClient;
import com.gestiontaller.client.util.FXMLLoaderUtil;
import com.gestiontaller.client.util.SessionManager;
import com.gestiontaller.common.dto.cliente.ClienteDTO;
import com.gestiontaller.common.dto.presupuesto.PresupuestoDTO;
import com.gestiontaller.common.dto.trabajo.MaterialAsignadoDTO;
import com.gestiontaller.common.dto.trabajo.TrabajoDTO;
import com.gestiontaller.common.model.trabajo.PrioridadTrabajo;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class TrabajoFormController {

    private static final Logger logger = LoggerFactory.getLogger(TrabajoFormController.class);

    @FXML private Label lblTitulo;
    @FXML private TextField txtCodigo;
    @FXML private ComboBox<PresupuestoDTO> cmbPresupuesto;
    @FXML private ComboBox<ClienteDTO> cmbCliente;
    @FXML private DatePicker dpFechaProgramada;
    @FXML private TextField txtDireccionInstalacion;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private ComboBox<PrioridadTrabajo> cmbPrioridad;
    @FXML private ComboBox<String> cmbUsuarioAsignado;
    @FXML private TextArea txtObservaciones;
    @FXML private TextField txtHorasReales;
    @FXML private TableView<MaterialAsignadoDTO> tablaMateriales;
    @FXML private TableColumn<MaterialAsignadoDTO, String> colProducto;
    @FXML private TableColumn<MaterialAsignadoDTO, Integer> colCantidadAsignada;
    @FXML private TableColumn<MaterialAsignadoDTO, Integer> colCantidadUsada;
    @FXML private TableColumn<MaterialAsignadoDTO, String> colObservaciones;
    @FXML private Button btnAgregarMaterial;
    @FXML private Button btnEditarMaterial;
    @FXML private Button btnEliminarMaterial;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private final TrabajoApiClient trabajoApiClient;
    private final ClienteApiClient clienteApiClient;
    private final PresupuestoApiClient presupuestoApiClient;
    private final FXMLLoaderUtil fxmlLoaderUtil;

    private TrabajoDTO trabajo;
    private boolean esEdicion = false;

    @Autowired
    public TrabajoFormController(
            TrabajoApiClient trabajoApiClient,
            ClienteApiClient clienteApiClient,
            PresupuestoApiClient presupuestoApiClient,
            FXMLLoaderUtil fxmlLoaderUtil) {
        this.trabajoApiClient = trabajoApiClient;
        this.clienteApiClient = clienteApiClient;
        this.presupuestoApiClient = presupuestoApiClient;
        this.fxmlLoaderUtil = fxmlLoaderUtil;
    }

    @FXML
    public void initialize() {
        // Configurar fecha programada predeterminada
        dpFechaProgramada.setValue(LocalDate.now().plusDays(3));

        // Configurar prioridades disponibles
        cmbPrioridad.setItems(FXCollections.observableArrayList(PrioridadTrabajo.values()));
        cmbPrioridad.setValue(PrioridadTrabajo.NORMAL);

        // Validación para horas reales (solo números)
        txtHorasReales.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtHorasReales.setText(oldValue);
            }
        });

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

        colObservaciones.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getObservaciones() != null ?
                        data.getValue().getObservaciones() : ""));

        // Configurar acciones al seleccionar material
        tablaMateriales.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    boolean haySeleccion = newSelection != null;
                    btnEditarMaterial.setDisable(!haySeleccion);
                    btnEliminarMaterial.setDisable(!haySeleccion);
                });

        // Cargar datos iniciales
        cargarClientes();
        cargarPresupuestos();
        cargarEstados();
        cargarUsuarios();

        // Configurar interacción entre presupuesto y cliente
        cmbPresupuesto.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // Al seleccionar un presupuesto, actualizar el cliente
                for (ClienteDTO cliente : cmbCliente.getItems()) {
                    if (cliente.getId().equals(newVal.getClienteId())) {
                        cmbCliente.setValue(cliente);
                        break;
                    }
                }
            }
        });

        // Guardar referencia al controlador
        if (btnGuardar.getScene() != null) {
            btnGuardar.getScene().setUserData(this);
        }
    }

    public void setTrabajo(TrabajoDTO trabajo, boolean esEdicion) {
        this.trabajo = trabajo;
        this.esEdicion = esEdicion;

        if (btnGuardar.getScene() == null) {
            // La vista aún no está completamente inicializada
            return;
        }

        if (esEdicion) {
            lblTitulo.setText("Editar Trabajo");
            cargarDatosTrabajo();
        } else {
            lblTitulo.setText("Nuevo Trabajo");
            inicializarNuevoTrabajo();
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
                    return null;
                }
            });
        } catch (Exception e) {
            logger.error("Error al cargar clientes", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al cargar clientes",
                    "No se pudieron cargar los clientes: " + e.getMessage());
        }
    }

    private void cargarPresupuestos() {
        try {
            // Cargar solo presupuestos aceptados
            cmbPresupuesto.setItems(FXCollections.observableArrayList(
                    presupuestoApiClient.obtenerPorEstado(com.gestiontaller.common.model.presupuesto.EstadoPresupuesto.ACEPTADO)));

            // Configurar la celda para mostrar información del presupuesto
            cmbPresupuesto.setCellFactory(lv -> new ListCell<PresupuestoDTO>() {
                @Override
                protected void updateItem(PresupuestoDTO item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getNumero() + " - " + item.getNombreCliente() +
                                " (" + String.format("%.2f €", item.getTotalPresupuesto()) + ")");
                    }
                }
            });

            // Configurar el toString para mostrar en la parte seleccionada del combo
            cmbPresupuesto.setConverter(new javafx.util.StringConverter<PresupuestoDTO>() {
                @Override
                public String toString(PresupuestoDTO presupuesto) {
                    if (presupuesto == null) {
                        return null;
                    }
                    return presupuesto.getNumero() + " - " + presupuesto.getNombreCliente();
                }

                @Override
                public PresupuestoDTO fromString(String string) {
                    return null;
                }
            });
        } catch (Exception e) {
            logger.error("Error al cargar presupuestos", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al cargar presupuestos",
                    "No se pudieron cargar los presupuestos: " + e.getMessage());
        }
    }

    private void cargarEstados() {
        try {
            // Aquí podrías cargar los estados desde el servidor
            // Por ahora usaremos algunos estados predefinidos
            cmbEstado.setItems(FXCollections.observableArrayList(
                    "PENDIENTE",
                    "EN_PROGRESO",
                    "TERMINADO",
                    "ENTREGADO",
                    "CANCELADO"
            ));
            cmbEstado.setValue("PENDIENTE");
        } catch (Exception e) {
            logger.error("Error al cargar estados", e);
        }
    }

    private void cargarUsuarios() {
        try {
            // Aquí podrías cargar los usuarios desde el servidor
            // Por ahora usaremos algunos usuarios predefinidos
            cmbUsuarioAsignado.setItems(FXCollections.observableArrayList(
                    "Sin asignar",
                    "Técnico 1",
                    "Técnico 2",
                    "Técnico 3"
            ));
            cmbUsuarioAsignado.setValue("Sin asignar");
        } catch (Exception e) {
            logger.error("Error al cargar usuarios", e);
        }
    }

    private void inicializarNuevoTrabajo() {
        // Generar código de trabajo
        try {
            String nuevoCodigo = trabajoApiClient.generarCodigoTrabajo();
            txtCodigo.setText(nuevoCodigo);
        } catch (Exception e) {
            logger.error("Error al generar código de trabajo", e);
            txtCodigo.setText("ERROR-GENERACION");
        }

        // Configurar valores por defecto
        dpFechaProgramada.setValue(LocalDate.now().plusDays(3));
        cmbEstado.setValue("PENDIENTE");
        cmbPrioridad.setValue(PrioridadTrabajo.NORMAL);
        cmbUsuarioAsignado.setValue("Sin asignar");

        // Inicializar objeto trabajo
        if (trabajo == null) {
            trabajo = new TrabajoDTO();
        }

        trabajo.setCodigo(txtCodigo.getText());
        trabajo.setFechaCreacion(LocalDateTime.now());
        trabajo.setFechaProgramada(dpFechaProgramada.getValue());
        trabajo.setPrioridad(PrioridadTrabajo.NORMAL);

        // Actualizar tabla de materiales
        tablaMateriales.setItems(FXCollections.observableArrayList(trabajo.getMaterialesAsignados()));
    }

    private void cargarDatosTrabajo() {
        // Llenar campos con datos del trabajo
        txtCodigo.setText(trabajo.getCodigo());
        txtCodigo.setEditable(false);

        // Seleccionar presupuesto si existe
        if (trabajo.getPresupuestoId() != null) {
            for (PresupuestoDTO presupuesto : cmbPresupuesto.getItems()) {
                if (presupuesto.getId().equals(trabajo.getPresupuestoId())) {
                    cmbPresupuesto.setValue(presupuesto);
                    break;
                }
            }
        }

        // Seleccionar cliente
        if (trabajo.getClienteId() != null) {
            for (ClienteDTO cliente : cmbCliente.getItems()) {
                if (cliente.getId().equals(trabajo.getClienteId())) {
                    cmbCliente.setValue(cliente);
                    break;
                }
            }
        }

        // Fecha programada
        if (trabajo.getFechaProgramada() != null) {
            dpFechaProgramada.setValue(trabajo.getFechaProgramada());
        } else {
            dpFechaProgramada.setValue(LocalDate.now().plusDays(3));
        }

        // Dirección
        txtDireccionInstalacion.setText(trabajo.getDireccionInstalacion() != null ?
                trabajo.getDireccionInstalacion() : "");

        // Estado
        if (trabajo.getNombreEstado() != null) {
            cmbEstado.setValue(trabajo.getNombreEstado());
        } else {
            cmbEstado.setValue("PENDIENTE");
        }

        // Prioridad
        if (trabajo.getPrioridad() != null) {
            cmbPrioridad.setValue(trabajo.getPrioridad());
        } else {
            cmbPrioridad.setValue(PrioridadTrabajo.NORMAL);
        }

        // Usuario asignado
        if (trabajo.getNombreUsuarioAsignado() != null) {
            cmbUsuarioAsignado.setValue(trabajo.getNombreUsuarioAsignado());
        } else {
            cmbUsuarioAsignado.setValue("Sin asignar");
        }

        // Observaciones
        txtObservaciones.setText(trabajo.getObservaciones() != null ?
                trabajo.getObservaciones() : "");

        // Horas reales
        if (trabajo.getHorasReales() != null) {
            txtHorasReales.setText(trabajo.getHorasReales().toString());
        } else {
            txtHorasReales.setText("");
        }

        // Materiales
        tablaMateriales.setItems(FXCollections.observableArrayList(trabajo.getMaterialesAsignados()));
    }

    @FXML
    private void handleAgregarMaterial() {
        if (cmbCliente.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Cliente requerido",
                    "Por favor, seleccione un cliente antes de agregar materiales.");
            return;
        }

        try {
            // Crear nuevo material
            MaterialAsignadoDTO nuevoMaterial = new MaterialAsignadoDTO();
            nuevoMaterial.setTrabajoId(trabajo.getId());
            nuevoMaterial.setCantidadAsignada(1);
            nuevoMaterial.setCantidadUsada(0);

            // Mostrar diálogo para configurar el material
            abrirDialogoEditarMaterial(nuevoMaterial, false);
        } catch (Exception e) {
            logger.error("Error al agregar material", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al agregar material",
                    "No se pudo agregar el material: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditarMaterial() {
        MaterialAsignadoDTO materialSeleccionado = tablaMateriales.getSelectionModel().getSelectedItem();
        if (materialSeleccionado == null) {
            return;
        }

        try {
            abrirDialogoEditarMaterial(materialSeleccionado, true);
        } catch (Exception e) {
            logger.error("Error al editar material", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al editar material",
                    "No se pudo editar el material: " + e.getMessage());
        }
    }

    private void abrirDialogoEditarMaterial(MaterialAsignadoDTO material, boolean esEdicion) {
        try {
            // Crear diálogo
            Dialog<MaterialAsignadoDTO> dialog = new Dialog<>();
            dialog.setTitle(esEdicion ? "Editar Material" : "Nuevo Material");
            dialog.setHeaderText(esEdicion ? "Editar material asignado" : "Agregar material al trabajo");

            // Botones
            ButtonType guardarButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(guardarButtonType, ButtonType.CANCEL);

            // Crear contenido
            VBox content = new VBox(10);
            content.setStyle("-fx-padding: 20px;");

            // ComboBox para seleccionar producto
            // En una implementación real, esto debería llenarse con productos reales
            ComboBox<String> cmbProducto = new ComboBox<>();
            cmbProducto.setPromptText("Seleccione un producto");
            cmbProducto.setItems(FXCollections.observableArrayList(
                    "Perfil marco 70mm (metros)",
                    "Perfil hoja 60mm (metros)",
                    "Tornillo 4.8x19mm (unidades)",
                    "Junta EPDM (metros)",
                    "Escuadra (unidades)",
                    "Maneta (unidades)"
            ));

            Label lblProducto = new Label("Producto:");
            content.getChildren().addAll(lblProducto, cmbProducto);

            // Campo para cantidad asignada
            TextField txtCantidadAsignada = new TextField();
            txtCantidadAsignada.setPromptText("Cantidad asignada");
            txtCantidadAsignada.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.matches("\\d*")) {
                    txtCantidadAsignada.setText(oldVal);
                }
            });

            Label lblCantidadAsignada = new Label("Cantidad asignada:");
            content.getChildren().addAll(lblCantidadAsignada, txtCantidadAsignada);

            // Campo para cantidad usada
            TextField txtCantidadUsada = new TextField();
            txtCantidadUsada.setPromptText("Cantidad usada");
            txtCantidadUsada.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.matches("\\d*")) {
                    txtCantidadUsada.setText(oldVal);
                }
            });

            Label lblCantidadUsada = new Label("Cantidad usada:");
            content.getChildren().addAll(lblCantidadUsada, txtCantidadUsada);

            // Campo para observaciones
            TextArea txtObservacionesMaterial = new TextArea();
            txtObservacionesMaterial.setPromptText("Observaciones del material");
            txtObservacionesMaterial.setPrefRowCount(3);

            Label lblObservacionesMaterial = new Label("Observaciones:");
            content.getChildren().addAll(lblObservacionesMaterial, txtObservacionesMaterial);

            // Cargar datos si es edición
            if (esEdicion) {
                if (material.getNombreProducto() != null) {
                    cmbProducto.setValue(material.getNombreProducto());
                }

                txtCantidadAsignada.setText(material.getCantidadAsignada() != null ?
                        material.getCantidadAsignada().toString() : "1");

                txtCantidadUsada.setText(material.getCantidadUsada() != null ?
                        material.getCantidadUsada().toString() : "0");

                txtObservacionesMaterial.setText(material.getObservaciones() != null ?
                        material.getObservaciones() : "");
            } else {
                // Valores por defecto para nuevo material
                txtCantidadAsignada.setText("1");
                txtCantidadUsada.setText("0");
            }

            dialog.getDialogPane().setContent(content);

            // Convertir resultado
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == guardarButtonType) {
                    try {
                        // Actualizar objeto material
                        material.setNombreProducto(cmbProducto.getValue());

                        try {
                            material.setCantidadAsignada(Integer.parseInt(txtCantidadAsignada.getText()));
                        } catch (NumberFormatException e) {
                            material.setCantidadAsignada(1);
                        }

                        try {
                            material.setCantidadUsada(Integer.parseInt(txtCantidadUsada.getText()));
                        } catch (NumberFormatException e) {
                            material.setCantidadUsada(0);
                        }

                        material.setObservaciones(txtObservacionesMaterial.getText());

                        return material;
                    } catch (Exception e) {
                        logger.error("Error al procesar datos del material", e);
                        mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error en datos",
                                "Por favor, verifique los datos ingresados: " + e.getMessage());
                        return null;
                    }
                }
                return null;
            });

            // Mostrar diálogo y procesar resultado
            Optional<MaterialAsignadoDTO> resultado = dialog.showAndWait();

            if (resultado.isPresent()) {
                if (!esEdicion) {
                    // Agregar a la lista
                    trabajo.getMaterialesAsignados().add(resultado.get());
                }

                // Actualizar tabla
                tablaMateriales.setItems(FXCollections.observableArrayList(trabajo.getMaterialesAsignados()));
            }
        } catch (Exception e) {
            logger.error("Error al abrir diálogo de material", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al procesar material",
                    "No se pudo procesar el material: " + e.getMessage());
        }
    }

    @FXML
    private void handleEliminarMaterial() {
        MaterialAsignadoDTO materialSeleccionado = tablaMateriales.getSelectionModel().getSelectedItem();
        if (materialSeleccionado == null) {
            return;
        }

        // Confirmar eliminación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro que desea eliminar este material?");
        confirmacion.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            // Eliminar material
            trabajo.getMaterialesAsignados().remove(materialSeleccionado);

            // Actualizar tabla
            tablaMateriales.setItems(FXCollections.observableArrayList(trabajo.getMaterialesAsignados()));
        }
    }

    @FXML
    private void handleGuardar() {
        if (!validarFormulario()) {
            return;
        }

        try {
            // Actualizar objeto trabajo con los datos del formulario
            actualizarTrabajoDesdeFormulario();

            // Guardar en la base de datos
            TrabajoDTO trabajoGuardado = trabajoApiClient.guardar(trabajo);

            // Mostrar mensaje de éxito
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Trabajo guardado",
                    "El trabajo ha sido guardado correctamente.");

            // Cerrar ventana
            Stage stage = (Stage) btnGuardar.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            logger.error("Error al guardar trabajo", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al guardar",
                    "No se pudo guardar el trabajo: " + e.getMessage());
        }
    }

    private boolean validarFormulario() {
        StringBuilder errores = new StringBuilder();

        if (txtCodigo.getText().trim().isEmpty()) {
            errores.append("- El código del trabajo es obligatorio\n");
        }

        if (cmbCliente.getValue() == null) {
            errores.append("- Debe seleccionar un cliente\n");
        }

        if (dpFechaProgramada.getValue() == null) {
            errores.append("- La fecha programada es obligatoria\n");
        }

        if (cmbEstado.getValue() == null || cmbEstado.getValue().isEmpty()) {
            errores.append("- Debe seleccionar un estado\n");
        }

        if (cmbPrioridad.getValue() == null) {
            errores.append("- Debe seleccionar una prioridad\n");
        }

        if (errores.length() > 0) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Corrija los siguientes errores:",
                    errores.toString());
            return false;
        }

        return true;
    }

    private void actualizarTrabajoDesdeFormulario() {
        trabajo.setCodigo(txtCodigo.getText().trim());

        // Presupuesto
        if (cmbPresupuesto.getValue() != null) {
            trabajo.setPresupuestoId(cmbPresupuesto.getValue().getId());
            trabajo.setNumeroPresupuesto(cmbPresupuesto.getValue().getNumero());
        }

        // Cliente
        if (cmbCliente.getValue() != null) {
            trabajo.setClienteId(cmbCliente.getValue().getId());
            trabajo.setNombreCliente(cmbCliente.getValue().getNombre() + " " + cmbCliente.getValue().getApellidos());
        }

        // Fechas
        if (!esEdicion) {
            trabajo.setFechaCreacion(LocalDateTime.now());
        }

        trabajo.setFechaProgramada(dpFechaProgramada.getValue());

        // Dirección
        trabajo.setDireccionInstalacion(txtDireccionInstalacion.getText().trim());

        // Estado
        // Aquí hay que manejar la conversión de nombre de estado a ID
        // En una implementación real, esto requeriría una llamada al servidor o un mapa de conversión

        // Prioridad
        trabajo.setPrioridad(cmbPrioridad.getValue());

        // Usuario asignado
        // Igual que con el estado, aquí habría que convertir del nombre al ID

        // Observaciones
        trabajo.setObservaciones(txtObservaciones.getText().trim());

        // Horas reales
        try {
            trabajo.setHorasReales(Integer.parseInt(txtHorasReales.getText().trim()));
        } catch (NumberFormatException e) {
            trabajo.setHorasReales(null);
        }

        // Los materiales ya se han actualizado en la lista
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