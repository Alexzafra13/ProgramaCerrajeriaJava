-- Template para crear una nueva serie de aluminio completa
-- Uso: Reemplazar CODIGO_SERIE, NOMBRE_SERIE, etc. con los valores reales

-- 1. Serie base
INSERT INTO serie_base (codigo, nombre, descripcion, tipo_material, precio_metro_base, descuento_serie, activa)
VALUES ('CODIGO_SERIE', 'NOMBRE_SERIE', 'DESCRIPCION_SERIE', 'ALUMINIO', PRECIO_BASE, 0, 1);

-- 2. Obtener ID de la serie
SET @serie_id = (SELECT id FROM serie_base WHERE codigo = 'CODIGO_SERIE' LIMIT 1);

-- 3. Serie aluminio específica
INSERT INTO serie_aluminio (id, tipo_serie, espesor_minimo, espesor_maximo, color, rotura_puente, permite_persiana)
VALUES (@serie_id, 'TIPO_SERIE', 1.5, 1.8, 'BLANCO', ROTURA_PUENTE, PERMITE_PERSIANA);

-- 4. Perfiles básicos
-- 4.1 Marcos
INSERT INTO perfil_serie (serie_id, codigo, nombre, tipo_perfil, peso_metro, precio_metro, longitud_barra)
VALUES
    (@serie_id, 'CODIGO_SERIE-ML', 'Marco Lateral', 'MARCO', 0.85, 10.5, 6000),
    (@serie_id, 'CODIGO_SERIE-MS', 'Marco Superior', 'MARCO', 0.85, 10.5, 6000),
    (@serie_id, 'CODIGO_SERIE-MI', 'Marco Inferior', 'MARCO', 0.85, 10.5, 6000);

-- 4.2 Hojas (depende del tipo de serie)
-- Si es corredera:
INSERT INTO perfil_serie (serie_id, codigo, nombre, tipo_perfil, peso_metro, precio_metro, longitud_barra)
VALUES
    (@serie_id, 'CODIGO_SERIE-HL', 'Hoja Lateral', 'HOJA', 0.65, 8.5, 6000),
    (@serie_id, 'CODIGO_SERIE-HC', 'Hoja Central', 'HOJA', 0.65, 8.5, 6000),
    (@serie_id, 'CODIGO_SERIE-HR', 'Hoja Ruleta', 'HOJA', 0.55, 7.5, 6000);

-- Si es abatible:
-- INSERT INTO perfil_serie (serie_id, codigo, nombre, tipo_perfil, peso_metro, precio_metro, longitud_barra)
-- VALUES
--    (@serie_id, 'CODIGO_SERIE-HA', 'Hoja Abatible', 'HOJA', 0.70, 9.0, 6000),
--    (@serie_id, 'CODIGO_SERIE-JP', 'Junquillo Plano', 'JUNQUILLO', 0.30, 4.5, 6000),
--    (@serie_id, 'CODIGO_SERIE-JC', 'Junquillo Curvo', 'JUNQUILLO', 0.35, 5.0, 6000);

-- 5. Descuentos estándar
INSERT INTO descuento_perfil_serie (serie_id, tipo_perfil, descuento_milimetros, descripcion)
VALUES
    (@serie_id, 'MARCO', 0, 'Alto marco: Sin descuento'),
    (@serie_id, 'MARCO', 41, 'Ancho marco: medida - 4.1 cm');

-- Descuentos específicos según tipo
-- Si es corredera:
INSERT INTO descuento_perfil_serie (serie_id, tipo_perfil, descuento_milimetros, descripcion)
VALUES
    (@serie_id, 'HOJA', 53, 'Alto hoja: altura - 5.3 cm'),
    (@serie_id, 'HOJA', 20, 'Ancho hoja: (ancho total / 2) - 2 cm');

-- Si es abatible:
-- INSERT INTO descuento_perfil_serie (serie_id, tipo_perfil, descuento_milimetros, descripcion)
-- VALUES
--    (@serie_id, 'HOJA', 48, 'Alto hoja: altura - 4.8 cm'),
--    (@serie_id, 'HOJA', 48, 'Ancho hoja: ancho - 4.8 cm');

-- 6. Productos en inventario para esta serie
-- 6.1 Perfiles para inventario
INSERT INTO producto (codigo, nombre, descripcion, categoria_id, tipo, unidad_medida,
                     precio_compra, precio_venta, stock_minimo, stock_actual, serie_id)
SELECT
    ps.codigo,
    CONCAT(ps.nombre, ' ', sb.codigo),
    CONCAT('Perfil ', ps.nombre, ' para serie ', sb.codigo),
    (SELECT id FROM categoria_producto WHERE nombre = 'Perfiles' LIMIT 1),
    'PERFIL_ALUMINIO',
    'METROS',
    ps.precio_metro,
    ps.precio_metro * 1.3, -- 30% margen
    10, -- Stock mínimo
    0,  -- Stock actual inicial
    ps.serie_id
FROM perfil_serie ps
JOIN serie_base sb ON ps.serie_id = sb.id
WHERE sb.codigo = 'CODIGO_SERIE';

-- 7. Plantillas de configuración
-- 7.1 Configuración para ventana de 2 hojas
INSERT INTO plantilla_configuracion_serie (serie_id, nombre, num_hojas, activa, descripcion)
VALUES (@serie_id, 'NOMBRE_SERIE 2 Hojas', 2, TRUE, 'Configuración para NOMBRE_SERIE de 2 hojas');

-- 7.2 Obtener ID de la configuración
SET @config_2hojas_id = (SELECT id FROM plantilla_configuracion_serie
                         WHERE serie_id = @serie_id AND num_hojas = 2 LIMIT 1);

-- 7.3 Perfiles para esta configuración (ejemplo para corredera)
INSERT INTO perfil_configuracion (configuracion_id, perfil_id, tipo_perfil, cantidad, descuento_cm)
SELECT
    @config_2hojas_id,
    ps.id,
    'MARCO_LATERAL',
    2,
    0
FROM perfil_serie ps WHERE ps.serie_id = @serie_id AND ps.codigo = 'CODIGO_SERIE-ML';

-- Continúa con más perfiles y materiales