-- Nuevas tablas para configuraciones de series

-- Tabla para plantillas de configuración de series
CREATE TABLE IF NOT EXISTS plantilla_configuracion_serie (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    serie_id BIGINT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    num_hojas INT NOT NULL,
    activa BOOLEAN DEFAULT TRUE,
    descripcion TEXT,
    FOREIGN KEY (serie_id) REFERENCES serie_base(id),
    UNIQUE KEY uk_serie_num_hojas (serie_id, num_hojas)
);

-- Tabla para perfiles en cada configuración
CREATE TABLE IF NOT EXISTS perfil_configuracion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    configuracion_id BIGINT NOT NULL,
    perfil_id BIGINT NOT NULL,
    tipo_perfil VARCHAR(50) NOT NULL,
    cantidad INT NOT NULL,
    descuento_cm DECIMAL(5,2),
    formula_calculo TEXT,
    FOREIGN KEY (configuracion_id) REFERENCES plantilla_configuracion_serie(id) ON DELETE CASCADE,
    FOREIGN KEY (perfil_id) REFERENCES perfil_serie(id)
);

-- Tabla para materiales en cada configuración
CREATE TABLE IF NOT EXISTS material_configuracion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    configuracion_id BIGINT NOT NULL,
    producto_id BIGINT,
    descripcion VARCHAR(255) NOT NULL,
    cantidad_base INT NOT NULL,
    formula_cantidad TEXT,
    FOREIGN KEY (configuracion_id) REFERENCES plantilla_configuracion_serie(id) ON DELETE CASCADE,
    FOREIGN KEY (producto_id) REFERENCES producto(id)
);

-- Datos iniciales - Configuración para ALUPROM-21 con 2 hojas
INSERT INTO plantilla_configuracion_serie (serie_id, nombre, num_hojas, activa, descripcion)
SELECT id, 'Ventana Corredera 2 Hojas', 2, TRUE, 'Configuración estándar para ventanas correderas de 2 hojas'
FROM serie_base
WHERE codigo = 'ALUPROM-21';

-- Obtener ID de la configuración recién creada
SET @config_id = LAST_INSERT_ID();

-- Insertar perfiles para la configuración
INSERT INTO perfil_configuracion (configuracion_id, perfil_id, tipo_perfil, cantidad, descuento_cm)
SELECT @config_id, ps.id, 'MARCO_LATERAL', 2, 0
FROM perfil_serie ps WHERE ps.codigo = 'ALUPROM21-ML';

INSERT INTO perfil_configuracion (configuracion_id, perfil_id, tipo_perfil, cantidad, descuento_cm)
SELECT @config_id, ps.id, 'MARCO_SUPERIOR', 1, 4.1
FROM perfil_serie ps WHERE ps.codigo = 'ALUPROM21-MS';

INSERT INTO perfil_configuracion (configuracion_id, perfil_id, tipo_perfil, cantidad, descuento_cm)
SELECT @config_id, ps.id, 'MARCO_INFERIOR', 1, 4.1
FROM perfil_serie ps WHERE ps.codigo = 'ALUPROM21-MI';

INSERT INTO perfil_configuracion (configuracion_id, perfil_id, tipo_perfil, cantidad, descuento_cm)
SELECT @config_id, ps.id, 'HOJA_LATERAL', 2, 5.3
FROM perfil_serie ps WHERE ps.codigo = 'ALUPROM21-HL';

INSERT INTO perfil_configuracion (configuracion_id, perfil_id, tipo_perfil, cantidad, descuento_cm)
SELECT @config_id, ps.id, 'HOJA_CENTRAL', 2, 5.3
FROM perfil_serie ps WHERE ps.codigo = 'ALUPROM21-HC';

INSERT INTO perfil_configuracion (configuracion_id, perfil_id, tipo_perfil, cantidad, descuento_cm, formula_calculo)
SELECT @config_id, ps.id, 'HOJA_RULETA', 4, 2, 'anchoTotal / 2 - 2'
FROM perfil_serie ps WHERE ps.codigo = 'ALUPROM21-HR';

-- Insertar materiales para la configuración
INSERT INTO material_configuracion (configuracion_id, producto_id, descripcion, cantidad_base, formula_cantidad)
SELECT @config_id, p.id, 'Juego de rodamientos', 2, 'numHojas'
FROM producto p WHERE p.codigo = 'H001';

INSERT INTO material_configuracion (configuracion_id, producto_id, descripcion, cantidad_base, formula_cantidad)
SELECT @config_id, p.id, 'Cierre ventana corredera', 1, NULL
FROM producto p WHERE p.codigo = 'H002';

INSERT INTO material_configuracion (configuracion_id, producto_id, descripcion, cantidad_base, formula_cantidad)
SELECT @config_id, p.id, 'Felpa (metros)', 0, '((anchoTotal/numHojas)*2 + altoTotal*2) * numHojas / 100'
FROM producto p WHERE p.codigo = 'A001';

INSERT INTO material_configuracion (configuracion_id, producto_id, descripcion, cantidad_base, formula_cantidad)
SELECT @config_id, p.id, 'Tornillos autorroscantes', 0, '20 * numHojas'
FROM producto p WHERE p.codigo = 'T001';