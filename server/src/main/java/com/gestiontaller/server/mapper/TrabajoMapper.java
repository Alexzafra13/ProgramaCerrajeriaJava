package com.gestiontaller.server.mapper;

import com.gestiontaller.common.dto.trabajo.TrabajoDTO;
import com.gestiontaller.server.model.cliente.Cliente;
import com.gestiontaller.server.model.presupuesto.Presupuesto;
import com.gestiontaller.server.model.trabajo.EstadoTrabajo;
import com.gestiontaller.server.model.trabajo.Trabajo;
import com.gestiontaller.server.model.usuario.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {MaterialAsignadoMapper.class, CambioEstadoMapper.class})
public interface TrabajoMapper {

    @Mapping(source = "presupuesto.id", target = "presupuestoId")
    @Mapping(source = "presupuesto.numero", target = "numeroPresupuesto")
    @Mapping(source = "cliente.id", target = "clienteId")
    @Mapping(source = "cliente.nombre", target = "nombreCliente", qualifiedByName = "getNombreClienteCompleto")
    @Mapping(source = "estado.id", target = "estadoId")
    @Mapping(source = "estado.nombre", target = "nombreEstado")
    @Mapping(source = "estado.color", target = "colorEstado")
    @Mapping(source = "usuarioAsignado.id", target = "usuarioAsignadoId")
    @Mapping(source = "usuarioAsignado.nombre", target = "nombreUsuarioAsignado")
    TrabajoDTO toDto(Trabajo entity);

    @Mapping(source = "presupuestoId", target = "presupuesto", qualifiedByName = "idToPresupuesto")
    @Mapping(source = "clienteId", target = "cliente", qualifiedByName = "idToCliente")
    @Mapping(source = "estadoId", target = "estado", qualifiedByName = "idToEstado")
    @Mapping(source = "usuarioAsignadoId", target = "usuarioAsignado", qualifiedByName = "idToUsuario")
    Trabajo toEntity(TrabajoDTO dto);

    @Mapping(source = "presupuestoId", target = "presupuesto", qualifiedByName = "idToPresupuesto")
    @Mapping(source = "clienteId", target = "cliente", qualifiedByName = "idToCliente")
    @Mapping(source = "estadoId", target = "estado", qualifiedByName = "idToEstado")
    @Mapping(source = "usuarioAsignadoId", target = "usuarioAsignado", qualifiedByName = "idToUsuario")
    void updateEntityFromDto(TrabajoDTO dto, @MappingTarget Trabajo entity);

    @Named("idToPresupuesto")
    default Presupuesto idToPresupuesto(Long id) {
        if (id == null) {
            return null;
        }
        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setId(id);
        return presupuesto;
    }

    @Named("idToCliente")
    default Cliente idToCliente(Long id) {
        if (id == null) {
            return null;
        }
        Cliente cliente = new Cliente();
        cliente.setId(id);
        return cliente;
    }

    @Named("idToEstado")
    default EstadoTrabajo idToEstado(Long id) {
        if (id == null) {
            return null;
        }
        EstadoTrabajo estado = new EstadoTrabajo();
        estado.setId(id);
        return estado;
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