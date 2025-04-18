package com.gestiontaller.server.repository.producto;

import com.gestiontaller.server.model.producto.Producto;
import com.gestiontaller.server.model.producto.TipoProducto;
import com.gestiontaller.server.model.serie.SerieBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByCodigo(String codigo);

    List<Producto> findByTipo(TipoProducto tipo);

    List<Producto> findBySerieAndTipo(SerieBase serie, TipoProducto tipo);

    @Query("SELECT p FROM Producto p WHERE p.stockActual < p.stockMinimo")
    List<Producto> findByStockActualLessThanStockMinimo();

    @Query("SELECT p FROM Producto p WHERE p.stockActual <= p.stockMinimo AND p.activo = true")
    List<Producto> findProductosBajoMinimo();

    @Query("SELECT p FROM Producto p WHERE p.tipo = :tipo AND p.medida LIKE %:medida%")
    List<Producto> findByTipoAndMedidaContaining(TipoProducto tipo, String medida);
}