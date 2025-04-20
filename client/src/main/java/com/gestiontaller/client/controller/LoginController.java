package com.gestiontaller.client.controller;

import com.gestiontaller.client.api.AuthApiClient;
import com.gestiontaller.common.model.auth.LoginResponse;
import com.gestiontaller.client.util.FXMLLoaderUtil;
import com.gestiontaller.client.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Component
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnLogin;

    @FXML
    private Button btnCancel;

    @FXML
    private Label lblError;

    private final AuthApiClient authApiClient;
    private final FXMLLoaderUtil fxmlLoaderUtil;

    @Autowired
    public LoginController(AuthApiClient authApiClient, FXMLLoaderUtil fxmlLoaderUtil) {
        this.authApiClient = authApiClient;
        this.fxmlLoaderUtil = fxmlLoaderUtil;
    }

    @FXML
    public void initialize() {
        lblError.setVisible(false);
        txtUsername.setText("admin");
        txtPassword.setText("admin");
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor complete todos los campos");
            return;
        }

        try {
            logger.debug("Intentando login con usuario: {}", username);
            LoginResponse response = authApiClient.login(username, password);

            // Debuguear la respuesta
            logger.debug("Respuesta de autenticación recibida: ID: {}, Username: {}, Rol: {}",
                    response.getId(), response.getUsername(), response.getRol());

            SessionManager.getInstance().setLoginInfo(response);

            // Verificar que se estableció correctamente
            logger.debug("Token almacenado en SessionManager: {}", SessionManager.getInstance().getToken());

            abrirDashboard();
        } catch (HttpClientErrorException e) {
            logger.error("Error de autenticación: {}", e.getMessage());
            mostrarError("Error de autenticación: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al iniciar sesión", e);
            mostrarError("Error al iniciar sesión: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    private void abrirDashboard() {
        try {
            Parent root = fxmlLoaderUtil.loadFXML("/fxml/dashboard.fxml");

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());

            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setTitle("Gestión de Taller - Dashboard");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

            logger.info("Dashboard abierto correctamente para usuario: {}",
                    SessionManager.getInstance().getUsername());
        } catch (Exception e) {
            logger.error("Error al cargar el dashboard", e);
            mostrarError("Error al cargar el dashboard: " + e.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        logger.warn("Error en login: {}", mensaje);
        lblError.setText(mensaje);
        lblError.setVisible(true);
    }
}