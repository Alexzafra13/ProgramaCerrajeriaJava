package com.gestiontaller.server.mapper;

import com.gestiontaller.common.dto.serie.SerieBaseDTO;
import com.gestiontaller.server.model.serie.SerieBase;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SerieBaseMapper {
    SerieBaseDTO toDto(SerieBase entity);

    SerieBase toEntity(SerieBaseDTO dto);

    void updateEntityFromDto(SerieBaseDTO dto, @MappingTarget SerieBase entity);
}