package com.gestiontaller.server.mapper;

import com.gestiontaller.server.model.calculo.MaterialAdicional;
import com.gestiontaller.server.model.producto.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface MaterialAdicionalMapper {

    @Mapping(source = "producto.id", target = "productoId")
    @Mapping(source = "producto.codigo", target = "codigoProducto")
    @Mapping(source = "producto.nombre", target = "nombreProducto")
    MaterialAdicionalDTO toDto(MaterialAdicional entity);

    @Mapping(source = "productoId", target = "producto", qualifiedByName = "idToProducto")
    @Mapping(target = "calculoVentana", ignore = true)
    MaterialAdicional toEntity(MaterialAdicionalDTO dto);

    void updateEntityFromDto(MaterialAdicionalDTO dto, @MappingTarget MaterialAdicional entity);

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