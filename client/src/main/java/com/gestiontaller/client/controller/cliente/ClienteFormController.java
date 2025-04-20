package com.gestiontaller.client.controller.cliente;

import com.gestiontaller.client.api.ClienteApiClient;
import com.gestiontaller.common.dto.cliente.ClienteDTO;
import com.gestiontaller.common.model.cliente.TipoCliente;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class ClienteFormController {

    private static final Logger logger = LoggerFactory.getLogger(ClienteFormController.class);

    @FXML private TextField txtCodigo;
    @FXML private ComboBox<TipoCliente> cmbTipoCliente;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtCifNif;
    @FXML private TextField txtDireccionFiscal; // Cambiado de txtDireccion a txtDireccionFiscal
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private TextField txtDescuento;
    @FXML private TextArea txtObservaciones;

    // Campos adicionales que pueden estar en formulario extendido
    @FXML private TextField txtRazonSocial;
    @FXML private TextField txtDireccionEnvio;
    @FXML private TextField txtCodigoPostal;
    @FXML private TextField txtLocalidad;
    @FXML private TextField txtProvincia;
    @FXML private TextField txtPais;
    @FXML private TextField txtTelefono2;
    @FXML private TextField txtWeb;

    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private final ClienteApiClient clienteApiClient;
    private ClienteDTO clienteActual;
    private boolean esEdicion = false;

    @Autowired
    public ClienteFormController(ClienteApiClient clienteApiClient) {
        this.clienteApiClient = clienteApiClient;
    }

    @FXML
    public void initialize() {
        // Configurar ComboBox de tipo de cliente
        cmbTipoCliente.setItems(FXCollections.observableArrayList(TipoCliente.values()));
        cmbTipoCliente.setValue(TipoCliente.PARTICULAR);

        // Configurar validaciones básicas
        configurarValidaciones();

        // Configurar visible/invisible campos según tipo de cliente
        cmbTipoCliente.valueProperty().addListener((obs, oldVal, newVal) -> {
            actualizarVisibilidadCampos(newVal);
        });

        // Ocultar el botón de usuario del controlador para facilitar el acceso desde otras clases
        if (this.getScene() != null) {
            this.getScene().setUserData(this);
        }
    }

    private Scene getScene() {
        return txtCodigo != null ? txtCodigo.getScene() : null;
    }

    public void setCliente(ClienteDTO cliente, boolean esEdicion) {
        this.clienteActual = cliente;
        this.esEdicion = esEdicion;

        if (cliente == null) {
            this.clienteActual = new ClienteDTO();
            return;
        }

        // Cargar datos del cliente en el formulario
        if (esEdicion) {
            txtCodigo.setText(cliente.getCodigo());
            txtCodigo.setEditable(false); // No permitir cambiar el código en modo edición
        } else {
            // Si es nuevo, generar código si no tiene
            if (cliente.getCodigo() == null || cliente.getCodigo().isEmpty()) {
                TipoCliente tipo = cliente.getTipoCliente() != null ?
                        cliente.getTipoCliente() : TipoCliente.PARTICULAR;
                try {
                    String codigo = clienteApiClient.generarCodigo(tipo);
                    txtCodigo.setText(codigo);
                } catch (Exception e) {
                    logger.error("Error al generar código", e);
                    // Dejar campo en blanco para que el usuario ingrese uno
                }
            } else {
                txtCodigo.setText(cliente.getCodigo());
            }
        }

        // Establecer tipo de cliente
        if (cliente.getTipoCliente() != null) {
            cmbTipoCliente.setValue(cliente.getTipoCliente());
        }

        // Cargar resto de campos básicos
        txtNombre.setText(cliente.getNombre() != null ? cliente.getNombre() : "");
        txtApellidos.setText(cliente.getApellidos() != null ? cliente.getApellidos() : "");
        txtCifNif.setText(cliente.getNifCif() != null ? cliente.getNifCif() : "");
        txtDireccionFiscal.setText(cliente.getDireccionFiscal() != null ? cliente.getDireccionFiscal() : ""); // Actualizado
        txtTelefono.setText(cliente.getTelefono1() != null ? cliente.getTelefono1() : "");
        txtEmail.setText(cliente.getEmail() != null ? cliente.getEmail() : "");

        if (cliente.getDescuento() != null) {
            txtDescuento.setText(cliente.getDescuento().toString());
        } else {
            txtDescuento.setText("0");
        }

        txtObservaciones.setText(cliente.getObservaciones() != null ? cliente.getObservaciones() : "");

        // Cargar campos adicionales si están presentes en el formulario
        if (txtRazonSocial != null) {
            txtRazonSocial.setText(cliente.getRazonSocial() != null ? cliente.getRazonSocial() : "");
        }

        if (txtDireccionEnvio != null) {
            txtDireccionEnvio.setText(cliente.getDireccionEnvio() != null ? cliente.getDireccionEnvio() : "");
        }

        if (txtCodigoPostal != null) {
            txtCodigoPostal.setText(cliente.getCodigoPostal() != null ? cliente.getCodigoPostal() : "");
        }

        if (txtLocalidad != null) {
            txtLocalidad.setText(cliente.getLocalidad() != null ? cliente.getLocalidad() : "");
        }

        if (txtProvincia != null) {
            txtProvincia.setText(cliente.getProvincia() != null ? cliente.getProvincia() : "");
        }

        if (txtPais != null) {
            txtPais.setText(cliente.getPais() != null ? cliente.getPais() : "España");
        }

        if (txtTelefono2 != null) {
            txtTelefono2.setText(cliente.getTelefono2() != null ? cliente.getTelefono2() : "");
        }

        if (txtWeb != null) {
            txtWeb.setText(cliente.getWeb() != null ? cliente.getWeb() : "");
        }

        // Actualizar visibilidad de campos según tipo de cliente
        actualizarVisibilidadCampos(cliente.getTipoCliente());
    }

    private void configurarValidaciones() {
        // Validación para campo de descuento (solo números y decimales)
        txtDescuento.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                txtDescuento.setText(oldValue);
            }
        });

        // Otras validaciones pueden agregarse aquí
    }

    private void actualizarVisibilidadCampos(TipoCliente tipo) {
        if (tipo == null) {
            return;
        }

        boolean esEmpresa = tipo == TipoCliente.EMPRESA || tipo == TipoCliente.ADMINISTRACION;

        // Campos de persona física
        if (txtApellidos != null) {
            txtApellidos.setDisable(esEmpresa);
        }

        // Campos de empresa
        if (txtRazonSocial != null) {
            txtRazonSocial.setDisable(!esEmpresa);
        }
    }

    @FXML
    private void handleGuardar() {
        if (!validarFormulario()) {
            return;
        }

        try {
            // Actualizar el objeto DTO con los datos del formulario
            actualizarClienteDesdeFormulario();

            // Guardar en la base de datos
            ClienteDTO clienteGuardado = clienteApiClient.guardar(clienteActual);

            // Mostrar mensaje de éxito
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Cliente guardado",
                    "El cliente ha sido guardado correctamente.");

            // Cerrar ventana
            Stage stage = (Stage) btnGuardar.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            logger.error("Error al guardar cliente", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al guardar",
                    "No se pudo guardar el cliente: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    private boolean validarFormulario() {
        StringBuilder errores = new StringBuilder();

        if (txtCodigo.getText().trim().isEmpty()) {
            errores.append("- El código es obligatorio\n");
        }

        if (cmbTipoCliente.getValue() == null) {
            errores.append("- Debe seleccionar un tipo de cliente\n");
        }

        TipoCliente tipo = cmbTipoCliente.getValue();
        if (tipo == TipoCliente.PARTICULAR || tipo == TipoCliente.AUTONOMO) {
            if (txtNombre.getText().trim().isEmpty()) {
                errores.append("- El nombre es obligatorio\n");
            }
        } else {
            if (txtRazonSocial != null && txtRazonSocial.getText().trim().isEmpty()) {
                errores.append("- La razón social es obligatoria\n");
            }
        }

        if (txtTelefono.getText().trim().isEmpty() && txtEmail.getText().trim().isEmpty()) {
            errores.append("- Debe proporcionar al menos un método de contacto (teléfono o email)\n");
        }

        if (errores.length() > 0) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Corrija los siguientes errores:",
                    errores.toString());
            return false;
        }

        return true;
    }

    private void actualizarClienteDesdeFormulario() {
        // Datos básicos
        clienteActual.setCodigo(txtCodigo.getText().trim());
        clienteActual.setTipoCliente(cmbTipoCliente.getValue());
        clienteActual.setNombre(txtNombre.getText().trim());
        clienteActual.setApellidos(txtApellidos.getText().trim());
        clienteActual.setNifCif(txtCifNif.getText().trim());
        clienteActual.setDireccionFiscal(txtDireccionFiscal.getText().trim()); // Actualizado
        clienteActual.setTelefono1(txtTelefono.getText().trim());
        clienteActual.setEmail(txtEmail.getText().trim());

        // Descuento
        if (!txtDescuento.getText().trim().isEmpty()) {
            try {
                clienteActual.setDescuento(new BigDecimal(txtDescuento.getText().trim()));
            } catch (NumberFormatException e) {
                clienteActual.setDescuento(BigDecimal.ZERO);
            }
        } else {
            clienteActual.setDescuento(BigDecimal.ZERO);
        }

        clienteActual.setObservaciones(txtObservaciones.getText().trim());

        // Campos adicionales si están presentes
        if (txtRazonSocial != null) {
            clienteActual.setRazonSocial(txtRazonSocial.getText().trim());
        }

        if (txtDireccionEnvio != null) {
            clienteActual.setDireccionEnvio(txtDireccionEnvio.getText().trim());
        }

        if (txtCodigoPostal != null) {
            clienteActual.setCodigoPostal(txtCodigoPostal.getText().trim());
        }

        if (txtLocalidad != null) {
            clienteActual.setLocalidad(txtLocalidad.getText().trim());
        }

        if (txtProvincia != null) {
            clienteActual.setProvincia(txtProvincia.getText().trim());
        }

        if (txtPais != null) {
            clienteActual.setPais(txtPais.getText().trim());
        }

        if (txtTelefono2 != null) {
            clienteActual.setTelefono2(txtTelefono2.getText().trim());
        }

        if (txtWeb != null) {
            clienteActual.setWeb(txtWeb.getText().trim());
        }

        // Si es nuevo, establecer fecha de alta
        if (!esEdicion) {
            clienteActual.setFechaAlta(LocalDate.now());
            clienteActual.setActivo(true);
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