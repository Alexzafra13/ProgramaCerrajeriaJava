package com.gestiontaller.client.util;

import javafx.fxml.FXMLLoader;
import org.springframework.context.ApplicationContext;
import java.io.IOException;
import java.net.URL;

public class FXMLLoaderUtil {

    private final ApplicationContext applicationContext;

    public FXMLLoaderUtil(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public <T> T loadFXML(String fxmlPath) {
        try {
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                // Intentar con una ruta alternativa
                resource = getClass().getClassLoader().getResource(fxmlPath.startsWith("/") ? fxmlPath.substring(1) : fxmlPath);

                if (resource == null) {
                    throw new IOException("No se pudo encontrar el recurso: " + fxmlPath);
                }
            }

            FXMLLoader loader = new FXMLLoader(resource);
            loader.setControllerFactory(applicationContext::getBean);
            T root = loader.load();

            // Guardar el controlador en userData del nodo raíz
            if (root instanceof javafx.scene.Parent) {
                ((javafx.scene.Parent) root).setUserData(loader.getController());
            }

            return root;
        } catch (IOException e) {
            throw new RuntimeException("Error loading FXML: " + fxmlPath, e);
        }
    }
}