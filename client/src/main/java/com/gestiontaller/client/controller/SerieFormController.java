package com.gestiontaller.client.controller;

import com.gestiontaller.client.api.SerieApiClient;
import com.gestiontaller.client.model.serie.*;
import com.gestiontaller.client.util.FXMLLoaderUtil;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SerieFormController {

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<TipoSerie> cmbTipoSerie;
    @FXML private TextField txtPrecioBase;
    @FXML private TextField txtDescuento;
    @FXML private TextField txtEspesorMinimo;
    @FXML private TextField txtEspesorMaximo;
    @FXML private TextField txtColor;
    @FXML private CheckBox chkRoturaPuente;
    @FXML private CheckBox chkPermitePersiana;
    @FXML private CheckBox chkActiva;
    @FXML private TextArea txtDescripcion;

    @FXML private TableView<PerfilSerieDTO> tablaPerfiles;
    @FXML private TableColumn<PerfilSerieDTO, String> colCodigoPerfil;
    @FXML private TableColumn<PerfilSerieDTO, String> colNombrePerfil;
    @FXML private TableColumn<PerfilSerieDTO, String> colTipoPerfil;
    @FXML private TableColumn<PerfilSerieDTO, Double> colPesoPerfil;
    @FXML private TableColumn<PerfilSerieDTO, Double> colPrecioPerfil;
    @FXML private TableColumn<PerfilSerieDTO, Integer> colLongitudBarra;

    @FXML private TableView<DescuentoPerfilSerieDTO> tablaDescuentos;
    @FXML private TableColumn<DescuentoPerfilSerieDTO, String> colTipoPerfilDesc;
    @FXML private TableColumn<DescuentoPerfilSerieDTO, Integer> colDescuentoMm;
    @FXML private TableColumn<DescuentoPerfilSerieDTO, String> colDescripcionDesc;

    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private final SerieApiClient serieApiClient;
    private final FXMLLoaderUtil fxmlLoaderUtil;
    private SerieAluminioDTO serieActual;
    private boolean esEdicion = false;

    @Autowired
    public SerieFormController(SerieApiClient serieApiClient, FXMLLoaderUtil fxmlLoaderUtil) {
        this.serieApiClient = serieApiClient;
        this.fxmlLoaderUtil = fxmlLoaderUtil;
    }

    @FXML
    public void initialize() {
        // Inicializar ComboBox
        cmbTipoSerie.setItems(FXCollections.observableArrayList(TipoSerie.values()));

        // Inicializar columnas de la tabla de perfiles
        colCodigoPerfil.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCodigo()));
        colNombrePerfil.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombre()));
        colTipoPerfil.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getTipoPerfil() != null ? data.getValue().getTipoPerfil().toString() : ""));
        colPesoPerfil.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPesoMetro()).asObject());
        colPrecioPerfil.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrecioMetro()).asObject());
        colLongitudBarra.setCellValueFactory(data -> new SimpleIntegerProperty(
                data.getValue().getLongitudBarra() != null ? data.getValue().getLongitudBarra() : 0).asObject());

        // Inicializar columnas de la tabla de descuentos
        colTipoPerfilDesc.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getTipoPerfil() != null ? data.getValue().getTipoPerfil().toString() : ""));
        colDescuentoMm.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getDescuentoMilimetros()).asObject());
        colDescripcionDesc.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescripcion()));

        // Valor por defecto para serie nueva
        serieActual = new SerieAluminioDTO();
        serieActual.setPerfiles(new ArrayList<>());
        serieActual.setDescuentosPerfiles(new ArrayList<>());
        serieActual.setActiva(true);
    }

    public void setSerie(SerieAluminioDTO serie) {
        this.serieActual = serie;
        this.esEdicion = serie.getId() != null;

        // Cargar datos de la serie en el formulario
        txtCodigo.setText(serie.getCodigo());
        txtNombre.setText(serie.getNombre());
        cmbTipoSerie.setValue(serie.getTipoSerie());
        txtPrecioBase.setText(String.valueOf(serie.getPrecioMetroBase()));
        txtDescuento.setText(String.valueOf(serie.getDescuentoSerie()));
        txtEspesorMinimo.setText(String.valueOf(serie.getEspesorMinimo()));
        txtEspesorMaximo.setText(String.valueOf(serie.getEspesorMaximo()));
        txtColor.setText(serie.getColor());
        chkRoturaPuente.setSelected(serie.isRoturaPuente());
        chkPermitePersiana.setSelected(serie.isPermitePersiana());
        chkActiva.setSelected(serie.isActiva());
        txtDescripcion.setText(serie.getDescripcion());

        // Cargar perfiles y descuentos
        if (serie.getPerfiles() != null) {
            tablaPerfiles.setItems(FXCollections.observableArrayList(serie.getPerfiles()));
        } else {
            tablaPerfiles.setItems(FXCollections.observableArrayList());
        }

        if (serie.getDescuentosPerfiles() != null) {
            tablaDescuentos.setItems(FXCollections.observableArrayList(serie.getDescuentosPerfiles()));
        } else {
            tablaDescuentos.setItems(FXCollections.observableArrayList());
        }
    }

    @FXML
    private void handleGuardar() {
        // Validar formulario
        if (!validarFormulario()) {
            return;
        }

        // Actualizar datos de la serie
        serieActual.setCodigo(txtCodigo.getText().trim());
        serieActual.setNombre(txtNombre.getText().trim());
        serieActual.setTipoSerie(cmbTipoSerie.getValue());
        serieActual.setPrecioMetroBase(Double.parseDouble(txtPrecioBase.getText().trim()));
        serieActual.setDescuentoSerie(Double.parseDouble(txtDescuento.getText().trim()));
        serieActual.setEspesorMinimo(Double.parseDouble(txtEspesorMinimo.getText().trim()));
        serieActual.setEspesorMaximo(Double.parseDouble(txtEspesorMaximo.getText().trim()));
        serieActual.setColor(txtColor.getText().trim());
        serieActual.setRoturaPuente(chkRoturaPuente.isSelected());
        serieActual.setPermitePersiana(chkPermitePersiana.isSelected());
        serieActual.setActiva(chkActiva.isSelected());
        serieActual.setDescripcion(txtDescripcion.getText().trim());

        // Guardar serie
        try {
            SerieAluminioDTO savedSerie = serieApiClient.guardarSerieAluminio(serieActual);
            serieActual = savedSerie;

            // Mostrar mensaje de éxito
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Serie guardada");
            alert.setHeaderText(null);
            alert.setContentText("La serie ha sido guardada correctamente.");
            alert.showAndWait();

            // Cerrar ventana
            Stage stage = (Stage) btnGuardar.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error al guardar la serie");
            alert.setContentText("Se ha producido un error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancelar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleAgregarPerfil() {
        try {
            // Primero guarda la serie si es nueva
            if (!esEdicion && serieActual.getId() == null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Guardar Serie");
                confirm.setHeaderText("Para agregar perfiles, primero debe guardar la serie");
                confirm.setContentText("¿Desea guardar la serie ahora?");

                if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                    if (!validarFormulario()) {
                        return;
                    }
                    handleGuardar();
                } else {
                    return;
                }
            }

            // Cargar formulario de perfil
            Parent root = fxmlLoaderUtil.loadFXML("/fxml/serie/perfil-form.fxml");

            // Configurar para un nuevo perfil
            PerfilFormController controller = (PerfilFormController) root.getUserData();

            PerfilSerieDTO nuevoPerfil = new PerfilSerieDTO();
            nuevoPerfil.setSerieId(serieActual.getId());
            controller.setPerfil(nuevoPerfil, serieActual.getNombre());

            // Mostrar formulario
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Nuevo Perfil");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Actualizar tabla
            List<PerfilSerieDTO> perfilesActualizados = serieApiClient.obtenerPerfilesPorSerieId(serieActual.getId());
            serieActual.setPerfiles(perfilesActualizados);
            tablaPerfiles.setItems(FXCollections.observableArrayList(perfilesActualizados));

        } catch (Exception e) {
            mostrarError("Error", "Error al abrir formulario de perfil: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditarPerfil() {
        PerfilSerieDTO seleccionado = tablaPerfiles.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Error", "Debe seleccionar un perfil para editar");
            return;
        }

        try {
            // Cargar formulario de perfil
            Parent root = fxmlLoaderUtil.loadFXML("/fxml/serie/perfil-form.fxml");

            // Configurar con el perfil seleccionado
            PerfilFormController controller = (PerfilFormController) root.getUserData();
            controller.setPerfil(seleccionado, serieActual.getNombre());

            // Mostrar formulario
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Editar Perfil");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Actualizar tabla
            List<PerfilSerieDTO> perfilesActualizados = serieApiClient.obtenerPerfilesPorSerieId(serieActual.getId());
            serieActual.setPerfiles(perfilesActualizados);
            tablaPerfiles.setItems(FXCollections.observableArrayList(perfilesActualizados));

        } catch (Exception e) {
            mostrarError("Error", "Error al abrir formulario de perfil: " + e.getMessage());
        }
    }

    @FXML
    private void handleEliminarPerfil() {
        PerfilSerieDTO seleccionado = tablaPerfiles.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Error", "Debe seleccionar un perfil para eliminar");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Está seguro que desea eliminar el perfil?");
        confirm.setContentText("Perfil: " + seleccionado.getNombre());

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                // Eliminar perfil
                serieApiClient.eliminarPerfilSerie(seleccionado.getId());

                // Actualizar tabla
                List<PerfilSerieDTO> perfilesActualizados = serieApiClient.obtenerPerfilesPorSerieId(serieActual.getId());
                serieActual.setPerfiles(perfilesActualizados);
                tablaPerfiles.setItems(FXCollections.observableArrayList(perfilesActualizados));

                // Mensaje de éxito
                Alert exito = new Alert(Alert.AlertType.INFORMATION);
                exito.setTitle("Perfil eliminado");
                exito.setHeaderText(null);
                exito.setContentText("El perfil ha sido eliminado correctamente");
                exito.showAndWait();

            } catch (Exception e) {
                mostrarError("Error", "No se pudo eliminar el perfil: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleAgregarDescuento() {
        // Implementación similar a handleAgregarPerfil pero para descuentos
    }

    @FXML
    private void handleEditarDescuento() {
        // Implementación similar a handleEditarPerfil pero para descuentos
    }

    @FXML
    private void handleEliminarDescuento() {
        // Implementación similar a handleEliminarPerfil pero para descuentos
    }

    private boolean validarFormulario() {
        StringBuilder errores = new StringBuilder();

        if (txtCodigo.getText().trim().isEmpty()) {
            errores.append("- El código es obligatorio\n");
        }

        if (txtNombre.getText().trim().isEmpty()) {
            errores.append("- El nombre es obligatorio\n");
        }

        if (cmbTipoSerie.getValue() == null) {
            errores.append("- El tipo de serie es obligatorio\n");
        }

        try {
            double precioBase = Double.parseDouble(txtPrecioBase.getText().trim());
            if (precioBase < 0) {
                errores.append("- El precio base debe ser un número positivo\n");
            }
        } catch (NumberFormatException e) {
            errores.append("- El precio base debe ser un número válido\n");
        }

        try {
            double descuento = Double.parseDouble(txtDescuento.getText().trim());
            if (descuento < 0 || descuento > 100) {
                errores.append("- El descuento debe estar entre 0 y 100\n");
            }
        } catch (NumberFormatException e) {
            errores.append("- El descuento debe ser un número válido\n");
        }

        try {
            double espesorMinimo = Double.parseDouble(txtEspesorMinimo.getText().trim());
            if (espesorMinimo <= 0) {
                errores.append("- El espesor mínimo debe ser mayor que 0\n");
            }
        } catch (NumberFormatException e) {
            errores.append("- El espesor mínimo debe ser un número válido\n");
        }

        try {
            double espesorMaximo = Double.parseDouble(txtEspesorMaximo.getText().trim());
            if (espesorMaximo <= 0) {
                errores.append("- El espesor máximo debe ser mayor que 0\n");
            }
        } catch (NumberFormatException e) {
            errores.append("- El espesor máximo debe ser un número válido\n");
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
}