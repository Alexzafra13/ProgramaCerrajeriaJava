-- Script para insertar datos iniciales cuando la base de datos está vacía

-- Creación de usuario administrador por defecto
INSERT INTO usuario (username, password, nombre, apellidos, email, telefono, rol, activo, fecha_creacion)
VALUES ('admin', '$2a$10$2EUJkodRlgGybDsgJL50Bu7c.E/T1qwgxkQTw9t1VJqQ8FuS5TxeG', 'Administrador', 'Sistema', 'admin@sistema.com', '600000000', 'ADMIN', 1, NOW())
ON DUPLICATE KEY UPDATE nombre = nombre;

-- Serie ALUPROM-21
INSERT INTO serie_base (codigo, nombre, descripcion, tipo_material, precio_metro_base, descuento_serie, activa)
VALUES ('ALUPROM-21', 'Serie ALUPROM 21', 'Serie de aluminio para ventanas correderas de dos hojas', 'ALUMINIO', 15.0, 0, 1)
ON DUPLICATE KEY UPDATE nombre = nombre;

-- Obtener ID de la serie insertada
SET @serie_aluprom21_id = (SELECT id FROM serie_base WHERE codigo = 'ALUPROM-21');

-- Insertar detalles de serie de aluminio
INSERT INTO serie_aluminio (id, tipo_serie, espesor_minimo, espesor_maximo, color, rotura_puente, permite_persiana)
VALUES (@serie_aluprom21_id, 'CORREDERA', 1.5, 1.8, 'BLANCO', 0, 1)
ON DUPLICATE KEY UPDATE tipo_serie = tipo_serie;

-- Perfiles para ALUPROM-21
INSERT INTO perfil_serie (serie_id, codigo, nombre, tipo_perfil, peso_metro, precio_metro, longitud_barra)
VALUES
    (@serie_aluprom21_id, 'ALUPROM21-ML', 'Marco Lateral', 'MARCO', 0.85, 10.5, 6000),
    (@serie_aluprom21_id, 'ALUPROM21-MS', 'Marco Superior', 'MARCO', 0.85, 10.5, 6000),
    (@serie_aluprom21_id, 'ALUPROM21-MI', 'Marco Inferior', 'MARCO', 0.85, 10.5, 6000),
    (@serie_aluprom21_id, 'ALUPROM21-HL', 'Hoja Lateral', 'HOJA', 0.65, 8.5, 6000),
    (@serie_aluprom21_id, 'ALUPROM21-HC', 'Hoja Central', 'HOJA', 0.65, 8.5, 6000),
    (@serie_aluprom21_id, 'ALUPROM21-HR', 'Hoja Ruleta', 'HOJA', 0.55, 7.5, 6000)
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

-- Descuentos para ALUPROM-21
INSERT INTO descuento_perfil_serie (serie_id, tipo_perfil, descuento_milimetros, descripcion)
VALUES
    (@serie_aluprom21_id, 'MARCO', 0, 'Alto marco: Sin descuento'),
    (@serie_aluprom21_id, 'MARCO', 41, 'Ancho marco: medida - 4.1 cm'),
    (@serie_aluprom21_id, 'HOJA', 53, 'Alto hoja: altura - 5.3 cm'),
    (@serie_aluprom21_id, 'HOJA', 20, 'Ancho hoja: (ancho total / 2) - 2 cm')
ON DUPLICATE KEY UPDATE descuento_milimetros = VALUES(descuento_milimetros);

-- Categorías de productos
INSERT INTO categoria_producto (nombre, descripcion)
VALUES ('Perfiles', 'Perfiles de aluminio para ventanas y puertas'),
       ('Herrajes', 'Herrajes para ventanas y puertas'),
       ('Tornillería', 'Tornillos y fijaciones'),
       ('Accesorios', 'Accesorios para instalación'),
       ('Vidrios', 'Vidrios para ventanas y puertas')
ON DUPLICATE KEY UPDATE nombre = nombre;

-- Obtener IDs de categorías
SET @cat_perfiles_id = (SELECT id FROM categoria_producto WHERE nombre = 'Perfiles');
SET @cat_herrajes_id = (SELECT id FROM categoria_producto WHERE nombre = 'Herrajes');
SET @cat_tornilleria_id = (SELECT id FROM categoria_producto WHERE nombre = 'Tornillería');
SET @cat_accesorios_id = (SELECT id FROM categoria_producto WHERE nombre = 'Accesorios');
SET @cat_vidrios_id = (SELECT id FROM categoria_producto WHERE nombre = 'Vidrios');

-- Productos de ejemplo
INSERT INTO producto (codigo, nombre, descripcion, categoria_id, tipo, unidad_medida, precio_compra, margen_beneficio, precio_venta, aplicar_iva, stock_minimo, stock_actual, activo)
VALUES
    -- Herrajes
    ('H001', 'Juego rodamientos corredera', 'Juego completo de rodamientos para ventana corredera', @cat_herrajes_id, 'HERRAJE', 'UNIDADES', 4.50, 30, 5.85, 1, 20, 50, 1),
    ('H002', 'Cierre ventana corredera', 'Cierre completo para ventana corredera', @cat_herrajes_id, 'HERRAJE', 'UNIDADES', 7.80, 30, 10.14, 1, 10, 25, 1),

    -- Tornillería
    ('T001', 'Tornillo autorroscante 3.5x16', 'Tornillo autorroscante para aluminio', @cat_tornilleria_id, 'TORNILLO', 'UNIDADES', 0.02, 30, 0.026, 1, 500, 1000, 1),
    ('T002', 'Tornillo autorroscante 3.9x19', 'Tornillo autorroscante para aluminio', @cat_tornilleria_id, 'TORNILLO', 'UNIDADES', 0.03, 30, 0.039, 1, 500, 1000, 1),

    -- Accesorios
    ('A001', 'Felpa 5mm (metro)', 'Felpa para ventanas correderas', @cat_accesorios_id, 'ACCESORIO', 'METROS', 0.45, 30, 0.585, 1, 50, 200, 1),
    ('A002', 'Goma EPDM (metro)', 'Goma para estanqueidad', @cat_accesorios_id, 'GOMA', 'METROS', 0.55, 30, 0.715, 1, 50, 200, 1),

    -- Vidrios
    ('V001', 'Vidrio simple 4mm', 'Vidrio simple transparente 4mm', @cat_vidrios_id, 'VIDRIO', 'METROS_CUADRADOS', 15.00, 30, 19.50, 1, 10, 50, 1),
    ('V002', 'Vidrio doble 4-12-4', 'Vidrio doble cámara 4-12-4', @cat_vidrios_id, 'VIDRIO', 'METROS_CUADRADOS', 25.00, 30, 32.50, 1, 10, 30, 1)
ON DUPLICATE KEY UPDATE nombre = nombre;

-- Estados de trabajo
INSERT INTO estado_trabajo (codigo, nombre, descripcion, color, orden, requiere_aprobacion, es_final)
VALUES ('PDTE', 'Pendiente', 'Trabajo pendiente de iniciar', '#FFA500', 10, 0, 0),
       ('ASIG', 'Asignado', 'Trabajo asignado a operario', '#4682B4', 20, 0, 0),
       ('PROG', 'En progreso', 'Trabajo en ejecución', '#1E90FF', 30, 0, 0),
       ('PAUS', 'Pausado', 'Trabajo temporalmente pausado', '#FF4500', 40, 0, 0),
       ('TERM', 'Terminado', 'Trabajo completado pendiente de verificación', '#32CD32', 50, 1, 0),
       ('VERI', 'Verificado', 'Trabajo verificado pendiente de instalación', '#008000', 60, 0, 0),
       ('INST', 'Instalado', 'Trabajo instalado en ubicación del cliente', '#006400', 70, 0, 0),
       ('FACT', 'Facturado', 'Trabajo facturado', '#000080', 80, 0, 1),
       ('CANC', 'Cancelado', 'Trabajo cancelado', '#FF0000', 90, 1, 1)
ON DUPLICATE KEY UPDATE nombre = nombre;

-- Cliente de ejemplo
INSERT INTO cliente (codigo, tipo_cliente, nombre, apellidos, razon_social, nif_cif, direccion_fiscal, codigo_postal, localidad, provincia, pais, telefono1, email, fecha_alta, activo)
VALUES ('CLI001', 'PARTICULAR', 'Juan', 'García López', NULL, '12345678Z', 'C/ Ejemplo 123', '28001', 'Madrid', 'Madrid', 'España', '600123456', 'juan.garcia@ejemplo.com', NOW(), 1),
       ('EMP001', 'EMPRESA', NULL, NULL, 'Construcciones Ejemplo S.L.', 'B12345678', 'Avda. Industrial 45', '08001', 'Barcelona', 'Barcelona', 'España', '934567890', 'ventas@construccionesejemplo.com', NOW(), 1)
ON DUPLICATE KEY UPDATE nombre = nombre;