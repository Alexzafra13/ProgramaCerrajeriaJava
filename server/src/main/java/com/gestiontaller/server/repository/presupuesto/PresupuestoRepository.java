package com.gestiontaller.server.repository.presupuesto;

import com.gestiontaller.common.model.presupuesto.EstadoPresupuesto;
import com.gestiontaller.server.model.presupuesto.Presupuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PresupuestoRepository extends JpaRepository<Presupuesto, Long> {

    Optional<Presupuesto> findByNumero(String numero);

    List<Presupuesto> findByClienteId(Long clienteId);

    List<Presupuesto> findByEstado(EstadoPresupuesto estado);

    List<Presupuesto> findByUsuarioId(Long usuarioId);

    @Query("SELECT p FROM Presupuesto p WHERE p.fechaCreacion BETWEEN :fechaInicio AND :fechaFin")
    List<Presupuesto> findByFechaCreacionBetween(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT p FROM Presupuesto p WHERE p.cliente.nombre LIKE %:busqueda% OR " +
            "p.cliente.apellidos LIKE %:busqueda% OR " +
            "p.cliente.razonSocial LIKE %:busqueda% OR " +
            "p.cliente.codigo LIKE %:busqueda% OR " +
            "p.numero LIKE %:busqueda%")
    List<Presupuesto> buscar(@Param("busqueda") String busqueda);

    @Query("SELECT p FROM Presupuesto p WHERE p.cliente.nombre LIKE %:busqueda% AND p.estado = :estado")
    List<Presupuesto> buscarPorEstado(
            @Param("busqueda") String busqueda,
            @Param("estado") EstadoPresupuesto estado);

    @Query("SELECT MAX(CAST(SUBSTRING(p.numero, LENGTH(:prefijo) + 1) AS int)) FROM Presupuesto p " +
            "WHERE p.numero LIKE CONCAT(:prefijo, '%') AND LENGTH(p.numero) > LENGTH(:prefijo)")
    Integer findLastNumeroByPrefijo(@Param("prefijo") String prefijo);
}