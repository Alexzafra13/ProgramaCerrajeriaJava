package com.gestiontaller.server.repository.cliente;

import com.gestiontaller.common.model.cliente.TipoCliente;
import com.gestiontaller.server.model.cliente.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByCodigo(String codigo);

    Optional<Cliente> findByNifCif(String nifCif);

    List<Cliente> findByTipoCliente(TipoCliente tipoCliente);

    List<Cliente> findByActivoTrue();

    @Query("SELECT c FROM Cliente c WHERE UPPER(c.nombre) LIKE UPPER(CONCAT('%', :texto, '%')) OR " +
            "UPPER(c.apellidos) LIKE UPPER(CONCAT('%', :texto, '%')) OR " +
            "UPPER(c.razonSocial) LIKE UPPER(CONCAT('%', :texto, '%')) OR " +
            "UPPER(c.nifCif) LIKE UPPER(CONCAT('%', :texto, '%')) OR " +
            "UPPER(c.codigo) LIKE UPPER(CONCAT('%', :texto, '%'))")
    List<Cliente> buscarPorTexto(@Param("texto") String texto);
}