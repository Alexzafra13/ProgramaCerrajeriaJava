package com.gestiontaller.server.mapper;

import com.gestiontaller.common.dto.presupuesto.PresupuestoDTO;
import com.gestiontaller.server.model.cliente.Cliente;
import com.gestiontaller.server.model.presupuesto.Presupuesto;
import com.gestiontaller.server.model.usuario.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {LineaPresupuestoMapper.class})
public interface PresupuestoMapper {

    @Mapping(source = "cliente.id", target = "clienteId")
    @Mapping(source = "cliente.nombre", target = "nombreCliente", qualifiedByName = "getNombreClienteCompleto")
    @Mapping(source = "usuario.id", target = "usuarioId")
    @Mapping(source = "usuario.nombre", target = "nombreUsuario")
    PresupuestoDTO toDto(Presupuesto entity);

    @Mapping(source = "clienteId", target = "cliente", qualifiedByName = "idToCliente")
    @Mapping(source = "usuarioId", target = "usuario", qualifiedByName = "idToUsuario")
    Presupuesto toEntity(PresupuestoDTO dto);

    @Mapping(source = "clienteId", target = "cliente", qualifiedByName = "idToCliente")
    @Mapping(source = "usuarioId", target = "usuario", qualifiedByName = "idToUsuario")
    void updateEntityFromDto(PresupuestoDTO dto, @MappingTarget Presupuesto entity);

    @Named("idToCliente")
    default Cliente idToCliente(Long id) {
        if (id == null) {
            return null;
        }
        Cliente cliente = new Cliente();
        cliente.setId(id);
        return cliente;
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