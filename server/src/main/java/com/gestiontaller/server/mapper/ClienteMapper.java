package com.gestiontaller.server.mapper;

import com.gestiontaller.common.dto.cliente.ClienteDTO;
import com.gestiontaller.server.model.cliente.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    ClienteDTO toDto(Cliente entity);

    Cliente toEntity(ClienteDTO dto);

    void updateEntityFromDto(ClienteDTO dto, @MappingTarget Cliente entity);
}