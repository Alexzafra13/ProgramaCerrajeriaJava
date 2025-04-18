package com.gestiontaller.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.util.concurrent.atomic.AtomicBoolean;

@Configuration
public class DataInitializerConfig {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private static final AtomicBoolean dataInitialized = new AtomicBoolean(false);

    @Autowired
    public DataInitializerConfig(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void initData() {
        // Verificar si ya se inicializaron los datos para evitar duplicados
        if (dataInitialized.getAndSet(true)) {
            return;
        }

        // Verifica si existe la tabla usuario
        try {
            // Comprobar si ya existen datos en la tabla de usuarios
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM usuario", Integer.class);

            if (count != null && count == 0) {
                // Insertar usuario admin
                String encodedPassword = passwordEncoder.encode("admin");
                jdbcTemplate.update(
                        "INSERT INTO usuario (username, password, nombre, apellidos, email, telefono, rol, activo, fecha_creacion) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())",
                        "admin", encodedPassword, "Administrador", "Sistema", "admin@sistema.com", "600000000", "ADMIN", true
                );

                System.out.println("Usuario administrador creado correctamente");

                // Continuar con la inicialización de otros datos de prueba si es necesario
                initOtherTestData();
            }
        } catch (Exception e) {
            System.err.println("Error al verificar/crear usuario administrador: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initOtherTestData() {
        try {
            // Verificar y crear datos adicionales como categorías, series, etc.
            // Solo si es necesario para pruebas iniciales

            // Ejemplo: verificar si existen categorías primero
            Integer catCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM categoria_producto", Integer.class);

            if (catCount != null && catCount == 0) {
                jdbcTemplate.update(
                        "INSERT INTO categoria_producto (nombre, descripcion) VALUES (?, ?)",
                        "Perfiles", "Perfiles de aluminio para ventanas y puertas"
                );
                jdbcTemplate.update(
                        "INSERT INTO categoria_producto (nombre, descripcion) VALUES (?, ?)",
                        "Herrajes", "Herrajes para ventanas y puertas"
                );

                System.out.println("Categorías básicas creadas");
            }

            // Añadir más inicializaciones según necesites para pruebas
        } catch (Exception e) {
            System.err.println("Error al inicializar datos de prueba: " + e.getMessage());
        }
    }
}