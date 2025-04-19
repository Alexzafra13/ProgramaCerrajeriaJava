package com.gestiontaller.server.factory;

import com.gestiontaller.server.dto.calculo.ResultadoCalculoDTO;
import com.gestiontaller.server.model.TipoCristal;
import com.gestiontaller.server.model.serie.TipoSerie;
import org.springframework.stereotype.Component;

/**
 * Fábrica para crear calculadores específicos según el tipo de serie y ventana
 */
@Component
public class CalculadorFactory {

    /**
     * Obtiene el calculador adecuado según el tipo de serie
     */
    public CalculadorEspecifico getCalculador(TipoSerie tipoSerie, Integer numHojas) {
        if (tipoSerie == TipoSerie.CORREDERA) {
            return new CalculadorCorredera(numHojas);
        } else if (tipoSerie == TipoSerie.ABATIBLE) {
            return new CalculadorAbatible(numHojas);
        } else if (tipoSerie == TipoSerie.OSCILOBATIENTE) {
            return new CalculadorOsciloBatiente(numHojas);
        }

        // Por defecto, usar un calculador genérico
        return new CalculadorGenerico(tipoSerie, numHojas);
    }

    /**
     * Interfaz para todos los calculadores específicos
     */
    public interface CalculadorEspecifico {
        ResultadoCalculoDTO calcular(Long serieId, Integer ancho, Integer alto,
                                     Boolean incluyePersiana, Integer alturaCajon,
                                     TipoCristal tipoCristal);
    }

    /**
     * Implementación para ventanas correderas
     */
    public class CalculadorCorredera implements CalculadorEspecifico {
        private final Integer numHojas;

        public CalculadorCorredera(Integer numHojas) {
            this.numHojas = numHojas;
        }

        @Override
        public ResultadoCalculoDTO calcular(Long serieId, Integer ancho, Integer alto,
                                            Boolean incluyePersiana, Integer alturaCajon,
                                            TipoCristal tipoCristal) {
            // Implementación específica para correderas
            // Esta implementación delegará en ConfiguracionSerieService
            // para aplicar la configuración adecuada
            return null;
        }
    }

    // Implementaciones para otros tipos (Abatible, OsciloBatiente, etc.)
    // ...

    /**
     * Implementación genérica para series no especializadas
     */
    public class CalculadorGenerico implements CalculadorEspecifico {
        private final TipoSerie tipoSerie;
        private final Integer numHojas;

        public CalculadorGenerico(TipoSerie tipoSerie, Integer numHojas) {
            this.tipoSerie = tipoSerie;
            this.numHojas = numHojas;
        }

        @Override
        public ResultadoCalculoDTO calcular(Long serieId, Integer ancho, Integer alto,
                                            Boolean incluyePersiana, Integer alturaCajon,
                                            TipoCristal tipoCristal) {
            // Implementación genérica
            return null;
        }
    }
}