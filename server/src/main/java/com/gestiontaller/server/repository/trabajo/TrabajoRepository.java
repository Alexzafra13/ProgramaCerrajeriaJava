package com.gestiontaller.server.repository.trabajo;

import com.gestiontaller.server.model.trabajo.Trabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    List<Trabajo> findByFechaProgramada(LocalDate fecha);

    @Query("SELECT t FROM Trabajo t WHERE t.fechaProgramada BETWEEN :inicio AND :fin")
    List<Trabajo> findByFechaProgramadaBetween(LocalDate inicio, LocalDate fin);
}