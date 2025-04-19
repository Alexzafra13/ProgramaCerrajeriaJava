-- Configuración ALUPROM-21 con 4 hojas

-- Obtener ID de la serie
SET @serie_aluprom21_id = (SELECT id FROM serie_base WHERE codigo = 'ALUPROM-21' LIMIT 1);

-- Configuración para 4 hojas
INSERT INTO plantilla_configuracion_serie (serie_id, nombre, num_hojas, activa, descripcion)
VALUES (@serie_aluprom21_id, 'Ventana Corredera 4 Hojas', 4, TRUE, 'Configuración para ventanas correderas de 4 hojas')
ON DUPLICATE KEY UPDATE nombre = nombre;

-- Obtener ID de la configuración
SET @config_4hojas_id = (SELECT id FROM plantilla_configuracion_serie
                         WHERE serie_id = @serie_aluprom21_id AND num_hojas = 4 LIMIT 1);

-- Limpiar configuraciones existentes para actualización segura
DELETE FROM perfil_configuracion WHERE configuracion_id = @config_4hojas_id;
DELETE FROM material_configuracion WHERE configuracion_id = @config_4hojas_id;

-- Insertar perfiles para la configuración
-- Marco y partes similares a 2 hojas
INSERT INTO perfil_configuracion (configuracion_id, perfil_id, tipo_perfil, cantidad, descuento_cm)
SELECT @config_4hojas_id, ps.id, 'MARCO_LATERAL', 2, 0
FROM perfil_serie ps WHERE ps.codigo = 'ALUPROM21-ML';

INSERT INTO perfil_configuracion (configuracion_id, perfil_id, tipo_perfil, cantidad, descuento_cm)
SELECT @config_4hojas_id, ps.id, 'MARCO_SUPERIOR', 1, 4.1
FROM perfil_serie ps WHERE ps.codigo = 'ALUPROM21-MS';

INSERT INTO perfil_configuracion (configuracion_id, perfil_id, tipo_perfil, cantidad, descuento_cm)
SELECT @config_4hojas_id, ps.id, 'MARCO_INFERIOR', 1, 4.1
FROM perfil_serie ps WHERE ps.codigo = 'ALUPROM21-MI';

-- Aumentar cantidad de hojas
INSERT INTO perfil_configuracion (configuracion_id, perfil_id, tipo_perfil, cantidad, descuento_cm)
SELECT @config_4hojas_id, ps.id, 'HOJA_LATERAL', 4, 5.3
FROM perfil_serie ps WHERE ps.codigo = 'ALUPROM21-HL';

INSERT INTO perfil_configuracion (configuracion_id, perfil_id, tipo_perfil, cantidad, descuento_cm)
SELECT @config_4hojas_id, ps.id, 'HOJA_CENTRAL', 4, 5.3
FROM perfil_serie ps WHERE ps.codigo = 'ALUPROM21-HC';

-- Fórmula específica para 4 hojas
INSERT INTO perfil_configuracion (configuracion_id, perfil_id, tipo_perfil, cantidad, formula_calculo)
SELECT @config_4hojas_id, ps.id, 'HOJA_RULETA', 8, '(anchoTotal - 3) / 4'
FROM perfil_serie ps WHERE ps.codigo = 'ALUPROM21-HR';

-- Materiales ajustados para 4 hojas
INSERT INTO material_configuracion (configuracion_id, producto_id, descripcion, cantidad_base, formula_cantidad)
SELECT @config_4hojas_id, p.id, 'Juego de rodamientos', 4, 'numHojas'
FROM producto p WHERE p.codigo = 'H001';

INSERT INTO material_configuracion (configuracion_id, producto_id, descripcion, cantidad_base, formula_cantidad)
SELECT @config_4hojas_id, p.id, 'Cierre ventana corredera', 2, NULL
FROM producto p WHERE p.codigo = 'H002';

INSERT INTO material_configuracion (configuracion_id, producto_id, descripcion, cantidad_base, formula_cantidad)
SELECT @config_4hojas_id, p.id, 'Felpa (metros)', 0, '((anchoTotal/100*4) + (altoVentana/100*4))'
FROM producto p WHERE p.codigo = 'A001';

INSERT INTO material_configuracion (configuracion_id, producto_id, descripcion, cantidad_base, formula_cantidad)
SELECT @config_4hojas_id, p.id, 'Tornillos autorroscantes', 0, '40 * numHojas'
FROM producto p WHERE p.codigo = 'T001';