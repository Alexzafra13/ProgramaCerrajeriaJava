package com.gestiontaller.server.mapper;

import com.gestiontaller.server.dto.inventario.MovimientoStockDTO;
import com.gestiontaller.server.model.inventario.MovimientoStock;
import com.gestiontaller.server.model.producto.Producto;
import com.gestiontaller.server.model.usuario.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface MovimientoStockMapper {

    @Mapping(source = "producto.id", target = "productoId")
    @Mapping(source = "producto.nombre", target = "nombreProducto")
    @Mapping(source = "usuario.id", target = "usuarioId")
    @Mapping(source = "usuario.nombre", target = "nombreUsuario")
    MovimientoStockDTO toDto(MovimientoStock entity);

    @Mapping(source = "productoId", target = "producto", qualifiedByName = "idToProducto")
    @Mapping(source = "usuarioId", target = "usuario", qualifiedByName = "idToUsuario")
    MovimientoStock toEntity(MovimientoStockDTO dto);

    void updateEntityFromDto(MovimientoStockDTO dto, @MappingTarget MovimientoStock entity);

    @Named("idToProducto")
    default Producto idToProducto(Long id) {
        if (id == null) {
            return null;
        }
        Producto producto = new Producto();
        producto.setId(id);
        return producto;
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