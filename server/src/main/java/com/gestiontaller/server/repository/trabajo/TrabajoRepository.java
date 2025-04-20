package com.gestiontaller.server.repository.trabajo;

import com.gestiontaller.server.model.trabajo.EstadoTrabajo;
import com.gestiontaller.server.model.trabajo.Trabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrabajoRepository extends JpaRepository<Trabajo, Long> {

    Optional<Trabajo> findByCodigo(String codigo);

    List<Trabajo> findByClienteId(Long clienteId);

    List<Trabajo> findByPresupuestoId(Long presupuestoId);

    List<Trabajo> findByUsuarioAsignadoId(Long usuarioId);

    List<Trabajo> findByEstadoId(Long estadoId);

    @Query("SELECT t FROM Trabajo t WHERE t.estado.codigo = :codigoEstado")
    List<Trabajo> findByCodigoEstado(@Param("codigoEstado") String codigoEstado);

    List<Trabajo> findByFechaProgramada(LocalDate fecha);

    @Query("SELECT t FROM Trabajo t WHERE t.fechaProgramada BETWEEN :inicio AND :fin")
    List<Trabajo> findByFechaProgramadaBetween(
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin);

    @Query("SELECT t FROM Trabajo t WHERE " +
            "(t.cliente.nombre LIKE %:busqueda% OR " +
            "t.cliente.apellidos LIKE %:busqueda% OR " +
            "t.cliente.razonSocial LIKE %:busqueda% OR " +
            "t.cliente.codigo LIKE %:busqueda% OR " +
            "t.codigo LIKE %:busqueda%)")
    List<Trabajo> buscar(@Param("busqueda") String busqueda);

    @Query("SELECT t FROM Trabajo t WHERE " +
            "(t.cliente.nombre LIKE %:busqueda% OR " +
            "t.cliente.apellidos LIKE %:busqueda% OR " +
            "t.cliente.razonSocial LIKE %:busqueda% OR " +
            "t.cliente.codigo LIKE %:busqueda% OR " +
            "t.codigo LIKE %:busqueda%) AND " +
            "t.estado.id = :estadoId")
    List<Trabajo> buscarPorEstado(
            @Param("busqueda") String busqueda,
            @Param("estadoId") Long estadoId);

    @Query("SELECT MAX(CAST(SUBSTRING(t.codigo, LENGTH(:prefijo) + 1) AS int)) FROM Trabajo t " +
            "WHERE t.codigo LIKE CONCAT(:prefijo, '%') AND LENGTH(t.codigo) > LENGTH(:prefijo)")
    Integer findLastCodigoByPrefijo(@Param("prefijo") String prefijo);
}