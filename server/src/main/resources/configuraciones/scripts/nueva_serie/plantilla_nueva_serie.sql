-- PLANTILLA PARA AÑADIR UNA NUEVA SERIE
-- Reemplazar NOMBRE_SERIE, CODIGO_SERIE y OTROS_VALORES con la información real

-- 1. Insertar información básica de la serie
INSERT INTO serie_base (codigo, nombre, descripcion, tipo_material, precio_metro_base, descuento_serie, activa)
VALUES
    ('CODIGO_SERIE', 'NOMBRE_SERIE', 'DESCRIPCION_SERIE', 'TIPO_MATERIAL', PRECIO_BASE, DESCUENTO, TRUE);

-- 2. Obtener ID de la serie insertada
SET @serie_id = LAST_INSERT_ID();

-- 3. Insertar detalles específicos (para serie de aluminio)
INSERT INTO serie_aluminio (id, tipo_serie, espesor_minimo, espesor_maximo, color, rotura_puente, permite_persiana)
VALUES (@serie_id, 'TIPO_SERIE', ESPESOR_MIN, ESPESOR_MAX, 'COLOR', ROTURA_PUENTE, PERMITE_PERSIANA);

-- 4. Añadir perfiles para la serie
-- Repetir esta sección para cada perfil
INSERT INTO perfil_serie (serie_id, codigo, nombre, tipo_perfil, peso_metro, precio_metro, longitud_barra)
VALUES
    (@serie_id, 'CODIGO_PERFIL_1', 'NOMBRE_PERFIL_1', 'TIPO_PERFIL', PESO, PRECIO, LONGITUD),
    (@serie_id, 'CODIGO_PERFIL_2', 'NOMBRE_PERFIL_2', 'TIPO_PERFIL', PESO, PRECIO, LONGITUD),
    -- Añadir más perfiles según sea necesario
    ;

-- 5. Definir descuentos estándar para la serie
INSERT INTO descuento_perfil_serie (serie_id, tipo_perfil, descuento_milimetros, descripcion)
VALUES
    (@serie_id, 'TIPO_PERFIL_1', DESCUENTO_1, 'DESCRIPCION_DESCUENTO_1'),
    (@serie_id, 'TIPO_PERFIL_2', DESCUENTO_2, 'DESCRIPCION_DESCUENTO_2'),
    -- Añadir más descuentos según sea necesario
    ;

-- 6. No olvides crear las configuraciones específicas para esta serie
-- Ver plantilla_nueva_configuracion.sql