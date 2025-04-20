package com.gestiontaller.server.mapper;

import com.gestiontaller.common.dto.serie.MaterialBaseSerieDTO;
import com.gestiontaller.server.model.producto.Producto;
import com.gestiontaller.server.model.serie.MaterialBaseSerie;
import com.gestiontaller.server.model.serie.SerieBase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface MaterialBaseSerieMapper {

    @Mapping(source = "serie.id", target = "serieId")
    @Mapping(source = "serie.nombre", target = "nombreSerie")
    @Mapping(source = "producto.id", target = "productoId")
    @Mapping(source = "producto.codigo", target = "codigoProducto")
    @Mapping(source = "producto.nombre", target = "nombreProducto")
    @Mapping(source = "producto.precioVenta", target = "precioUnitario")
    @Mapping(source = "producto.stockActual", target = "stockActual")
    MaterialBaseSerieDTO toDto(MaterialBaseSerie entity);

    @Mapping(source = "serieId", target = "serie", qualifiedByName = "idToSerie")
    @Mapping(source = "productoId", target = "producto", qualifiedByName = "idToProducto")
    MaterialBaseSerie toEntity(MaterialBaseSerieDTO dto);

    void updateEntityFromDto(MaterialBaseSerieDTO dto, @MappingTarget MaterialBaseSerie entity);

    @Named("idToSerie")
    default SerieBase idToSerie(Long id) {
        if (id == null) {
            return null;
        }
        SerieBase serie = new SerieBase();
        serie.setId(id);
        return serie;
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