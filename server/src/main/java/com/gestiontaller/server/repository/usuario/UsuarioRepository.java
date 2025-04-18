package com.gestiontaller.server.repository.usuario;

import com.gestiontaller.server.model.usuario.Rol;
import com.gestiontaller.server.model.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);

    List<Usuario> findByRol(Rol rol);

    List<Usuario> findByActivoTrue();
}