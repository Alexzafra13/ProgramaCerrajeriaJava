package com.gestiontaller.client.controller;

import com.gestiontaller.client.api.ComponenteSerieApiClient;
import com.gestiontaller.client.api.ProductoApiClient;
import com.gestiontaller.common.dto.producto.ProductoDTO;
import com.gestiontaller.common.dto.serie.MaterialBaseSerieDTO;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class ComponenteFormController {

    @FXML private TextField txtSerie;
    @FXML private TextField txtProducto;
    @FXML private Button btnBuscarProducto;
    @FXML private ComboBox<String> cmbTipoMaterial;
    @FXML private TextField txtDescripcion;
    @FXML private CheckBox chkPredeterminado;
    @FXML private TextArea txtNotasTecnicas;
    @FXML private TextField txtStockActual;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private final ComponenteSerieApiClient componenteSerieApiClient;
    private final ProductoApiClient productoApiClient;
    private MaterialBaseSerieDTO componenteActual;
    private boolean esEdicion = false;
    private ProductoDTO productoSeleccionado;

    @Autowired
    public ComponenteFormController(ComponenteSerieApiClient componenteSerieApiClient,
                                    ProductoApiClient productoApiClient) {
        this.componenteSerieApiClient = componenteSerieApiClient;
        this.productoApiClient = productoApiClient;
    }

    @FXML
    public void initialize() {
        // Inicializar tipos de material
        cmbTipoMaterial.setItems(FXCollections.observableArrayList(
                "HERRAJE_BASICO", "TORNILLERIA", "ACCESORIO", "SELLANTE"));
        cmbTipoMaterial.setValue("HERRAJE_BASICO");

        // Ocultar el boton de usuario del controlador para facilitar el acceso desde otras clases
        if (this.getScene() != null) {
            this.getScene().setUserData(this);
        }
    }

    private Scene getScene() {
        return txtSerie != null ? txtSerie.getScene() : null;
    }

    public void setComponente(MaterialBaseSerieDTO componente, String nombreSerie) {
        this.componenteActual = componente;
        this.esEdicion = componente.getId() != null;

        // Establecer valores en el formulario
        txtSerie.setText(nombreSerie);

        if (esEdicion) {
            // Si es edición, cargar todos los datos
            if (componente.getCodigoProducto() != null) {
                txtProducto.setText(componente.getCodigoProducto() + " - " + componente.getNombreProducto());
                txtStockActual.setText(componente.getStockActual() != null ?
                        componente.getStockActual().toString() : "0");
            }
            cmbTipoMaterial.setValue(componente.getTipoMaterial());
            txtDescripcion.setText(componente.getDescripcion());
            chkPredeterminado.setSelected(componente.getEsPredeterminado() != null &&
                    componente.getEsPredeterminado());
            txtNotasTecnicas.setText(componente.getNotasTecnicas());

            // Cargar el producto completo si es posible
            if (componente.getProductoId() != null) {
                try {
                    productoSeleccionado = productoApiClient.obtenerProductoPorId(componente.getProductoId());
                } catch (Exception e) {
                    productoSeleccionado = null;
                }
            }
        } else {
            // Si es nuevo, usar valores por defecto
            chkPredeterminado.setSelected(true);
        }
    }

    @FXML
    private void handleBuscarProducto() {
        try {
            // Mostrar diálogo de selección de producto
            // Aquí debería abrirse un diálogo para seleccionar producto
            // Por simplicidad, simularemos la selección

            // En una implementación real, aquí se abriría una pantalla de búsqueda
            // y se devolvería el producto seleccionado

            Dialog<ProductoDTO> dialog = new Dialog<>();
            dialog.setTitle("Seleccionar Producto");
            dialog.setHeaderText("Seleccione un producto para este componente");

            ButtonType seleccionarButtonType = new ButtonType("Seleccionar", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(seleccionarButtonType, ButtonType.CANCEL);

            // Aquí simplemente crearemos una lista simulada de productos
            // En la implementación real, esto vendría de la API
            ListView<ProductoDTO> listView = new ListView<>();

            // Obtener productos reales de la API
            List<ProductoDTO> productos = productoApiClient.buscarProductos("", null);
            listView.setItems(FXCollections.observableArrayList(productos));

            // Configurar la celda para mostrar código y nombre
            listView.setCellFactory(lv -> new ListCell<ProductoDTO>() {
                @Override
                protected void updateItem(ProductoDTO item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getCodigo() + " - " + item.getNombre());
                    }
                }
            });

            dialog.getDialogPane().setContent(listView);

            // Convertir la selección a ProductoDTO
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == seleccionarButtonType) {
                    return listView.getSelectionModel().getSelectedItem();
                }
                return null;
            });

            Optional<ProductoDTO> resultado = dialog.showAndWait();

            if (resultado.isPresent()) {
                productoSeleccionado = resultado.get();
                txtProducto.setText(productoSeleccionado.getCodigo() + " - " + productoSeleccionado.getNombre());
                txtStockActual.setText(productoSeleccionado.getStockActual() != null ?
                        productoSeleccionado.getStockActual().toString() : "0");

                // Si no se ha especificado una descripción, usar el nombre del producto
                if (txtDescripcion.getText().isEmpty()) {
                    txtDescripcion.setText(productoSeleccionado.getNombre());
                }
            }

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al buscar producto",
                    "No se pudo abrir la búsqueda de productos: " + e.getMessage());
        }
    }

    @FXML
    private void handleGuardar() {
        // Validar el formulario
        if (!validarFormulario()) {
            return;
        }

        // Actualizar los valores del componente
        componenteActual.setTipoMaterial(cmbTipoMaterial.getValue());
        componenteActual.setDescripcion(txtDescripcion.getText());
        componenteActual.setEsPredeterminado(chkPredeterminado.isSelected());
        componenteActual.setNotasTecnicas(txtNotasTecnicas.getText());

        // Establecer el producto seleccionado
        if (productoSeleccionado != null) {
            componenteActual.setProductoId(productoSeleccionado.getId());
            componenteActual.setCodigoProducto(productoSeleccionado.getCodigo());
            componenteActual.setNombreProducto(productoSeleccionado.getNombre());
            componenteActual.setPrecioUnitario(productoSeleccionado.getPrecioVenta());
            componenteActual.setStockActual(productoSeleccionado.getStockActual());
        }

        try {
            // Guardar en el servidor
            MaterialBaseSerieDTO resultado = componenteSerieApiClient.guardarComponente(componenteActual);

            // Mostrar mensaje de éxito
            mostrarAlerta(Alert.AlertType.INFORMATION, "Información", "Componente guardado",
                    "El componente ha sido guardado correctamente.");

            // Cerrar ventana
            Stage stage = (Stage) btnGuardar.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al guardar",
                    "No se pudo guardar el componente: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    private boolean validarFormulario() {
        StringBuilder errores = new StringBuilder();

        if (cmbTipoMaterial.getValue() == null) {
            errores.append("- Debe seleccionar un tipo de material\n");
        }

        if (txtDescripcion.getText().trim().isEmpty()) {
            errores.append("- Debe ingresar una descripción\n");
        }

        if (productoSeleccionado == null) {
            errores.append("- Debe seleccionar un producto\n");
        }

        if (errores.length() > 0) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Complete los campos requeridos",
                    errores.toString());
            return false;
        }

        return true;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String cabecera, String contenido) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(cabecera);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
}