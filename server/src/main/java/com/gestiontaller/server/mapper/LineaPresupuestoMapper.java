package com.gestiontaller.server.mapper;

import com.gestiontaller.common.dto.presupuesto.LineaPresupuestoDTO;
import com.gestiontaller.server.model.presupuesto.LineaPresupuesto;
import com.gestiontaller.server.model.presupuesto.Presupuesto;
import com.gestiontaller.server.model.serie.SerieBase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface LineaPresupuestoMapper {

    @Mapping(source = "presupuesto.id", target = "presupuestoId")
    @Mapping(source = "serie.id", target = "serieId")
    @Mapping(source = "serie.nombre", target = "nombreSerie")
    @Mapping(source = "calculoVentana.id", target = "calculoVentanaId")
    LineaPresupuestoDTO toDto(LineaPresupuesto entity);

    @Mapping(source = "presupuestoId", target = "presupuesto", qualifiedByName = "idToPresupuesto")
    @Mapping(source = "serieId", target = "serie", qualifiedByName = "idToSerie")
    @Mapping(target = "calculoVentana", ignore = true)
    LineaPresupuesto toEntity(LineaPresupuestoDTO dto);

    @Mapping(source = "presupuestoId", target = "presupuesto", qualifiedByName = "idToPresupuesto")
    @Mapping(source = "serieId", target = "serie", qualifiedByName = "idToSerie")
    @Mapping(target = "calculoVentana", ignore = true)
    void updateEntityFromDto(LineaPresupuestoDTO dto, @MappingTarget LineaPresupuesto entity);

    @Named("idToPresupuesto")
    default Presupuesto idToPresupuesto(Long id) {
        if (id == null) {
            return null;
        }
        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setId(id);
        return presupuesto;
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
}