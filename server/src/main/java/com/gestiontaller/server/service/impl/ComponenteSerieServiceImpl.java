package com.gestiontaller.server.service.impl;

import com.gestiontaller.common.dto.calculo.MaterialAdicionalDTO;
import com.gestiontaller.common.dto.calculo.ResultadoCalculoDTO;
import com.gestiontaller.common.dto.serie.MaterialBaseSerieDTO;
import com.gestiontaller.server.mapper.MaterialBaseSerieMapper;
import com.gestiontaller.server.model.configuracion.MaterialConfiguracion;
import com.gestiontaller.server.model.serie.MaterialBaseSerie;
import com.gestiontaller.server.model.serie.SerieBase;
import com.gestiontaller.common.model.inventario.TipoMovimiento;
import com.gestiontaller.server.repository.configuracion.MaterialConfiguracionRepository;
import com.gestiontaller.server.repository.configuracion.PlantillaConfiguracionSerieRepository;
import com.gestiontaller.server.repository.producto.ProductoRepository;
import com.gestiontaller.server.repository.serie.MaterialBaseSerieRepository;
import com.gestiontaller.server.repository.serie.SerieBaseRepository;
import com.gestiontaller.server.service.interfaces.ComponenteSerieService;
import com.gestiontaller.server.service.interfaces.InventarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ComponenteSerieServiceImpl implements ComponenteSerieService {

    private static final Logger logger = LoggerFactory.getLogger(ComponenteSerieServiceImpl.class);

    private final MaterialBaseSerieRepository materialBaseRepo;
    private final SerieBaseRepository serieRepo;
    private final ProductoRepository productoRepo;
    private final MaterialConfiguracionRepository materialConfigRepo;
    private final PlantillaConfiguracionSerieRepository plantillaConfigRepo;
    private final MaterialBaseSerieMapper materialBaseMapper;
    private final InventarioService inventarioService;
    private final FormulaEvaluatorService formulaEvaluator;

    @Autowired
    public ComponenteSerieServiceImpl(
            MaterialBaseSerieRepository materialBaseRepo,
            SerieBaseRepository serieRepo,
            ProductoRepository productoRepo,
            MaterialConfiguracionRepository materialConfigRepo,
            PlantillaConfiguracionSerieRepository plantillaConfigRepo,
            MaterialBaseSerieMapper materialBaseMapper,
            InventarioService inventarioService,
            FormulaEvaluatorService formulaEvaluator) {
        this.materialBaseRepo = materialBaseRepo;
        this.serieRepo = serieRepo;
        this.productoRepo = productoRepo;
        this.materialConfigRepo = materialConfigRepo;
        this.plantillaConfigRepo = plantillaConfigRepo;
        this.materialBaseMapper = materialBaseMapper;
        this.inventarioService = inventarioService;
        this.formulaEvaluator = formulaEvaluator;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaterialBaseSerieDTO> obtenerMaterialesBasePorSerie(Long serieId) {
        logger.debug("Obteniendo materiales base para serie ID: {}", serieId);
        return materialBaseRepo.findBySerieId(serieId).stream()
                .map(materialBaseMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaterialBaseSerieDTO> obtenerMaterialesBasePorTipo(Long serieId, String tipoMaterial) {
        logger.debug("Obteniendo materiales de tipo {} para serie ID: {}", tipoMaterial, serieId);
        return materialBaseRepo.findBySerieIdAndTipoMaterial(serieId, tipoMaterial).stream()
                .map(materialBaseMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MaterialBaseSerieDTO guardarMaterialBase(MaterialBaseSerieDTO materialDTO) {
        logger.debug("Guardando material base: {} para serie ID: {}",
                materialDTO.getNombreProducto(), materialDTO.getSerieId());

        // Verificar serie
        SerieBase serie = serieRepo.findById(materialDTO.getSerieId())
                .orElseThrow(() -> new RuntimeException("Serie no encontrada"));

        MaterialBaseSerie material;
        if (materialDTO.getId() != null) {
            material = materialBaseRepo.findById(materialDTO.getId())
                    .orElse(materialBaseMapper.toEntity(materialDTO));
            materialBaseMapper.updateEntityFromDto(materialDTO, material);
        } else {
            material = materialBaseMapper.toEntity(materialDTO);
        }

        MaterialBaseSerie savedMaterial = materialBaseRepo.save(material);
        logger.info("Material base guardado con ID: {}", savedMaterial.getId());

        return materialBaseMapper.toDto(savedMaterial);
    }

    @Override
    @Transactional
    public void eliminarMaterialBase(Long id) {
        logger.debug("Eliminando material base con ID: {}", id);
        if (!materialBaseRepo.existsById(id)) {
            throw new RuntimeException("Material base no encontrado");
        }
        materialBaseRepo.deleteById(id);
        logger.info("Material base eliminado con ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, List<MaterialBaseSerieDTO>> calcularComponentesParaVentana(
            Long serieId, Integer numHojas, Integer anchoTotal, Integer altoTotal,
            Boolean incluyePersiana) {

        logger.debug("Calculando componentes para ventana: Serie ID: {}, {} hojas, {}x{} mm",
                serieId, numHojas, anchoTotal, altoTotal);

        Map<String, List<MaterialBaseSerieDTO>> componentesPorCategoria = new HashMap<>();

        // Obtener configuración específica para esta serie y número de hojas
        var configuracion = plantillaConfigRepo.findBySerieIdAndNumHojas(serieId, numHojas)
                .orElseThrow(() -> new RuntimeException("No existe configuración para esta serie y número de hojas"));

        // Obtener materiales configurados
        List<MaterialConfiguracion> materiales = materialConfigRepo.findByConfiguracionId(configuracion.getId());

        // Variables para evaluación de fórmulas
        Map<String, Object> variables = new HashMap<>();
        variables.put("anchoTotal", anchoTotal / 10.0); // mm a cm
        variables.put("altoTotal", altoTotal / 10.0);   // mm a cm
        variables.put("numHojas", numHojas);
        variables.put("incluyePersiana", incluyePersiana);

        // Procesar materiales
        for (MaterialConfiguracion material : materiales) {
            // Preparar DTO
            MaterialBaseSerieDTO materialDTO = new MaterialBaseSerieDTO();
            materialDTO.setSerieId(serieId);
            materialDTO.setDescripcion(material.getDescripcion());

            // Si hay producto asociado, obtener sus datos
            if (material.getProducto() != null) {
                materialDTO.setProductoId(material.getProducto().getId());
                materialDTO.setCodigoProducto(material.getProducto().getCodigo());
                materialDTO.setNombreProducto(material.getProducto().getNombre());
                materialDTO.setPrecioUnitario(material.getProducto().getPrecioVenta());
                materialDTO.setStockActual(material.getProducto().getStockActual());
            }

            // Calcular cantidad
            int cantidad = material.getCantidadBase();
            if (material.getFormulaCantidad() != null && !material.getFormulaCantidad().isEmpty()) {
                Integer valorCalculado = formulaEvaluator.evaluarFormulaEntero(
                        material.getFormulaCantidad(), variables);
                if (valorCalculado != null) {
                    cantidad = valorCalculado;
                }
            }

            // Categorizar el material
            String categoria = determinarCategoria(material);

            // Añadir a la lista correspondiente
            componentesPorCategoria
                    .computeIfAbsent(categoria, k -> new ArrayList<>())
                    .add(materialDTO);
        }

        // También incluir materiales base de la serie que no estén ya en la configuración
        List<MaterialBaseSerie> materialesBase = materialBaseRepo.findBySerieIdAndEsPredeterminadoTrue(serieId);

        for (MaterialBaseSerie materialBase : materialesBase) {
            // Verificar si este material ya está incluido en la configuración
            boolean yaIncluido = false;
            for (List<MaterialBaseSerieDTO> lista : componentesPorCategoria.values()) {
                if (lista.stream().anyMatch(m ->
                        m.getProductoId() != null &&
                                m.getProductoId().equals(materialBase.getProducto().getId()))) {
                    yaIncluido = true;
                    break;
                }
            }

            if (!yaIncluido) {
                MaterialBaseSerieDTO dto = materialBaseMapper.toDto(materialBase);

                // Añadir a la categoría correspondiente
                componentesPorCategoria
                        .computeIfAbsent(materialBase.getTipoMaterial(), k -> new ArrayList<>())
                        .add(dto);
            }
        }

        return componentesPorCategoria;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verificarDisponibilidadComponentes(ResultadoCalculoDTO resultado) {
        // Verificar disponibilidad de cada material adicional
        for (MaterialAdicionalDTO material : resultado.getMaterialesAdicionales()) {
            if (material.getProductoId() == null) {
                continue; // Materiales sin producto asociado no afectan al stock
            }

            var producto = productoRepo.findById(material.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + material.getProductoId()));

            if (producto.getStockActual() < material.getCantidad()) {
                logger.warn("Stock insuficiente para el producto: {} (Requerido: {}, Disponible: {})",
                        producto.getCodigo(), material.getCantidad(), producto.getStockActual());
                return false;
            }
        }

        return true;
    }

    @Override
    @Transactional
    public void reservarComponentesParaVentana(ResultadoCalculoDTO resultado, Long trabajoId) {
        // Para cada material, registrar una reserva
        for (MaterialAdicionalDTO material : resultado.getMaterialesAdicionales()) {
            if (material.getProductoId() == null) {
                continue;
            }

            // Crear movimiento de reserva
            inventarioService.actualizarStockProducto(
                    material.getProductoId(),
                    material.getCantidad(),
                    TipoMovimiento.RESERVA,
                    "Trabajo #" + trabajoId);
        }
    }

    @Override
    @Transactional
    public void consumirComponentesParaVentana(ResultadoCalculoDTO resultado, Long trabajoId) {
        // Para cada material, registrar una salida de stock
        for (MaterialAdicionalDTO material : resultado.getMaterialesAdicionales()) {
            if (material.getProductoId() == null) {
                continue;
            }

            // Crear movimiento de salida
            inventarioService.actualizarStockProducto(
                    material.getProductoId(),
                    material.getCantidad(),
                    TipoMovimiento.SALIDA,
                    "Trabajo #" + trabajoId);
        }
    }

    @Override
    @Transactional
    public void generarConfiguracionEstandardComponentes(Long serieId, String tipoSerie) {
        // Esta implementación generaría configuraciones estándar para una nueva serie
        // basándose en plantillas predefinidas para cada tipo de serie (corredera, abatible, etc.)

        SerieBase serie = serieRepo.findById(serieId)
                .orElseThrow(() -> new RuntimeException("Serie no encontrada"));

        logger.info("Generando configuración estándar de componentes para serie: {} ({})",
                serie.getCodigo(), tipoSerie);

        // Aquí se implementaría la lógica para generar las configuraciones estándar
        // basadas en el tipo de serie (CORREDERA, ABATIBLE, etc.)

        // Como ejemplo, crearemos algunos materiales base genéricos
        if ("CORREDERA".equals(tipoSerie)) {
            generarComponentesCorredera(serie);
        } else if ("ABATIBLE".equals(tipoSerie)) {
            generarComponentesAbatible(serie);
        } else {
            logger.warn("No hay plantilla de componentes para el tipo: {}", tipoSerie);
        }
    }

    // Métodos auxiliares privados

    private String determinarCategoria(MaterialConfiguracion material) {
        String descripcion = material.getDescripcion().toLowerCase();

        if (descripcion.contains("tornillo") || descripcion.contains("tuerca") || descripcion.contains("arandela")) {
            return "TORNILLERIA";
        } else if (descripcion.contains("cierre") || descripcion.contains("maneta") ||
                descripcion.contains("rodamiento") || descripcion.contains("bisagra")) {
            return "HERRAJES";
        } else if (descripcion.contains("felpa") || descripcion.contains("goma") ||
                descripcion.contains("burlete") || descripcion.contains("junta")) {
            return "ACCESORIOS";
        } else {
            return "OTROS_MATERIALES";
        }
    }

    private void generarComponentesCorredera(SerieBase serie) {
        // Ejemplo de implementación para ventanas correderas
        // En una implementación real, esto podría leer de un archivo de configuración
        // o de una plantilla en la base de datos

        // Crear materiales base para esta serie
        // Estos son ejemplos, en la implementación real se buscarían productos existentes

        // Cierres
        createMaterialBase(serie, "H010", "Cierre de presión estándar", "HERRAJE_BASICO", true,
                "Cierre de presión para ventanas correderas de la serie " + serie.getCodigo());

        // Rodamientos
        createMaterialBase(serie, "H020", "Kit rodamientos corredera", "HERRAJE_BASICO", true,
                "Kit completo de rodamientos para ventana corredera " + serie.getCodigo());

        // Tornillos
        createMaterialBase(serie, "T010", "Tornillo 4,8x25mm cabeza redonda", "TORNILLERIA", true,
                "Tornillos específicos para serie " + serie.getCodigo());

        // Felpa
        createMaterialBase(serie, "A001", "Felpa 5mm (metro)", "ACCESORIO", true,
                "Felpa para estanqueidad de hojas correderas");
    }

    private void generarComponentesAbatible(SerieBase serie) {
        // Implementación similar para ventanas abatibles

        // Cierres
        createMaterialBase(serie, "H030", "Maneta de cierre abatible", "HERRAJE_BASICO", true,
                "Maneta para ventanas abatibles de la serie " + serie.getCodigo());

        // Bisagras
        createMaterialBase(serie, "H040", "Kit bisagras ventana abatible", "HERRAJE_BASICO", true,
                "Kit completo de bisagras para ventana abatible " + serie.getCodigo());

        // Tornillos
        createMaterialBase(serie, "T020", "Tornillo 4,8x32mm cabeza avellanada", "TORNILLERIA", true,
                "Tornillos específicos para serie " + serie.getCodigo());

        // Gomas
        createMaterialBase(serie, "A002", "Goma EPDM (metro)", "ACCESORIO", true,
                "Goma para estanqueidad de ventanas abatibles");
    }

    private void createMaterialBase(SerieBase serie, String codigoProducto, String descripcion,
                                    String tipoMaterial, boolean esPredeterminado, String notasTecnicas) {
        // Buscar el producto
        var producto = productoRepo.findByCodigo(codigoProducto).orElse(null);

        if (producto == null) {
            logger.warn("Producto no encontrado: {}. No se pudo crear material base.", codigoProducto);
            return;
        }

        // Verificar si ya existe
        MaterialBaseSerie existente = materialBaseRepo.findBySerieIdAndProductoId(serie.getId(), producto.getId());

        if (existente != null) {
            logger.debug("Material base ya existe para serie {} y producto {}",
                    serie.getCodigo(), codigoProducto);
            return;
        }

        // Crear nuevo material base
        MaterialBaseSerie material = new MaterialBaseSerie();
        material.setSerie(serie);
        material.setProducto(producto);
        material.setDescripcion(descripcion);
        material.setTipoMaterial(tipoMaterial);
        material.setEsPredeterminado(esPredeterminado);
        material.setNotasTecnicas(notasTecnicas);

        materialBaseRepo.save(material);
        logger.info("Creado material base: {} para serie {}", descripcion, serie.getCodigo());
    }
}