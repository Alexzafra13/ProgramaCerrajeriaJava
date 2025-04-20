package com.gestiontaller.client.controller;

import com.gestiontaller.client.api.SerieApiClient;
import com.gestiontaller.client.model.serie.PerfilSerieDTO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PerfilFormController {

    @FXML private TextField txtSerie;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<TipoPerfil> cmbTipoPerfil;
    @FXML private TextField txtPeso;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtLongitud;

    private final SerieApiClient serieApiClient;
    private PerfilSerieDTO perfilActual;
    private boolean esEdicion = false;

    @Autowired
    public PerfilFormController(SerieApiClient serieApiClient) {
        this.serieApiClient = serieApiClient;
    }

    @FXML
    public void initialize() {
        // Inicializar ComboBox
        cmbTipoPerfil.setItems(FXCollections.observableArrayList(TipoPerfil.values()));

        // Valores por defecto
        txtLongitud.setText("6000");
    }

    public void setPerfil(PerfilSerieDTO perfil, String nombreSerie) {
        this.perfilActual = perfil;
        this.esEdicion = perfil.getId() != null;

        // Mostrar nombre de la serie (solo lectura)
        txtSerie.setText(nombreSerie);

        // Cargar datos si es edición
        if (esEdicion) {
            txtCodigo.setText(perfil.getCodigo());
            txtNombre.setText(perfil.getNombre());
            cmbTipoPerfil.setValue(perfil.getTipoPerfil());
            txtPeso.setText(String.valueOf(perfil.getPesoMetro()));
            txtPrecio.setText(String.valueOf(perfil.getPrecioMetro()));

            if (perfil.getLongitudBarra() != null) {
                txtLongitud.setText(String.valueOf(perfil.getLongitudBarra()));
            }
        }
    }

    @FXML
    private void handleGuardar() {
        // Validar formulario
        if (!validarFormulario()) {
            return;
        }

        // Actualizar datos del perfil
        perfilActual.setCodigo(txtCodigo.getText().trim());
        perfilActual.setNombre(txtNombre.getText().trim());
        perfilActual.setTipoPerfil(cmbTipoPerfil.getValue());
        perfilActual.setPesoMetro(Double.parseDouble(txtPeso.getText().trim()));
        perfilActual.setPrecioMetro(Double.parseDouble(txtPrecio.getText().trim()));
        perfilActual.setLongitudBarra(Integer.parseInt(txtLongitud.getText().trim()));

        try {
            // Guardar en la base de datos
            PerfilSerieDTO savedPerfil = serieApiClient.guardarPerfilSerie(perfilActual);

            // Mostrar mensaje de éxito
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Perfil guardado");
            alert.setHeaderText(null);
            alert.setContentText("El perfil ha sido guardado correctamente");
            alert.showAndWait();

            // Cerrar ventana
            Stage stage = (Stage) txtCodigo.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error al guardar el perfil");
            alert.setContentText("Se ha producido un error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancelar() {
        Stage stage = (Stage) txtCodigo.getScene().getWindow();
        stage.close();
    }

    private boolean validarFormulario() {
        StringBuilder errores = new StringBuilder();

        if (txtCodigo.getText().trim().isEmpty()) {
            errores.append("- El código es obligatorio\n");
        }

        if (txtNombre.getText().trim().isEmpty()) {
            errores.append("- El nombre es obligatorio\n");
        }

        if (cmbTipoPerfil.getValue() == null) {
            errores.append("- El tipo de perfil es obligatorio\n");
        }

        try {
            double peso = Double.parseDouble(txtPeso.getText().trim());
            if (peso <= 0) {
                errores.append("- El peso debe ser mayor que 0\n");
            }
        } catch (NumberFormatException e) {
            errores.append("- El peso debe ser un número válido\n");
        }

        try {
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            if (precio < 0) {
                errores.append("- El precio no puede ser negativo\n");
            }
        } catch (NumberFormatException e) {
            errores.append("- El precio debe ser un número válido\n");
        }

        try {
            int longitud = Integer.parseInt(txtLongitud.getText().trim());
            if (longitud <= 0) {
                errores.append("- La longitud debe ser mayor que 0\n");
            }
        } catch (NumberFormatException e) {
            errores.append("- La longitud debe ser un número entero válido\n");
        }

        if (errores.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Por favor corrija los siguientes errores:");
            alert.setContentText(errores.toString());
            alert.showAndWait();
            return false;
        }

        return true;
    }
}