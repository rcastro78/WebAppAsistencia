package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.AfiliadoCreadoResumen;
import com.asistencia_el_salvador.web_app_asistencia.model.ContactoEmergenciaAfiliado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactoEmergenciaAfiliadoRepository
        extends JpaRepository<ContactoEmergenciaAfiliado,String> {
    List<ContactoEmergenciaAfiliado> findByDuiAfiliado(String dui);
}
