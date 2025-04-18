package com.gestiontaller.server.mapper;

import com.gestiontaller.server.dto.calculo.CorteDTO;
import com.gestiontaller.server.model.calculo.CorteVentana;
import com.gestiontaller.server.model.producto.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CorteVentanaMapper {

    @Mapping(source = "perfil.id", target = "perfilId")
    @Mapping(source = "perfil.codigo", target = "codigoPerfil")
    @Mapping(source = "perfil.nombre", target = "nombrePerfil")
    CorteDTO toDto(CorteVentana entity);

    @Mapping(source = "perfilId", target = "perfil", qualifiedByName = "idToPerfil")
    @Mapping(target = "calculoVentana", ignore = true)
    CorteVentana toEntity(CorteDTO dto);

    void updateEntityFromDto(CorteDTO dto, @MappingTarget CorteVentana entity);

    @Named("idToPerfil")
    default Producto idToPerfil(Long id) {
        if (id == null) {
            return null;
        }
        Producto perfil = new Producto();
        perfil.setId(id);
        return perfil;
    }
}