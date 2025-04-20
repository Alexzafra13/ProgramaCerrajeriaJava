package com.gestiontaller.client.controller.cliente;

import com.gestiontaller.client.api.ClienteApiClient;
import com.gestiontaller.client.util.FXMLLoaderUtil;
import com.gestiontaller.common.dto.cliente.ClienteDTO;
import com.gestiontaller.common.model.cliente.TipoCliente;

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

import java.util.List;
import java.util.Optional;

@Component
public class ClienteListController {

    private static final Logger logger = LoggerFactory.getLogger(ClienteListController.class);

    @FXML private TextField txtBusqueda;
    @FXML private TableView<ClienteDTO> tablaClientes;
    @FXML private TableColumn<ClienteDTO, String> colCodigo;
    @FXML private TableColumn<ClienteDTO, String> colNombre;
    @FXML private TableColumn<ClienteDTO, String> colTipo;
    @FXML private TableColumn<ClienteDTO, String> colTelefono;
    @FXML private TableColumn<ClienteDTO, String> colEmail;

    private final ClienteApiClient clienteApiClient;
    private final FXMLLoaderUtil fxmlLoaderUtil;

    @Autowired
    public ClienteListController(ClienteApiClient clienteApiClient, FXMLLoaderUtil fxmlLoaderUtil) {
        this.clienteApiClient = clienteApiClient;
        this.fxmlLoaderUtil = fxmlLoaderUtil;
    }

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarAccionesTabla();
        cargarClientes();
    }

    private void configurarColumnas() {
        colCodigo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCodigo()));

        colNombre.setCellValueFactory(data -> {
            ClienteDTO cliente = data.getValue();
            TipoCliente tipo = cliente.getTipoCliente();

            if (tipo == TipoCliente.EMPRESA || tipo == TipoCliente.ADMINISTRACION) {
                return new SimpleStringProperty(cliente.getRazonSocial() != null ? cliente.getRazonSocial() : "");
            } else {
                String nombreCompleto = (cliente.getNombre() != null ? cliente.getNombre() : "") +
                        " " +
                        (cliente.getApellidos() != null ? cliente.getApellidos() : "");
                return new SimpleStringProperty(nombreCompleto.trim());
            }
        });

        colTipo.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTipoCliente() != null ?
                        data.getValue().getTipoCliente().toString() : ""));

        colTelefono.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTelefono1() != null ?
                        data.getValue().getTelefono1() : ""));

        colEmail.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEmail() != null ?
                        data.getValue().getEmail() : ""));
    }

    private void configurarAccionesTabla() {
        // Habilitar selección de fila única
        tablaClientes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Configurar doble clic para editar
        tablaClientes.setRowFactory(tv -> {
            TableRow<ClienteDTO> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    handleEditar();
                }
            });
            return row;
        });
    }

    private void cargarClientes() {
        try {
            List<ClienteDTO> clientes = clienteApiClient.obtenerTodos();
            tablaClientes.setItems(FXCollections.observableArrayList(clientes));
        } catch (Exception e) {
            logger.error("Error al cargar clientes", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al cargar clientes",
                    "No se pudieron cargar los clientes: " + e.getMessage());
        }
    }

    @FXML
    private void handleBuscar() {
        String textoBusqueda = txtBusqueda.getText().trim();

        if (textoBusqueda.isEmpty()) {
            cargarClientes();
            return;
        }

        try {
            List<ClienteDTO> clientesFiltrados = clienteApiClient.buscar(textoBusqueda);
            tablaClientes.setItems(FXCollections.observableArrayList(clientesFiltrados));
        } catch (Exception e) {
            logger.error("Error al buscar clientes", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al buscar clientes",
                    "No se pudo realizar la búsqueda: " + e.getMessage());
        }
    }

    @FXML
    private void handleNuevoCliente() {
        try {
            // Cargar vista del formulario
            Parent root = fxmlLoaderUtil.loadFXML("/fxml/cliente/cliente-form.fxml");

            // Obtener controlador y configurarlo para nuevo cliente
            ClienteFormController controller = (ClienteFormController) root.getUserData();
            controller.setCliente(new ClienteDTO(), false);

            // Configurar y mostrar ventana
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Nuevo Cliente");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Recargar lista después de cerrar
            cargarClientes();
        } catch (Exception e) {
            logger.error("Error al abrir formulario de cliente", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al abrir formulario",
                    "No se pudo abrir el formulario de cliente: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditar() {
        ClienteDTO clienteSeleccionado = tablaClientes.getSelectionModel().getSelectedItem();
        if (clienteSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Selección requerida",
                    "Por favor, seleccione un cliente para editar.");
            return;
        }

        try {
            // Cargar cliente completo
            ClienteDTO clienteCompleto = clienteApiClient.obtenerPorId(clienteSeleccionado.getId());

            // Cargar vista del formulario
            Parent root = fxmlLoaderUtil.loadFXML("/fxml/cliente/cliente-form.fxml");

            // Obtener controlador y configurarlo para editar
            ClienteFormController controller = (ClienteFormController) root.getUserData();
            controller.setCliente(clienteCompleto, true);

            // Configurar y mostrar ventana
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Editar Cliente");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Recargar lista después de cerrar
            cargarClientes();
        } catch (Exception e) {
            logger.error("Error al abrir formulario de edición", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al abrir formulario",
                    "No se pudo abrir el formulario de edición: " + e.getMessage());
        }
    }

    @FXML
    private void handleEliminar() {
        ClienteDTO clienteSeleccionado = tablaClientes.getSelectionModel().getSelectedItem();
        if (clienteSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Selección requerida",
                    "Por favor, seleccione un cliente para eliminar.");
            return;
        }

        // Confirmar eliminación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro que desea eliminar este cliente?");
        confirmacion.setContentText("Esta acción desactivará el cliente: " + clienteSeleccionado.getCodigo());

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                clienteApiClient.eliminar(clienteSeleccionado.getId());

                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Cliente eliminado",
                        "El cliente ha sido desactivado correctamente.");

                // Recargar lista
                cargarClientes();
            } catch (Exception e) {
                logger.error("Error al eliminar cliente", e);
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al eliminar cliente",
                        "No se pudo eliminar el cliente: " + e.getMessage());
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