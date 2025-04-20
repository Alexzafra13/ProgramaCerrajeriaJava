-- Actualización de la base de datos para soportar componentes de series (herrajes, accesorios, etc.)
-- Este script debe ser colocado en: server/src/main/resources/db/migration/V6__componentes_serie.sql

-- 1. Crear tabla para materiales base de series
CREATE TABLE IF NOT EXISTS material_base_serie (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    serie_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    tipo_material VARCHAR(50) NOT NULL COMMENT 'HERRAJE_BASICO, TORNILLERIA, ACCESORIO, etc.',
    es_predeterminado BOOLEAN DEFAULT TRUE,
    notas_tecnicas TEXT,
    FOREIGN KEY (serie_id) REFERENCES serie_base(id),
    FOREIGN KEY (producto_id) REFERENCES producto(id),
    UNIQUE KEY uk_serie_producto (serie_id, producto_id)
);

-- 2. Insertar datos para la serie ALUPROM-21
-- Primero, asegurarnos de que los productos existen en el catálogo
INSERT IGNORE INTO producto (codigo, nombre, descripcion, categoria_id, tipo, unidad_medida, precio_compra, margen_beneficio, precio_venta, stock_minimo, stock_actual)
VALUES
    ('H010', 'Cierre de presión estándar', 'Cierre de presión para ventana corredera ALUPROM-21',
     (SELECT id FROM categoria_producto WHERE nombre = 'Herrajes'), 'HERRAJE', 'UNIDADES', 3.20, 30, 4.16, 20, 100),

    ('H020', 'Kit rodamientos corredera', 'Kit completo de rodamientos para ventana corredera ALUPROM-21',
     (SELECT id FROM categoria_producto WHERE nombre = 'Herrajes'), 'HERRAJE', 'UNIDADES', 6.50, 30, 8.45, 15, 80),

    ('T010', 'Tornillo 4,8x25mm cabeza redonda', 'Tornillos específicos para serie ALUPROM-21',
     (SELECT id FROM categoria_producto WHERE nombre = 'Tornillería'), 'TORNILLO', 'UNIDADES', 0.05, 30, 0.065, 200, 1000),

    ('A001', 'Felpa 5mm (metro)', 'Felpa para estanqueidad de hojas correderas',
     (SELECT id FROM categoria_producto WHERE nombre = 'Accesorios'), 'ACCESORIO', 'METROS', 0.45, 30, 0.585, 50, 200);

-- Obtener ID de la serie ALUPROM-21
SET @serie_aluprom21_id = (SELECT id FROM serie_base WHERE codigo = 'ALUPROM-21' LIMIT 1);

-- Asociar componentes a la serie ALUPROM-21
INSERT IGNORE INTO material_base_serie (serie_id, producto_id, descripcion, tipo_material, es_predeterminado, notas_tecnicas)
SELECT
    @serie_aluprom21_id,
    p.id,
    'Cierre de presión estándar para ALUPROM-21',
    'HERRAJE_BASICO',
    1,
    'Cierre de presión específico para ventanas correderas de la serie ALUPROM-21'
FROM producto p WHERE p.codigo = 'H010';

INSERT IGNORE INTO material_base_serie (serie_id, producto_id, descripcion, tipo_material, es_predeterminado, notas_tecnicas)
SELECT
    @serie_aluprom21_id,
    p.id,
    'Kit de rodamientos para ALUPROM-21',
    'HERRAJE_BASICO',
    1,
    'Kit completo con 2 rodamientos, soportes y tornillos de fijación'
FROM producto p WHERE p.codigo = 'H020';

INSERT IGNORE INTO material_base_serie (serie_id, producto_id, descripcion, tipo_material, es_predeterminado, notas_tecnicas)
SELECT
    @serie_aluprom21_id,
    p.id,
    'Tornillo 4,8x25mm para ALUPROM-21',
    'TORNILLERIA',
    1,
    'Tornillo autorroscante cabeza redonda, específico para perfiles de aluminio'
FROM producto p WHERE p.codigo = 'T010';

INSERT IGNORE INTO material_base_serie (serie_id, producto_id, descripcion, tipo_material, es_predeterminado, notas_tecnicas)
SELECT
    @serie_aluprom21_id,
    p.id,
    'Felpa 5mm para estanqueidad ALUPROM-21',
    'ACCESORIO',
    1,
    'Felpa para mejorar estanqueidad en ventanas correderas'
FROM producto p WHERE p.codigo = 'A001';

-- 3. Configurar materiales para las plantillas de configuración existentes
-- Obtener ID de la configuración para 2 hojas
SET @config_2hojas_id = (SELECT id FROM plantilla_configuracion_serie
                         WHERE serie_id = @serie_aluprom21_id
                         AND num_hojas = 2 LIMIT 1);

-- Obtener ID de la configuración para 4 hojas
SET @config_4hojas_id = (SELECT id FROM plantilla_configuracion_serie
                         WHERE serie_id = @serie_aluprom21_id
                         AND num_hojas = 4 LIMIT 1);

-- Configurar materiales para ventana de 2 hojas
INSERT IGNORE INTO material_configuracion (configuracion_id, producto_id, descripcion, cantidad_base, formula_cantidad)
SELECT
    @config_2hojas_id,
    p.id,
    'Cierres de presión',
    2,
    NULL
FROM producto p WHERE p.codigo = 'H010';

INSERT IGNORE INTO material_configuracion (configuracion_id, producto_id, descripcion, cantidad_base, formula_cantidad)
SELECT
    @config_2hojas_id,
    p.id,
    'Kit de rodamientos',
    1,
    NULL
FROM producto p WHERE p.codigo = 'H020';

INSERT IGNORE INTO material_configuracion (configuracion_id, producto_id, descripcion, cantidad_base, formula_cantidad)
SELECT
    @config_2hojas_id,
    p.id,
    'Tornillos 4,8x25mm',
    18,
    NULL
FROM producto p WHERE p.codigo = 'T010';

INSERT IGNORE INTO material_configuracion (configuracion_id, producto_id, descripcion, cantidad_base, formula_cantidad)
SELECT
    @config_2hojas_id,
    p.id,
    'Felpa (metros)',
    0,
    '((anchoTotal/100*2) + (altoVentana/100*2)) * numHojas'
FROM producto p WHERE p.codigo = 'A001';

-- Configurar materiales para ventana de 4 hojas
INSERT IGNORE INTO material_configuracion (configuracion_id, producto_id, descripcion, cantidad_base, formula_cantidad)
SELECT
    @config_4hojas_id,
    p.id,
    'Cierres de presión',
    4,
    NULL
FROM producto p WHERE p.codigo = 'H010';

INSERT IGNORE INTO material_configuracion (configuracion_id, producto_id, descripcion, cantidad_base, formula_cantidad)
SELECT
    @config_4hojas_id,
    p.id,
    'Kit de rodamientos',
    2,
    NULL
FROM producto p WHERE p.codigo = 'H020';

INSERT IGNORE INTO material_configuracion (configuracion_id, producto_id, descripcion, cantidad_base, formula_cantidad)
SELECT
    @config_4hojas_id,
    p.id,
    'Tornillos 4,8x25mm',
    26,
    NULL
FROM producto p WHERE p.codigo = 'T010';

INSERT IGNORE INTO material_configuracion (configuracion_id, producto_id, descripcion, cantidad_base, formula_cantidad)
SELECT
    @config_4hojas_id,
    p.id,
    'Felpa (metros)',
    0,
    '((anchoTotal/100*4) + (altoVentana/100*4))'
FROM producto p WHERE p.codigo = 'A001';

-- 4. Crear un índice para mejorar el rendimiento de las consultas
CREATE INDEX idx_material_config_config_id ON material_configuracion(configuracion_id);
CREATE INDEX idx_material_base_serie_id ON material_base_serie(serie_id);
CREATE INDEX idx_material_base_tipo ON material_base_serie(tipo_material);