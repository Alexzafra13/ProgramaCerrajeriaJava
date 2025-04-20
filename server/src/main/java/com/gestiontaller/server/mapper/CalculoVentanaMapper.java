package com.gestiontaller.server.mapper;

import com.gestiontaller.common.dto.calculo.CalculoVentanaDTO;
import com.gestiontaller.server.model.calculo.CalculoVentana;
import com.gestiontaller.server.model.presupuesto.LineaPresupuesto;
import com.gestiontaller.server.model.serie.SerieBase;
import com.gestiontaller.server.model.usuario.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {CorteVentanaMapper.class, MaterialAdicionalMapper.class})
public interface CalculoVentanaMapper {

    @Mapping(source = "lineaPresupuesto.id", target = "lineaPresupuestoId")
    @Mapping(source = "serie.id", target = "serieId")
    @Mapping(source = "serie.nombre", target = "nombreSerie")
    @Mapping(source = "plantilla.id", target = "plantillaId")
    @Mapping(source = "plantilla.nombre", target = "nombrePlantilla")
    @Mapping(source = "usuario.id", target = "usuarioId")
    @Mapping(source = "usuario.nombre", target = "nombreUsuario")
    CalculoVentanaDTO toDto(CalculoVentana entity);

    @Mapping(source = "lineaPresupuestoId", target = "lineaPresupuesto", qualifiedByName = "idToLineaPresupuesto")
    @Mapping(source = "serieId", target = "serie", qualifiedByName = "idToSerie")
    @Mapping(source = "usuarioId", target = "usuario", qualifiedByName = "idToUsuario")
    @Mapping(target = "cortes", ignore = true)
    @Mapping(target = "materialesAdicionales", ignore = true)
    CalculoVentana toEntity(CalculoVentanaDTO dto);

    void updateEntityFromDto(CalculoVentanaDTO dto, @MappingTarget CalculoVentana entity);

    @Named("idToLineaPresupuesto")
    default LineaPresupuesto idToLineaPresupuesto(Long id) {
        if (id == null) {
            return null;
        }
        LineaPresupuesto lineaPresupuesto = new LineaPresupuesto();
        lineaPresupuesto.setId(id);
        return lineaPresupuesto;
    }

    @Named("idToSerie")
    default SerieBase idToSerie(Long id) {
        if (id == null) {
            return null;
        }
        SerieBase serie = new SerieBase();
        serie.setId(id);
        return serie;
    }

    @Named("idToUsuario")
    default Usuario idToUsuario(Long id) {
        if (id == null) {
            return null;
        }
        Usuario usuario = new Usuario();
        usuario.setId(id);
        return usuario;
    }
}