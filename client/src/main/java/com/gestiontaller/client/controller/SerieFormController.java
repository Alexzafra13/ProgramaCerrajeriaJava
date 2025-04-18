package com.gestiontaller.client.controller;

import com.gestiontaller.client.api.SerieApiClient;
import com.gestiontaller.client.model.TipoMaterial;
import com.gestiontaller.client.model.serie.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    @FXML private TableColumn<PerfilSerieDTO, TipoPerfil> colTipoPerfil;
    @FXML private TableColumn<PerfilSerieDTO, Double> colPesoPerfil;
    @FXML private TableColumn<PerfilSerieDTO, Double> colPrecioPerfil;
    @FXML private TableColumn<PerfilSerieDTO, Integer> colLongitudBarra;

    @FXML private TableView<DescuentoPerfilSerieDTO> tablaDescuentos;
    @FXML private TableColumn<DescuentoPerfilSerieDTO, TipoPerfil> colTipoPerfilDesc;
    @FXML private TableColumn<DescuentoPerfilSerieDTO, Integer> colDescuentoMm;
    @FXML private TableColumn<DescuentoPerfilSerieDTO, String> colDescripcionDesc;

    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private final SerieApiClient serieApiClient;
    private SerieAluminioDTO serieActual;
    private boolean esEdicion = false;

    @Autowired
    public SerieFormController(SerieApiClient serieApiClient) {
        this.serieApiClient = serieApiClient;
    }

    @FXML
    public void initialize() {
        // Inicializar ComboBox
        cmbTipoSerie.getItems().addAll(TipoSerie.values());

        // Inicializar columnas de la tabla de perfiles
        colCodigoPerfil.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombrePerfil.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTipoPerfil.setCellValueFactory(new PropertyValueFactory<>("tipoPerfil"));
        colPesoPerfil.setCellValueFactory(new PropertyValueFactory<>("pesoMetro"));
        colPrecioPerfil.setCellValueFactory(new PropertyValueFactory<>("precioMetro"));
        colLongitudBarra.setCellValueFactory(new PropertyValueFactory<>("longitudBarra"));

        // Inicializar columnas de la tabla de descuentos
        colTipoPerfilDesc.setCellValueFactory(new PropertyValueFactory<>("tipoPerfil"));
        colDescuentoMm.setCellValueFactory(new PropertyValueFactory<>("descuentoMilimetros"));
        colDescripcionDesc.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        // Valor por defecto para serie nueva
        serieActual = new SerieAluminioDTO();
        serieActual.setTipoMaterial(TipoMaterial.ALUMINIO);
        serieActual.setActiva(true);
    }

    public void setSerie(SerieAluminioDTO serie) {
        this.serieActual = serie;
        this.esEdicion = true;

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
            tablaPerfiles.getItems().setAll(serie.getPerfiles());
        }

        if (serie.getDescuentosPerfiles() != null) {
            tablaDescuentos.getItems().setAll(serie.getDescuentosPerfiles());
        }
    }

    @FXML
    private void handleGuardar(ActionEvent event) {
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
    private void handleCancelar(ActionEvent event) {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleAgregarPerfil(ActionEvent event) {
        // Implementación para agregar perfil
    }

    @FXML
    private void handleEditarPerfil(ActionEvent event) {
        // Implementación para editar perfil
    }

    @FXML
    private void handleEliminarPerfil(ActionEvent event) {
        // Implementación para eliminar perfil
    }

    @FXML
    private void handleAgregarDescuento(ActionEvent event) {
        // Implementación para agregar descuento
    }

    @FXML
    private void handleEditarDescuento(ActionEvent event) {
        // Implementación para editar descuento
    }

    @FXML
    private void handleEliminarDescuento(ActionEvent event) {
        // Implementación para eliminar descuento
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de validación");
            alert.setHeaderText("Por favor corrija los siguientes errores:");
            alert.setContentText(errores.toString());
            alert.showAndWait();
            return false;
        }

        return true;
    }
}