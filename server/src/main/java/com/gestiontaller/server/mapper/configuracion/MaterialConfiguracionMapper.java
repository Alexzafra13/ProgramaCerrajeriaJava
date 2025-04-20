package com.gestiontaller.server.mapper.configuracion;

import com.gestiontaller.common.dto.configuracion.MaterialConfiguracionDTO;
import com.gestiontaller.server.model.configuracion.MaterialConfiguracion;
import com.gestiontaller.server.model.configuracion.PlantillaConfiguracionSerie;
import com.gestiontaller.server.model.producto.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface MaterialConfiguracionMapper {

    @Mapping(source = "configuracion.id", target = "configuracionId")
    @Mapping(source = "producto.id", target = "productoId")
    MaterialConfiguracionDTO toDto(MaterialConfiguracion entity);

    @Mapping(source = "configuracionId", target = "configuracion", qualifiedByName = "idToConfiguracion")
    @Mapping(source = "productoId", target = "producto", qualifiedByName = "idToProducto")
    MaterialConfiguracion toEntity(MaterialConfiguracionDTO dto);

    void updateEntityFromDto(MaterialConfiguracionDTO dto, @MappingTarget MaterialConfiguracion entity);

    @Named("idToConfiguracion")
    default PlantillaConfiguracionSerie idToConfiguracion(Long id) {
        if (id == null) {
            return null;
        }
        PlantillaConfiguracionSerie config = new PlantillaConfiguracionSerie();
        config.setId(id);
        return config;
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