package com.gestiontaller.client.controller;

import com.gestiontaller.client.api.AuthApiClient;
import com.gestiontaller.client.model.auth.LoginResponse;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Component
public class LoginController {

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
            System.out.println("Intentando login con usuario: " + username);
            LoginResponse response = authApiClient.login(username, password);

            // Debuguear la respuesta
            System.out.println("Respuesta de autenticación recibida:");
            System.out.println("ID: " + response.getId());
            System.out.println("Username: " + response.getUsername());
            System.out.println("Rol: " + response.getRol());
            System.out.println("Token: " + response.getToken());

            SessionManager.getInstance().setLoginInfo(response);

            // Verificar que se estableció correctamente
            System.out.println("Token almacenado en SessionManager: " +
                    SessionManager.getInstance().getToken());

            abrirDashboard();
        } catch (HttpClientErrorException e) {
            mostrarError("Error de autenticación: " + e.getMessage());
            System.err.println("Error de autenticación: " + e);
        } catch (Exception e) {
            mostrarError("Error al iniciar sesión: " + e.getMessage());
            System.err.println("Error inesperado: " + e);
            e.printStackTrace(); // Para depuración
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
        } catch (Exception e) {
            mostrarError("Error al cargar el dashboard: " + e.getMessage());
            System.err.println("Error al cargar dashboard: " + e);
            e.printStackTrace(); // Para depuración
        }
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
    }
}