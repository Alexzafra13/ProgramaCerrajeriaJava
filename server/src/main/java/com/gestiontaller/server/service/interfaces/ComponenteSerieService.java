package com.gestiontaller.server.service.interfaces;

import com.gestiontaller.common.dto.serie.MaterialBaseSerieDTO;
import com.gestiontaller.common.dto.calculo.ResultadoCalculoDTO;

import java.util.List;
import java.util.Map;

/**
 * Servicio para la gestión de componentes estándar (materiales, herrajes, accesorios)
 * asociados a una serie de ventanas.
 */
public interface ComponenteSerieService {

    /**
     * Obtiene todos los materiales base asociados a una serie
     */
    List<MaterialBaseSerieDTO> obtenerMaterialesBasePorSerie(Long serieId);

    /**
     * Obtiene los materiales base de un tipo específico para una serie
     */
    List<MaterialBaseSerieDTO> obtenerMaterialesBasePorTipo(Long serieId, String tipoMaterial);

    /**
     * Guarda un material base para una serie
     */
    MaterialBaseSerieDTO guardarMaterialBase(MaterialBaseSerieDTO materialDTO);

    /**
     * Elimina un material base
     */
    void eliminarMaterialBase(Long id);

    /**
     * Calcula los componentes necesarios para una ventana según su configuración
     * y parámetros específicos (ancho, alto, número de hojas, etc.)
     */
    Map<String, List<MaterialBaseSerieDTO>> calcularComponentesParaVentana(
            Long serieId, Integer numHojas, Integer anchoTotal, Integer altoTotal,
            Boolean incluyePersiana);

    /**
     * Verifica la disponibilidad de componentes para fabricar una ventana
     */
    boolean verificarDisponibilidadComponentes(ResultadoCalculoDTO resultado);

    /**
     * Reserva componentes para una ventana (sin descontar del stock)
     */
    void reservarComponentesParaVentana(ResultadoCalculoDTO resultado, Long trabajoId);

    /**
     * Consume componentes para una ventana (descuenta del stock)
     */
    void consumirComponentesParaVentana(ResultadoCalculoDTO resultado, Long trabajoId);

    /**
     * Genera configuraciones estándar de componentes para una nueva serie
     * basándose en una serie existente o en plantillas predefinidas
     */
    void generarConfiguracionEstandardComponentes(Long serieId, String tipoSerie);
}