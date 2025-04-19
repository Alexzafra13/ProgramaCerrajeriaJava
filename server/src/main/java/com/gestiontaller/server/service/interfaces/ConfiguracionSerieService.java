package com.gestiontaller.server.service.interfaces;

import com.gestiontaller.server.dto.configuracion.PlantillaConfiguracionSerieDTO;

import java.util.List;

public interface ConfiguracionSerieService {
    List<PlantillaConfiguracionSerieDTO> obtenerConfiguracionesPorSerie(Long serieId);

    PlantillaConfiguracionSerieDTO obtenerConfiguracionPorId(Long id);

    PlantillaConfiguracionSerieDTO obtenerConfiguracionPorSerieYHojas(Long serieId, Integer numHojas);

    PlantillaConfiguracionSerieDTO guardarConfiguracion(PlantillaConfiguracionSerieDTO dto);

    void eliminarConfiguracion(Long id);

    // Método para aplicar la configuración a dimensiones específicas
    ResultadoCalculoDTO aplicarConfiguracion(Long configuracionId, Integer anchoTotal,
                                             Integer altoTotal, Boolean incluyePersiana,
                                             Integer alturaCajon, TipoCristal tipoCristal);
}