-- Plantilla para configurar componentes estándar de una serie
-- Ubicación recomendada: server/src/main/resources/templates/template_componentes_serie.sql

-- Instrucciones:
-- 1. Reemplazar CODIGO_SERIE por el código de la serie (ej: ALUPROM-25)
-- 2. Ajustar las descripciones y cantidades según las especificaciones de la serie
-- 3. Ajustar las fórmulas de cálculo si es necesario
-- 4. Ejecutar el script tras haber creado la serie y sus configuraciones básicas

-- Obtener ID de la serie
SET @serie_id = (SELECT id FROM serie_base WHERE codigo = 'CODIGO_SERIE' LIMIT 1);

-- Si la serie no existe, salir con error
SELECT IF(@serie_id IS NULL,
          SIGNAL SQLSTATE '45000'
          SET MESSAGE_TEXT = 'La serie CODIGO_SERIE no existe. Crea la serie primero.',
          'Serie encontrada correctamente') AS resultado;

-- 1. Definir materiales base para la serie
-- Este primer bloque define los componentes estándar que se asocian a la serie

-- Herrajes básicos
INSERT INTO material_base_serie (serie_id, producto_id, descripcion, tipo_material, es_predeterminado, notas_tecnicas)
SELECT
    @serie_id,
    p.id,
    'Cierre de presión estándar para CODIGO_SERIE',
    'HERRAJE_BASICO',
    1,
    'Cierre de presión específico para ventanas correderas de la serie CODIGO_SERIE'
FROM producto p WHERE p.codigo = 'CODIGO_CIERRE';

INSERT INTO material_base_serie (serie_id, producto_id, descripcion, tipo_material, es_predeterminado, notas_tecnicas)
SELECT
    @serie_id,
    p.id,
    'Kit de rodamientos para CODIGO_SERIE',
    'HERRAJE_BASICO',
    1,
    'Kit completo con 2 rodamientos, soportes y tornillos de fijación'
FROM producto p WHERE p.codigo = 'CODIGO_RODAMIENTOS';

-- Tornillería
INSERT INTO material_base_serie (serie_id, producto_id, descripcion, tipo_material, es_predeterminado, notas_tecnicas)
SELECT
    @serie_id,
    p.id,
    'Tornillo específico para CODIGO_SERIE',
    'TORNILLERIA',
    1,
    'Tornillo autorroscante de medidas específicas para esta serie'
FROM producto p WHERE p.codigo = 'CODIGO_TORNILLO';

-- Accesorios
INSERT INTO material_base_serie (serie_id, producto_id, descripcion, tipo_material, es_predeterminado, notas_tecnicas)
SELECT
    @serie_id,
    p.id,
    'Felpa para estanqueidad CODIGO_SERIE',
    'ACCESORIO',
    1,
    'Felpa para mejorar estanqueidad en ventanas'
FROM producto p WHERE p.codigo = 'CODIGO_FELPA';

-- 2. Configurar materiales para las distintas configuraciones
-- Este segundo bloque asigna componentes específicos a cada configuración (2 hojas, 4 hojas, etc.)

-- Obtener IDs de las configuraciones para la serie
SET @config_2hojas_id = (SELECT id FROM plantilla_configuracion_serie
                         WHERE serie_id = @serie_id AND num_hojas = 2 LIMIT 1);

SET @config_4hojas_id = (SELECT id FROM plantilla_configuracion_serie
                         WHERE serie_id = @serie_id AND num_hojas = 4 LIMIT 1);

-- Configuración para 2 hojas
INSERT INTO material_configuracion (configuracion_id, producto_id, descripcion, cantidad_base, formula_cantidad)
SELECT
    @config_2hojas_id,
    p.id,
    'Cierres de presión',
    2,
    'Math.ceil(anchoTotal / 150)' -- Fórmula opcional basada en el ancho
FROM producto p WHERE p.codigo = 'CODIGO_CIERRE';

INSERT INTO material_configuracion (configuracion_id, producto_id, descripcion, cantidad_base, formula_cantidad)
SELECT
    @config_2hojas_id,
    p.id,
    'Kit de rodamientos',
    1,
    'numHojas / 2' -- Cantidad basada en número de hojas
FROM producto p WHERE p.codigo = 'CODIGO_RODAMIENTOS';

INSERT INTO material_configuracion (configuracion_id, producto_id, descripcion, cantidad_base, formula_cantidad)
SELECT
    @config_2hojas_id,
    p.id,
    'Tornillos específicos',
    18,
    '18 + Math.ceil(anchoTotal / 100)' -- Cantidad base más extra por ancho
FROM producto p WHERE p.codigo = 'CODIGO_TORNILLO';

INSERT INTO material_configuracion (configuracion_id, producto_id, descripcion, cantidad_base, formula_cantidad)
SELECT
    @config_2hojas_id,
    p.id,
    'Felpa (metros)',
    0,
    '((anchoTotal/100*2) + (altoVentana/100*2)) * numHojas * 1.1' -- Perímetro de las hojas con 10% extra
FROM producto p WHERE p.codigo = 'CODIGO_FELPA';

-- Configuración para 4 hojas (similar pero con cantidades diferentes)
-- Solo si existe la configuración para 4 hojas
IF @config_4hojas_id IS NOT NULL THEN
    INSERT INTO material_configuracion (configuracion_id, producto_id, descripcion, cantidad_base, formula_cantidad)
    SELECT
        @config_4hojas_id,
        p.id,
        'Cierres de presión',
        4,
        'Math.ceil(anchoTotal / 150) * 2'
    FROM producto p WHERE p.codigo = 'CODIGO_CIERRE';

    INSERT INTO material_configuracion (configuracion_id, producto_id, descripcion, cantidad_base, formula_cantidad)
    SELECT
        @config_4hojas_id,
        p.id,
        'Kit de rodamientos',
        2,
        'numHojas / 2'
    FROM producto p WHERE p.codigo = 'CODIGO_RODAMIENTOS';

    INSERT INTO material_configuracion (configuracion_id, producto_id, descripcion, cantidad_base, formula_cantidad)
    SELECT
        @config_4hojas_id,
        p.id,
        'Tornillos específicos',
        26,
        '26 + Math.ceil(anchoTotal / 75)'
    FROM producto p WHERE p.codigo = 'CODIGO_TORNILLO';

    INSERT INTO material_configuracion (configuracion_id, producto_id, descripcion, cantidad_base, formula_cantidad)
    SELECT
        @config_4hojas_id,
        p.id,
        'Felpa (metros)',
        0,
        '((anchoTotal/100*4) + (altoVentana/100*4)) * 1.1'
    FROM producto p WHERE p.codigo = 'CODIGO_FELPA';
END IF;

-- Mostrar resumen de la configuración creada
SELECT 'Componentes configurados correctamente para la serie CODIGO_SERIE' AS resultado;
SELECT COUNT(*) AS 'Materiales base configurados' FROM material_base_serie WHERE serie_id = @serie_id;
SELECT COUNT(*) AS 'Materiales para 2 hojas' FROM material_configuracion WHERE configuracion_id = @config_2hojas_id;
IF @config_4hojas_id IS NOT NULL THEN
    SELECT COUNT(*) AS 'Materiales para 4 hojas' FROM material_configuracion WHERE configuracion_id = @config_4hojas_id;
END IF;