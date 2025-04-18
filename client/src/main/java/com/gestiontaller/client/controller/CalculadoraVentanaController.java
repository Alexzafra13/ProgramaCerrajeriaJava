package com.gestiontaller.client.controller;

import com.gestiontaller.client.api.SerieApiClient;
import com.gestiontaller.client.model.TipoCristal;
import com.gestiontaller.client.model.presupuesto.TipoPresupuesto;
import com.gestiontaller.client.model.serie.SerieAluminioDTO;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    @FXML private TableView<MaterialDTO> tablaMateriales;
    @FXML private TableColumn<MaterialDTO, String> colDescripcionMaterial;
    @FXML private TableColumn<MaterialDTO, Integer> colCantidadMaterial;
    @FXML private TableColumn<MaterialDTO, Double> colPrecioUnitario;
    @FXML private TableColumn<MaterialDTO, Double> colPrecioTotal;

    private final SerieApiClient serieApiClient;

    @Autowired
    public CalculadoraVentanaController(SerieApiClient serieApiClient) {
        this.serieApiClient = serieApiClient;
    }

    @FXML
    public void initialize() {
        // Inicializar ComboBox
        cmbTipoPresupuesto.setItems(FXCollections.observableArrayList(TipoPresupuesto.values()));
        cmbTipoPresupuesto.setValue(TipoPresupuesto.VENTANA_CORREDERA);

        cmbTipoCristal.setItems(FXCollections.observableArrayList(TipoCristal.values()));
        cmbTipoCristal.setValue(TipoCristal.SIMPLE);

        // Cargar series de aluminio
        cargarSeries();

        // Inicializar tablas
        inicializarTablas();

        // Configurar listeners
        configurarListeners();

        // Por defecto, deshabilitar el campo de altura cajón
        txtAlturaCajon.setDisable(true);

        // Valores iniciales
        txtNumeroHojas.setText("2");
    }

    private void cargarSeries() {
        try {
            ObservableList<SerieAluminioDTO> series = FXCollections.observableArrayList(serieApiClient.obtenerSeriesAluminio());
            cmbSerie.setItems(series);

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
        colCodigoPerfil.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCodigoPerfil()));
        colNombrePerfil.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombrePerfil()));
        colLongitud.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getLongitud()).asObject());
        colCantidad.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCantidad()).asObject());
        colDescripcion.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescripcion()));

        // Configurar columnas de tabla de materiales
        colDescripcionMaterial.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescripcion()));
        colCantidadMaterial.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCantidad()).asObject());
        colPrecioUnitario.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrecioUnitario()).asObject());
        colPrecioTotal.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrecioTotal()).asObject());
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

        // Aquí iría la lógica real para realizar el cálculo
        // Por ahora solo simularemos resultados

        // Actualizar UI con resultado
        lblResumen.setText("Ventana Corredera ALUPROM-21 con 2 hojas");
        lblMedidasTotales.setText(txtAncho.getText() + " x " + txtAlto.getText() + " mm");
        lblMedidasVentana.setText(txtAncho.getText() + " x " + (Integer.parseInt(txtAlto.getText()) -
                (chkPersiana.isSelected() ? Integer.parseInt(txtAlturaCajon.getText()) : 0)) + " mm");
        lblPrecioTotal.setText("350.75 €");

        // Simular datos para las tablas (en una implementación real, estos datos vendrían de la API)
        ObservableList<CorteDTO> cortes = FXCollections.observableArrayList(
                new CorteDTO("ALUPROM21-ML", "Marco Lateral", 2000, 2, "Marco lateral"),
                new CorteDTO("ALUPROM21-MS", "Marco Superior", 1000, 1, "Marco superior"),
                new CorteDTO("ALUPROM21-MI", "Marco Inferior", 1000, 1, "Marco inferior"),
                new CorteDTO("ALUPROM21-HL", "Hoja Lateral", 1920, 2, "Hoja lateral")
        );

        ObservableList<MaterialDTO> materiales = FXCollections.observableArrayList(
                new MaterialDTO("Juego de rodamientos", 2, 8.50, 17.00),
                new MaterialDTO("Felpa (metros)", 10, 0.80, 8.00),
                new MaterialDTO("Cierre ventana corredera", 1, 12.30, 12.30),
                new MaterialDTO("Vidrio simple 4mm", 2, 15.00, 30.00)
        );

        tablaCortes.setItems(cortes);
        tablaMateriales.setItems(materiales);

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

    // Clases internas para las tablas
    public static class CorteDTO {
        private final String codigoPerfil;
        private final String nombrePerfil;
        private final int longitud;
        private final int cantidad;
        private final String descripcion;

        public CorteDTO(String codigoPerfil, String nombrePerfil, int longitud, int cantidad, String descripcion) {
            this.codigoPerfil = codigoPerfil;
            this.nombrePerfil = nombrePerfil;
            this.longitud = longitud;
            this.cantidad = cantidad;
            this.descripcion = descripcion;
        }

        public String getCodigoPerfil() { return codigoPerfil; }
        public String getNombrePerfil() { return nombrePerfil; }
        public int getLongitud() { return longitud; }
        public int getCantidad() { return cantidad; }
        public String getDescripcion() { return descripcion; }
    }

    public static class MaterialDTO {
        private final String descripcion;
        private final int cantidad;
        private final double precioUnitario;
        private final double precioTotal;

        public MaterialDTO(String descripcion, int cantidad, double precioUnitario, double precioTotal) {
            this.descripcion = descripcion;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
            this.precioTotal = precioTotal;
        }

        public String getDescripcion() { return descripcion; }
        public int getCantidad() { return cantidad; }
        public double getPrecioUnitario() { return precioUnitario; }
        public double getPrecioTotal() { return precioTotal; }
    }
}