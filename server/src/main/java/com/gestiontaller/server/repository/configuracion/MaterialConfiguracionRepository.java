package com.gestiontaller.server.repository.configuracion;

import com.gestiontaller.server.model.configuracion.MaterialConfiguracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialConfiguracionRepository extends JpaRepository<MaterialConfiguracion, Long> {
    List<MaterialConfiguracion> findByConfiguracionId(Long configuracionId);
}