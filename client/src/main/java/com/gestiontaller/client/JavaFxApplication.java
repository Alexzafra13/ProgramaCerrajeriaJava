package com.gestiontaller.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class JavaFxApplication extends Application {

    private ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(ClientApplication.class)
                .run();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = springContext.getBean(FXMLLoader.class);
        fxmlLoader.setLocation(getClass().getResource("/fxml/login.fxml"));
        Parent root = fxmlLoader.load();

        primaryStage.setTitle("Gestión de Taller - Aluminio");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    @Override
    public void stop() {
        springContext.close();
        Platform.exit();
    }
}