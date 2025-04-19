package com.gestiontaller.server.mapper.configuracion;

import com.gestiontaller.server.dto.configuracion.PerfilConfiguracionDTO;
import com.gestiontaller.server.model.configuracion.PerfilConfiguracion;
import com.gestiontaller.server.model.configuracion.PlantillaConfiguracionSerie;
import com.gestiontaller.server.model.serie.PerfilSerie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PerfilConfiguracionMapper {

    @Mapping(source = "configuracion.id", target = "configuracionId")
    @Mapping(source = "perfil.id", target = "perfilId")
    @Mapping(source = "perfil.codigo", target = "codigoPerfil")
    @Mapping(source = "perfil.nombre", target = "nombrePerfil")
    PerfilConfiguracionDTO toDto(PerfilConfiguracion entity);

    @Mapping(source = "configuracionId", target = "configuracion", qualifiedByName = "idToConfiguracion")
    @Mapping(source = "perfilId", target = "perfil", qualifiedByName = "idToPerfil")
    PerfilConfiguracion toEntity(PerfilConfiguracionDTO dto);

    void updateEntityFromDto(PerfilConfiguracionDTO dto, @MappingTarget PerfilConfiguracion entity);

    @Named("idToConfiguracion")
    default PlantillaConfiguracionSerie idToConfiguracion(Long id) {
        if (id == null) {
            return null;
        }
        PlantillaConfiguracionSerie config = new PlantillaConfiguracionSerie();
        config.setId(id);
        return config;
    }

    @Named("idToPerfil")
    default PerfilSerie idToPerfil(Long id) {
        if (id == null) {
            return null;
        }
        PerfilSerie perfil = new PerfilSerie();
        perfil.setId(id);
        return perfil;
    }
}