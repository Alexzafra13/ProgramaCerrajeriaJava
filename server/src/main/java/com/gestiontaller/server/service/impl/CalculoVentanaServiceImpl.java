package com.gestiontaller.server.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestiontaller.server.dto.calculo.CalculoVentanaDTO;
import com.gestiontaller.server.dto.calculo.CorteDTO;
import com.gestiontaller.server.dto.calculo.MaterialAdicionalDTO;
import com.gestiontaller.server.dto.calculo.ParametrosCalculoDTO;
import com.gestiontaller.server.dto.calculo.ResultadoCalculoDTO;
import com.gestiontaller.server.mapper.CalculoVentanaMapper;
import com.gestiontaller.server.mapper.CorteVentanaMapper;
import com.gestiontaller.server.mapper.MaterialAdicionalMapper;
import com.gestiontaller.server.model.calculo.CalculoVentana;
import com.gestiontaller.server.model.calculo.CorteVentana;
import com.gestiontaller.server.model.calculo.MaterialAdicional;
import com.gestiontaller.server.model.presupuesto.LineaPresupuesto;
import com.gestiontaller.server.model.serie.PerfilSerie;
import com.gestiontaller.server.model.serie.SerieBase;
import com.gestiontaller.server.model.trabajo.Trabajo;
import com.gestiontaller.server.repository.calculo.CalculoVentanaRepository;
import com.gestiontaller.server.repository.presupuesto.LineaPresupuestoRepository;
import com.gestiontaller.server.repository.serie.PerfilSerieRepository;
import com.gestiontaller.server.repository.trabajo.TrabajoRepository;
import com.gestiontaller.server.service.interfaces.CalculoVentanaService;
import com.gestiontaller.server.util.calculadora.CalculadoraVentana;
import com.gestiontaller.server.util.optimizador.OptimizadorCortes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gestiontaller.server.repository.serie.SerieBaseRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalculoVentanaServiceImpl implements CalculoVentanaService {

    private final CalculoVentanaRepository calculoVentanaRepository;
    private final LineaPresupuestoRepository lineaPresupuestoRepository;
    private final SerieBaseRepository serieBaseRepository;
    private final PerfilSerieRepository perfilSerieRepository;
    private final TrabajoRepository trabajoRepository;
    private final CalculoVentanaMapper calculoVentanaMapper;
    private final CorteVentanaMapper corteVentanaMapper;
    private final MaterialAdicionalMapper materialAdicionalMapper;
    private final CalculadoraVentana calculadora;
    private final OptimizadorCortes optimizadorCortes;
    private final ObjectMapper objectMapper;

    @Autowired
    public CalculoVentanaServiceImpl(
            CalculoVentanaRepository calculoVentanaRepository,
            LineaPresupuestoRepository lineaPresupuestoRepository,
            SerieBaseRepository serieBaseRepository,
            PerfilSerieRepository perfilSerieRepository,
            TrabajoRepository trabajoRepository,
            CalculoVentanaMapper calculoVentanaMapper,
            CorteVentanaMapper corteVentanaMapper,
            MaterialAdicionalMapper materialAdicionalMapper,
            CalculadoraVentana calculadora,
            OptimizadorCortes optimizadorCortes,
            ObjectMapper objectMapper) {
        this.calculoVentanaRepository = calculoVentanaRepository;
        this.lineaPresupuestoRepository = lineaPresupuestoRepository;
        this.serieBaseRepository = serieBaseRepository;
        this.perfilSerieRepository = perfilSerieRepository;
        this.trabajoRepository = trabajoRepository;
        this.calculoVentanaMapper = calculoVentanaMapper;
        this.corteVentanaMapper = corteVentanaMapper;
        this.materialAdicionalMapper = materialAdicionalMapper;
        this.calculadora = calculadora;
        this.optimizadorCortes = optimizadorCortes;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public ResultadoCalculoDTO calcularVentana(ParametrosCalculoDTO parametros) {
        // Obtener la serie
        SerieBase serie = serieBaseRepository.findById(parametros.getSerieId())
                .orElseThrow(() -> new RuntimeException("Serie no encontrada"));

        // Obtener perfiles necesarios
        List<PerfilSerie> perfiles = perfilSerieRepository.findBySerieId(parametros.getSerieId());

        // Verificar si es la serie ALUPROM-21
        ResultadoCalculoDTO resultado;
        if (serie.getCodigo().equals("ALUPROM-21")) {
            // Para ALUPROM-21 siempre usamos 2 hojas
            parametros.setNumeroHojas(2);

            resultado = calculadora.calcularVentana(
                    parametros.getAncho(),
                    parametros.getAlto(),
                    parametros.getNumeroHojas(),
                    parametros.getIncluyePersiana(),
                    parametros.getAlturaCajonPersiana(),
                    parametros.getAltoEsTotal(),
                    serie,
                    perfiles
            );
        } else {
            // Para otras series, usar cálculo genérico
            resultado = calculadora.calcularVentana(
                    parametros.getAncho(),
                    parametros.getAlto(),
                    parametros.getNumeroHojas(),
                    parametros.getIncluyePersiana(),
                    parametros.getAlturaCajonPersiana(),
                    parametros.getAltoEsTotal(),
                    serie,
                    perfiles
            );
        }

        try {
            // Convertir el resultado a JSON para almacenamiento
            resultado.setResultadoJson(objectMapper.writeValueAsString(resultado));
        } catch (Exception e) {
            // Manejar error de serialización
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

    // Continúa desde donde se truncó el archivo
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
