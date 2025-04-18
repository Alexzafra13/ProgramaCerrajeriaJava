package com.gestiontaller.server.mapper;

import com.gestiontaller.server.dto.usuario.UsuarioDTO;
import com.gestiontaller.server.model.usuario.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioDTO toDto(Usuario entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "ultimoAcceso", ignore = true)
    Usuario toEntity(UsuarioDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "ultimoAcceso", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateEntityFromDto(UsuarioDTO dto, @MappingTarget Usuario entity);
}