package com.gestiontaller.server.factory;

import com.gestiontaller.common.dto.calculo.ResultadoCalculoDTO;
import com.gestiontaller.common.model.ventana.TipoCristal;
import com.gestiontaller.common.model.serie.TipoSerie;
import org.springframework.stereotype.Component;

/**
 * Fábrica para crear calculadores específicos según el tipo de serie y ventana
 * (Versión simplificada temporal)
 */
@Component
public class CalculadorFactory {

    /**
     * Obtiene el calculador adecuado según el tipo de serie
     * Temporalmente devuelve siempre el calculador genérico
     */
    public CalculadorEspecifico getCalculador(TipoSerie tipoSerie, Integer numHojas) {
        // Por ahora, devolvemos siempre un calculador genérico
        return new CalculadorGenerico(tipoSerie, numHojas);

        // Versión final:
        /*
        if (tipoSerie == TipoSerie.CORREDERA) {
            return new CalculadorCorredera(numHojas);
        } else if (tipoSerie == TipoSerie.ABATIBLE) {
            return new CalculadorAbatible(numHojas);
        } else if (tipoSerie == TipoSerie.OSCILOBATIENTE) {
            return new CalculadorOsciloBatiente(numHojas);
        }
        */
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
            // Implementación genérica básica (puedes dejarla vacía por ahora)
            ResultadoCalculoDTO resultado = new ResultadoCalculoDTO();
            resultado.setAncho(ancho);
            resultado.setAlto(alto);
            resultado.setNumeroHojas(numHojas);
            resultado.setIncluyePersiana(incluyePersiana);
            resultado.setAlturaCajon(alturaCajon);
            resultado.setTipoCristal(tipoCristal);

            // Devolver un resultado básico
            return resultado;
        }
    }

    // Las clases específicas serán implementadas más adelante
    /*
    public class CalculadorCorredera implements CalculadorEspecifico {
        // ...
    }

    public class CalculadorAbatible implements CalculadorEspecifico {
        // ...
    }

    public class CalculadorOsciloBatiente implements CalculadorEspecifico {
        // ...
    }
    */
}