-- Productos base necesarios para todas las configuraciones

-- Crear productos básicos necesarios
INSERT IGNORE INTO producto (codigo, nombre, descripcion, categoria_id, tipo, unidad_medida, precio_compra, margen_beneficio, precio_venta, aplicar_iva, stock_minimo, stock_actual, activo)
VALUES
    -- Herrajes
    ('H001', 'Juego rodamientos corredera', 'Juego completo de rodamientos para ventana corredera',
     (SELECT id FROM categoria_producto WHERE nombre = 'Herrajes' LIMIT 1),
     'HERRAJE', 'UNIDADES', 4.50, 30, 5.85, 1, 20, 50, 1),

    ('H002', 'Cierre ventana corredera', 'Cierre completo para ventana corredera',
     (SELECT id FROM categoria_producto WHERE nombre = 'Herrajes' LIMIT 1),
     'HERRAJE', 'UNIDADES', 7.80, 30, 10.14, 1, 10, 25, 1),

    -- Tornillería
    ('T001', 'Tornillo autorroscante 3.5x16', 'Tornillo autorroscante para aluminio',
     (SELECT id FROM categoria_producto WHERE nombre = 'Tornillería' LIMIT 1),
     'TORNILLO', 'UNIDADES', 0.02, 30, 0.026, 1, 500, 1000, 1),

    ('T002', 'Tornillo autorroscante 3.9x19', 'Tornillo autorroscante para aluminio',
     (SELECT id FROM categoria_producto WHERE nombre = 'Tornillería' LIMIT 1),
     'TORNILLO', 'UNIDADES', 0.03, 30, 0.039, 1, 500, 1000, 1),

    -- Accesorios
    ('A001', 'Felpa 5mm (metro)', 'Felpa para ventanas correderas',
     (SELECT id FROM categoria_producto WHERE nombre = 'Accesorios' LIMIT 1),
     'ACCESORIO', 'METROS', 0.45, 30, 0.585, 1, 50, 200, 1),

    ('A002', 'Goma EPDM (metro)', 'Goma para estanqueidad',
     (SELECT id FROM categoria_producto WHERE nombre = 'Accesorios' LIMIT 1),
     'GOMA', 'METROS', 0.55, 30, 0.715, 1, 50, 200, 1),

    -- Vidrios
    ('V001', 'Vidrio simple 4mm', 'Vidrio simple transparente 4mm',
     (SELECT id FROM categoria_producto WHERE nombre = 'Vidrios' LIMIT 1),
     'VIDRIO', 'METROS_CUADRADOS', 15.00, 30, 19.50, 1, 10, 50, 1),

    ('V002', 'Vidrio doble 4-12-4', 'Vidrio doble cámara 4-12-4',
     (SELECT id FROM categoria_producto WHERE nombre = 'Vidrios' LIMIT 1),
     'VIDRIO', 'METROS_CUADRADOS', 25.00, 30, 32.50, 1, 10, 30, 1);