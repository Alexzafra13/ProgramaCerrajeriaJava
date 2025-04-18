-- Usuario administrador por defecto
INSERT INTO usuario (username, password, nombre, apellidos, email, telefono, rol, activo, fecha_creacion)
VALUES ('admin', '$2a$10$uVXfHzJuiGbsjGEwgL4GM.lxRpZbp.F.ERVNoRGaO9U/INh72O2OS', 'Administrador', 'Sistema', 'admin@sistema.com', '600000000', 'ADMIN', 1, NOW());

-- Serie ALUPROM-21
INSERT INTO serie_base (codigo, nombre, descripcion, tipo_material, precio_metro_base, descuento_serie, activa)
VALUES ('ALUPROM-21', 'Serie Aluprom 21', 'Serie de aluminio para ventanas correderas de dos hojas', 'ALUMINIO', 15.0, 0, 1);

-- Obtener ID de la serie ALUPROM-21
SET @aluprom21_id = LAST_INSERT_ID();

-- Detalles de serie ALUPROM-21
INSERT INTO serie_aluminio (id, tipo_serie, espesor_minimo, espesor_maximo, color, rotura_puente, permite_persiana)
VALUES (@aluprom21_id, 'CORREDERA', 1.5, 1.8, 'BLANCO', 0, 1);

-- Perfiles para ALUPROM-21
INSERT INTO perfil_serie (serie_id, codigo, nombre, tipo_perfil, peso_metro, precio_metro, longitud_barra)
VALUES
    (@aluprom21_id, 'ALUPROM21-ML', 'Marco Lateral', 'MARCO', 0.85, 10.5, 6000),
    (@aluprom21_id, 'ALUPROM21-MS', 'Marco Superior', 'MARCO', 0.85, 10.5, 6000),
    (@aluprom21_id, 'ALUPROM21-MI', 'Marco Inferior', 'MARCO', 0.85, 10.5, 6000),
    (@aluprom21_id, 'ALUPROM21-HL', 'Hoja Lateral', 'HOJA', 0.65, 8.5, 6000),
    (@aluprom21_id, 'ALUPROM21-HC', 'Hoja Central', 'HOJA', 0.65, 8.5, 6000),
    (@aluprom21_id, 'ALUPROM21-HR', 'Hoja Ruleta', 'HOJA', 0.55, 7.5, 6000);

-- Descuentos para ALUPROM-21
INSERT INTO descuento_perfil_serie (serie_id, tipo_perfil, descuento_milimetros, descripcion)
VALUES
    (@aluprom21_id, 'MARCO', 0, 'Alto marco: Sin descuento'),
    (@aluprom21_id, 'MARCO', 41, 'Ancho marco: medida - 4.1 cm'),
    (@aluprom21_id, 'HOJA', 53, 'Alto hoja: altura - 5.3 cm'),
    (@aluprom21_id, 'HOJA', 20, 'Ancho hoja: (ancho total / 2) - 2 cm');