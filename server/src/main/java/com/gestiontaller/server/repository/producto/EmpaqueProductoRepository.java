package com.gestiontaller.server.repository.producto;

import com.gestiontaller.server.model.producto.EmpaqueProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpaqueProductoRepository extends JpaRepository<EmpaqueProducto, Long> {

    List<EmpaqueProducto> findByProductoId(Long productoId);

    List<EmpaqueProducto> findByProductoCodigo(String codigoProducto);
}