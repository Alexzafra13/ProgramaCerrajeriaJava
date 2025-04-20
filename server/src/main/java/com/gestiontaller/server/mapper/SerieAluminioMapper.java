package com.gestiontaller.server.mapper;

import com.gestiontaller.common.dto.serie.SerieAluminioDTO;
import com.gestiontaller.server.model.serie.SerieAluminio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {PerfilSerieMapper.class, DescuentoPerfilSerieMapper.class})
public interface SerieAluminioMapper {
    @Mapping(target = "perfiles", ignore = true)
    @Mapping(target = "descuentosPerfiles", ignore = true)
    SerieAluminioDTO toDto(SerieAluminio entity);

    @Mapping(target = "perfiles", ignore = true)
    @Mapping(target = "descuentosPerfiles", ignore = true)
    SerieAluminio toEntity(SerieAluminioDTO dto);

    void updateEntityFromDto(SerieAluminioDTO dto, @MappingTarget SerieAluminio entity);
}