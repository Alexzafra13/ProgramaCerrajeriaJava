// client/src/main/java/com/gestiontaller/client/controller/CalculadoraVentanaController.java

package com.gestiontaller.client.controller;

import com.gestiontaller.client.api.ConfiguracionSerieApiClient;
import com.gestiontaller.client.api.SerieApiClient;
import com.gestiontaller.client.model.TipoCristal;
import com.gestiontaller.client.model.calculo.CorteDTO;
import com.gestiontaller.client.model.calculo.MaterialAdicionalDTO;
import com.gestiontaller.client.model.calculo.ResultadoCalculoDTO;
import com.gestiontaller.client.model.configuracion.PlantillaConfiguracionSerieDTO;
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
    private final ConfiguracionSerieApiClient configuracionSerieApiClient;

    @Autowired
    public CalculadoraVentanaController(
            SerieApiClient serieApiClient,
            ConfiguracionSerieApiClient configuracionSerieApiClient) {
        this.serieApiClient = serieApiClient;
        this.configuracionSerieApiClient = configuracionSerieApiClient;
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
    }

    private void cargarSeries() {
        try {
            System.out.println("Iniciando carga de series...");
            List<SerieAluminioDTO> series = serieApiClient.obtenerSeriesAluminio();

            // Si no hay series disponibles, mostrar error
            if (series.isEmpty()) {
                mostrarError("Error", "No se encontraron series de aluminio. Contacte con el administrador.");
                return;
            }

            ObservableList<SerieAluminioDTO> seriesObservables = FXCollections.observableArrayList(series);
            cmbSerie.setItems(seriesObservables);

            // Buscar ALUPROM-21 u otra serie por defecto
            boolean encontrada = false;
            for (SerieAluminioDTO serie : series) {
                if ("ALUPROM-21".equals(serie.getCodigo())) {
                    System.out.println("Serie ALUPROM-21 encontrada. Estableciendo como valor por defecto.");
                    cmbSerie.setValue(serie);
                    encontrada = true;
                    break;
                }
            }

            if (!encontrada && !series.isEmpty()) {
                System.out.println("No se encontró ALUPROM-21. Seleccionando primera serie disponible: " +
                        series.get(0).getCodigo());
                cmbSerie.setValue(series.get(0));
            }

            // Cargar configuraciones para la serie seleccionada
            if (cmbSerie.getValue() != null) {
                cargarConfiguracionesSerie(cmbSerie.getValue().getId());
            }
        } catch (Exception e) {
            System.err.println("Error al cargar series: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error de conexión",
                    "No se pudieron cargar las series. Verifique la conexión con el servidor.");
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

        // Listener para serie seleccionada
        cmbSerie.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // Buscar configuraciones disponibles para esta serie
                cargarConfiguracionesSerie(newVal.getId());
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

    private void cargarConfiguracionesSerie(Long serieId) {
        try {
            List<PlantillaConfiguracionSerieDTO> configuraciones =
                    configuracionSerieApiClient.obtenerConfiguracionesPorSerie(serieId);

            if (configuraciones.isEmpty()) {
                mostrarError("Error de configuración",
                        "No existen configuraciones para esta serie. Contacte con el administrador.");
                txtNumeroHojas.setEditable(true);
                txtNumeroHojas.setDisable(false);
                txtNumeroHojas.setText("2"); // Valor por defecto general
                return;
            }

            // Si hay configuraciones disponibles, actualizar el número de hojas según la primera configuración
            PlantillaConfiguracionSerieDTO primerConfig = configuraciones.get(0);
            txtNumeroHojas.setText(primerConfig.getNumHojas().toString());

            // Si solo hay una configuración con un número específico de hojas, deshabilitar el campo
            if (configuraciones.size() == 1) {
                txtNumeroHojas.setEditable(false);
                txtNumeroHojas.setDisable(true);
            } else {
                txtNumeroHojas.setEditable(true);
                txtNumeroHojas.setDisable(false);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar configuraciones de serie: " + e.getMessage());
            mostrarError("Error", "No se pudieron cargar las configuraciones para esta serie");
        }
    }

    @FXML
    private void handleCalcular(ActionEvent event) {
        // Validar formulario
        if (!validarFormulario()) {
            return;
        }

        SerieAluminioDTO serie = cmbSerie.getValue();
        if (serie == null) {
            mostrarError("Error", "Debe seleccionar una serie");
            return;
        }

        int ancho = Integer.parseInt(txtAncho.getText().trim());
        int alto = Integer.parseInt(txtAlto.getText().trim());
        int numHojas = Integer.parseInt(txtNumeroHojas.getText().trim());
        boolean incluyePersiana = chkPersiana.isSelected();
        Integer alturaCajon = null;
        if (incluyePersiana && !txtAlturaCajon.getText().trim().isEmpty()) {
            alturaCajon = Integer.parseInt(txtAlturaCajon.getText().trim());
        }
        TipoCristal tipoCristal = cmbTipoCristal.getValue();

        try {
            // Obtener la configuración específica para esta serie y número de hojas
            PlantillaConfiguracionSerieDTO config =
                    configuracionSerieApiClient.obtenerConfiguracionPorSerieYHojas(serie.getId(), numHojas);

            if (config == null) {
                mostrarError("Error", "No existe configuración para " + serie.getNombre() +
                        " con " + numHojas + " hojas.");
                return;
            }

            // Aplicar la configuración para calcular
            ResultadoCalculoDTO resultado = configuracionSerieApiClient.aplicarConfiguracion(
                    config.getId(), ancho, alto, incluyePersiana, alturaCajon, tipoCristal);

            if (resultado == null) {
                mostrarError("Error", "No se pudo obtener el resultado del cálculo");
                return;
            }

            // Actualizar la interfaz con el resultado
            actualizarUI(resultado);

        } catch (Exception e) {
            mostrarError("Error", "Error al realizar el cálculo: " + e.getMessage());
        }
    }

    @FXML
    private void handleLimpiar(ActionEvent event) {
        txtAncho.clear();
        txtAlto.clear();

        // No limpiar el número de hojas, lo dejamos según la configuración de la serie
        if (cmbSerie.getValue() != null) {
            cargarConfiguracionesSerie(cmbSerie.getValue().getId());
        } else {
            txtNumeroHojas.setText("2");
        }

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

    // Método para actualizar la interfaz con el resultado
    private void actualizarUI(ResultadoCalculoDTO resultado) {
        // Actualizar resumen
        lblResumen.setText(resultado.getResumen());
        lblMedidasTotales.setText(resultado.getAncho() + " x " + resultado.getAlto() + " mm");

        if (resultado.getAltoVentana() != null && !resultado.getAltoVentana().equals(resultado.getAlto())) {
            lblMedidasVentana.setText(resultado.getAnchoVentana() + " x " + resultado.getAltoVentana() + " mm");
        } else {
            lblMedidasVentana.setText("Igual a medidas totales");
        }

        lblPrecioTotal.setText(String.format("%.2f €", resultado.getPrecioTotal()));

        // Actualizar tabla de cortes
        if (resultado.getCortes() != null) {
            ObservableList<CorteDTO> cortes = FXCollections.observableArrayList(resultado.getCortes());
            tablaCortes.setItems(cortes);
        }

        // Actualizar tabla de materiales
        if (resultado.getMaterialesAdicionales() != null) {
            ObservableList<MaterialAdicionalDTO> materiales = FXCollections.observableArrayList(resultado.getMaterialesAdicionales());
            tablaMateriales.setItems(materiales);
        }

        // Cambiar a la pestaña de resumen
        tabResultados.getSelectionModel().select(tabResumen);
    }
}