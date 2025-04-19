-- Configuración ALUPROM-21 con 2 hojas

-- Obtener ID de la serie
SET @serie_aluprom21_id = (SELECT id FROM serie_base WHERE codigo = 'ALUPROM-21' LIMIT 1);

-- Configuración para 2 hojas
INSERT INTO plantilla_configuracion_serie (serie_id, nombre, num_hojas, activa, descripcion)
VALUES (@serie_aluprom21_id, 'Ventana Corredera 2 Hojas', 2, TRUE, 'Configuración estándar para ventanas correderas de 2 hojas')
ON DUPLICATE KEY UPDATE nombre = nombre;

-- Obtener ID de la configuración recién creada o existente
SET @config_2hojas_id = (SELECT id FROM plantilla_configuracion_serie
                         WHERE serie_id = @serie_aluprom21_id AND num_hojas = 2 LIMIT 1);

-- Limpiar configuraciones existentes para actualización segura
DELETE FROM perfil_configuracion WHERE configuracion_id = @config_2hojas_id;
DELETE FROM material_configuracion WHERE configuracion_id = @config_2hojas_id;

-- Insertar perfiles para la configuración
INSERT INTO perfil_configuracion (configuracion_id, perfil_id, tipo_perfil, cantidad, descuento_cm)
SELECT @config_2hojas_id, ps.id, 'MARCO_LATERAL', 2, 0
FROM perfil_serie ps WHERE ps.codigo = 'ALUPROM21-ML';

INSERT INTO perfil_configuracion (configuracion_id, perfil_id, tipo_perfil, cantidad, descuento_cm)
SELECT @config_2hojas_id, ps.id, 'MARCO_SUPERIOR', 1, 4.1
FROM perfil_serie ps WHERE ps.codigo = 'ALUPROM21-MS';

INSERT INTO perfil_configuracion (configuracion_id, perfil_id, tipo_perfil, cantidad, descuento_cm)
SELECT @config_2hojas_id, ps.id, 'MARCO_INFERIOR', 1, 4.1
FROM perfil_serie ps WHERE ps.codigo = 'ALUPROM21-MI';

INSERT INTO perfil_configuracion (configuracion_id, perfil_id, tipo_perfil, cantidad, descuento_cm)
SELECT @config_2hojas_id, ps.id, 'HOJA_LATERAL', 2, 5.3
FROM perfil_serie ps WHERE ps.codigo = 'ALUPROM21-HL';

INSERT INTO perfil_configuracion (configuracion_id, perfil_id, tipo_perfil, cantidad, descuento_cm)
SELECT @config_2hojas_id, ps.id, 'HOJA_CENTRAL', 2, 5.3
FROM perfil_serie ps WHERE ps.codigo = 'ALUPROM21-HC';

INSERT INTO perfil_configuracion (configuracion_id, perfil_id, tipo_perfil, cantidad, formula_calculo)
SELECT @config_2hojas_id, ps.id, 'HOJA_RULETA', 4, 'anchoTotal / 2 - 2'
FROM perfil_serie ps WHERE ps.codigo = 'ALUPROM21-HR';

-- Añadir materiales para la configuración
-- (necesitamos tener productos definidos en la base de datos)
INSERT INTO material_configuracion (configuracion_id, producto_id, descripcion, cantidad_base, formula_cantidad)
SELECT @config_2hojas_id, p.id, 'Juego de rodamientos', 2, 'numHojas'
FROM producto p WHERE p.codigo = 'H001';

INSERT INTO material_configuracion (configuracion_id, producto_id, descripcion, cantidad_base, formula_cantidad)
SELECT @config_2hojas_id, p.id, 'Cierre ventana corredera', 1, NULL
FROM producto p WHERE p.codigo = 'H002';

INSERT INTO material_configuracion (configuracion_id, producto_id, descripcion, cantidad_base, formula_cantidad)
SELECT @config_2hojas_id, p.id, 'Felpa (metros)', 0, '((anchoTotal/100*2) + (altoVentana/100*2)) * numHojas'
FROM producto p WHERE p.codigo = 'A001';

INSERT INTO material_configuracion (configuracion_id, producto_id, descripcion, cantidad_base, formula_cantidad)
SELECT @config_2hojas_id, p.id, 'Tornillos autorroscantes', 0, '20 * numHojas'
FROM producto p WHERE p.codigo = 'T001';