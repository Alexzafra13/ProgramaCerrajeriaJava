package com.gestiontaller.server.repository.inventario;

import com.gestiontaller.server.model.inventario.EstadoRetal;
import com.gestiontaller.server.model.inventario.Retal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RetalRepository extends JpaRepository<Retal, Long> {

    List<Retal> findByProductoId(Long productoId);

    List<Retal> findByEstado(EstadoRetal estado);

    @Query("SELECT r FROM Retal r WHERE r.estado = :estado AND r.longitud >= :longitudMinima ORDER BY r.longitud ASC")
    List<Retal> findByEstadoAndLongitudMinima(EstadoRetal estado, Integer longitudMinima);

    @Query("SELECT r FROM Retal r WHERE r.estado = :estado AND r.producto.id = :productoId AND r.longitud >= :longitudMinima ORDER BY r.longitud ASC")
    List<Retal> findByEstadoAndProductoIdAndLongitudMinima(EstadoRetal estado, Long productoId, Integer longitudMinima);
}