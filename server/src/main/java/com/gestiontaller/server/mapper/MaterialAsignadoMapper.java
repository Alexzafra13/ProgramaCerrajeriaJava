package com.gestiontaller.server.mapper;

import com.gestiontaller.common.dto.trabajo.MaterialAsignadoDTO;
import com.gestiontaller.server.model.producto.Producto;
import com.gestiontaller.server.model.trabajo.MaterialAsignado;
import com.gestiontaller.server.model.trabajo.Trabajo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface MaterialAsignadoMapper {

    @Mapping(source = "trabajo.id", target = "trabajoId")
    @Mapping(source = "producto.id", target = "productoId")
    @Mapping(source = "producto.codigo", target = "codigoProducto")
    @Mapping(source = "producto.nombre", target = "nombreProducto")
    MaterialAsignadoDTO toDto(MaterialAsignado entity);

    @Mapping(source = "trabajoId", target = "trabajo", qualifiedByName = "idToTrabajo")
    @Mapping(source = "productoId", target = "producto", qualifiedByName = "idToProducto")
    MaterialAsignado toEntity(MaterialAsignadoDTO dto);

    @Mapping(source = "trabajoId", target = "trabajo", qualifiedByName = "idToTrabajo")
    @Mapping(source = "productoId", target = "producto", qualifiedByName = "idToProducto")
    void updateEntityFromDto(MaterialAsignadoDTO dto, @MappingTarget MaterialAsignado entity);

    @Named("idToTrabajo")
    default Trabajo idToTrabajo(Long id) {
        if (id == null) {
            return null;
        }
        Trabajo trabajo = new Trabajo();
        trabajo.setId(id);
        return trabajo;
    }

    @Named("idToProducto")
    default Producto idToProducto(Long id) {
        if (id == null) {
            return null;
        }
        Producto producto = new Producto();
        producto.setId(id);
        return producto;
    }
}