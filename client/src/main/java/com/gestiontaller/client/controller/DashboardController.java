package com.gestiontaller.client.controller;

import com.gestiontaller.client.util.FXMLLoaderUtil;
import com.gestiontaller.client.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DashboardController {

    @FXML private Button btnHome;
    @FXML private Button btnClientes;
    @FXML private Button btnPresupuestos;
    @FXML private Button btnTrabajos;
    @FXML private Button btnSeries;
    @FXML private Button btnProductos;
    @FXML private Button btnInventario;
    @FXML private Button btnCalculadora;
    @FXML private Button btnConfiguracion;
    @FXML private Button btnLogout;
    @FXML private Label lblUsuarioActual;
    @FXML private Label lblTitulo;
    @FXML private StackPane contentArea;

    private final FXMLLoaderUtil fxmlLoaderUtil;

    @Autowired
    public DashboardController(FXMLLoaderUtil fxmlLoaderUtil) {
        this.fxmlLoaderUtil = fxmlLoaderUtil;
    }

    @FXML
    public void initialize() {
        // Mostrar información del usuario actual
        String username = SessionManager.getInstance().getUsername();
        String rol = SessionManager.getInstance().getRol() != null
                ? SessionManager.getInstance().getRol().toString()
                : "N/A";

        lblUsuarioActual.setText("Usuario: " + username + " (" + rol + ")");

        // Cargar la vista de inicio por defecto
        cargarVistaInicio();
    }

    private void cargarVistaInicio() {
        try {
            Parent root = fxmlLoaderUtil.loadFXML("/fxml/dashboard/inicio.fxml");

            // Establecer título y contenido
            lblTitulo.setText("Panel de Control");
            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
        } catch (Exception e) {
            // Si falla, mostrar un mensaje temporal
            Label label = new Label("Bienvenido al sistema de gestión de taller");
            label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
            contentArea.getChildren().clear();
            contentArea.getChildren().add(label);
            e.printStackTrace(); // Para depuración
        }
    }

    @FXML
    private void handleNavigateHome(ActionEvent event) {
        cargarVistaInicio();
    }

    @FXML
    private void handleNavigateClientes(ActionEvent event) {
        cargarVista("/fxml/cliente/cliente-list.fxml", "Gestión de Clientes");
    }

    @FXML
    private void handleNavigatePresupuestos(ActionEvent event) {
        cargarVista("/fxml/presupuesto/presupuesto-list.fxml", "Gestión de Presupuestos");
    }

    @FXML
    private void handleNavigateTrabajos(ActionEvent event) {
        cargarVista("/fxml/trabajo/trabajo-list.fxml", "Gestión de Trabajos");
    }

    @FXML
    private void handleNavigateSeries(ActionEvent event) {
        cargarVista("/fxml/serie/serie-list.fxml", "Series de Aluminio");
    }

    @FXML
    private void handleNavigateProductos(ActionEvent event) {
        cargarVista("/fxml/producto/producto-list.fxml", "Gestión de Productos");
    }

    @FXML
    private void handleNavigateInventario(ActionEvent event) {
        cargarVista("/fxml/inventario/inventario-list.fxml", "Gestión de Inventario");
    }

    @FXML
    private void handleNavigateCalculadora(ActionEvent event) {
        cargarVista("/fxml/calculadora/calculadora-ventana.fxml", "Calculadora de Ventanas");
    }

    @FXML
    private void handleNavigateConfiguracion(ActionEvent event) {
        cargarVista("/fxml/configuracion/configuracion.fxml", "Configuración");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        // Limpiar la sesión
        SessionManager.getInstance().clearSession();

        try {
            // Volver a la pantalla de login
            Parent root = fxmlLoaderUtil.loadFXML("/fxml/login.fxml");

            // Configurar la escena
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());

            // Mostrar en la misma ventana
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setTitle("Gestión de Taller - Login");
            stage.setScene(scene);
            stage.setMaximized(false);
            stage.setWidth(600);
            stage.setHeight(400);
            stage.centerOnScreen();
        } catch (Exception e) {
            System.err.println("Error al volver al login: " + e.getMessage());
            e.printStackTrace(); // Para depuración
        }
    }

    private void cargarVista(String rutaFxml, String titulo) {
        try {
            Parent root = fxmlLoaderUtil.loadFXML(rutaFxml);

            lblTitulo.setText(titulo);
            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
        } catch (Exception e) {
            System.err.println("Error al cargar vista " + rutaFxml + ": " + e.getMessage());

            // Mostrar mensaje de error
            Label label = new Label("Error al cargar la vista: " + e.getMessage());
            label.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            contentArea.getChildren().clear();
            contentArea.getChildren().add(label);
            e.printStackTrace(); // Para depuración
        }
    }
}