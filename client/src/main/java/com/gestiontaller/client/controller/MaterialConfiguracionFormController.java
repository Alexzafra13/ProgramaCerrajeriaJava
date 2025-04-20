package com.gestiontaller.client.controller;

import com.gestiontaller.client.api.ComponenteSerieApiClient;
import com.gestiontaller.client.api.ProductoApiClient;
import com.gestiontaller.common.dto.configuracion.MaterialConfiguracionDTO;
import com.gestiontaller.common.dto.producto.ProductoDTO;
import com.gestiontaller.common.dto.serie.MaterialBaseSerieDTO;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class MaterialConfiguracionFormController {

    @FXML private TextField txtConfiguracion;
    @FXML private TextField txtProducto;
    @FXML private Button btnBuscarProducto;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtCantidadBase;
    @FXML private TextField txtPrecioUnitario;
    @FXML private TextArea txtFormulaCantidad;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private final ComponenteSerieApiClient componenteSerieApiClient;
    private final ProductoApiClient productoApiClient;
    private MaterialConfiguracionDTO materialActual;
    private boolean esEdicion = false;
    private ProductoDTO productoSeleccionado;

    @Autowired
    public MaterialConfiguracionFormController(ComponenteSerieApiClient componenteSerieApiClient,
                                               ProductoApiClient productoApiClient) {
        this.componenteSerieApiClient = componenteSerieApiClient;
        this.productoApiClient = productoApiClient;
    }

    @FXML
    public void initialize() {
        // Ocultar el boton de usuario del controlador para facilitar el acceso desde otras clases
        if (this.getScene() != null) {
            this.getScene().setUserData(this);
        }
    }

    private Scene getScene() {
        return txtConfiguracion != null ? txtConfiguracion.getScene() : null;
    }

    public void setMaterial(MaterialConfiguracionDTO material, String nombreConfiguracion) {
        this.materialActual = material;
        this.esEdicion = material.getId() != null;

        // Establecer valores en el formulario
        txtConfiguracion.setText(nombreConfiguracion);

        if (esEdicion) {
            // Si es edición, cargar todos los datos
            txtDescripcion.setText(material.getDescripcion());
            txtCantidadBase.setText(material.getCantidadBase().toString());
            txtFormulaCantidad.setText(material.getFormulaCantidad());

            // Cargar datos del producto si existe
            if (material.getProductoId() != null) {
                try {
                    productoSeleccionado = productoApiClient.obtenerProductoPorId(material.getProductoId());
                    txtProducto.setText(productoSeleccionado.getCodigo() + " - " + productoSeleccionado.getNombre());
                    txtPrecioUnitario.setText(String.format("%.2f €", productoSeleccionado.getPrecioVenta()));
                } catch (Exception e) {
                    productoSeleccionado = null;
                }
            }
        } else {
            // Si es nuevo, usar valores por defecto
            txtCantidadBase.setText("1");
        }
    }

    @FXML
    private void handleBuscarProducto() {
        try {
            // Mostrar diálogo de selección de producto
            Dialog<ProductoDTO> dialog = new Dialog<>();
            dialog.setTitle("Seleccionar Producto");
            dialog.setHeaderText("Seleccione un producto para este material");

            ButtonType seleccionarButtonType = new ButtonType("Seleccionar", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(seleccionarButtonType, ButtonType.CANCEL);

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
                txtPrecioUnitario.setText(String.format("%.2f €", productoSeleccionado.getPrecioVenta()));

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

        // Actualizar los valores del material
        materialActual.setDescripcion(txtDescripcion.getText());

        try {
            materialActual.setCantidadBase(Integer.parseInt(txtCantidadBase.getText().trim()));
        } catch (NumberFormatException e) {
            materialActual.setCantidadBase(1); // Valor por defecto
        }

        materialActual.setFormulaCantidad(txtFormulaCantidad.getText());

        // Establecer el producto seleccionado
        if (productoSeleccionado != null) {
            materialActual.setProductoId(productoSeleccionado.getId());
            materialActual.setPrecioUnitario(productoSeleccionado.getPrecioVenta());
        }

        try {
            // Guardar en el servidor
            MaterialConfiguracionDTO resultado = componenteSerieApiClient.guardarMaterialConfiguracion(materialActual);

            // Mostrar mensaje de éxito
            mostrarAlerta(Alert.AlertType.INFORMATION, "Información", "Material guardado",
                    "El material ha sido guardado correctamente.");

            // Cerrar ventana
            Stage stage = (Stage) btnGuardar.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al guardar",
                    "No se pudo guardar el material: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    private boolean validarFormulario() {
        StringBuilder errores = new StringBuilder();

        if (txtDescripcion.getText().trim().isEmpty()) {
            errores.append("- Debe ingresar una descripción\n");
        }

        try {
            int cantidadBase = Integer.parseInt(txtCantidadBase.getText().trim());
            if (cantidadBase < 0) {
                errores.append("- La cantidad base no puede ser negativa\n");
            }
        } catch (NumberFormatException e) {
            errores.append("- La cantidad base debe ser un número entero\n");
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