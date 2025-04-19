package com.gestiontaller.server.repository.configuracion;

import com.gestiontaller.server.model.configuracion.PerfilConfiguracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerfilConfiguracionRepository extends JpaRepository<PerfilConfiguracion, Long> {
    List<PerfilConfiguracion> findByConfiguracionId(Long configuracionId);
}