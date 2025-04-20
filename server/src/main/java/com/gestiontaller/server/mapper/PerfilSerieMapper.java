package com.gestiontaller.server.mapper;

import com.gestiontaller.common.dto.serie.PerfilSerieDTO;
import com.gestiontaller.server.model.serie.PerfilSerie;
import com.gestiontaller.server.model.serie.SerieBase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PerfilSerieMapper {
    @Mapping(source = "serie.id", target = "serieId")
    @Mapping(source = "serie.nombre", target = "nombreSerie")
    PerfilSerieDTO toDto(PerfilSerie entity);

    @Mapping(source = "serieId", target = "serie", qualifiedByName = "idToSerie")
    PerfilSerie toEntity(PerfilSerieDTO dto);

    void updateEntityFromDto(PerfilSerieDTO dto, @MappingTarget PerfilSerie entity);

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