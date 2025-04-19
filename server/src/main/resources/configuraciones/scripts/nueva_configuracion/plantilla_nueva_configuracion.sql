-- PLANTILLA PARA AÑADIR UNA NUEVA CONFIGURACIÓN A UNA SERIE EXISTENTE
-- Reemplazar CODIGO_SERIE, NUM_HOJAS y otros valores con la información real

-- 1. Obtener ID de la serie
SET @serie_id = (SELECT id FROM serie_base WHERE codigo = 'CODIGO_SERIE' LIMIT 1);

-- 2. Insertar configuración
INSERT INTO plantilla_configuracion_serie (serie_id, nombre, num_hojas, activa, descripcion)
VALUES (@serie_id, 'NOMBRE_CONFIGURACION', NUM_HOJAS, TRUE, 'DESCRIPCION_CONFIGURACION');

-- 3. Obtener ID de la configuración creada
SET @config_id = LAST_INSERT_ID();

-- 4. Insertar perfiles para la configuración
-- Ejemplo para un perfil de marco
INSERT INTO perfil_configuracion (configuracion_id, perfil_id, tipo_perfil, cantidad, descuento_cm)
SELECT @config_id, ps.id, 'TIPO_ESPECIFICO', CANTIDAD, DESCUENTO
FROM perfil_serie ps WHERE ps.codigo = 'CODIGO_PERFIL';

-- Ejemplo para un perfil con fórmula de cálculo
INSERT INTO perfil_configuracion (configuracion_id, perfil_id, tipo_perfil, cantidad, formula_calculo)
SELECT @config_id, ps.id, 'TIPO_ESPECIFICO', CANTIDAD, 'FORMULA_JS'
FROM perfil_serie ps WHERE ps.codigo = 'CODIGO_PERFIL';

-- 5. Insertar materiales para la configuración
INSERT INTO material_configuracion (configuracion_id, producto_id, descripcion, cantidad_base, formula_cantidad)
SELECT @config_id, p.id, 'DESCRIPCION_MATERIAL', CANTIDAD_BASE, 'FORMULA_CANTIDAD'
FROM producto p WHERE p.codigo = 'CODIGO_PRODUCTO';

-- Repetir los pasos 4 y 5 para cada perfil y material necesario