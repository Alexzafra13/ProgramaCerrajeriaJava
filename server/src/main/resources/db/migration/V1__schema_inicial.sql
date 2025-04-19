-- Esquema inicial para la base de datos (migraciones Flyway)

-- Tabla de usuarios
CREATE TABLE IF NOT EXISTS usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100),
    email VARCHAR(100),
    telefono VARCHAR(20),
    rol VARCHAR(20) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion DATETIME NOT NULL,
    ultimo_acceso DATETIME,
    preferencias_visuales TEXT
);

-- Tabla de categorías de productos
CREATE TABLE IF NOT EXISTS categoria_producto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    categoria_padre_id BIGINT,
    FOREIGN KEY (categoria_padre_id) REFERENCES categoria_producto(id)
);

-- Tabla de series base
CREATE TABLE IF NOT EXISTS serie_base (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    tipo_material VARCHAR(20) NOT NULL,
    precio_metro_base DECIMAL(10,2) NOT NULL,
    descuento_serie DECIMAL(5,2) DEFAULT 0,
    activa BOOLEAN NOT NULL DEFAULT TRUE
);

-- Tabla de series de aluminio
CREATE TABLE IF NOT EXISTS serie_aluminio (
    id BIGINT NOT NULL PRIMARY KEY,
    tipo_serie VARCHAR(20) NOT NULL,
    espesor_minimo DECIMAL(5,2) NOT NULL,
    espesor_maximo DECIMAL(5,2) NOT NULL,
    color VARCHAR(50),
    rotura_puente BOOLEAN NOT NULL DEFAULT FALSE,
    permite_persiana BOOLEAN NOT NULL DEFAULT FALSE,
    parametros_tecnicos JSON,
    FOREIGN KEY (id) REFERENCES serie_base(id)
);

-- Tabla de series de hierro
CREATE TABLE IF NOT EXISTS serie_hierro (
    id BIGINT NOT NULL PRIMARY KEY,
    espesor DECIMAL(5,2) NOT NULL,
    galvanizado BOOLEAN NOT NULL DEFAULT FALSE,
    tipo_acabado VARCHAR(50),
    FOREIGN KEY (id) REFERENCES serie_base(id)
);

-- Tabla de perfiles de serie
CREATE TABLE IF NOT EXISTS perfil_serie (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    serie_id BIGINT NOT NULL,
    codigo VARCHAR(50) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    tipo_perfil VARCHAR(20) NOT NULL,
    peso_metro DECIMAL(10,3) NOT NULL,
    precio_metro DECIMAL(10,2) NOT NULL,
    longitud_barra INT,
    propiedades JSON,
    FOREIGN KEY (serie_id) REFERENCES serie_base(id)
);

-- Tabla de descuentos de perfiles
CREATE TABLE IF NOT EXISTS descuento_perfil_serie (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    serie_id BIGINT NOT NULL,
    tipo_perfil VARCHAR(20) NOT NULL,
    descuento_milimetros INT NOT NULL,
    descripcion VARCHAR(255),
    formula_calculo TEXT,
    FOREIGN KEY (serie_id) REFERENCES serie_base(id)
);

-- Tabla de productos
CREATE TABLE IF NOT EXISTS producto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    codigo_proveedor VARCHAR(50),
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    categoria_id BIGINT,
    tipo VARCHAR(20) NOT NULL,
    unidad_medida VARCHAR(20) NOT NULL,
    precio_compra DECIMAL(10,2) NOT NULL,
    margen_beneficio DECIMAL(5,2) DEFAULT 0,
    precio_venta DECIMAL(10,2) NOT NULL,
    aplicar_iva BOOLEAN NOT NULL DEFAULT TRUE,
    stock_minimo INT DEFAULT 0,
    stock_actual INT DEFAULT 0,
    ubicacion VARCHAR(100),
    serie_id BIGINT,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    imagen VARCHAR(255),
    unidades_por_caja INT,
    medida VARCHAR(50),
    propiedades JSON,
    FOREIGN KEY (categoria_id) REFERENCES categoria_producto(id),
    FOREIGN KEY (serie_id) REFERENCES serie_base(id)
);

-- Tabla de empaques de productos
CREATE TABLE IF NOT EXISTS empaque_producto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    codigo VARCHAR(50) NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    unidades_por_empaque INT NOT NULL,
    cantidad_empaques INT NOT NULL,
    unidades_sueltas INT,
    stock_total_unidades INT NOT NULL,
    FOREIGN KEY (producto_id) REFERENCES producto(id)
);

-- Tabla de clientes
CREATE TABLE IF NOT EXISTS cliente (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    tipo_cliente VARCHAR(20) NOT NULL,
    nombre VARCHAR(100),
    apellidos VARCHAR(100),
    razon_social VARCHAR(150),
    nif_cif VARCHAR(20) UNIQUE,
    direccion_fiscal VARCHAR(255),
    direccion_envio VARCHAR(255),
    codigo_postal VARCHAR(10),
    localidad VARCHAR(100),
    provincia VARCHAR(100),
    pais VARCHAR(50) DEFAULT 'España',
    telefono1 VARCHAR(20),
    telefono2 VARCHAR(20),
    email VARCHAR(100),
    web VARCHAR(100),
    descuento DECIMAL(5,2) DEFAULT 0,
    fecha_alta DATE NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    observaciones TEXT
);

-- Tabla de estados de trabajo
CREATE TABLE IF NOT EXISTS estado_trabajo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(50) NOT NULL,
    descripcion TEXT,
    color VARCHAR(20) DEFAULT '#808080',
    orden INT NOT NULL,
    notificaciones JSON,
    acciones JSON,
    requiere_aprobacion BOOLEAN DEFAULT FALSE,
    es_final BOOLEAN DEFAULT FALSE
);

-- Tabla de presupuestos
CREATE TABLE IF NOT EXISTS presupuesto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero VARCHAR(20) NOT NULL UNIQUE,
    fecha_creacion DATETIME NOT NULL,
    fecha_validez DATE NOT NULL,
    cliente_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    estado VARCHAR(20) NOT NULL,
    observaciones TEXT,
    base_imponible DECIMAL(10,2) NOT NULL DEFAULT 0,
    importe_iva DECIMAL(10,2) NOT NULL DEFAULT 0,
    total_presupuesto DECIMAL(10,2) NOT NULL DEFAULT 0,
    descuento DECIMAL(5,2) DEFAULT 0,
    tiempo_estimado INT,
    motivo_rechazo TEXT,
    direccion_obra VARCHAR(255),
    referencia VARCHAR(100),
    FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

-- Tabla de líneas de presupuesto
CREATE TABLE IF NOT EXISTS linea_presupuesto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    presupuesto_id BIGINT NOT NULL,
    orden INT NOT NULL,
    tipo_presupuesto VARCHAR(30),
    descripcion VARCHAR(255) NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    ancho INT,
    alto INT,
    medidas VARCHAR(100),
    serie_id BIGINT,
    precio_unitario DECIMAL(10,2) NOT NULL DEFAULT 0,
    descuento DECIMAL(5,2) DEFAULT 0,
    importe DECIMAL(10,2) NOT NULL DEFAULT 0,
    detalles_tecnicos JSON,
    tipo_vidrio VARCHAR(50),
    color_perfil VARCHAR(50),
    acabado_especial VARCHAR(100),
    incluye_persiana BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (presupuesto_id) REFERENCES presupuesto(id),
    FOREIGN KEY (serie_id) REFERENCES serie_base(id)
);

-- Tabla de trabajos
CREATE TABLE IF NOT EXISTS trabajo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    presupuesto_id BIGINT,
    cliente_id BIGINT NOT NULL,
    fecha_creacion DATETIME NOT NULL,
    fecha_programada DATE,
    fecha_inicio DATETIME,
    fecha_finalizacion DATETIME,
    direccion_instalacion VARCHAR(255),
    estado_id BIGINT,
    prioridad VARCHAR(20) NOT NULL,
    usuario_asignado_id BIGINT,
    observaciones TEXT,
    horas_reales INT,
    fotos_trabajo_terminado TEXT,
    firma_cliente VARCHAR(255),
    FOREIGN KEY (presupuesto_id) REFERENCES presupuesto(id),
    FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    FOREIGN KEY (estado_id) REFERENCES estado_trabajo(id),
    FOREIGN KEY (usuario_asignado_id) REFERENCES usuario(id)
);

-- Tabla de cambios de estado
CREATE TABLE IF NOT EXISTS cambio_estado (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    trabajo_id BIGINT NOT NULL,
    estado_anterior_id BIGINT,
    estado_nuevo_id BIGINT NOT NULL,
    fecha_cambio DATETIME NOT NULL,
    usuario_id BIGINT NOT NULL,
    observaciones TEXT,
    motivo_cambio VARCHAR(255),
    automatico BOOLEAN DEFAULT FALSE,
    datos_asociados JSON,
    FOREIGN KEY (trabajo_id) REFERENCES trabajo(id),
    FOREIGN KEY (estado_anterior_id) REFERENCES estado_trabajo(id),
    FOREIGN KEY (estado_nuevo_id) REFERENCES estado_trabajo(id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

-- Tabla de material asignado
CREATE TABLE IF NOT EXISTS material_asignado (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    trabajo_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad_asignada INT NOT NULL,
    cantidad_usada INT,
    observaciones TEXT,
    FOREIGN KEY (trabajo_id) REFERENCES trabajo(id),
    FOREIGN KEY (producto_id) REFERENCES producto(id)
);

-- Tabla de plantillas de cálculo
CREATE TABLE IF NOT EXISTS plantilla_calculo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    serie_id BIGINT NOT NULL,
    tipo_modelo VARCHAR(30) NOT NULL,
    numero_hojas INT,
    tiene_persianas BOOLEAN DEFAULT FALSE,
    activa BOOLEAN NOT NULL DEFAULT TRUE,
    configuracion JSON,
    imagen VARCHAR(255),
    FOREIGN KEY (serie_id) REFERENCES serie_base(id)
);

-- Tabla de cálculos de ventana
CREATE TABLE IF NOT EXISTS calculo_ventana (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    linea_presupuesto_id BIGINT,
    serie_id BIGINT NOT NULL,
    plantilla_id BIGINT,
    anchura INT NOT NULL,
    altura INT NOT NULL,
    anchura_total INT,
    altura_total INT,
    tiene_persianas BOOLEAN DEFAULT FALSE,
    altura_cajon_persiana INT,
    fecha_calculo DATETIME NOT NULL,
    usuario_id BIGINT,
    resultado_calculo JSON,
    observaciones TEXT,
    FOREIGN KEY (linea_presupuesto_id) REFERENCES linea_presupuesto(id),
    FOREIGN KEY (serie_id) REFERENCES serie_base(id),
    FOREIGN KEY (plantilla_id) REFERENCES plantilla_calculo(id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

-- Tabla de cortes de ventana
CREATE TABLE IF NOT EXISTS corte_ventana (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    calculo_ventana_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    tipo_perfil VARCHAR(20),
    longitud INT NOT NULL,
    cantidad INT NOT NULL,
    angulo1 DOUBLE DEFAULT 90.0,
    angulo2 DOUBLE DEFAULT 90.0,
    descripcion VARCHAR(255),
    ubicacion VARCHAR(100),
    descuento_aplicado INT,
    grupo_corte VARCHAR(50),
    FOREIGN KEY (calculo_ventana_id) REFERENCES calculo_ventana(id),
    FOREIGN KEY (producto_id) REFERENCES producto(id)
);

-- Tabla de materiales adicionales
CREATE TABLE IF NOT EXISTS material_adicional (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    calculo_ventana_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    descripcion VARCHAR(255),
    unidades_individuales INT,
    cajas INT,
    unidades_por_caja INT,
    FOREIGN KEY (calculo_ventana_id) REFERENCES calculo_ventana(id),
    FOREIGN KEY (producto_id) REFERENCES producto(id)
);

-- Tabla de movimientos de stock
CREATE TABLE IF NOT EXISTS movimiento_stock (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    fecha DATETIME NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2),
    documento_origen VARCHAR(100),
    documento_id BIGINT,
    usuario_id BIGINT,
    observaciones TEXT,
    FOREIGN KEY (producto_id) REFERENCES producto(id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

-- Tabla de retales
CREATE TABLE IF NOT EXISTS retal (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    longitud INT NOT NULL,
    fecha_creacion DATETIME NOT NULL,
    trabajo_id BIGINT,
    ubicacion VARCHAR(100),
    estado VARCHAR(20) NOT NULL,
    motivo_descarte VARCHAR(20),
    usuario_id BIGINT,
    observaciones TEXT,
    FOREIGN KEY (producto_id) REFERENCES producto(id),
    FOREIGN KEY (trabajo_id) REFERENCES trabajo(id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);