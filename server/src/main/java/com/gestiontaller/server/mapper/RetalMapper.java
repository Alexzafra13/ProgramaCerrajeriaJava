package com.gestiontaller.server.mapper;

import com.gestiontaller.server.dto.inventario.RetalDTO;
import com.gestiontaller.server.model.inventario.Retal;
import com.gestiontaller.server.model.producto.Producto;
import com.gestiontaller.server.model.trabajo.Trabajo;
import com.gestiontaller.server.model.usuario.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface RetalMapper {

    @Mapping(source = "producto.id", target = "productoId")
    @Mapping(source = "producto.nombre", target = "nombreProducto")
    @Mapping(source = "producto.tipo", target = "tipoProducto")
    @Mapping(source = "trabajoOrigen.id", target = "trabajoOrigenId")
    @Mapping(source = "trabajoOrigen.codigo", target = "codigoTrabajoOrigen")
    @Mapping(source = "usuarioRegistro.id", target = "usuarioRegistroId")
    @Mapping(source = "usuarioRegistro.nombre", target = "nombreUsuarioRegistro")
    RetalDTO toDto(Retal entity);

    @Mapping(source = "productoId", target = "producto", qualifiedByName = "idToProducto")
    @Mapping(source = "trabajoOrigenId", target = "trabajoOrigen", qualifiedByName = "idToTrabajo")
    @Mapping(source = "usuarioRegistroId", target = "usuarioRegistro", qualifiedByName = "idToUsuario")
    Retal toEntity(RetalDTO dto);

    void updateEntityFromDto(RetalDTO dto, @MappingTarget Retal entity);

    @Named("idToProducto")
    default Producto idToProducto(Long id) {
        if (id == null) {
            return null;
        }
        Producto producto = new Producto();
        producto.setId(id);
        return producto;
    }

    @Named("idToTrabajo")
    default Trabajo idToTrabajo(Long id) {
        if (id == null) {
            return null;
        }
        Trabajo trabajo = new Trabajo();
        trabajo.setId(id);
        return trabajo;
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