package com.gestiontaller.server.mapper;

import com.gestiontaller.common.dto.cliente.ClienteDTO;
import com.gestiontaller.server.model.cliente.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    ClienteDTO toDto(Cliente entity);

    Cliente toEntity(ClienteDTO dto);

    void updateEntityFromDto(ClienteDTO dto, @MappingTarget Cliente entity);

    @Named("getNombreClienteCompleto")
    default String getNombreClienteCompleto(Cliente cliente) {
        if (cliente == null) {
            return "";
        }

        if (cliente.getTipoCliente() != null) {
            switch (cliente.getTipoCliente()) {
                case PARTICULAR:
                case AUTONOMO:
                    return (cliente.getNombre() != null ? cliente.getNombre() : "") +
                            " " +
                            (cliente.getApellidos() != null ? cliente.getApellidos() : "");
                case EMPRESA:
                case ADMINISTRACION:
                    return cliente.getRazonSocial() != null ? cliente.getRazonSocial() : "";
                default:
                    return cliente.getNombre() != null ? cliente.getNombre() : "";
            }
        }

        return cliente.getNombre() != null ? cliente.getNombre() : "";
    }
}