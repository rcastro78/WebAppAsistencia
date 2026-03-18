package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.Usuario;
import com.asistencia_el_salvador.web_app_asistencia.model.UsuarioComercio;
import com.asistencia_el_salvador.web_app_asistencia.model.UsuarioComercioPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioComercioRepository extends JpaRepository<UsuarioComercio, UsuarioComercioPK> {
    Optional<UsuarioComercio> findByEmailAsociado(String emailAsociado);
}
