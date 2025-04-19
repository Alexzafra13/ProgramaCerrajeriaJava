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
        this.jsEngine = new ScriptEngineManager().getEngineByName("javascript");
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlantillaConfiguracionSerieDTO> obtenerConfiguracionesPorSerie(Long serieId) {
        return configRepo.findBySerieIdAndActivaTrue(serieId).stream()
                .map(configMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PlantillaConfiguracionSerieDTO obtenerConfiguracionPorId(Long id) {
        return configRepo.findByIdWithDetails(id)
                .map(configMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Configuración no encontrada con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public PlantillaConfiguracionSerieDTO obtenerConfiguracionPorSerieYHojas(Long serieId, Integer numHojas) {
        return configRepo.findBySerieIdAndNumHojas(serieId, numHojas)
                .map(configMapper::toDto)
                .orElseThrow(() -> new RuntimeException(
                        "No existe configuración para la serie ID " + serieId +
                                " con " + numHojas + " hojas"));
    }

    @Override
    @Transactional
    public PlantillaConfiguracionSerieDTO guardarConfiguracion(PlantillaConfiguracionSerieDTO dto) {
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

        return configMapper.toDto(savedConfig);
    }

    @Override
    @Transactional
    public void eliminarConfiguracion(Long id) {
        configRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultadoCalculoDTO aplicarConfiguracion(Long configuracionId, Integer anchoTotal,
                                                    Integer altoTotal, Boolean incluyePersiana,
                                                    Integer alturaCajon, TipoCristal tipoCristal) {
        PlantillaConfiguracionSerie config = configRepo.findByIdWithDetails(configuracionId)
                .orElseThrow(() -> new RuntimeException("Configuración no encontrada"));

        ResultadoCalculoDTO resultado = new ResultadoCalculoDTO();
        List<CorteDTO> cortes = new ArrayList<>();
        List<MaterialAdicionalDTO> materiales = new ArrayList<>();

        // Variables para el contexto de evaluación
        Bindings bindings = jsEngine.createBindings();
        bindings.put("anchoTotal", anchoTotal / 10.0); // Convertir mm a cm
        bindings.put("altoTotal", altoTotal / 10.0);
        bindings.put("numHojas", config.getNumHojas());
        bindings.put("incluyePersiana", incluyePersiana);

        // Calcular altura efectiva considerando persiana
        double altoVentana = altoTotal / 10.0; // cm
        if (incluyePersiana && alturaCajon != null) {
            bindings.put("alturaCajon", alturaCajon / 10.0);
            bindings.put("altoVentana", altoVentana - (alturaCajon / 10.0));
        } else {
            bindings.put("altoVentana", altoVentana);
            bindings.put("alturaCajon", 0);
        }

        // Determinar tipo de presupuesto según la serie
        TipoPresupuesto tipoPresupuesto = TipoPresupuesto.VENTANA_CORREDERA; // Default
        if (config.getSerie().getCodigo().startsWith("ALUPROM")) {
            tipoPresupuesto = TipoPresupuesto.VENTANA_CORREDERA;
        } else if (config.getSerie().getCodigo().contains("ABAT")) {
            tipoPresupuesto = TipoPresupuesto.VENTANA_ABATIBLE;
        }

        // Procesar perfiles
        for (PerfilConfiguracion perfil : config.getPerfiles()) {
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
            if (perfil.getFormulaCalculo() != null && !perfil.getFormulaCalculo().isEmpty()) {
                // Evaluar fórmula JS
                try {
                    Object result = jsEngine.eval(perfil.getFormulaCalculo(), bindings);
                    double valor = Double.parseDouble(result.toString());
                    longitud = (int) Math.round(valor * 10); // Convertir cm a mm
                } catch (ScriptException e) {
                    throw new RuntimeException("Error al evaluar fórmula: " + e.getMessage());
                }
            } else {
                // Aplicar descuento fijo
                String tipoPerfilStr = perfil.getTipoPerfil();
                if (tipoPerfilStr.contains("LATERAL") || tipoPerfilStr.contains("CENTRAL")) {
                    // Perfiles verticales - aplicar al alto
                    double descuento = perfil.getDescuentoCm() != null ? perfil.getDescuentoCm() : 0;
                    longitud = (int) Math.round((altoVentana - descuento) * 10); // cm a mm
                } else {
                    // Perfiles horizontales - aplicar al ancho
                    double descuento = perfil.getDescuentoCm() != null ? perfil.getDescuentoCm() : 0;
                    longitud = (int) Math.round(((anchoTotal/10.0) - descuento) * 10); // cm a mm
                }
            }

            corte.setLongitud(longitud);
            corte.setDescuentoAplicado(perfil.getDescuentoCm() != null ?
                    (int)(perfil.getDescuentoCm() * 10) : 0); // cm a mm

            cortes.add(corte);
        }

        // Procesar materiales adicionales
        for (MaterialConfiguracion material : config.getMateriales()) {
            MaterialAdicionalDTO materialDTO = new MaterialAdicionalDTO();
            materialDTO.setDescripcion(material.getDescripcion());

            // Calcular cantidad según fórmula o valor base
            int cantidad = material.getCantidadBase();
            if (material.getFormulaCantidad() != null && !material.getFormulaCantidad().isEmpty()) {
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

            materiales.add(materialDTO);
        }

        // Configurar resultado
        resultado.setTipoPresupuesto(tipoPresupuesto);
        resultado.setAncho(anchoTotal);
        resultado.setAlto(altoTotal);

        // Ajustar dimensiones si hay persiana
        if (incluyePersiana && alturaCajon != null) {
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
        resultado.setMaterialesAdicionales(materiales);
        resultado.setTipoCristal(tipoCristal);

        // Calcular precio total
        double precioTotal = cortes.stream()
                .mapToDouble(c -> (c.getLongitud() / 1000.0) * c.getCantidad() * 15.0) // Precio base por metro
                .sum();

        precioTotal += materiales.stream()
                .mapToDouble(m -> m.getPrecioTotal() != null ? m.getPrecioTotal() : 0.0)
                .sum();

        resultado.setPrecioTotal(precioTotal);

        // Añadir resumen
        String descripcionPersiana = incluyePersiana ? " con persiana" : "";
        String tipoCristalDesc = tipoCristal == TipoCristal.SIMPLE ?
                "vidrio simple" : "cristal climalit doble";

        resultado.setResumen(String.format("Ventana %s %s %dx%dmm%s con %s",
                tipoPresupuesto.toString().replace("VENTANA_", ""),
                config.getSerie().getCodigo(),
                anchoTotal, altoTotal,
                descripcionPersiana,
                tipoCristalDesc));

        return resultado;
    }
}