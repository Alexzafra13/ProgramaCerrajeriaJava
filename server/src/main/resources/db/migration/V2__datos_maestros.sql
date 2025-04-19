-- Datos maestros necesarios para el funcionamiento del sistema

-- Usuario administrador por defecto
INSERT INTO usuario (username, password, nombre, apellidos, email, telefono, rol, activo, fecha_creacion)
VALUES ('admin', '$2a$10$2EUJkodRlgGybDsgJL50Bu7c.E/T1qwgxkQTw9t1VJqQ8FuS5TxeG', 'Administrador', 'Sistema', 'admin@sistema.com', '600000000', 'ADMIN', 1, NOW())
ON DUPLICATE KEY UPDATE nombre = nombre;

-- Categorías de productos
INSERT IGNORE INTO categoria_producto (nombre, descripcion)
VALUES
    ('Perfiles', 'Perfiles de aluminio para ventanas y puertas'),
    ('Herrajes', 'Herrajes para ventanas y puertas'),
    ('Tornillería', 'Tornillos y fijaciones'),
    ('Accesorios', 'Accesorios para instalación'),
    ('Vidrios', 'Vidrios para ventanas y puertas');

-- Estados de trabajo
INSERT IGNORE INTO estado_trabajo (codigo, nombre, descripcion, color, orden, requiere_aprobacion, es_final)
VALUES
    ('PDTE', 'Pendiente', 'Trabajo pendiente de iniciar', '#FFA500', 10, 0, 0),
    ('ASIG', 'Asignado', 'Trabajo asignado a operario', '#4682B4', 20, 0, 0),
    ('PROG', 'En progreso', 'Trabajo en ejecución', '#1E90FF', 30, 0, 0),
    ('PAUS', 'Pausado', 'Trabajo temporalmente pausado', '#FF4500', 40, 0, 0),
    ('TERM', 'Terminado', 'Trabajo completado pendiente de verificación', '#32CD32', 50, 1, 0),
    ('VERI', 'Verificado', 'Trabajo verificado pendiente de instalación', '#008000', 60, 0, 0),
    ('INST', 'Instalado', 'Trabajo instalado en ubicación del cliente', '#006400', 70, 0, 0),
    ('FACT', 'Facturado', 'Trabajo facturado', '#000080', 80, 0, 1),
    ('CANC', 'Cancelado', 'Trabajo cancelado', '#FF0000', 90, 1, 1);

-- Cliente de ejemplo
INSERT IGNORE INTO cliente (codigo, tipo_cliente, nombre, apellidos, razon_social, nif_cif, direccion_fiscal, codigo_postal, localidad, provincia, pais, telefono1, email, fecha_alta, activo)
VALUES
    ('CLI001', 'PARTICULAR', 'Juan', 'García López', NULL, '12345678Z', 'C/ Ejemplo 123', '28001', 'Madrid', 'Madrid', 'España', '600123456', 'juan.garcia@ejemplo.com', NOW(), 1),
    ('EMP001', 'EMPRESA', NULL, NULL, 'Construcciones Ejemplo S.L.', 'B12345678', 'Avda. Industrial 45', '08001', 'Barcelona', 'Barcelona', 'España', '934567890', 'ventas@construccionesejemplo.com', NOW(), 1);