package com.gestiontaller.server.service.impl;

import com.gestiontaller.server.dto.calculo.CorteDTO;
import com.gestiontaller.server.dto.calculo.MaterialAdicionalDTO;
import com.gestiontaller.server.dto.calculo.ResultadoCalculoDTO;
import com.gestiontaller.server.dto.configuracion.PlantillaConfiguracionSerieDTO;
import com.gestiontaller.server.mapper.configuracion.PlantillaConfiguracionSerieMapper;
import com.gestiontaller.server.model.TipoCristal;
import com.gestiontaller.server.model.configuracion.MaterialConfiguracion;
import com.gestiontaller.server.model.configuracion.PerfilConfiguracion;
import com.gestiontaller.server.model.configuracion.PlantillaConfiguracionSerie;
import com.gestiontaller.server.model.presupuesto.TipoPresupuesto;
import com.gestiontaller.server.model.serie.SerieBase;
import com.gestiontaller.server.model.serie.TipoSerie;
import com.gestiontaller.server.repository.configuracion.MaterialConfiguracionRepository;
import com.gestiontaller.server.repository.configuracion.PerfilConfiguracionRepository;
import com.gestiontaller.server.repository.configuracion.PlantillaConfiguracionSerieRepository;
import com.gestiontaller.server.repository.serie.SerieBaseRepository;
import com.gestiontaller.server.service.interfaces.ConfiguracionSerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConfiguracionSerieServiceImpl implements ConfiguracionSerieService {

    private final PlantillaConfiguracionSerieRepository configRepo;
    private final PerfilConfiguracionRepository perfilConfigRepo;
    private final MaterialConfiguracionRepository materialConfigRepo;
    private final SerieBaseRepository serieRepo;
    private final PlantillaConfiguracionSerieMapper configMapper;
    private final ScriptEngine jsEngine;

    @Autowired
    public ConfiguracionSerieServiceImpl(
            PlantillaConfiguracionSerieRepository configRepo,
            PerfilConfiguracionRepository perfilConfigRepo,
            MaterialConfiguracionRepository materialConfigRepo,
            SerieBaseRepository serieRepo,
            PlantillaConfiguracionSerieMapper configMapper) {
        this.configRepo = configRepo;
        this.perfilConfigRepo = perfilConfigRepo;
        this.materialConfigRepo = materialConfigRepo;
        this.serieRepo = serieRepo;
        this.configMapper = configMapper;

        // Inicializar el motor JavaScript para cálculos de fórmulas
        ScriptEngine engine = null;
        try {
            engine = new ScriptEngineManager().getEngineByName("nashorn");
            if (engine == null) {
                // Intenta con el motor JavaScript genérico si nashorn no está disponible
                engine = new ScriptEngineManager().getEngineByName("JavaScript");
            }
        } catch (Exception e) {
            System.err.println("Error al inicializar el motor de script: " + e.getMessage());
        }

        this.jsEngine = engine;

        if (this.jsEngine == null) {
            System.err.println("ADVERTENCIA: No se pudo obtener el motor JavaScript. Se usarán cálculos predeterminados.");
        } else {
            System.out.println("Motor de script inicializado correctamente: " + this.jsEngine.getFactory().getEngineName());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlantillaConfiguracionSerieDTO> obtenerConfiguracionesPorSerie(Long serieId) {
        System.out.println("Obteniendo configuraciones para serie ID: " + serieId);
        return configRepo.findBySerieIdAndActivaTrue(serieId).stream()
                .map(configMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PlantillaConfiguracionSerieDTO obtenerConfiguracionPorId(Long id) {
        System.out.println("Obteniendo configuración por ID: " + id);
        return configRepo.findByIdWithDetails(id)
                .map(configMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Configuración no encontrada con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public PlantillaConfiguracionSerieDTO obtenerConfiguracionPorSerieYHojas(Long serieId, Integer numHojas) {
        System.out.println("Obteniendo configuración para serie ID: " + serieId + " con " + numHojas + " hojas");
        return configRepo.findBySerieIdAndNumHojas(serieId, numHojas)
                .map(configMapper::toDto)
                .orElseThrow(() -> new RuntimeException(
                        "No existe configuración para la serie ID " + serieId +
                                " con " + numHojas + " hojas"));
    }

    @Override
    @Transactional
    public PlantillaConfiguracionSerieDTO guardarConfiguracion(PlantillaConfiguracionSerieDTO dto) {
        System.out.println("Guardando configuración: " + dto.getNombre());
        // Verificar serie
        SerieBase serie = serieRepo.findById(dto.getSerieId())
                .orElseThrow(() -> new RuntimeException("Serie no encontrada con ID: " + dto.getSerieId()));

        PlantillaConfiguracionSerie config;
        if (dto.getId() != null) {
            // Actualizar existente
            config = configRepo.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Configuración no encontrada con ID: " + dto.getId()));
            configMapper.updateEntityFromDto(dto, config);
        } else {
            // Crear nueva
            config = configMapper.toEntity(dto);
        }

        // Guardar configuración principal
        PlantillaConfiguracionSerie savedConfig = configRepo.save(config);
        System.out.println("Configuración guardada con ID: " + savedConfig.getId());

        return configMapper.toDto(savedConfig);
    }

    @Override
    @Transactional
    public void eliminarConfiguracion(Long id) {
        System.out.println("Eliminando configuración ID: " + id);
        configRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultadoCalculoDTO aplicarConfiguracion(Long configuracionId, Integer anchoTotal,
                                                    Integer altoTotal, Boolean incluyePersiana,
                                                    Integer alturaCajon, TipoCristal tipoCristal) {
        try {
            System.out.println("Iniciando cálculo para configuración ID: " + configuracionId);
            System.out.println("Parámetros: ancho=" + anchoTotal + ", alto=" + altoTotal +
                    ", incluyePersiana=" + incluyePersiana +
                    ", alturaCajon=" + alturaCajon +
                    ", tipoCristal=" + tipoCristal);

            // Verificar que configuracionId existe
            if (configuracionId == null) {
                throw new RuntimeException("El ID de configuración no puede ser nulo");
            }

            // Buscar la configuración
            PlantillaConfiguracionSerie config = configRepo.findById(configuracionId)
                    .orElseThrow(() -> new RuntimeException("Configuración no encontrada con ID: " + configuracionId));

            System.out.println("Configuración encontrada: " + config.getNombre() + " para " + config.getNumHojas() + " hojas");

            // Verificar serie
            if (config.getSerie() == null) {
                throw new RuntimeException("La configuración no tiene serie asociada");
            }

            System.out.println("Serie asociada: " + config.getSerie().getCodigo());

            // Lista de perfiles y materiales
            List<PerfilConfiguracion> perfiles = perfilConfigRepo.findByConfiguracionId(configuracionId);
            List<MaterialConfiguracion> materiales = materialConfigRepo.findByConfiguracionId(configuracionId);

            System.out.println("Perfiles encontrados: " + perfiles.size());
            System.out.println("Materiales encontrados: " + materiales.size());

            // Inicializar resultado
            ResultadoCalculoDTO resultado = new ResultadoCalculoDTO();
            List<CorteDTO> cortes = new ArrayList<>();
            List<MaterialAdicionalDTO> materialesAdicionales = new ArrayList<>();

            // Variables para el contexto de evaluación
            double altoVentana = altoTotal / 10.0; // cm
            double anchoTotalCm = anchoTotal / 10.0; // cm
            double alturaCajonCm = 0;

            // Ajustar altura si hay persiana
            if (incluyePersiana != null && incluyePersiana && alturaCajon != null && alturaCajon > 0) {
                alturaCajonCm = alturaCajon / 10.0;
                altoVentana = altoVentana - alturaCajonCm;
                System.out.println("Altura con persiana: alto ventana = " + altoVentana + " cm");
            }

            // Configurar variables de cálculo
            Bindings bindings = null;
            if (jsEngine != null) {
                bindings = jsEngine.createBindings();
                bindings.put("anchoTotal", anchoTotalCm);
                bindings.put("altoTotal", altoTotal / 10.0);
                bindings.put("numHojas", config.getNumHojas());
                bindings.put("incluyePersiana", incluyePersiana);
                bindings.put("alturaCajon", alturaCajonCm);
                bindings.put("altoVentana", altoVentana);
            }

            // Determinar tipo de presupuesto según la serie
            TipoPresupuesto tipoPresupuesto = determinarTipoPresupuesto(config.getSerie());
            System.out.println("Tipo de presupuesto determinado: " + tipoPresupuesto);

            // Procesar perfiles existentes si hay
            if (!perfiles.isEmpty()) {
                for (PerfilConfiguracion perfil : perfiles) {
                    try {
                        CorteDTO corte = procesarPerfil(perfil, bindings, altoVentana, anchoTotalCm);
                        System.out.println("Perfil procesado: " + corte.getNombrePerfil() + ", longitud: " + corte.getLongitud() + "mm");
                        cortes.add(corte);
                    } catch (Exception e) {
                        System.err.println("Error procesando perfil " + perfil.getId() + ": " + e.getMessage());
                    }
                }
            } else {
                System.out.println("ADVERTENCIA: No hay perfiles definidos. Creando perfiles básicos.");
                // Crear perfiles básicos según el tipo de ventana
                cortes.addAll(crearPerfilesBasicos(config.getNumHojas(), anchoTotal, altoTotal));
            }

            // Procesar materiales existentes si hay
            if (!materiales.isEmpty()) {
                for (MaterialConfiguracion material : materiales) {
                    try {
                        MaterialAdicionalDTO materialDTO = procesarMaterial(material, bindings);
                        System.out.println("Material procesado: " + materialDTO.getDescripcion() + ", cantidad: " + materialDTO.getCantidad());
                        materialesAdicionales.add(materialDTO);
                    } catch (Exception e) {
                        System.err.println("Error procesando material " + material.getId() + ": " + e.getMessage());
                    }
                }
            } else {
                System.out.println("ADVERTENCIA: No hay materiales definidos. Creando materiales estándar.");
                // Crear materiales estándar para la ventana según el número de hojas
                materialesAdicionales.addAll(crearMaterialesEstandar(config.getNumHojas()));
            }

            // Configurar resultado
            resultado.setTipoPresupuesto(tipoPresupuesto);
            resultado.setAncho(anchoTotal);
            resultado.setAlto(altoTotal);

            // Ajustar dimensiones si hay persiana
            if (incluyePersiana != null && incluyePersiana && alturaCajon != null && alturaCajon > 0) {
                resultado.setAltoVentana(altoTotal - alturaCajon);
                resultado.setAnchoVentana(anchoTotal);
            } else {
                resultado.setAltoVentana(altoTotal);
                resultado.setAnchoVentana(anchoTotal);
            }

            resultado.setNumeroHojas(config.getNumHojas());
            resultado.setIncluyePersiana(incluyePersiana);
            resultado.setAlturaCajon(alturaCajon);
            resultado.setCortes(cortes);
            resultado.setMaterialesAdicionales(materialesAdicionales);
            resultado.setTipoCristal(tipoCristal);

            // Calcular precio total
            double precioTotal = calcularPrecioTotal(cortes, materialesAdicionales);
            resultado.setPrecioTotal(precioTotal);

            // Añadir resumen
            String descripcionPersiana = incluyePersiana != null && incluyePersiana ? " con persiana" : "";
            String tipoCristalDesc = tipoCristal == TipoCristal.SIMPLE ?
                    "vidrio simple" : "cristal climalit doble";

            resultado.setResumen(String.format("Ventana %s %s %dx%dmm%s con %s",
                    formatearTipoPresupuesto(tipoPresupuesto),
                    config.getSerie().getCodigo(),
                    anchoTotal, altoTotal,
                    descripcionPersiana,
                    tipoCristalDesc));

            System.out.println("Cálculo completado con éxito: " + resultado.getResumen());
            return resultado;
        } catch (Exception e) {
            System.err.println("ERROR en aplicarConfiguracion: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al aplicar configuración: " + e.getMessage(), e);
        }
    }

    private TipoPresupuesto determinarTipoPresupuesto(SerieBase serie) {
        if (serie == null || serie.getCodigo() == null) {
            return TipoPresupuesto.VENTANA_CORREDERA; // Valor por defecto
        }

        String codigo = serie.getCodigo().toUpperCase();
        if (codigo.contains("CORREDERA")) {
            return TipoPresupuesto.VENTANA_CORREDERA;
        } else if (codigo.contains("ABAT")) {
            return TipoPresupuesto.VENTANA_ABATIBLE;
        } else if (codigo.contains("OSCILO")) {
            return TipoPresupuesto.VENTANA_OSCILOBATIENTE;
        } else if (codigo.contains("FIJA")) {
            return TipoPresupuesto.VENTANA_FIJA;
        } else if (codigo.contains("PUERTA")) {
            return TipoPresupuesto.PUERTA_ALUMINIO;
        }

        // Si es ALUPROM, asumimos corredera por defecto
        if (codigo.startsWith("ALUPROM")) {
            return TipoPresupuesto.VENTANA_CORREDERA;
        }

        return TipoPresupuesto.OTRO_ALUMINIO;
    }

    private String formatearTipoPresupuesto(TipoPresupuesto tipo) {
        if (tipo == null) {
            return "Ventana";
        }

        return tipo.toString()
                .replace("VENTANA_", "")
                .replace("_", " ")
                .toLowerCase();
    }

    private CorteDTO procesarPerfil(PerfilConfiguracion perfil, Bindings bindings, double altoVentana, double anchoTotal) {
        CorteDTO corte = new CorteDTO();

        // Datos básicos del perfil
        corte.setPerfilId(perfil.getPerfil().getId());
        corte.setCodigoPerfil(perfil.getPerfil().getCodigo());
        corte.setNombrePerfil(perfil.getPerfil().getNombre());
        corte.setTipoPerfil(perfil.getPerfil().getTipoPerfil());
        corte.setCantidad(perfil.getCantidad());
        corte.setDescripcion(perfil.getTipoPerfil());

        // Calcular longitud
        int longitud;
        if (jsEngine != null && perfil.getFormulaCalculo() != null && !perfil.getFormulaCalculo().isEmpty()) {
            // Evaluar fórmula JS
            try {
                Object result = jsEngine.eval(perfil.getFormulaCalculo(), bindings);
                double valor = Double.parseDouble(result.toString());
                longitud = (int) Math.round(valor * 10); // Convertir cm a mm
            } catch (ScriptException e) {
                throw new RuntimeException("Error al evaluar fórmula: " + e.getMessage());
            }
        } else {
            // Aplicar descuento fijo o usar lógica básica
            String tipoPerfilStr = perfil.getTipoPerfil();
            if (tipoPerfilStr.contains("LATERAL") || tipoPerfilStr.contains("VERTICAL")) {
                // Perfiles verticales - aplicar al alto
                double descuento = perfil.getDescuentoCm() != null ? perfil.getDescuentoCm() : 0;
                longitud = (int) Math.round((altoVentana - descuento) * 10); // cm a mm
            } else {
                // Perfiles horizontales - aplicar al ancho
                double descuento = perfil.getDescuentoCm() != null ? perfil.getDescuentoCm() : 0;
                longitud = (int) Math.round((anchoTotal - descuento) * 10); // cm a mm
            }
        }

        corte.setLongitud(longitud);
        corte.setDescuentoAplicado(perfil.getDescuentoCm() != null ?
                (int)(perfil.getDescuentoCm() * 10) : 0); // cm a mm

        return corte;
    }

    private MaterialAdicionalDTO procesarMaterial(MaterialConfiguracion material, Bindings bindings) {
        MaterialAdicionalDTO materialDTO = new MaterialAdicionalDTO();
        materialDTO.setDescripcion(material.getDescripcion());

        // Calcular cantidad según fórmula o valor base
        int cantidad = material.getCantidadBase();
        if (jsEngine != null && material.getFormulaCantidad() != null && !material.getFormulaCantidad().isEmpty()) {
            try {
                Object result = jsEngine.eval(material.getFormulaCantidad(), bindings);
                cantidad = (int) Math.ceil(Double.parseDouble(result.toString()));
            } catch (ScriptException e) {
                throw new RuntimeException("Error al evaluar fórmula de cantidad: " + e.getMessage());
            }
        }

        materialDTO.setCantidad(cantidad);

        // Si hay producto asociado, obtener precio
        if (material.getProducto() != null) {
            materialDTO.setProductoId(material.getProducto().getId());
            materialDTO.setPrecioUnitario(material.getProducto().getPrecioVenta());
        } else {
            // Valores por defecto para pruebas
            materialDTO.setPrecioUnitario(10.0);
        }

        // Calcular precio total
        materialDTO.setPrecioTotal(materialDTO.getCantidad() * materialDTO.getPrecioUnitario());

        return materialDTO;
    }

    private double calcularPrecioTotal(List<CorteDTO> cortes, List<MaterialAdicionalDTO> materiales) {
        double precioPerfiles = cortes.stream()
                .mapToDouble(c -> (c.getLongitud() / 1000.0) * c.getCantidad() * 15.0) // Precio base por metro
                .sum();

        double precioMateriales = materiales.stream()
                .mapToDouble(m -> m.getPrecioTotal() != null ? m.getPrecioTotal() : 0.0)
                .sum();

        return precioPerfiles + precioMateriales;
    }

    /**
     * Crea perfiles básicos para una ventana con el número de hojas especificado
     */
    private List<CorteDTO> crearPerfilesBasicos(int numHojas, int anchoTotal, int altoTotal) {
        List<CorteDTO> perfilesBasicos = new ArrayList<>();

        // Marco lateral (2 unidades)
        CorteDTO marcoLateral = new CorteDTO();
        marcoLateral.setCodigoPerfil("ALUPROM21-ML");
        marcoLateral.setNombrePerfil("Marco Lateral");
        marcoLateral.setLongitud(altoTotal); // Alto completo
        marcoLateral.setCantidad(2);
        marcoLateral.setDescripcion("Marco Lateral");
        perfilesBasicos.add(marcoLateral);

        // Marco superior (1 unidad)
        CorteDTO marcoSuperior = new CorteDTO();
        marcoSuperior.setCodigoPerfil("ALUPROM21-MS");
        marcoSuperior.setNombrePerfil("Marco Superior");
        marcoSuperior.setLongitud(anchoTotal - 41); // Descontar 4.1 cm
        marcoSuperior.setCantidad(1);
        marcoSuperior.setDescripcion("Marco Superior");
        perfilesBasicos.add(marcoSuperior);

        // Marco inferior (1 unidad)
        CorteDTO marcoInferior = new CorteDTO();
        marcoInferior.setCodigoPerfil("ALUPROM21-MI");
        marcoInferior.setNombrePerfil("Marco Inferior");
        marcoInferior.setLongitud(anchoTotal - 41); // Descontar 4.1 cm
        marcoInferior.setCantidad(1);
        marcoInferior.setDescripcion("Marco Inferior");
        perfilesBasicos.add(marcoInferior);

        // Hojas según el número especificado
        for (int i = 0; i < numHojas; i++) {
            // Hoja lateral
            CorteDTO hojaLateral = new CorteDTO();
            hojaLateral.setCodigoPerfil("ALUPROM21-HL");
            hojaLateral.setNombrePerfil("Hoja Lateral");
            hojaLateral.setLongitud(altoTotal - 53); // Descontar 5.3 cm
            hojaLateral.setCantidad(2); // Dos por hoja (izquierda y derecha)
            hojaLateral.setDescripcion("Hoja Lateral " + (i+1));
            perfilesBasicos.add(hojaLateral);

            // Ruletas horizontales
            CorteDTO hojaRuleta = new CorteDTO();
            hojaRuleta.setCodigoPerfil("ALUPROM21-HR");
            hojaRuleta.setNombrePerfil("Hoja Ruleta");
            hojaRuleta.setLongitud((anchoTotal / numHojas) - 20); // Ancho total dividido entre hojas, menos 2 cm
            hojaRuleta.setCantidad(2); // Superior e inferior
            hojaRuleta.setDescripcion("Hoja Ruleta " + (i+1));
            perfilesBasicos.add(hojaRuleta);
        }

        return perfilesBasicos;
    }

    /**
     * Crea materiales estándar para una ventana con el número de hojas especificado
     */
    private List<MaterialAdicionalDTO> crearMaterialesEstandar(int numHojas) {
        List<MaterialAdicionalDTO> materialesEstandar = new ArrayList<>();

        // Para ventana de 2 hojas: 2 cierres, 4 rodamientos, 1 kit, 18 tornillos
        // Para ventana de 4 hojas: 4 cierres, 8 rodamientos, 2 kits, 26 tornillos

        // Cierres de presión
        MaterialAdicionalDTO cierres = new MaterialAdicionalDTO();
        cierres.setDescripcion("Cierres de presión");
        cierres.setCantidad(numHojas == 2 ? 2 : 4); // 2 para ventana de 2 hojas, 4 para ventana de 4 hojas
        cierres.setPrecioUnitario(5.0);
        cierres.setPrecioTotal(cierres.getCantidad() * cierres.getPrecioUnitario());
        materialesEstandar.add(cierres);

        // Rodamientos
        MaterialAdicionalDTO rodamientos = new MaterialAdicionalDTO();
        rodamientos.setDescripcion("Rodamientos (ruedas para hojas)");
        rodamientos.setCantidad(numHojas * 2); // 2 por hoja
        rodamientos.setPrecioUnitario(2.5);
        rodamientos.setPrecioTotal(rodamientos.getCantidad() * rodamientos.getPrecioUnitario());
        materialesEstandar.add(rodamientos);

        // Kit corredera
        MaterialAdicionalDTO kitCorredera = new MaterialAdicionalDTO();
        kitCorredera.setDescripcion("Kit corredera ALUPROM-21");
        kitCorredera.setCantidad(numHojas == 2 ? 1 : 2); // 1 para ventana de 2 hojas, 2 para ventana de 4 hojas
        kitCorredera.setPrecioUnitario(15.0);
        kitCorredera.setPrecioTotal(kitCorredera.getCantidad() * kitCorredera.getPrecioUnitario());
        materialesEstandar.add(kitCorredera);

        // Tornillos para marco
        MaterialAdicionalDTO tornillosMarco = new MaterialAdicionalDTO();
        tornillosMarco.setDescripcion("Tornillos para marco 4,8x25");
        tornillosMarco.setCantidad(10); // Siempre 10 para el marco
        tornillosMarco.setPrecioUnitario(0.15);
        tornillosMarco.setPrecioTotal(tornillosMarco.getCantidad() * tornillosMarco.getPrecioUnitario());
        materialesEstandar.add(tornillosMarco);

        // Tornillos para hojas
        MaterialAdicionalDTO tornillosHojas = new MaterialAdicionalDTO();
        tornillosHojas.setDescripcion("Tornillos para hojas 4,8x25");
        tornillosHojas.setCantidad(4 * numHojas); // 4 por cada hoja
        tornillosHojas.setPrecioUnitario(0.15);
        tornillosHojas.setPrecioTotal(tornillosHojas.getCantidad() * tornillosHojas.getPrecioUnitario());
        materialesEstandar.add(tornillosHojas);

        // Felpa (metros)
        MaterialAdicionalDTO felpa = new MaterialAdicionalDTO();
        felpa.setDescripcion("Felpa para estanqueidad (metros)");
        felpa.setCantidad(8 * numHojas / 2); // Aproximación básica
        felpa.setPrecioUnitario(1.2);
        felpa.setPrecioTotal(felpa.getCantidad() * felpa.getPrecioUnitario());
        materialesEstandar.add(felpa);

        return materialesEstandar;
    }
}