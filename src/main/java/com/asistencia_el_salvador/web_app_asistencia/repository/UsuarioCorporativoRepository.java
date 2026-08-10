package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.UsuarioClienteCorporativo;
import com.asistencia_el_salvador.web_app_asistencia.model.UsuarioComercio;
import com.asistencia_el_salvador.web_app_asistencia.model.UsuarioComercioPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioCorporativoRepository extends JpaRepository<UsuarioClienteCorporativo, String> {
    Optional<UsuarioClienteCorporativo> findByEmailAsociado(String emailAsociado);
    List<UsuarioClienteCorporativo> findByNit(String nit);
    UsuarioClienteCorporativo findByDui(String dui);
}
