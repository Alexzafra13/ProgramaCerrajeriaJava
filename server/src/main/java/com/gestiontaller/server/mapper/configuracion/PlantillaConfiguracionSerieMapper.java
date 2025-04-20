package com.gestiontaller.server.mapper.configuracion;

import com.gestiontaller.common.dto.configuracion.PlantillaConfiguracionSerieDTO;
import com.gestiontaller.server.model.configuracion.PlantillaConfiguracionSerie;
import com.gestiontaller.server.model.serie.SerieBase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {PerfilConfiguracionMapper.class, MaterialConfiguracionMapper.class})
public interface PlantillaConfiguracionSerieMapper {

    @Mapping(source = "serie.id", target = "serieId")
    @Mapping(source = "serie.nombre", target = "nombreSerie")
    PlantillaConfiguracionSerieDTO toDto(PlantillaConfiguracionSerie entity);

    @Mapping(source = "serieId", target = "serie", qualifiedByName = "idToSerie")
    PlantillaConfiguracionSerie toEntity(PlantillaConfiguracionSerieDTO dto);

    void updateEntityFromDto(PlantillaConfiguracionSerieDTO dto, @MappingTarget PlantillaConfiguracionSerie entity);

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