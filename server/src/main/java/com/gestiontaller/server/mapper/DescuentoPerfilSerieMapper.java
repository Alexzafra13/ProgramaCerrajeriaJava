package com.gestiontaller.server.mapper;

import com.gestiontaller.server.dto.serie.DescuentoPerfilSerieDTO;
import com.gestiontaller.server.model.serie.DescuentoPerfilSerie;
import com.gestiontaller.server.model.serie.SerieBase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface DescuentoPerfilSerieMapper {
    @Mapping(source = "serie.id", target = "serieId")
    DescuentoPerfilSerieDTO toDto(DescuentoPerfilSerie entity);

    @Mapping(source = "serieId", target = "serie", qualifiedByName = "idToSerie")
    DescuentoPerfilSerie toEntity(DescuentoPerfilSerieDTO dto);

    void updateEntityFromDto(DescuentoPerfilSerieDTO dto, @MappingTarget DescuentoPerfilSerie entity);

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