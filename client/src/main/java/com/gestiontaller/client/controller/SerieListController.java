package com.gestiontaller.client.controller;

import com.gestiontaller.client.api.SerieApiClient;
import com.gestiontaller.client.util.FXMLLoaderUtil;
import com.gestiontaller.common.dto.serie.SerieAluminioDTO;
import com.gestiontaller.common.model.serie.TipoSerie;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SerieListController {

    // Elementos de la tabla
    @FXML private TableView<SerieAluminioDTO> tablaSeries;
    @FXML private TableColumn<SerieAluminioDTO, String> colCodigo;
    @FXML private TableColumn<SerieAluminioDTO, String> colNombre;
    @FXML private TableColumn<SerieAluminioDTO, String> colTipo;
    @FXML private TableColumn<SerieAluminioDTO, String> colRoturaPuente;
    @FXML private TableColumn<SerieAluminioDTO, String> colPermitePersiana;
    @FXML private TableColumn<SerieAluminioDTO, String> colPrecioBase;

    // Elementos de filtro
    @FXML private ComboBox<TipoSerie> cmbFiltroTipo;
    @FXML private TextField txtBusqueda;

    // Botones
    @FXML private Button btnAgregar;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnCreacionRapida;

    // Servicios
    private final SerieApiClient serieApiClient;
    private final FXMLLoaderUtil fxmlLoaderUtil;

    @Autowired
    public SerieListController(SerieApiClient serieApiClient, FXMLLoaderUtil fxmlLoaderUtil) {
        this.serieApiClient = serieApiClient;
        this.fxmlLoaderUtil = fxmlLoaderUtil;
    }

    @FXML
    public void initialize() {
        // Configurar columnas de la tabla
        configurarColumnas();

        // Inicializar filtros
        inicializarFiltros();

        // Configurar eventos de selección
        configurarEventosSeleccion();

        // Cargar datos iniciales
        cargarSeries();
    }

    private void configurarColumnas() {
        colCodigo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCodigo()));
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombre()));
        colTipo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTipoSerie() != null ?
                data.getValue().getTipoSerie().toString() : ""));
        colRoturaPuente.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().isRoturaPuente() ? "Sí" : "No"));
        colPermitePersiana.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().isPermitePersiana() ? "Sí" : "No"));
        colPrecioBase.setCellValueFactory(data -> new SimpleStringProperty(
                String.format("%.2f €/m", data.getValue().getPrecioMetroBase())));
    }

    private void inicializarFiltros() {
        // Cargar datos de filtro
        cmbFiltroTipo.setItems(FXCollections.observableArrayList(TipoSerie.values()));
        cmbFiltroTipo.getItems().add(0, null); // Opción para mostrar todos
        cmbFiltroTipo.setPromptText("Todos");

        // Configurar listener para filtro
        cmbFiltroTipo.valueProperty().addListener((obs, oldVal, newVal) -> filtrarSeries());
        txtBusqueda.textProperty().addListener((obs, oldVal, newVal) -> filtrarSeries());
    }

    private void configurarEventosSeleccion() {
        // Listener para habilitar/deshabilitar botones según selección
        tablaSeries.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    boolean haySeleccion = newSelection != null;
                    btnEditar.setDisable(!haySeleccion);
                    btnEliminar.setDisable(!haySeleccion);
                });
    }

    private void cargarSeries() {
        try {
            List<SerieAluminioDTO> series = serieApiClient.obtenerSeriesAluminio();
            tablaSeries.setItems(FXCollections.observableArrayList(series));
        } catch (Exception e) {
            mostrarError("Error al cargar series", "No se pudieron cargar las series: " + e.getMessage());
        }
    }

    private void filtrarSeries() {
        try {
            List<SerieAluminioDTO> todasLasSeries = serieApiClient.obtenerSeriesAluminio();
            TipoSerie tipoSeleccionado = cmbFiltroTipo.getValue();
            String textoBusqueda = txtBusqueda.getText().toLowerCase();

            List<SerieAluminioDTO> seriesFiltradas = todasLasSeries.stream()
                    .filter(serie -> tipoSeleccionado == null || serie.getTipoSerie() == tipoSeleccionado)
                    .filter(serie -> textoBusqueda.isEmpty() ||
                            serie.getCodigo().toLowerCase().contains(textoBusqueda) ||
                            serie.getNombre().toLowerCase().contains(textoBusqueda))
                    .collect(Collectors.toList());

            tablaSeries.setItems(FXCollections.observableArrayList(seriesFiltradas));
        } catch (Exception e) {
            mostrarError("Error al filtrar series", "No se pudieron filtrar las series: " + e.getMessage());
        }
    }

    @FXML
    private void handleAgregar() {
        try {
            // Cargar la vista del formulario
            Parent root = fxmlLoaderUtil.loadFXML("/fxml/serie/serie-form.fxml");

            // Obtener el controlador y configurarlo para una nueva serie
            SerieFormController controller = (SerieFormController) root.getUserData();
            controller.setSerie(new SerieAluminioDTO());

            // Mostrar en una nueva ventana
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Nueva Serie");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Actualizar la tabla después de cerrar el formulario
            cargarSeries();
        } catch (Exception e) {
            mostrarError("Error", "Error al abrir el formulario: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditar() {
        SerieAluminioDTO seleccionada = tablaSeries.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarError("Error", "Debe seleccionar una serie para editar");
            return;
        }

        try {
            // Cargar la vista del formulario
            Parent root = fxmlLoaderUtil.loadFXML("/fxml/serie/serie-form.fxml");

            // Obtener el controlador y configurarlo con la serie seleccionada
            SerieFormController controller = (SerieFormController) root.getUserData();
            controller.setSerie(seleccionada);

            // Mostrar en una nueva ventana
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Editar Serie");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Actualizar la tabla después de cerrar el formulario
            cargarSeries();
        } catch (Exception e) {
            mostrarError("Error", "Error al abrir el formulario: " + e.getMessage());
        }
    }

    @FXML
    private void handleEliminar() {
        SerieAluminioDTO seleccionada = tablaSeries.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarError("Error", "Debe seleccionar una serie para eliminar");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro que desea eliminar la serie?");
        confirmacion.setContentText("Serie: " + seleccionada.getNombre());

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                // Llamar al API para eliminar
                serieApiClient.eliminarSerie(seleccionada.getId());

                // Actualizar tabla
                cargarSeries();

                // Mostrar mensaje de éxito
                Alert exito = new Alert(Alert.AlertType.INFORMATION);
                exito.setTitle("Serie eliminada");
                exito.setHeaderText(null);
                exito.setContentText("La serie ha sido eliminada correctamente");
                exito.showAndWait();
            } catch (Exception e) {
                mostrarError("Error", "No se pudo eliminar la serie: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleCreacionRapida() {
        try {
            Parent root = fxmlLoaderUtil.loadFXML("/fxml/serie/serie-creacion-rapida.fxml");

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Creación Rápida de Serie");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Actualizar la tabla después de cerrar el formulario
            cargarSeries();
        } catch (Exception e) {
            mostrarError("Error", "Error al abrir el formulario de creación rápida: " + e.getMessage());
        }
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}