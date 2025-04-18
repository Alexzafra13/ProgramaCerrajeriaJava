package com.gestiontaller.client.controller;

import com.gestiontaller.client.api.AuthApiClient;
import com.gestiontaller.client.model.auth.LoginResponse;
import com.gestiontaller.client.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

import java.io.IOException;

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
    private final FXMLLoader fxmlLoader;

    @Autowired
    public LoginController(AuthApiClient authApiClient, FXMLLoader fxmlLoader) {
        this.authApiClient = authApiClient;
        this.fxmlLoader = fxmlLoader;
    }

    @FXML
    public void initialize() {
        // Inicialización de la pantalla
        lblError.setVisible(false);

        // Para desarrollo, valores por defecto
        txtUsername.setText("admin");
        txtPassword.setText("admin");
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        // Validación básica
        if (username.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor complete todos los campos");
            return;
        }

        try {
            // Intento de login
            LoginResponse response = authApiClient.login(username, password);

            // Guardar información de sesión
            SessionManager.getInstance().setLoginInfo(response);

            // Abrir el dashboard
            abrirDashboard();

        } catch (HttpClientErrorException e) {
            mostrarError("Error de autenticación: " + e.getMessage());
        } catch (Exception e) {
            mostrarError("Error al iniciar sesión: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        // Cerrar la aplicación
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    private void abrirDashboard() {
        try {
            // Cargar la vista del dashboard
            fxmlLoader.setLocation(getClass().getResource("/fxml/dashboard.fxml"));
            Parent root = fxmlLoader.load();

            // Configurar la escena
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());

            // Mostrar en la misma ventana
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setTitle("Gestión de Taller - Dashboard");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            mostrarError("Error al cargar el dashboard: " + e.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
    }
}