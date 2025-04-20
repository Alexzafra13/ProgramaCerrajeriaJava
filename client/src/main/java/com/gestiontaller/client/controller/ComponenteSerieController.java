package com.gestiontaller.client.controller;

import com.gestiontaller.client.api.ComponenteSerieApiClient;
import com.gestiontaller.client.api.SerieApiClient;
import com.gestiontaller.client.util.FXMLLoaderUtil;
import com.gestiontaller.common.dto.configuracion.MaterialConfiguracionDTO;
import com.gestiontaller.common.dto.configuracion.PlantillaConfiguracionSerieDTO;
import com.gestiontaller.common.dto.serie.MaterialBaseSerieDTO;
import com.gestiontaller.common.dto.serie.SerieAluminioDTO;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ComponenteSerieController {

    // Controles para filtrado
    @FXML private ComboBox<SerieAluminioDTO> cmbSerie;
    @FXML private ComboBox<String> cmbTipoComponente;
    @FXML private Button btnFiltrar;

    // Tabla de componentes de serie
    @FXML private TableView<MaterialBaseSerieDTO> tablaComponentes;
    @FXML private TableColumn<MaterialBaseSerieDTO, String> colCodigo;
    @FXML private TableColumn<MaterialBaseSerieDTO, String> colNombre;
    @FXML private TableColumn<MaterialBaseSerieDTO, String> colTipoMaterial;
    @FXML private TableColumn<MaterialBaseSerieDTO, String> colDescripcion;
    @FXML private TableColumn<MaterialBaseSerieDTO, Boolean> colPredeterminado;
    @FXML private Button btnAgregarComponente;
    @FXML private Button btnEditarComponente;
    @FXML private Button btnEliminarComponente;

    // Controles para configuraciones
    @FXML private ComboBox<Integer> cmbNumHojas;
    @FXML private Button btnCargarConfiguracion;
    @FXML private TableView<MaterialConfiguracionDTO> tablaMaterialesConfiguracion;
    @FXML private TableColumn<MaterialConfiguracionDTO, String> colCodigoConfig;
    @FXML private TableColumn<MaterialConfiguracionDTO, String> colDescripcionConfig;
    @FXML private TableColumn<MaterialConfiguracionDTO, Integer> colCantidadBase;
    @FXML private TableColumn<MaterialConfiguracionDTO, String> colFormula;
    @FXML private Button btnAgregarMaterial;
    @FXML private Button btnEditarMaterial;
    @FXML private Button btnEliminarMaterial;

    // Controles para generación automática
    @FXML private ComboBox<String> cmbTipoSerieGenerar;
    @FXML private CheckBox chkHerrajesBasicos;
    @FXML private CheckBox chkTornilleria;
    @FXML private CheckBox chkAccesorios;
    @FXML private Button btnGenerarComponentes;

    // Clientes API
    private final SerieApiClient serieApiClient;
    private final ComponenteSerieApiClient componenteSerieApiClient;
    private final FXMLLoaderUtil fxmlLoaderUtil;

    // Estado
    private PlantillaConfiguracionSerieDTO configuracionActual;

    @Autowired
    public ComponenteSerieController(SerieApiClient serieApiClient,
                                     ComponenteSerieApiClient componenteSerieApiClient,
                                     FXMLLoaderUtil fxmlLoaderUtil) {
        this.serieApiClient = serieApiClient;
        this.componenteSerieApiClient = componenteSerieApiClient;
        this.fxmlLoaderUtil = fxmlLoaderUtil;
    }

    @FXML
    public void initialize() {
        // Inicializar tipos de componentes
        cmbTipoComponente.setItems(FXCollections.observableArrayList(
                "Todos", "HERRAJE_BASICO", "TORNILLERIA", "ACCESORIO", "SELLANTE"));
        cmbTipoComponente.setValue("Todos");

        // Inicializar tipos de serie para generar
        cmbTipoSerieGenerar.setItems(FXCollections.observableArrayList("CORREDERA", "ABATIBLE", "OSCILOBATIENTE"));
        cmbTipoSerieGenerar.setValue("CORREDERA");

        // Inicializar números de hojas
        cmbNumHojas.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 6));
        cmbNumHojas.setValue(2);

        // Configurar columnas de la tabla de componentes
        configurarColumnasTablaComponentes();

        // Configurar columnas de la tabla de materiales de configuración
        configurarColumnasTablaMateriales();

        // Cargar series
        cargarSeries();

        // Configurar eventos de selección
        configurarEventosSeleccion();

        // Desactivar botones de edición hasta que haya selección
        btnEditarComponente.setDisable(true);
        btnEliminarComponente.setDisable(true);
        btnEditarMaterial.setDisable(true);
        btnEliminarMaterial.setDisable(true);
    }

    private void configurarColumnasTablaComponentes() {
        colCodigo.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getCodigoProducto()));
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getNombreProducto()));
        colTipoMaterial.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getTipoMaterial()));
        colDescripcion.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getDescripcion()));
        colPredeterminado.setCellValueFactory(cellData -> new SimpleBooleanProperty(
                cellData.getValue().getEsPredeterminado() != null && cellData.getValue().getEsPredeterminado()));

        // Formatear columna de predeterminado para mostrar checkboxes
        colPredeterminado.setCellFactory(col -> new TableCell<MaterialBaseSerieDTO, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    CheckBox checkBox = new CheckBox();
                    checkBox.setSelected(item);
                    checkBox.setDisable(true);
                    setGraphic(checkBox);
                }
            }
        });
    }

    private void configurarColumnasTablaMateriales() {
        colCodigoConfig.setCellValueFactory(cellData -> {
            if (cellData.getValue().getProductoId() != null) {
                // Buscar el código en los componentes cargados
                Optional<MaterialBaseSerieDTO> componente = tablaComponentes.getItems().stream()
                        .filter(c -> c.getProductoId().equals(cellData.getValue().getProductoId()))
                        .findFirst();
                return new SimpleStringProperty(componente.map(MaterialBaseSerieDTO::getCodigoProducto)
                        .orElse("No encontrado"));
            }
            return new SimpleStringProperty("Sin producto");
        });

        colDescripcionConfig.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getDescripcion()));
        colCantidadBase.setCellValueFactory(cellData -> new SimpleIntegerProperty(
                cellData.getValue().getCantidadBase()).asObject());
        colFormula.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFormulaCantidad()));
    }

    private void cargarSeries() {
        try {
            List<SerieAluminioDTO> series = serieApiClient.obtenerSeriesAluminio();

            // Configurar el combo para mostrar código y nombre
            cmbSerie.setCellFactory(lv -> new ListCell<SerieAluminioDTO>() {
                @Override
                protected void updateItem(SerieAluminioDTO item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getCodigo() + " - " + item.getNombre());
                    }
                }
            });

            // Configurar cómo se muestra el valor seleccionado
            cmbSerie.setButtonCell(new ListCell<SerieAluminioDTO>() {
                @Override
                protected void updateItem(SerieAluminioDTO item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getCodigo() + " - " + item.getNombre());
                    }
                }
            });

            cmbSerie.setItems(FXCollections.observableArrayList(series));

            // Seleccionar primera serie
            if (!series.isEmpty()) {
                cmbSerie.setValue(series.get(0));
                // Cargar componentes para la serie seleccionada
                cargarComponentesPorSerie();
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al cargar series",
                    "No se pudieron cargar las series de aluminio: " + e.getMessage());
        }
    }

    private void configurarEventosSeleccion() {
        // Listener para selección en tabla de componentes
        tablaComponentes.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    btnEditarComponente.setDisable(newVal == null);
                    btnEliminarComponente.setDisable(newVal == null);
                }
        );

        // Listener para selección en tabla de materiales de configuración
        tablaMaterialesConfiguracion.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    btnEditarMaterial.setDisable(newVal == null);
                    btnEliminarMaterial.setDisable(newVal == null);
                }
        );

        // Listener para cambio de serie
        cmbSerie.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                cargarComponentesPorSerie();
            }
        });
    }

    @FXML
    private void handleFiltrar() {
        cargarComponentesPorSerie();
    }

    private void cargarComponentesPorSerie() {
        SerieAluminioDTO serie = cmbSerie.getValue();
        if (serie == null) {
            return;
        }

        try {
            List<MaterialBaseSerieDTO> componentes;
            String tipoSeleccionado = cmbTipoComponente.getValue();

            if (tipoSeleccionado.equals("Todos")) {
                componentes = componenteSerieApiClient.obtenerComponentesPorSerie(serie.getId());
            } else {
                componentes = componenteSerieApiClient.obtenerComponentesPorTipo(serie.getId(), tipoSeleccionado);
            }

            tablaComponentes.setItems(FXCollections.observableArrayList(componentes));
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al cargar componentes",
                    "No se pudieron cargar los componentes: " + e.getMessage());
        }
    }

    @FXML
    private void handleAgregarComponente() {
        SerieAluminioDTO serie = cmbSerie.getValue();
        if (serie == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Serie no seleccionada",
                    "Debe seleccionar una serie primero.");
            return;
        }

        try {
            Parent root = fxmlLoaderUtil.loadFXML("/fxml/serie/componente-form.fxml");
            ComponenteFormController controller = (ComponenteFormController) root.getUserData();

            // Inicializar con nueva instancia y serie seleccionada
            MaterialBaseSerieDTO nuevoComponente = new MaterialBaseSerieDTO();
            nuevoComponente.setSerieId(serie.getId());
            nuevoComponente.setEsPredeterminado(true);
            controller.setComponente(nuevoComponente, serie.getCodigo() + " - " + serie.getNombre());

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Nuevo Componente para Serie");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Recargar componentes
            cargarComponentesPorSerie();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al abrir formulario",
                    "No se pudo abrir el formulario: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditarComponente() {
        MaterialBaseSerieDTO componente = tablaComponentes.getSelectionModel().getSelectedItem();
        if (componente == null) {
            return;
        }

        try {
            Parent root = fxmlLoaderUtil.loadFXML("/fxml/serie/componente-form.fxml");
            ComponenteFormController controller = (ComponenteFormController) root.getUserData();

            // Inicializar con componente seleccionado
            SerieAluminioDTO serie = cmbSerie.getValue();
            controller.setComponente(componente, serie.getCodigo() + " - " + serie.getNombre());

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Editar Componente");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Recargar componentes
            cargarComponentesPorSerie();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al abrir formulario",
                    "No se pudo abrir el formulario: " + e.getMessage());
        }
    }

    @FXML
    private void handleEliminarComponente() {
        MaterialBaseSerieDTO componente = tablaComponentes.getSelectionModel().getSelectedItem();
        if (componente == null) {
            return;
        }

        // Confirmar eliminación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar este componente?");
        confirmacion.setContentText("Componente: " + componente.getNombreProducto());

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                componenteSerieApiClient.eliminarComponente(componente.getId());

                // Recargar componentes
                cargarComponentesPorSerie();

                // Mostrar mensaje de éxito
                mostrarAlerta(Alert.AlertType.INFORMATION, "Información", "Componente eliminado",
                        "El componente ha sido eliminado correctamente.");
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al eliminar",
                        "No se pudo eliminar el componente: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleCargarConfiguracion() {
        SerieAluminioDTO serie = cmbSerie.getValue();
        Integer numHojas = cmbNumHojas.getValue();

        if (serie == null || numHojas == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Datos incompletos",
                    "Debe seleccionar una serie y número de hojas.");
            return;
        }

        try {
            // Obtener la configuración
            configuracionActual = componenteSerieApiClient.obtenerConfiguracionPorSerieYHojas(
                    serie.getId(), numHojas);

            if (configuracionActual == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Configuración no encontrada",
                        "No existe configuración para " + serie.getCodigo() + " con " + numHojas + " hojas.");
                tablaMaterialesConfiguracion.getItems().clear();
                return;
            }

            // Cargar materiales en la tabla
            tablaMaterialesConfiguracion.setItems(FXCollections.observableArrayList(
                    configuracionActual.getMateriales()));
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al cargar configuración",
                    "No se pudo cargar la configuración: " + e.getMessage());
        }
    }

    @FXML
    private void handleAgregarMaterial() {
        if (configuracionActual == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Sin configuración",
                    "Primero debe cargar una configuración.");
            return;
        }

        try {
            Parent root = fxmlLoaderUtil.loadFXML("/fxml/serie/material-configuracion-form.fxml");
            MaterialConfiguracionFormController controller = (MaterialConfiguracionFormController) root.getUserData();

            // Inicializar con nueva instancia y configuración actual
            MaterialConfiguracionDTO nuevoMaterial = new MaterialConfiguracionDTO();
            nuevoMaterial.setConfiguracionId(configuracionActual.getId());
            nuevoMaterial.setCantidadBase(1); // Valor por defecto

            controller.setMaterial(nuevoMaterial,
                    configuracionActual.getNombreSerie() + " - " + configuracionActual.getNumHojas() + " hojas");

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Nuevo Material para Configuración");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Recargar la configuración
            handleCargarConfiguracion();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al abrir formulario",
                    "No se pudo abrir el formulario: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditarMaterial() {
        MaterialConfiguracionDTO material = tablaMaterialesConfiguracion.getSelectionModel().getSelectedItem();
        if (material == null || configuracionActual == null) {
            return;
        }

        try {
            Parent root = fxmlLoaderUtil.loadFXML("/fxml/serie/material-configuracion-form.fxml");
            MaterialConfiguracionFormController controller = (MaterialConfiguracionFormController) root.getUserData();

            // Inicializar con material seleccionado
            controller.setMaterial(material,
                    configuracionActual.getNombreSerie() + " - " + configuracionActual.getNumHojas() + " hojas");

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Editar Material de Configuración");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Recargar la configuración
            handleCargarConfiguracion();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al abrir formulario",
                    "No se pudo abrir el formulario: " + e.getMessage());
        }
    }

    @FXML
    private void handleEliminarMaterial() {
        MaterialConfiguracionDTO material = tablaMaterialesConfiguracion.getSelectionModel().getSelectedItem();
        if (material == null) {
            return;
        }

        // Confirmar eliminación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar este material?");
        confirmacion.setContentText("Material: " + material.getDescripcion());

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                componenteSerieApiClient.eliminarMaterialConfiguracion(material.getId());

                // Recargar la configuración
                handleCargarConfiguracion();

                // Mostrar mensaje de éxito
                mostrarAlerta(Alert.AlertType.INFORMATION, "Información", "Material eliminado",
                        "El material ha sido eliminado correctamente.");
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al eliminar",
                        "No se pudo eliminar el material: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleGenerarComponentes() {
        SerieAluminioDTO serie = cmbSerie.getValue();
        if (serie == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Serie no seleccionada",
                    "Debe seleccionar una serie primero.");
            return;
        }

        String tipoSerie = cmbTipoSerieGenerar.getValue();
        if (tipoSerie == null || tipoSerie.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Tipo de serie no seleccionado",
                    "Debe seleccionar un tipo de serie.");
            return;
        }

        // Confirmar generación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Generación");
        confirmacion.setHeaderText("Generar componentes para " + serie.getCodigo());
        confirmacion.setContentText("Se generarán componentes estándar para una serie de tipo " + tipoSerie + ". Esto puede sobrescribir configuraciones existentes. ¿Desea continuar?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                componenteSerieApiClient.generarConfiguracionEstandar(serie.getId(), tipoSerie);

                // Recargar componentes
                cargarComponentesPorSerie();

                // Mostrar mensaje de éxito
                mostrarAlerta(Alert.AlertType.INFORMATION, "Información", "Componentes generados",
                        "Los componentes estándar han sido generados correctamente para la serie " + serie.getCodigo() + ".");
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error en generación",
                        "No se pudieron generar los componentes: " + e.getMessage());
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