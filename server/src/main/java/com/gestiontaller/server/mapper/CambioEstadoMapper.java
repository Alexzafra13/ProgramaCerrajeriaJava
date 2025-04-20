package com.gestiontaller.server.mapper;

import com.gestiontaller.common.dto.trabajo.CambioEstadoDTO;
import com.gestiontaller.server.model.trabajo.CambioEstado;
import com.gestiontaller.server.model.trabajo.EstadoTrabajo;
import com.gestiontaller.server.model.trabajo.Trabajo;
import com.gestiontaller.server.model.usuario.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CambioEstadoMapper {

    @Mapping(source = "trabajo.id", target = "trabajoId")
    @Mapping(source = "estadoAnterior.id", target = "estadoAnteriorId")
    @Mapping(source = "estadoAnterior.nombre", target = "nombreEstadoAnterior")
    @Mapping(source = "estadoNuevo.id", target = "estadoNuevoId")
    @Mapping(source = "estadoNuevo.nombre", target = "nombreEstadoNuevo")
    @Mapping(source = "usuario.id", target = "usuarioId")
    @Mapping(source = "usuario.nombre", target = "nombreUsuario")
    CambioEstadoDTO toDto(CambioEstado entity);

    @Mapping(source = "trabajoId", target = "trabajo", qualifiedByName = "idToTrabajo")
    @Mapping(source = "estadoAnteriorId", target = "estadoAnterior", qualifiedByName = "idToEstado")
    @Mapping(source = "estadoNuevoId", target = "estadoNuevo", qualifiedByName = "idToEstado")
    @Mapping(source = "usuarioId", target = "usuario", qualifiedByName = "idToUsuario")
    CambioEstado toEntity(CambioEstadoDTO dto);

    @Mapping(source = "trabajoId", target = "trabajo", qualifiedByName = "idToTrabajo")
    @Mapping(source = "estadoAnteriorId", target = "estadoAnterior", qualifiedByName = "idToEstado")
    @Mapping(source = "estadoNuevoId", target = "estadoNuevo", qualifiedByName = "idToEstado")
    @Mapping(source = "usuarioId", target = "usuario", qualifiedByName = "idToUsuario")
    void updateEntityFromDto(CambioEstadoDTO dto, @MappingTarget CambioEstado entity);

    @Named("idToTrabajo")
    default Trabajo idToTrabajo(Long id) {
        if (id == null) {
            return null;
        }
        Trabajo trabajo = new Trabajo();
        trabajo.setId(id);
        return trabajo;
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
}