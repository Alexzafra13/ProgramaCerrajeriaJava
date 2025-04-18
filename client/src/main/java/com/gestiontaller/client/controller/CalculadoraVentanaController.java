package com.gestiontaller.client.controller;

import com.gestiontaller.client.api.SerieApiClient;
import com.gestiontaller.client.model.TipoCristal;
import com.gestiontaller.client.model.presupuesto.TipoPresupuesto;
import com.gestiontaller.client.model.serie.SerieAluminioDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CalculadoraVentanaController {

    @FXML private ComboBox<TipoPresupuesto> cmbTipoPresupuesto;
    @FXML private ComboBox<SerieAluminioDTO> cmbSerie;
    @FXML private TextField txtAncho;
    @FXML private TextField txtAlto;
    @FXML private TextField txtNumeroHojas;
    @FXML private CheckBox chkPersiana;
    @FXML private TextField txtAlturaCajon;
    @FXML private CheckBox chkAltoTotal;
    @FXML private HBox hboxTipoCristal;
    @FXML private ComboBox<TipoCristal> cmbTipoCristal;

    @FXML private Button btnCalcular;
    @FXML private Button btnLimpiar;

    @FXML private TabPane tabResultados;
    @FXML private Tab tabResumen;
    @FXML private Tab tabCortes;
    @FXML private Tab tabMateriales;

    @FXML private Label lblResumen;
    @FXML private Label lblMedidasTotales;
    @FXML private Label lblMedidasVentana;
    @FXML private Label lblPrecioTotal;

    @FXML private TableView<CorteDTO> tablaCortes;
    @FXML private TableColumn<CorteDTO, String> colCodigoPerfil;
    @FXML private TableColumn<CorteDTO, String> colNombrePerfil;
    @FXML private TableColumn<CorteDTO, Integer> colLongitud;
    @FXML private TableColumn<CorteDTO, Integer> colCantidad;
    @FXML private TableColumn<CorteDTO, String> colDescripcion;

    @FXML private TableView<MaterialAdicionalDTO> tablaMateriales;
    @FXML private TableColumn<MaterialAdicionalDTO, String> colDescripcionMaterial;
    @FXML private TableColumn<MaterialAdicionalDTO, Integer> colCantidadMaterial;
    @FXML private TableColumn<MaterialAdicionalDTO, Double> colPrecioUnitario;
    @FXML private TableColumn<MaterialAdicionalDTO, Double> colPrecioTotal;

    private final SerieApiClient serieApiClient;
    // Otros servicios de cliente necesarios...

    @Autowired
    public CalculadoraVentanaController(SerieApiClient serieApiClient) {
        this.serieApiClient = serieApiClient;
        // Inicialización de otros servicios...
    }

    @FXML
    public void initialize() {
        // Inicializar ComboBox
        cmbTipoPresupuesto.getItems().setAll(TipoPresupuesto.values());
        cmbTipoPresupuesto.setValue(TipoPresupuesto.VENTANA_CORREDERA);

        cmbTipoCristal.getItems().setAll(TipoCristal.values());
        cmbTipoCristal.setValue(TipoCristal.SIMPLE);

        // Cargar series de aluminio
        cargarSeries();

        // Inicializar tablas
        inicializarTablas();

        // Configurar listeners
        configurarListeners();

        // Por defecto, deshabilitar el campo de altura cajón
        txtAlturaCajon.setDisable(true);
    }

    private void cargarSeries() {
        try {
            List<SerieAluminioDTO> series = serieApiClient.obtenerSeriesAluminio();
            cmbSerie.getItems().setAll(series);

            // Seleccionar por defecto la serie ALUPROM-21 si está disponible
            for (SerieAluminioDTO serie : series) {
                if ("ALUPROM-21".equals(serie.getCodigo())) {
                    cmbSerie.setValue(serie);
                    break;
                }
            }
        } catch (Exception e) {
            mostrarError("Error al cargar series", "No se pudieron cargar las series: " + e.getMessage());
        }
    }

    private void inicializarTablas() {
        // Configurar columnas de tabla de cortes
        colCodigoPerfil.setCellValueFactory(new PropertyValueFactory<>("codigoPerfil"));
        colNombrePerfil.setCellValueFactory(new PropertyValueFactory<>("nombrePerfil"));
        colLongitud.setCellValueFactory(new PropertyValueFactory<>("longitud"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        // Configurar columnas de tabla de materiales
        colDescripcionMaterial.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colCantidadMaterial.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecioUnitario.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colPrecioTotal.setCellValueFactory(new PropertyValueFactory<>("precioTotal"));
    }

    private void configurarListeners() {
        // Listener para checkbox de persiana
        chkPersiana.selectedProperty().addListener((obs, oldVal, newVal) -> {
            txtAlturaCajon.setDisable(!newVal);
            if (!newVal) {
                txtAlturaCajon.setText("");
            } else {
                txtAlturaCajon.setText("160"); // Valor por defecto
            }
        });

        // Listener para tipo de presupuesto
        cmbTipoPresupuesto.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == TipoPresupuesto.VENTANA_CORREDERA) {
                txtNumeroHojas.setText("2"); // Por defecto 2 hojas para correderas
            } else if (newVal == TipoPresupuesto.VENTANA_ABATIBLE) {
                txtNumeroHojas.setText("1"); // Por defecto 1 hoja para abatibles
            }
        });

        // Formateo numérico para campos de texto
        txtAncho.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                txtAncho.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });

        txtAlto.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                txtAlto.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });

        txtNumeroHojas.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                txtNumeroHojas.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });

        txtAlturaCajon.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                txtAlturaCajon.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });
    }

    @FXML
    private void handleCalcular(ActionEvent event) {
        // Validar formulario
        if (!validarFormulario()) {
            return;
        }

        // TO-DO: Implementar la lógica de cálculo a través de llamadas a API
        // Por ahora mostraremos datos de ejemplo

        // Actualizar UI con resultado
        lblResumen.setText("Ventana Corredera ALUPROM-21 con 2 hojas");
        lblMedidasTotales.setText(txtAncho.getText() + " x " + txtAlto.getText() + " mm");
        lblMedidasVentana.setText(txtAncho.getText() + " x " + (Integer.parseInt(txtAlto.getText()) -
                (chkPersiana.isSelected() ? Integer.parseInt(txtAlturaCajon.getText()) : 0)) + " mm");
        lblPrecioTotal.setText("350.75 €");

        // Simular datos para las tablas
        // En una implementación real, estos datos vendrían de la API
        tablaCortes.getItems().clear();
        tablaMateriales.getItems().clear();

        // Cambiar a la pestaña de resumen
        tabResultados.getSelectionModel().select(tabResumen);
    }

    @FXML
    private void handleLimpiar(ActionEvent event) {
        txtAncho.clear();
        txtAlto.clear();
        txtNumeroHojas.setText("2");
        chkPersiana.setSelected(false);
        txtAlturaCajon.clear();
        chkAltoTotal.setSelected(false);
        cmbTipoCristal.setValue(TipoCristal.SIMPLE);

        // Limpiar tablas y resultados
        tablaCortes.getItems().clear();
        tablaMateriales.getItems().clear();
        lblResumen.setText("No hay resultados todavía");
        lblMedidasTotales.setText("-");
        lblMedidasVentana.setText("-");
        lblPrecioTotal.setText("0.00 €");
    }

    private boolean validarFormulario() {
        StringBuilder errores = new StringBuilder();

        if (cmbSerie.getValue() == null) {
            errores.append("- Debe seleccionar una serie\n");
        }

        if (txtAncho.getText().trim().isEmpty()) {
            errores.append("- El ancho es obligatorio\n");
        } else {
            try {
                int ancho = Integer.parseInt(txtAncho.getText().trim());
                if (ancho <= 0) {
                    errores.append("- El ancho debe ser mayor que 0\n");
                }
            } catch (NumberFormatException e) {
                errores.append("- El ancho debe ser un número entero\n");
            }
        }

        if (txtAlto.getText().trim().isEmpty()) {
            errores.append("- El alto es obligatorio\n");
        } else {
            try {
                int alto = Integer.parseInt(txtAlto.getText().trim());
                if (alto <= 0) {
                    errores.append("- El alto debe ser mayor que 0\n");
                }
            } catch (NumberFormatException e) {
                errores.append("- El alto debe ser un número entero\n");
            }
        }

        if (txtNumeroHojas.getText().trim().isEmpty()) {
            errores.append("- El número de hojas es obligatorio\n");
        } else {
            try {
                int numeroHojas = Integer.parseInt(txtNumeroHojas.getText().trim());
                if (numeroHojas <= 0) {
                    errores.append("- El número de hojas debe ser mayor que 0\n");
                }
            } catch (NumberFormatException e) {
                errores.append("- El número de hojas debe ser un número entero\n");
            }
        }

        if (chkPersiana.isSelected() && txtAlturaCajon.getText().trim().isEmpty()) {
            errores.append("- La altura del cajón de persiana es obligatoria\n");
        } else if (chkPersiana.isSelected()) {
            try {
                int alturaCajon = Integer.parseInt(txtAlturaCajon.getText().trim());
                if (alturaCajon <= 0) {
                    errores.append("- La altura del cajón debe ser mayor que 0\n");
                }
            } catch (NumberFormatException e) {
                errores.append("- La altura del cajón debe ser un número entero\n");
            }
        }

        if (errores.length() > 0) {
            mostrarError("Error de validación", errores.toString());
            return false;
        }

        return true;
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // DTOs simplificados para la demostración
    public static class CorteDTO {
        private String codigoPerfil;
        private String nombrePerfil;
        private Integer longitud;
        private Integer cantidad;
        private String descripcion;

        // Getters and setters...
    }

    public static class MaterialAdicionalDTO {
        private String descripcion;
        private Integer cantidad;
        private Double precioUnitario;
        private Double precioTotal;

        // Getters and setters...
    }
}