-- Template para crear una configuración de ventana para una serie existente
-- Uso: Reemplazar CODIGO_SERIE, NUM_HOJAS, etc. con los valores reales

-- 1. Obtener ID de la serie
SET @serie_id = (SELECT id FROM serie_base WHERE codigo = 'CODIGO_SERIE' LIMIT 1);

-- 2. Crear configuración
INSERT INTO plantilla_configuracion_serie (serie_id, nombre, num_hojas, activa, descripcion)
VALUES (@serie_id, 'NOMBRE_CONFIGURACION', NUM_HOJAS, TRUE, 'DESCRIPCION_CONFIGURACION');

-- 3. Obtener ID de la configuración
SET @config_id = LAST_INSERT_ID();

-- 4. Configurar perfiles necesarios
-- 4.1 Marcos
INSERT INTO perfil_configuracion (configuracion_id, perfil_id, tipo_perfil, cantidad, descuento_cm)
SELECT
    @config_id,
    ps.id,
    'MARCO_LATERAL',
    2,
    0
FROM perfil_serie ps WHERE ps.serie_id = @serie_id AND ps.codigo = 'CODIGO_SERIE-ML';

INSERT INTO perfil_configuracion (configuracion_id, perfil_id, tipo_perfil, cantidad, descuento_cm)
SELECT
    @config_id,
    ps.id,
    'MARCO_SUPERIOR',
    1,
    4.1
FROM perfil_serie ps WHERE ps.serie_id = @serie_id AND ps.codigo = 'CODIGO_SERIE-MS';

INSERT INTO perfil_configuracion (configuracion_id, perfil_id, tipo_perfil, cantidad, descuento_cm)
SELECT
    @config_id,
    ps.id,
    'MARCO_INFERIOR',
    1,
    4.1
FROM perfil_serie ps WHERE ps.serie_id = @serie_id AND ps.codigo = 'CODIGO_SERIE-MI';

-- 4.2 Hojas (depende del tipo y número)
-- Para ventana corredera de 2 hojas:
INSERT INTO perfil_configuracion (configuracion_id, perfil_id, tipo_perfil, cantidad, descuento_cm)
SELECT
    @config_id,
    ps.id,
    'HOJA_LATERAL',
    2,
    5.3
FROM perfil_serie ps WHERE ps.serie_id = @serie_id AND ps.codigo = 'CODIGO_SERIE-HL';

INSERT INTO perfil_configuracion (configuracion_id, perfil_id, tipo_perfil, cantidad, descuento_cm)
SELECT
    @config_id,
    ps.id,
    'HOJA_CENTRAL',
    2,
    5.3
FROM perfil_serie ps WHERE ps.serie_id = @serie_id AND ps.codigo = 'CODIGO_SERIE-HC';

-- 4.3 Ejemplo de perfil con fórmula dinámica
INSERT INTO perfil_configuracion (configuracion_id, perfil_id, tipo_perfil, cantidad, formula_calculo)
SELECT
    @config_id,
    ps.id,
    'HOJA_RULETA',
    4,
    'anchoTotal / 2 - 2'
FROM perfil_serie ps WHERE ps.serie_id = @serie_id AND ps.codigo = 'CODIGO_SERIE-HR';

-- 5. Materiales necesarios
INSERT INTO material_configuracion (configuracion_id, producto_id, descripcion, cantidad_base, formula_cantidad)
SELECT
    @config_id,
    p.id,
    'Juego de rodamientos',
    2,
    'numHojas'
FROM producto p WHERE p.codigo = 'H001';

INSERT INTO material_configuracion (configuracion_id, producto_id, descripcion, cantidad_base, formula_cantidad)
SELECT
    @config_id,
    p.id,
    'Cierre ventana corredera',
    1,
    NULL
FROM producto p WHERE p.codigo = 'H002';

INSERT INTO material_configuracion (configuracion_id, producto_id, descripcion, cantidad_base, formula_cantidad)
SELECT
    @config_id,
    p.id,
    'Felpa (metros)',
    0,
    '((anchoTotal/100*2) + (altoVentana/100*2)) * numHojas'
FROM producto p WHERE p.codigo = 'A001';