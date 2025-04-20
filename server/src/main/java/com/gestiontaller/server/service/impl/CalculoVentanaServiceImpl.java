package com.gestiontaller.server.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestiontaller.common.dto.calculo.*;
import com.gestiontaller.common.dto.configuracion.PlantillaConfiguracionSerieDTO;
import com.gestiontaller.server.mapper.CalculoVentanaMapper;
import com.gestiontaller.server.mapper.CorteVentanaMapper;
import com.gestiontaller.server.mapper.MaterialAdicionalMapper;
import com.gestiontaller.server.model.calculo.CalculoVentana;
import com.gestiontaller.server.model.calculo.CorteVentana;
import com.gestiontaller.server.model.calculo.MaterialAdicional;
import com.gestiontaller.server.model.presupuesto.LineaPresupuesto;
import com.gestiontaller.server.model.serie.SerieBase;
import com.gestiontaller.server.model.trabajo.Trabajo;
import com.gestiontaller.server.repository.calculo.CalculoVentanaRepository;
import com.gestiontaller.server.repository.presupuesto.LineaPresupuestoRepository;
import com.gestiontaller.server.repository.serie.SerieBaseRepository;
import com.gestiontaller.server.repository.trabajo.TrabajoRepository;
import com.gestiontaller.server.service.interfaces.CalculoVentanaService;
import com.gestiontaller.server.service.interfaces.ConfiguracionSerieService;
import com.gestiontaller.server.util.optimizador.OptimizadorCortes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalculoVentanaServiceImpl implements CalculoVentanaService {

    private final CalculoVentanaRepository calculoVentanaRepository;
    private final LineaPresupuestoRepository lineaPresupuestoRepository;
    private final SerieBaseRepository serieBaseRepository;
    private final TrabajoRepository trabajoRepository;
    private final CalculoVentanaMapper calculoVentanaMapper;
    private final CorteVentanaMapper corteVentanaMapper;
    private final MaterialAdicionalMapper materialAdicionalMapper;
    private final OptimizadorCortes optimizadorCortes;
    private final ObjectMapper objectMapper;
    private final ConfiguracionSerieService configuracionService;

    @Autowired
    public CalculoVentanaServiceImpl(
            CalculoVentanaRepository calculoVentanaRepository,
            LineaPresupuestoRepository lineaPresupuestoRepository,
            SerieBaseRepository serieBaseRepository,
            TrabajoRepository trabajoRepository,
            CalculoVentanaMapper calculoVentanaMapper,
            CorteVentanaMapper corteVentanaMapper,
            MaterialAdicionalMapper materialAdicionalMapper,
            OptimizadorCortes optimizadorCortes,
            ObjectMapper objectMapper,
            ConfiguracionSerieService configuracionService) {
        this.calculoVentanaRepository = calculoVentanaRepository;
        this.lineaPresupuestoRepository = lineaPresupuestoRepository;
        this.serieBaseRepository = serieBaseRepository;
        this.trabajoRepository = trabajoRepository;
        this.calculoVentanaMapper = calculoVentanaMapper;
        this.corteVentanaMapper = corteVentanaMapper;
        this.materialAdicionalMapper = materialAdicionalMapper;
        this.optimizadorCortes = optimizadorCortes;
        this.objectMapper = objectMapper;
        this.configuracionService = configuracionService;
    }

    @Override
    @Transactional
    public ResultadoCalculoDTO calcularVentana(ParametrosCalculoDTO parametros) {
        // Verificar que existe la serie
        serieBaseRepository.findById(parametros.getSerieId())
                .orElseThrow(() -> new RuntimeException("Serie no encontrada con ID: " + parametros.getSerieId()));

        // Obtener la configuración específica para esta serie y número de hojas
        PlantillaConfiguracionSerieDTO config =
                configuracionService.obtenerConfiguracionPorSerieYHojas(
                        parametros.getSerieId(),
                        parametros.getNumeroHojas());

        // Aplicar la configuración para obtener el resultado
        ResultadoCalculoDTO resultado = configuracionService.aplicarConfiguracion(
                config.getId(),
                parametros.getAncho(),
                parametros.getAlto(),
                parametros.getIncluyePersiana(),
                parametros.getAlturaCajonPersiana(),
                parametros.getTipoCristal()
        );

        // Serializar el resultado para almacenamiento
        try {
            resultado.setResultadoJson(objectMapper.writeValueAsString(resultado));
        } catch (Exception e) {
            throw new RuntimeException("Error al serializar resultado: " + e.getMessage());
        }

        return resultado;
    }

    @Override
    @Transactional
    public CalculoVentanaDTO guardarCalculo(CalculoVentanaDTO calculoDTO) {
        CalculoVentana calculo;
        boolean esNuevo = calculoDTO.getId() == null;

        if (esNuevo) {
            calculo = calculoVentanaMapper.toEntity(calculoDTO);
            calculo.setFechaCalculo(LocalDateTime.now());
        } else {
            calculo = calculoVentanaRepository.findById(calculoDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Cálculo no encontrado"));
            calculoVentanaMapper.updateEntityFromDto(calculoDTO, calculo);
        }

        // Asignar relaciones si es necesario
        if (calculoDTO.getLineaPresupuestoId() != null) {
            LineaPresupuesto lineaPresupuesto = lineaPresupuestoRepository.findById(calculoDTO.getLineaPresupuestoId())
                    .orElseThrow(() -> new RuntimeException("Línea de presupuesto no encontrada"));
            calculo.setLineaPresupuesto(lineaPresupuesto);
        }

        if (calculoDTO.getSerieId() != null) {
            SerieBase serie = serieBaseRepository.findById(calculoDTO.getSerieId())
                    .orElseThrow(() -> new RuntimeException("Serie no encontrada"));
            calculo.setSerie(serie);
        }

        // Guardar el cálculo
        CalculoVentana guardado = calculoVentanaRepository.save(calculo);

        // Si es nuevo, guardar cortes y materiales
        if (esNuevo && calculoDTO.getCortes() != null) {
            for (CorteDTO corteDTO : calculoDTO.getCortes()) {
                CorteVentana corte = corteVentanaMapper.toEntity(corteDTO);
                corte.setCalculoVentana(guardado);
                guardado.getCortes().add(corte);
            }
        }

        if (esNuevo && calculoDTO.getMaterialesAdicionales() != null) {
            for (MaterialAdicionalDTO materialDTO : calculoDTO.getMaterialesAdicionales()) {
                MaterialAdicional material = materialAdicionalMapper.toEntity(materialDTO);
                material.setCalculoVentana(guardado);
                guardado.getMaterialesAdicionales().add(material);
            }
        }

        // Guardar con las relaciones
        CalculoVentana finalGuardado = calculoVentanaRepository.save(guardado);
        return calculoVentanaMapper.toDto(finalGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public CalculoVentanaDTO obtenerCalculo(Long id) {
        CalculoVentana calculo = calculoVentanaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cálculo no encontrado"));
        return calculoVentanaMapper.toDto(calculo);
    }

    @Override
    @Transactional
    public ResultadoCalculoDTO calcularParaLineaPresupuesto(Long lineaPresupuestoId) {
        LineaPresupuesto linea = lineaPresupuestoRepository.findById(lineaPresupuestoId)
                .orElseThrow(() -> new RuntimeException("Línea de presupuesto no encontrada"));

        if (linea.getSerie() == null) {
            throw new RuntimeException("La línea de presupuesto no tiene serie asignada");
        }

        // Crear parámetros básicos desde la línea
        ParametrosCalculoDTO parametros = new ParametrosCalculoDTO();
        parametros.setSerieId(linea.getSerie().getId());
        parametros.setAncho(linea.getAncho());
        parametros.setAlto(linea.getAlto());
        parametros.setTipoPresupuesto(linea.getTipoPresupuesto());
        parametros.setIncluyePersiana(linea.getIncluyePersiana());

        // Si no tiene número de hojas, buscar la primera configuración disponible
        // para esta serie y usar su número de hojas por defecto
        if (parametros.getNumeroHojas() == null || parametros.getNumeroHojas() <= 0) {
            List<PlantillaConfiguracionSerieDTO> configs =
                    configuracionService.obtenerConfiguracionesPorSerie(linea.getSerie().getId());
            if (!configs.isEmpty()) {
                parametros.setNumeroHojas(configs.get(0).getNumHojas());
            } else {
                // Si no hay configuraciones, usar 2 hojas por defecto
                parametros.setNumeroHojas(2);
            }
        }

        // Calcular ventana
        ResultadoCalculoDTO resultado = calcularVentana(parametros);

        // Guardar el resultado en la base de datos
        CalculoVentanaDTO calculoDTO = new CalculoVentanaDTO();
        calculoDTO.setLineaPresupuestoId(lineaPresupuestoId);
        calculoDTO.setSerieId(linea.getSerie().getId());
        calculoDTO.setAnchura(parametros.getAncho());
        calculoDTO.setAltura(parametros.getAlto());
        calculoDTO.setTienePersianas(parametros.getIncluyePersiana());
        calculoDTO.setResultadoCalculo(resultado.getResultadoJson());
        calculoDTO.setCortes(resultado.getCortes());
        calculoDTO.setMaterialesAdicionales(resultado.getMaterialesAdicionales());

        guardarCalculo(calculoDTO);

        return resultado;
    }

    @Override
    @Transactional
    public ResultadoCalculoDTO optimizarCortesParaTrabajo(Long trabajoId) {
        Trabajo trabajo = trabajoRepository.findById(trabajoId)
                .orElseThrow(() -> new RuntimeException("Trabajo no encontrado"));

        if (trabajo.getPresupuesto() == null) {
            throw new RuntimeException("El trabajo no tiene presupuesto asociado");
        }

        // Obtener todos los cálculos relacionados con este trabajo
        List<CalculoVentana> calculos = calculoVentanaRepository.findByTrabajoId(trabajoId);

        if (calculos.isEmpty()) {
            throw new RuntimeException("No hay cálculos de ventanas para este trabajo");
        }

        // Convertir los cálculos a DTOs
        List<ResultadoCalculoDTO> resultados = new ArrayList<>();
        for (CalculoVentana calculo : calculos) {
            try {
                ResultadoCalculoDTO resultado = objectMapper.readValue(
                        calculo.getResultadoCalculo(), ResultadoCalculoDTO.class);
                resultados.add(resultado);
            } catch (Exception e) {
                throw new RuntimeException("Error al deserializar el resultado del cálculo: " + e.getMessage());
            }
        }

        // Optimizar los cortes para todas las ventanas
        List<ResultadoCalculoDTO> optimizados = optimizadorCortes.optimizarCortesMultiplesVentanas(resultados);

        // Para efectos de retorno, devolvemos un resumen consolidado
        ResultadoCalculoDTO resumen = new ResultadoCalculoDTO();
        resumen.setCortes(new ArrayList<>());
        resumen.setMaterialesAdicionales(new ArrayList<>());

        // Consolidar cortes y materiales
        for (ResultadoCalculoDTO optimizado : optimizados) {
            resumen.getCortes().addAll(optimizado.getCortes());
            resumen.getMaterialesAdicionales().addAll(optimizado.getMaterialesAdicionales());
        }

        // Actualizar los cálculos en la base de datos con los resultados optimizados
        for (int i = 0; i < calculos.size(); i++) {
            try {
                CalculoVentana calculo = calculos.get(i);
                calculo.setResultadoCalculo(objectMapper.writeValueAsString(optimizados.get(i)));
                calculoVentanaRepository.save(calculo);
            } catch (Exception e) {
                throw new RuntimeException("Error al actualizar el cálculo optimizado: " + e.getMessage());
            }
        }

        return resumen;
    }
}