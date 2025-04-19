-- Definición de Serie ALUPROM-21

-- Serie ALUPROM-21 Base
INSERT INTO serie_base (codigo, nombre, descripcion, tipo_material, precio_metro_base, descuento_serie, activa)
VALUES ('ALUPROM-21', 'Serie ALUPROM 21', 'Serie de aluminio para ventanas correderas', 'ALUMINIO', 15.0, 0, 1)
ON DUPLICATE KEY UPDATE nombre = nombre;

-- Obtener ID de la serie insertada
SET @serie_aluprom21_id = (SELECT id FROM serie_base WHERE codigo = 'ALUPROM-21' LIMIT 1);

-- Insertar detalles específicos de la serie de aluminio
INSERT INTO serie_aluminio (id, tipo_serie, espesor_minimo, espesor_maximo, color, rotura_puente, permite_persiana)
VALUES (@serie_aluprom21_id, 'CORREDERA', 1.5, 1.8, 'BLANCO', 0, 1)
ON DUPLICATE KEY UPDATE tipo_serie = tipo_serie;

-- Perfiles para ALUPROM-21
INSERT IGNORE INTO perfil_serie (serie_id, codigo, nombre, tipo_perfil, peso_metro, precio_metro, longitud_barra)
VALUES
    (@serie_aluprom21_id, 'ALUPROM21-ML', 'Marco Lateral', 'MARCO', 0.85, 10.5, 6000),
    (@serie_aluprom21_id, 'ALUPROM21-MS', 'Marco Superior', 'MARCO', 0.85, 10.5, 6000),
    (@serie_aluprom21_id, 'ALUPROM21-MI', 'Marco Inferior', 'MARCO', 0.85, 10.5, 6000),
    (@serie_aluprom21_id, 'ALUPROM21-HL', 'Hoja Lateral', 'HOJA', 0.65, 8.5, 6000),
    (@serie_aluprom21_id, 'ALUPROM21-HC', 'Hoja Central', 'HOJA', 0.65, 8.5, 6000),
    (@serie_aluprom21_id, 'ALUPROM21-HR', 'Hoja Ruleta', 'HOJA', 0.55, 7.5, 6000);

-- Descuentos generales para ALUPROM-21
INSERT IGNORE INTO descuento_perfil_serie (serie_id, tipo_perfil, descuento_milimetros, descripcion)
VALUES
    (@serie_aluprom21_id, 'MARCO', 0, 'Alto marco: Sin descuento'),
    (@serie_aluprom21_id, 'MARCO', 41, 'Ancho marco: medida - 4.1 cm'),
    (@serie_aluprom21_id, 'HOJA', 53, 'Alto hoja: altura - 5.3 cm'),
    (@serie_aluprom21_id, 'HOJA', 20, 'Ancho hoja: (ancho total / 2) - 2 cm');