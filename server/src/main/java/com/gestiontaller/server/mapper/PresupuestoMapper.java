package com.gestiontaller.server.mapper;

import com.gestiontaller.common.dto.presupuesto.PresupuestoDTO;
import com.gestiontaller.server.model.cliente.Cliente;
import com.gestiontaller.server.model.presupuesto.Presupuesto;
import com.gestiontaller.server.model.usuario.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {LineaPresupuestoMapper.class, ClienteMapper.class})
public interface PresupuestoMapper {

    @Mapping(source = "cliente.id", target = "clienteId")
    @Mapping(source = "cliente", target = "nombreCliente", qualifiedByName = "getNombreClienteCompleto")
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
}