package com.gestiontaller.server.util.generador;

import com.gestiontaller.common.dto.serie.DescuentoPerfilSerieDTO;
import com.gestiontaller.common.dto.serie.PerfilSerieDTO;
import com.gestiontaller.common.dto.serie.SerieAluminioDTO;
import com.gestiontaller.common.model.material.TipoMaterial;
import com.gestiontaller.common.model.serie.TipoPerfil;
import com.gestiontaller.common.model.serie.TipoSerie;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilidad para generar estructuras de serie completas
 */
@Component
public class GeneradorSeries {

    /**
     * Genera una serie de aluminio completa con todos sus componentes
     */
    public SerieAluminioDTO generarSerieAluminio(String codigo, String nombre, String descripcion,
                                                 TipoSerie tipoSerie, boolean roturaPuente, boolean permitePersiana) {
        SerieAluminioDTO serie = new SerieAluminioDTO();
        serie.setCodigo(codigo);
        serie.setNombre(nombre);
        serie.setDescripcion(descripcion);
        serie.setTipoMaterial(TipoMaterial.ALUMINIO);
        serie.setTipoSerie(tipoSerie);
        serie.setRoturaPuente(roturaPuente);
        serie.setPermitePersiana(permitePersiana);
        serie.setEspesorMinimo(1.5);
        serie.setEspesorMaximo(1.8);
        serie.setPrecioMetroBase(15.0); // Valor por defecto, se actualizará
        serie.setDescuentoSerie(0.0);
        serie.setActiva(true);

        // Generar perfiles según el tipo de serie
        serie.setPerfiles(generarPerfilesParaAluminio(codigo, tipoSerie));

        // Generar descuentos según el tipo de serie
        serie.setDescuentosPerfiles(generarDescuentosParaAluminio(codigo, tipoSerie));

        return serie;
    }

    /**
     * Genera perfiles básicos para una serie de aluminio
     */
    private List<PerfilSerieDTO> generarPerfilesParaAluminio(String codigoSerie, TipoSerie tipoSerie) {
        List<PerfilSerieDTO> perfiles = new ArrayList<>();

        // Perfiles comunes a todas las series
        perfiles.add(crearPerfil(codigoSerie + "-ML", "Marco Lateral", TipoPerfil.MARCO, 0.85, 10.5, 6000));
        perfiles.add(crearPerfil(codigoSerie + "-MS", "Marco Superior", TipoPerfil.MARCO, 0.85, 10.5, 6000));
        perfiles.add(crearPerfil(codigoSerie + "-MI", "Marco Inferior", TipoPerfil.MARCO, 0.85, 10.5, 6000));

        // Perfiles específicos según tipo de ventana
        if (tipoSerie == TipoSerie.CORREDERA) {
            perfiles.add(crearPerfil(codigoSerie + "-HL", "Hoja Lateral", TipoPerfil.HOJA, 0.65, 8.5, 6000));
            perfiles.add(crearPerfil(codigoSerie + "-HC", "Hoja Central", TipoPerfil.HOJA, 0.65, 8.5, 6000));
            perfiles.add(crearPerfil(codigoSerie + "-HR", "Hoja Ruleta", TipoPerfil.HOJA, 0.55, 7.5, 6000));
        } else if (tipoSerie == TipoSerie.ABATIBLE) {
            perfiles.add(crearPerfil(codigoSerie + "-HA", "Hoja Abatible", TipoPerfil.HOJA, 0.70, 9.0, 6000));
            perfiles.add(crearPerfil(codigoSerie + "-JP", "Junquillo Plano", TipoPerfil.JUNQUILLO, 0.30, 4.5, 6000));
            perfiles.add(crearPerfil(codigoSerie + "-JC", "Junquillo Curvo", TipoPerfil.JUNQUILLO, 0.35, 5.0, 6000));
        }

        return perfiles;
    }

    /**
     * Genera descuentos estándar para una serie de aluminio
     */
    private List<DescuentoPerfilSerieDTO> generarDescuentosParaAluminio(String codigoSerie, TipoSerie tipoSerie) {
        List<DescuentoPerfilSerieDTO> descuentos = new ArrayList<>();

        // Descuentos comunes para marco
        descuentos.add(crearDescuento(TipoPerfil.MARCO, 0, "Alto marco: Sin descuento"));
        descuentos.add(crearDescuento(TipoPerfil.MARCO, 41, "Ancho marco: medida - 4.1 cm"));

        // Descuentos específicos según tipo
        if (tipoSerie == TipoSerie.CORREDERA) {
            descuentos.add(crearDescuento(TipoPerfil.HOJA, 53, "Alto hoja: altura - 5.3 cm"));
            descuentos.add(crearDescuento(TipoPerfil.HOJA, 20, "Ancho hoja: (ancho total / 2) - 2 cm"));
        } else if (tipoSerie == TipoSerie.ABATIBLE) {
            descuentos.add(crearDescuento(TipoPerfil.HOJA, 48, "Alto hoja: altura - 4.8 cm"));
            descuentos.add(crearDescuento(TipoPerfil.HOJA, 48, "Ancho hoja: ancho - 4.8 cm"));
        }

        return descuentos;
    }

    /**
     * Crea un objeto PerfilSerieDTO con valores predeterminados
     */
    private PerfilSerieDTO crearPerfil(String codigo, String nombre, TipoPerfil tipoPerfil,
                                       double pesoMetro, double precioMetro, int longitudBarra) {
        PerfilSerieDTO perfil = new PerfilSerieDTO();
        perfil.setCodigo(codigo);
        perfil.setNombre(nombre);
        perfil.setTipoPerfil(tipoPerfil);
        perfil.setPesoMetro(pesoMetro);
        perfil.setPrecioMetro(precioMetro);
        perfil.setLongitudBarra(longitudBarra);
        return perfil;
    }

    /**
     * Crea un objeto DescuentoPerfilSerieDTO con valores predeterminados
     */
    private DescuentoPerfilSerieDTO crearDescuento(TipoPerfil tipoPerfil, int descuentoMilimetros, String descripcion) {
        DescuentoPerfilSerieDTO descuento = new DescuentoPerfilSerieDTO();
        descuento.setTipoPerfil(tipoPerfil);
        descuento.setDescuentoMilimetros(descuentoMilimetros);
        descuento.setDescripcion(descripcion);
        return descuento;
    }
}