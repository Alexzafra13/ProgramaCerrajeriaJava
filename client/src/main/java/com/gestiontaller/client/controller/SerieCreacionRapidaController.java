package com.gestiontaller.client.controller;

import com.gestiontaller.client.api.SerieApiClient;
import com.gestiontaller.client.model.serie.SerieAluminioDTO;
import com.gestiontaller.client.model.serie.TipoSerie;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SerieCreacionRapidaController {

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<TipoSerie> cmbTipoSerie;
    @FXML private TextField txtPrecioBase;
    @FXML private CheckBox chkRoturaPuente;
    @FXML private CheckBox chkPermitePersiana;
    @FXML private TextArea txtDescripcion;
    @FXML private Button btnCrear;
    @FXML private Button btnCancelar;
    @FXML private Label lblEstado;

    private final SerieApiClient serieApiClient;

    @Autowired
    public SerieCreacionRapidaController(SerieApiClient serieApiClient) {
        this.serieApiClient = serieApiClient;
    }

    @FXML
    public void initialize() {
        // Inicializar combo box
        cmbTipoSerie.setItems(FXCollections.observableArrayList(TipoSerie.values()));
        cmbTipoSerie.setValue(TipoSerie.CORREDERA);

        // Esconder label de estado inicialmente
        lblEstado.setVisible(false);
    }

    @FXML
    private void handleCrear() {
        // Validar campos
        if (!validarCampos()) {
            return;
        }

        // Obtener valores
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String tipoSerie = cmbTipoSerie.getValue().toString();
        boolean roturaPuente = chkRoturaPuente.isSelected();
        boolean permitePersiana = chkPermitePersiana.isSelected();

        try {
            // Mostrar mensaje de procesamiento
            lblEstado.setText("Creando serie...");
            lblEstado.setVisible(true);

            // Crear la serie
            SerieAluminioDTO serie = serieApiClient.crearSerieCompleta(
                    codigo, nombre, descripcion, tipoSerie, roturaPuente, permitePersiana);

            // Mostrar mensaje de éxito
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Serie creada");
            alert.setHeaderText("Serie creada correctamente");
            alert.setContentText("Se ha creado la serie " + serie.getCodigo() + " con todos sus componentes.");
            alert.showAndWait();

            // Cerrar ventana
            Stage stage = (Stage) btnCrear.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            // Mostrar error
            lblEstado.setVisible(false);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error al crear la serie");
            alert.setContentText("Se ha producido un error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancelar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    private boolean validarCampos() {
        StringBuilder errores = new StringBuilder();

        if (txtCodigo.getText().trim().isEmpty()) {
            errores.append("- El código es obligatorio\n");
        }

        if (txtNombre.getText().trim().isEmpty()) {
            errores.append("- El nombre es obligatorio\n");
        }

        if (cmbTipoSerie.getValue() == null) {
            errores.append("- Debe seleccionar un tipo de serie\n");
        }

        try {
            double precioBase = Double.parseDouble(txtPrecioBase.getText().trim());
            if (precioBase <= 0) {
                errores.append("- El precio base debe ser mayor que 0\n");
            }
        } catch (NumberFormatException e) {
            errores.append("- El precio base debe ser un número válido\n");
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